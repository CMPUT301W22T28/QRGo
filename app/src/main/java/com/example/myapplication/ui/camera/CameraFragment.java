package com.example.myapplication.ui.camera;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView cameraImage;
    private TextView sizeImageText;
    private double sizeImage;
    private Bitmap imageBitMap;


    private FragmentCameraBinding binding;
    private IntentIntegrator intentIntegrator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        CameraViewModel cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Activate camera from clicking on the camera image
        cameraImage = binding.cameraImageHolder;

        //initialize integrator
        intentIntegrator =  new IntentIntegrator(getActivity()).forSupportFragment(this);

        sizeImageText = binding.imageSizeText;

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt, REQUEST_IMAGE_CAPTURE);
            }

        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitMap = (Bitmap) extras.get("data");

            Log.d("MainActivity", "" + imageBitMap.toString());

            sizeImage = imageBitMap.getAllocationByteCount()/ 1e6;

            if (sizeImage <= 20) {
                //set the image to be in the placeholder.
                cameraImage.setImageBitmap(imageBitMap);
                sizeImageText.setText("Image Size: " + imageBitMap.getAllocationByteCount()/1e6 + "/20MB");

            }

            else {

                //compress the bitmap of the image.
                int scaledWidth = imageBitMap.getWidth() / 10;
                int scaledHeight = imageBitMap.getHeight() / 10;

                imageBitMap = Bitmap.createScaledBitmap(imageBitMap,scaledWidth, scaledWidth, false);

                sizeImage = imageBitMap.getAllocationByteCount()/ 1e6;

                cameraImage.setImageBitmap(imageBitMap);
                sizeImageText.setText("Image Size: " + imageBitMap.getAllocationByteCount()/1e6 + "/20MB");
                Toast.makeText(this.getActivity(), "Image Size too large, Image Compressed", Toast.LENGTH_SHORT).show();

            }
        }
    }
}