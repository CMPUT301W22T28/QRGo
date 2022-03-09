package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.ui.leaderboard.LeaderboardViewModel;
import com.example.myapplication.ui.profile.ProfileViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityMainBinding binding;
    private String myUsername;
    final String TAG = "MainActivity";
    Player myPlayerProfile = null;
    Context activityContext;
    final int MY_CAMERA_REQUEST_CODE = 100;
    final int QR_CODE_SCAN = 49374;

    ProfileViewModel profileViewModel;
    LeaderboardViewModel leaderboardViewModel;

    private String qrResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityContext = this;
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // bottom nav bar setup
        setupNavBar();

        /*
        All other methods to setup fragment go after this line
        * */

        // setting up the view model/sending data
        setupProfileViewModel();

        getProfileFromDatabase();

        // changing anything in the layout. i.e. removing the top action bar
        layoutChanges();


    }

    private void setupProfileViewModel() {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void setupLeaderboardViewModel() {
        leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
    }

    private void setupNavBar() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_search, R.id.navigation_camera, R.id.navigation_leaderboard, R.id.navigation_profile)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void getProfileFromDatabase() {

        Log.d("ProfileFragment",getIntent().getStringExtra("Username"));
        this.myUsername = getIntent().getStringExtra("Username");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference docRef = db.collection("Users").document(this.myUsername);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Boolean isAdmin = snapshot.getBoolean("admin");

                    if (myPlayerProfile == null) {
                        if (isAdmin == null || !isAdmin) {
                            myPlayerProfile = new Player(myUsername, false);
                        }
                        else {
                            myPlayerProfile = new Player(myUsername, true);
                        }
                    }
                    profileViewModel.setUsername(myUsername);

                    myPlayerProfile.resetQrCodeList();
                    Long scannedCount = snapshot.getLong("scanned_count");

                    // get qrcodes
                    Object obj = snapshot.get("scanned_qrcodes");
                    Iterable<?> ar = (Iterable<?>) obj;
                    ArrayList<String> qrCodeHashes = new ArrayList<>();
                    assert ar != null;
                    for (Object x : ar) {
                        qrCodeHashes.add((String) x);
                    }
                    ScoringQRCode temp;
                    int i = 0;
                    for (String s: qrCodeHashes) {
                        temp = new ScoringQRCode(s);
                        temp.setScore(i++);
                        myPlayerProfile.addScoringQRCode(temp);
                    }

                    if (scannedCount == null || qrCodeHashes.size() != scannedCount.intValue()) {
                        docRef.update(
                                "scanned_count", qrCodeHashes.size()
                        );
                    }

                    // TODO: update the view model to add the qrcodes
                    profileViewModel.setProfileQrCodes(myPlayerProfile.getQrCodes());

                    Long topQRCode = snapshot.getLong("scanned_highest");
                    if (topQRCode != null) {
                        myPlayerProfile.setHighestScore(topQRCode.intValue());
                        Log.d(TAG, "Top qr code: "+topQRCode.intValue());
                    }
                    else {
                        myPlayerProfile.setHighestScore(-1);
                    }
                    profileViewModel.setTopQRCodeScore(myPlayerProfile.getTopQrCodeScore());

                    Long sumOfQRCodes = snapshot.getLong("scanned_sum");
                    if (sumOfQRCodes != null) {
                        myPlayerProfile.setTotalScore(sumOfQRCodes.intValue());
                    }
                    else {
                        myPlayerProfile.setTotalScore(-1);
                    }
                    profileViewModel.setTotalScore(myPlayerProfile.getTotalScore());

                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String scanContent = "null";
        String scanFormat = "";

        if (requestCode == QR_CODE_SCAN && resultCode == REQUEST_IMAGE_CAPTURE) {

//            Toast.makeText(MainActivity.this, "" + requestCode + " " + resultCode, Toast.LENGTH_LONG).show();

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                if (scanningResult.getContents() != null) {
                    scanContent = scanningResult.getContents().toString();
                    scanFormat = scanningResult.getFormatName().toString();
                }

                Toast.makeText(MainActivity.this, scanContent + "   type:" + scanFormat, Toast.LENGTH_SHORT).show();

                //Get hash
                qrResult = scanContent;

            } else {

                Toast.makeText(MainActivity.this, "Nothing scanned", Toast.LENGTH_SHORT).show();

            }

        }
        else if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitMap = (Bitmap) extras.get("data");

        }
    }

    private void layoutChanges() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(getApplicationContext(), "camera permission denied", Toast.LENGTH_LONG).show();


            }

        }
    }

    public void setBarCodeScanner(ImageView cameraImage) {

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission("android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);

                }

                else {

                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);

                    integrator.setPrompt("Scan a barcode or QRcode").setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

                    integrator.setOrientationLocked(true);

                    integrator.initiateScan();
                }
            }

        });

    }

    public String getMyUsername() {
        return this.myUsername;
    }
}