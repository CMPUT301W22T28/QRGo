package com.example.myapplication.fragments.camera;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.activity.QRScanActivity;
import com.example.myapplication.R;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.databinding.FragmentCameraBinding;
import com.example.myapplication.fragments.search.SearchFragmentDirections;
import com.firebase.geofire.GeoFireUtils;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.osmdroid.util.GeoPoint;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final String GAME_STATUS_QRCODE_COLLECTION = "GameStatusQRCode";
    private final String USERS_COLLECTION = "Users";

    private ImageView cameraImage;
    private TextView sizeImageText;
    private Switch savePictureSwitch;
    private Switch saveGeolocationSwitch;
    private double sizeImage;
    private Bitmap imageBitMap;
    private Button savePostButton;
    private String QRCodeString = null;
    private GeoPoint currentLocation = new GeoPoint(0.0, 0.0); //null island;
    private String encodedQRCodeString;
    private boolean flag;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference imageStore = FirebaseStorage.getInstance("gs://qrgo-e62ee.appspot.com/").getReference();
    private String postUUID;
    ScoringQRCode scoringQRCode;


    private FragmentCameraBinding binding;
    LoginActivity loginActivity = new LoginActivity();

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

        Context ctx = getActivity().getApplicationContext();
        flag = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx) == com.google.android.gms.common.ConnectionResult.SUCCESS;

        savePostButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                savePost();
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
    public void updateLocation(HashMap<String, Object> container) {

        final int[] requestResult = {-1};
        final Context context = this.getActivity();

        // always check location permissions
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            requestResult[0] = 1;
            //obtain the location of the user.
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    Location location = task.getResult();
                    if (location != null){
                        currentLocation.setLatitude(location.getLatitude());
                        currentLocation.setLongitude(location.getLongitude());

                        if (container !=null) {
                            //update the latitude and longitdue values in the HashMap as well as the geohash.
                            container.replace("latitude" , null, currentLocation.getLatitude());
                            container.replace("longitude" , null, currentLocation.getLongitude());

                            container.replace("geoHash", null, GeoFireUtils.getGeoHashForLocation(new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude())));

                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to obtain Current Location", Toast.LENGTH_LONG).show();

                }
            });

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                requestResult[0] = 0;
        }
    }

    /**
     * this function checks if the user has allowed the app to obtain geolocation data on his behalf
     * if not then a permission request is made on the screen of the user.
     */
    public void checkLocationPermission() {
        final Context context = this.getActivity();

        if (ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
            //Do nothing you good.
        }
        else {

            //if permission hasn't been granted, then ask for permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }

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

                //launch the qrcode scanner from the camera fragment when the camera icon is clicked.
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

                    //if the save picture switch is disabled, then remove the image and set it to the
                    //drawable
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
                //user granted permission to use his location
                Toast.makeText(getContext(), "Location Services Enabled.", Toast.LENGTH_SHORT).show();


            } else {
                //permission denied.
                Toast.makeText(getContext(), "Location must be enabled to save geolocation.", Toast.LENGTH_LONG).show();

            }

        }
    }

    /**
     * responsible for checking if the scanned QRCode is a gamestatus qrcode, if so then it disables
     * the buttons and doesn't count it as a valid qrcode that the user can save. However, if it's
     * a valid scoring qrcode, then the user can proceed normally.
     * @param scannedString
     * @param context
     */
    public void checkGameStatusQRCode(String scannedString, Context context ){

        db.collection(GAME_STATUS_QRCODE_COLLECTION)
                .whereEqualTo("username",scannedString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // If we find any matching GameStatusQRCode's we redirect...
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                disablingButtons();
                                getAdminStatus(getActivity().getIntent().getStringExtra("Username"), scannedString.substring(3));
                            }
                            // If there are no matches, then it is a scoring qrcode
                            if (task.getResult().size() == 0){
                                scoringQRCode = new ScoringQRCode(QRCodeString);
                                TextView qrCodeScoreValue = binding.qrcodeScoreValue;
                                qrCodeScoreValue.setText(scoringQRCode.getScore() + "");
                                enablingButtons();
                            }
                        } else {
                        }

                    }
                });
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
                QRCodeString = result;

                loginActivity.checkLoginQRCode(result, getContext(), this, "CameraFragment");

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

    /**
     * This is responsible for saving the user's applied data to the post directory in the firestore
     * as well as the scoring qrcode if it doesn't exist in the database already.
     * After the checks are done to ensure that the data is valid. This is then followed by updating
     * the users data accordingly as well as the creation of the actual post.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void savePost() {
        //get a post ID that is unique.
        postUUID = UUID.randomUUID().toString();

        //show the loader till the process is complete.
        showLoader();

        HashMap<String, Object> scoringQRCodeData = new HashMap<>();

        HashMap<String, Object> post = new HashMap<>();

        scoringQRCodeData.put("comment_ids", new ArrayList<String>());

        encodedQRCodeString = scoringQRCode.getHash();
        scoringQRCodeData.put("score",scoringQRCode.getScore());

        post.put("qrcode_hash", encodedQRCodeString);

        post.put("username",getActivity().getIntent().getStringExtra("Username"));

        // User did not check location
        scoringQRCodeData.put("latitude", null);
        scoringQRCodeData.put("longitude", null);
        scoringQRCodeData.put("geoHash", null);

        if (saveGeolocationSwitch.isChecked()) {

            checkLocationPermission();

            if (flag) {
                updateLocation(scoringQRCodeData);
            }
        }

        post.put("url", null);

        if (savePictureSwitch.isChecked()) {

            //Update or upload an Image
            saveTheImage(post, scoringQRCodeData);
        }
        else {
            //user didn't save a picture so go to checking the validity of the qrcode.
            checkScoringQRCodeExists(encodedQRCodeString, scoringQRCodeData, post);
        }
    }

    public void checkScoringQRCodeExists(String encodedQRCodeString, HashMap<String, Object> scoringQRCodeData,
                                         HashMap<String, Object> post) {
        //check if the QRCode is already in there, if so, update it's stats. If the document doesnt exist, create a new one
        db.collection("ScoringQRCodes")
                .document(encodedQRCodeString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                //remove the comment ids from newly created hashmap.
                                scoringQRCodeData.remove("comment_ids");

                                //only update the relevant fields.
                                db.collection("Users").document(getActivity().getIntent().getStringExtra("Username"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot doc = task.getResult();

                                                ArrayList<String> hashes = getUserQRCodes(doc.getData());

                                                //only update the data if the user hasn't scanned it before.
                                                if (hashes.contains(encodedQRCodeString)== false) {
                                                    updateScoringQRCode(scoringQRCodeData);
                                                }
                                            }
                                        });
                                //Check that this man isn't pulling some bs
                                saveUserPost(post);

                            } else {
                                //the qrcode is new and thus we need to post it as a new one
                                createScoringQRCode(scoringQRCodeData);

                                //save the user post.
                                saveUserPost(post);
                            }
                        }
                        else {

                            //do nothing
                        }
                    }
                });
    }

    /**
     * This function is responsible for creating a new instance of a scoring qrcode to be saved in
     * firestore as well as adding the qr code to the user instance in scanned qrcodes.
     * @param scoringQRCodeData
     */
    public void createScoringQRCode(HashMap<String, Object> scoringQRCodeData) {
        List<String> scannedBy = new ArrayList<>();

        scannedBy.add(getActivity().getIntent().getStringExtra("Username"));

        scoringQRCodeData.put("scanned_by", scannedBy);
        scoringQRCodeData.put("score", scoringQRCode.getScore());
        db.collection("Users").document(getActivity().getIntent().getStringExtra("Username")).update("scanned_qrcodes",
                FieldValue.arrayUnion(encodedQRCodeString))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //do nothing
                    }
                });


        scoringQRCodeData.put("num_scanned_by", 1);

        scoringQRCodeData.put("last_scanned", Calendar.getInstance().getTime());


        db.collection("ScoringQRCodes")
                .document(encodedQRCodeString)
                .set(scoringQRCodeData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    /**
     * this function is responsible for updating the scoring qrcode metadata that is present in the
     * firestore instance, this can be the last_scanned date and the number of people that scanned
     * the qrcode.
     * @param scoringQRCodeData
     */
    public void updateScoringQRCode(HashMap<String, Object> scoringQRCodeData) {

        scoringQRCodeData.put("last_scanned", Calendar.getInstance().getTime());

        db.collection("ScoringQRCodes")
                .document(encodedQRCodeString)
                .update(scoringQRCodeData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


        db.collection("Users").document(getActivity().getIntent().getStringExtra("Username"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot document = task.getResult();

                            Map<String,Object > userInstance = document.getData();

                            ArrayList<String> qrCodeHashes = getUserQRCodes(userInstance);

                            if( qrCodeHashes.contains(encodedQRCodeString)==false) {

                                db.collection("ScoringQRCodes").document(encodedQRCodeString).update("num_scanned_by", FieldValue.increment(1));

                                db.collection("ScoringQRCodes").document(encodedQRCodeString).update("scanned_by", FieldValue.arrayUnion(
                                        getActivity().getIntent().getStringExtra("Username")
                                ));

                                //add to the users scanned qrcodes.
                                db.collection("Users").document(getActivity().getIntent().getStringExtra("Username"))
                                        .update("scanned_qrcodes",FieldValue.arrayUnion(encodedQRCodeString))
                                        .addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    removeLoader();
                                                }
                                            }
                                        }
                                );


                            }
                            else {

                                removeLoader();
                            }

                        }
                    }
                });

    }

    /**
     * return the scanned_qrcodes in the user instance with the given username.
     * @param userInstance
     * @return
     */

    public ArrayList<String> getUserQRCodes(Map<String, Object> userInstance) {
        Iterable<?> ar = (Iterable<?>) userInstance.get("scanned_qrcodes");
        ArrayList<String> qrCodeHashes = new ArrayList<>();
        assert ar != null;
        for (Object x : ar) {
            qrCodeHashes.add((String) x);
        }

        return qrCodeHashes;
    }

    public void showLoader() {

        ProgressBar saveInstanceProgressBar = binding.progressBar;
        saveInstanceProgressBar.setVisibility(View.VISIBLE);

        cameraImage.setVisibility(View.INVISIBLE);
        sizeImageText.setVisibility(View.INVISIBLE);
        savePictureSwitch.setVisibility(View.INVISIBLE);
        saveGeolocationSwitch.setVisibility(View.INVISIBLE);
        savePostButton.setVisibility(View.INVISIBLE);

        TextView saveGeolocationText = binding.geolocationText;
        saveGeolocationText.setVisibility(View.INVISIBLE);

        TextView savePictureText = binding.savePictureText;

        savePictureText.setVisibility(View.INVISIBLE);

        TextView qrCodeScoreText = binding.qrcodeScoreText;

        qrCodeScoreText.setVisibility(View.INVISIBLE);

        TextView qrCodeScoreValue = binding.qrcodeScoreValue;

        qrCodeScoreValue.setVisibility(View.INVISIBLE);

    }

    public void removeLoader() {

        ProgressBar saveInstanceProgressBar = binding.progressBar;
        saveInstanceProgressBar.setVisibility(View.INVISIBLE);

        cameraImage.setVisibility(View.VISIBLE);
        sizeImageText.setVisibility(View.VISIBLE);
        savePictureSwitch.setVisibility(View.VISIBLE);
        saveGeolocationSwitch.setVisibility(View.VISIBLE);
        savePostButton.setVisibility(View.VISIBLE);

        TextView saveGeolocationText = binding.geolocationText;
        saveGeolocationText.setVisibility(View.VISIBLE);

        TextView savePictureText = binding.savePictureText;

        savePictureText.setVisibility(View.VISIBLE);

        TextView qrCodeScoreText = binding.qrcodeScoreText;

        qrCodeScoreText.setVisibility(View.VISIBLE);

        TextView qrCodeScoreValue = binding.qrcodeScoreValue;

        qrCodeScoreValue.setVisibility(View.VISIBLE);

        clearAllFields();

    }

    /**
     * clear all the data fields in the CameraFragment instance.
     */
    public void clearAllFields() {

        cameraImage.setImageDrawable(null);
        cameraImage.setBackgroundResource(R.drawable.ic_outline_photo_camera_24);
        sizeImageText.setText("Image Size 0/64KB");
        imageBitMap = null;
        saveGeolocationSwitch.setChecked(false);
        savePictureSwitch.setChecked(false);
        currentLocation.setLongitude(0);
        currentLocation.setLatitude(0);
        encodedQRCodeString  = null;

        TextView qrCodeScoreValue = binding.qrcodeScoreValue;

        qrCodeScoreValue.setText("-");

        disablingButtons();

    }

    /**
     * this function saves the image that the user took using the camera into the firestore database
     * using the storage space, whereby we obtain the url of the image that we can use as reference.
     * @param post
     * @param scoringQRCodeData
     * @return
     */
    public HashMap<String, Object> saveTheImage(HashMap<String, Object> post,HashMap<String, Object> scoringQRCodeData) {

        StorageReference imageToStore = imageStore.child(String.format("images/%s", postUUID));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        if (imageBitMap!=null) {

            imageBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //check if the user has already scanned this specific qrcode, if so don't post an image.
            db.collection("Users").document(getActivity().getIntent().getStringExtra("Username"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();

                            Map<String,Object > userInstance = document.getData();

                            ArrayList<String> qrCodeHashes = getUserQRCodes(userInstance);

                            Boolean save = false;
                            for (String s: qrCodeHashes) {
                                if (s.equals(scoringQRCode.getHash())) {
                                    checkScoringQRCodeExists(encodedQRCodeString, scoringQRCodeData, post);
                                    return;
                                }
                            }

                            //if the man doesn't have the qr code, then save the image for sure.

                            UploadTask uploadTask = imageToStore.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads

                                    Toast.makeText(getActivity(), "Post Couldn't be Saved", Toast.LENGTH_SHORT);
                                }
                            })
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            imageToStore.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {

                                                        //replace the post url with the proper one.
                                                        post.replace("url", null, task.getResult().toString());


                                                        checkScoringQRCodeExists(encodedQRCodeString, scoringQRCodeData, post);


                                                    }
                                                    else{

                                                        //show the display again
                                                        removeLoader();

                                                        Toast.makeText(getContext(), "Failed to save QR Code", Toast.LENGTH_LONG).show();

                                                    }

                                                }

                                            });

                                        }
                                    });

                        }
                    });
        }

        else {

            //if the user has not taken an image, then proceed as usual.
            checkScoringQRCodeExists(encodedQRCodeString, scoringQRCodeData, post);

        }

        return null;
    }

    /**
     * this function posts the new scanned qr code that the user has scanned into the posts firestore
     * if there exists a post that has the same username and qrcode hash, it will not be posted.
     * @param post
     */
    public void saveUserPost(HashMap<String, Object> post) {
        db.collection("Posts")
        .whereEqualTo("qrcode_hash", post.get("qrcode_hash"))
                .whereEqualTo("username", post.get("username"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot doc = task.getResult();

                if (doc.isEmpty()) {
                    db.collection("Posts").document(postUUID).set(post);
                    Toast.makeText(getContext(), "Post Saved Successfully!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "You already have this QRcode!", Toast.LENGTH_LONG).show();
                }

                removeLoader();
            }
        });
    }

    /**
     * checks the admin status of the user, then redirect the user accordingly to the username
     * of admin view.
     * @param Username
     * @param scannedUsername
     */
    public void getAdminStatus(String Username, String scannedUsername){
        CameraFragment cameraFragment = this;
        db.collection(USERS_COLLECTION)
                .document(Username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Boolean isAdmin = task.getResult().getBoolean("admin");
                        CameraFragmentDirections.ActionNavigationCameraToNavigationProfile action = CameraFragmentDirections.actionNavigationCameraToNavigationProfile(
                                isAdmin,
                                // Redirect to username which is scannedString but remove the "gs-"
                                scannedUsername,
                                Username
                        );
                        NavHostFragment.findNavController(cameraFragment).navigate(action);


                } else {
                    Log.d("CameraFragment", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}