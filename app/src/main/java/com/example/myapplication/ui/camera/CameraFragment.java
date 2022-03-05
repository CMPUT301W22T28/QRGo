package com.example.myapplication.ui.camera;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCameraBinding;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CameraFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView cameraImage;
    private TextView sizeImageText;
    private Switch savePictureSwitch;
    private double sizeImage;
    private Bitmap imageBitMap;
    final int MY_CAMERA_REQUEST_CODE = 100;


    private FragmentCameraBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        CameraViewModel cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Activate camera from clicking on the camera image
        cameraImage = binding.cameraImageHolder;

        sizeImageText = binding.imageSizeText;

        savePictureSwitch = binding.savePictureSwitch;

        MainActivity activity = (MainActivity) getActivity();

        activity.setBarCodeScanner(cameraImage);

        setSavePicture();


//        cameraImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.CAMERA},
//                            MY_CAMERA_REQUEST_CODE);
//
//
//                }
//
//                else {
//
//                    IntentIntegrator integrator = new IntentIntegrator(getActivity());
//
////                    IntentIntegrator.forSupportFragment(CameraFragment.this)
////                            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
////                            .setBeepEnabled(true).setPrompt("Hello world").setOrientationLocked(true).setBarcodeImageEnabled(true)
////                            .initiateScan();
//
//                    integrator.setPrompt("Scan a barcode or QRcode");
//
//                    integrator.setOrientationLocked(true);
//
//                    integrator.initiateScan();
////
////                    integrator.forSupportFragment(getParentFragment()).initiateScan();
//
////                    Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                    startActivityForResult(cInt, REQUEST_IMAGE_CAPTURE);
//                }
//            }
//
//        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setSavePicture() {

        savePictureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b==true) {
                    Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cInt, REQUEST_IMAGE_CAPTURE);
                }

                else {
                    cameraImage.setImageDrawable(null);
                    cameraImage.setBackgroundResource(R.drawable.ic_outline_photo_camera_24);
                    sizeImageText.setText("Image Size 0/64KB");
                    imageBitMap = null;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitMap = (Bitmap) extras.get("data");

            cameraImage.setBackgroundResource(0);

            sizeImage = imageBitMap.getAllocationByteCount() / 1e3;

            if (sizeImage <= 64) {
                //set the image to be in the placeholder.
                cameraImage.setImageBitmap(imageBitMap);
                sizeImageText.setText("Image Size: " + imageBitMap.getAllocationByteCount() / 1e3 + "/64KB");

            } else {

                //compress the bitmap of the image.
                int scaledWidth = imageBitMap.getWidth() / 2;
                int scaledHeight = imageBitMap.getHeight() / 2;

                Bitmap newImageBitMap = Bitmap.createScaledBitmap(imageBitMap, scaledWidth, scaledHeight, false);

                sizeImage = imageBitMap.getAllocationByteCount() / 1e3;

                cameraImage.setImageBitmap(newImageBitMap);
                sizeImageText.setText("Image Size: " + newImageBitMap.getAllocationByteCount() / 1e3 + "/64KB");
                Toast.makeText(this.getActivity(), "Image Size too large, Image Compressed", Toast.LENGTH_SHORT).show();

            }
        }
    }
    public void setImageInHolder(Bitmap image) {

        Log.d("MainActivity", "Triggered nigga");

        /*

            double sizeImage = imageBitMap.getAllocationByteCount() / 1e3;

            if (sizeImage <= 64) {
                //set the image to be in the placeholder.
                cameraImage.setImageBitmap(imageBitMap);
                sizeImageText.setText("Image Size: " + imageBitMap.getAllocationByteCount() / 1e3 + "/64KB");

            } else {

                //compress the bitmap of the image.
                int scaledWidth = imageBitMap.getWidth() / 2;
                int scaledHeight = imageBitMap.getHeight() / 2;

                Bitmap newImageBitMap = Bitmap.createScaledBitmap(imageBitMap, scaledWidth, scaledHeight, false);

                sizeImage = imageBitMap.getAllocationByteCount() / 1e3;

                cameraImage.setImageBitmap(imageBitMap);
                sizeImageText.setText("Image Size: " + imageBitMap.getAllocationByteCount() / 1e3 + "/64KB");
                Toast.makeText(this.getActivity(), "Image Size too large, Image Compressed", Toast.LENGTH_SHORT).show();

            }
         */
    }
}