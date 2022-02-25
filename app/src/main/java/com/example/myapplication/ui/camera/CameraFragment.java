package com.example.myapplication.ui.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCameraBinding;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Objects;
import com.example.myapplication.ui.camera.Capture;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private IntentIntegrator intentIntegrator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        CameraViewModel cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Activate camera from clicking on the camera image
        ImageView cameraImage = binding.cameraImageHolder;

        //initialize integrator
        intentIntegrator =  new IntentIntegrator(getActivity()).forSupportFragment(this);

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initialize the integrator for fragments.
//               FragmentIntentIntegrator intentIntegrator = new FragmentIntentIntegrator(CameraFragment.this);

                //Lock the orientation of the screen
                intentIntegrator.setOrientationLocked(true);

                //Set the capture activity
//                intentIntegrator.setCaptureActivity(new Capture());

                //Start the scan
                intentIntegrator.initiateScan();
//
//                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                someActivityResultLauncher.launch(cInt);
            }

        });
        return root;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Log.d("MainActivity", "result of picture ");
                    }
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}