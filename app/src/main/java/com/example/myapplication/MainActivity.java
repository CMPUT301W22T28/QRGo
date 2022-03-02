package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String myUsername = "ostrander001";
    final String TAG = "MainActivity";
    Player myPlayerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void layoutChanges() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
    }

    public String getMyUsername() {
        return this.myUsername;
    }
}