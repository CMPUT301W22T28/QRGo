package com.example.myapplication.ui.camera;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Objects;

public class CameraFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView cameraImage;
    private TextView sizeImageText;
    private double sizeImage;
    private Bitmap imageBitMap;
    private String qrCodeData;


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

            sizeImage = imageBitMap.getAllocationByteCount()/ 1e3;

            qrCodeData = scanQRImage(imageBitMap);

            if (sizeImage <= 64) {
                //set the image to be in the placeholder.
                cameraImage.setImageBitmap(imageBitMap);
                sizeImageText.setText("Image Size: " + imageBitMap.getAllocationByteCount()/1e3 + "/64KB");

            }

            else {

                //compress the bitmap of the image.
                int scaledWidth = imageBitMap.getWidth() / 2;
                int scaledHeight = imageBitMap.getHeight() / 2;

                imageBitMap = Bitmap.createScaledBitmap(imageBitMap,scaledWidth, scaledWidth, false);

                sizeImage = imageBitMap.getAllocationByteCount()/ 1e3;

                cameraImage.setImageBitmap(imageBitMap);
                sizeImageText.setText("Image Size: " + imageBitMap.getAllocationByteCount()/1e3 + "/64KB");
                Toast.makeText(this.getActivity(), "Image Size too large, Image Compressed", Toast.LENGTH_SHORT).show();

            }
        }

        Log.d("MainActivity", ""+ qrCodeData);
    }

    public String scanQRImage(Bitmap bMap) {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getActivity())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        Frame myFrame = new Frame.Builder()
                .setBitmap(bMap)
                .build();

        SparseArray<Barcode> barcodes = barcodeDetector.detect(myFrame);

        if(barcodes.size() != 0) {

            // Print the QR code's message
            Log.d("MainActivity",
                    barcodes.valueAt(0).displayValue
            );
        }
        else {
            Log.d("MainActivity","No Value");
        }

        return null;
    }
}