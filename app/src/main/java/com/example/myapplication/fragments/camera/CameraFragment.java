package com.example.myapplication.fragments.camera;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.activity.QRScanActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCameraBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * This class represents the camera fragment that is responsible for scanning valid QRCodes,
 * taking pictures of the qrcode if the user wants them saved and finally enable the geolocation
 * feature so that the geolocation of the qr code is saved.
 *
 * @author: Mohamed Ali
 * @see: CameraFragmentViewModel
 *
 */
public class CameraFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_REQUEST_CODE = 2;
    private static final int QRCODE_SCAN_CAPTURE = 6;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ImageView cameraImage;
    private TextView sizeImageText;
    private Switch savePictureSwitch;
    private Switch saveGeolocationSwitch;
    private double longitude;
    private double latitude;
    private double sizeImage;
    private Bitmap imageBitMap;
    private Button savePostButton;
    private String QRCodeString = null;
    private FusedLocationProviderClient fusedLocationClient;


    private FragmentCameraBinding binding;

    /**
     *Inflates the camera fragment view so that it displays on the screen
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */

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

        savePostButton = binding.savePostButton;

        saveGeolocationSwitch = binding.geolocationSwitch;

        disablingButtons();

        setQRCodeScanner();

        setSavePicture();

        setGeolocationSwitch();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Log.d("CameraFragment", getActivity().getIntent().getStringExtra("Username"));


        savePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (saveGeolocationSwitch.isChecked()) {

                    setLocation();
                }

                else {
                    Log.d("MainActivity", longitude + " " + latitude);
                }
                //saveQRPost(String QRHash, );
            }
        });

        return root;
    }

    /**
     * enables the geolocation saving feature by asking the user for permission to access the
     * geolocation if the permission is already not granted.
     */
    private void setGeolocationSwitch() {

        saveGeolocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b==true) {

                    checkLocationPermission();
                }
                else {
                    //nothing to do
                }
            }
        });

    }

    /**
     * checks if the user has granted permmission for the app to use their geolocation before
     * proceeding to obtain the geolocation of the user.
     */
    public void setLocation() {

        checkLocationPermission();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            //update the longitude and latitude

                            latitude = location.getLatitude();

                            longitude = location.getLongitude();
                        }
                    }
                });
    }

    /**
     * this function checks if the user has allowed the app to obtain geolocation data on his behalf
     * if not then a permission request is made on the screen of the user.
     */
    public void checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("MainActivity", "In the fucking if statement");


            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

        Log.d("MainActivity" , "" + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) + " "
                + PackageManager.PERMISSION_GRANTED + " " + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    /**
     * this function checks if the user has allowed the app to obtain camera access on his behalf
     * if not then a permission request is made on the screen of the user.
     */
    public void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);

        }
    }

    /**
     * This method sets up the onClickListener needed to launch the qr code scanner using the
     * zxing library, upon successful detection of valid qrcode, the fragment is relaunched.
     * @see: QRScanActivity
     */
    public void setQRCodeScanner() {

        checkCameraPermission();

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getContext(), QRScanActivity.class).putExtra("Prev","CameraFragment");
                startActivityForResult(intent, 6);

            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * sets the onClickListener for the switch responsible to save a picture of the qrcode, if
     * enabled, the camera activity is launched.
     */
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

    /**
     * responsible for dealing with obtaining the permission of the user to use the geolocation of
     * the user's device if he decided to save the geolocation of the qrcode he scanned.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getContext(), "Location Services Enabled.", Toast.LENGTH_SHORT).show();

                Log.d("MainActivity", "Accepted");

            } else {

                Toast.makeText(getContext(), "Location must be enabled to save geolocation.", Toast.LENGTH_LONG).show();

                Log.d("MainActivity", "DENIED");

            }

        }
    }

    /**
     * This function deals with 2 situations. The first situation is when the user has chosen to
     * save a picture of the qrcode, and he has taken a picture, this function executes after
     * the user has taken a picture then updates the placeholder ic image of the camera icon to the
     * actual image taken by the user's camera and saves the picture's bitmap to be stored in the
     * post if the user agrees to set the respective image of his choice. The 2nd situation involves
     * the user scanning a valid qr code and we obtain the result to that qrcode and calculate the
     * score.
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
        else if (requestCode == QRCODE_SCAN_CAPTURE) {
            if(resultCode == RESULT_OK) {
                // Get the result from the returned Intent
                final String result = data.getStringExtra("ScoringQRCode");

                // Use the data - in this case, display it in a Toast.
                Log.d("CameraFragment",result);
                enablingButtons();
            } else {
                // AnotherActivity was not successful. No data to retrieve.
            }
        }

    }

    /**
     * Disable the buttons on the screen. this is done initially as the fragment loads.
     */
    public void disablingButtons() {

        savePostButton.setEnabled(false);
        savePostButton.setAlpha(.7f);
        savePostButton.setBackgroundColor(Color.GRAY);
        savePostButton.setTextColor(Color.BLACK);

        saveGeolocationSwitch.setEnabled(false);
        saveGeolocationSwitch.setTextColor(Color.BLACK);

        savePictureSwitch.setEnabled(false);
        savePictureSwitch.setTextColor(Color.BLACK);

    }

    /**
     * allow the buttons to be clicked and interacted with on the screen. This is done after the
     * user scans a valid QRCode.
     */
    public void enablingButtons() {

        savePostButton.setEnabled(true);
        savePostButton.setAlpha(1.0f);
        savePostButton.setTextColor(Color.WHITE);
        savePostButton.setBackgroundColor(Color.parseColor("#FF3700B3"));

        saveGeolocationSwitch.setEnabled(true);
        saveGeolocationSwitch.setTextColor(Color.WHITE);

        savePictureSwitch.setEnabled(true);
        savePictureSwitch.setTextColor(Color.WHITE);


    }
}