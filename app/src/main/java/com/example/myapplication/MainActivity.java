package com.example.myapplication;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityMainBinding binding;
    private String myUsername = "ostrander001";
    final String TAG = "MainActivity";
    Player myPlayerProfile;
    Context activityContext;
    final int MY_CAMERA_REQUEST_CODE = 100;
    private String qrResult= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityContext = this;
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // bottom nav bar setup
        setupNavBar();

        getProfileFromDatabase();

        // changing anything in the layout. i.e. removing the top action bar
        layoutChanges();


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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Users").document(this.myUsername);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {

                        Boolean isAdmin = document.getBoolean("admin");

                        if (isAdmin == null || !isAdmin) {
                            myPlayerProfile = new Player(myUsername, false);
                        }
                        else {
                            myPlayerProfile = new Player(myUsername, true);
                        }
                        Long scanned_count = document.getLong("scanned_count");

                        // get qrcodes
                        Object obj = document.get("scanned_qrcodes");
                        Iterable<?> ar = (Iterable<?>) obj;
                        ArrayList<String> qrCodeHashes = new ArrayList<>();
                        assert ar != null;
                        for (Object x : ar) {
                            qrCodeHashes.add((String) x);
                        }

                        if (scanned_count == null || qrCodeHashes.size() != scanned_count.intValue()) {
                            docRef.update(
                                    "scanned_count", qrCodeHashes.size()
                            );
                        }



                        Log.d(TAG, String.valueOf(document.getLong("scanned_highest")));

                        // get list of qrCodes, avoiding warnings


                    }
                    else {
                        Log.d(TAG,"we ain't rlly here LOL");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String scanContent = "null";
        String scanFormat = "";

        Toast.makeText(MainActivity.this, "" + requestCode + " " + resultCode, Toast.LENGTH_LONG).show();

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

                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, REQUEST_IMAGE_CAPTURE);

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

//                    IntentIntegrator.forSupportFragment(CameraFragment.this)
//                            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
//                            .setBeepEnabled(true).setPrompt("Hello world").setOrientationLocked(true).setBarcodeImageEnabled(true)
//                            .initiateScan();

                    integrator.setPrompt("Scan a barcode or QRcode").setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

                    integrator.setOrientationLocked(true);

                    integrator.initiateScan();
//
//                    integrator.forSupportFragment(getParentFragment()).initiateScan();

//                    Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cInt, REQUEST_IMAGE_CAPTURE);
                }
            }

        });

    }

    public void setSavePicture(Switch savePictureSwitch) {

        savePictureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b==true) {
                    Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cInt, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    public String getMyUsername() {
        return this.myUsername;
    }
}