package com.example.myapplication.activity;


import android.content.Context;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.fragments.profile.ProfileViewModel;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityContext = this;
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // bottom nav bar setup
        setupNavBar();

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
    public String getMyUsername() {
        return this.myUsername;
    }

}