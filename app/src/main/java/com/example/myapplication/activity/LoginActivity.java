package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings.Secure;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.qrCode.GameStatusQRCode;
import com.example.myapplication.dataClasses.qrCode.LoginQRCode;
import com.example.myapplication.fragments.camera.CameraFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Activity is responsible for the signup/automatic login/logging in via LoginQRCode.
 * @author Amro Amanuddein
 * @see QRScanActivity
 *
 */
public class LoginActivity extends AppCompatActivity {
    // Firestore collection names
    private final String USERS_COLLECTION = "Users";
    private final String LOGIN_QRCODE_COLLECTION = "LoginQRCode";
    private final String GAME_STATUS_QRCODE_COLLECTION = "GameStatusQRCode";
    // Tag for Logcat
    private final String LOGIN_TAG = "LoginActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String res = this.getIntent().getStringExtra("LoginQRCode");
        // If the getting string from the intent returns null, then no qrcode got scanned yet
        if (res != null) {
            disableSignUp();
            checkLoginQRCode(res, getApplicationContext(), null, "LoginActivity");
        }
        else {
            /**
             * Link that helped with extracting device id of android device.
             * @see "https://stackoverflow.com/questions/8769781/how-can-i-get-the-deviceid-of-my-android-emulator"
             */
            String androidId = Secure.getString(getApplicationContext().getContentResolver(),
                    Secure.ANDROID_ID);
            getUsernameFromAndroidId(androidId);
        }
        Button signUpButton = (Button) findViewById(R.id.btn_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText usernameField = (TextInputEditText) findViewById(R.id.username_field);
                TextInputEditText emailField = (TextInputEditText) findViewById(R.id.email_field);
                TextInputEditText phoneField = (TextInputEditText) findViewById(R.id.phone_field);

                String userNameInput = usernameField.getText().toString().trim();
                // If the user enters only spaces into the username field or if they exceed 20 chars
                if (userNameInput.length() == 0 || userNameInput.length() > 20) {
                    TextInputLayout usernameContainer = (TextInputLayout) findViewById(R.id.username_field_container);
                    usernameContainer.setBoxStrokeColor(Color.RED);
                    usernameContainer.setError("Enter a valid username!");
                } else {
                    checkUsernameExists(userNameInput, emailField.getText().toString(), phoneField.getText().toString());
                }
            }
        });
    }

    /**
     * This method will check if there is a user linked with the device's android id
     * if it does find a user, it will start the MainActivity/Log them in automatically
     * and if it does not find a user attributed with the android id, it will show the
     * sign up page.
     * @param androidId id of the phone that is using the app currently
     */
    public void getUsernameFromAndroidId(String androidId){
        disableSignUp();
        db.collection(USERS_COLLECTION)
                .whereArrayContains("devices",androidId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Send the username found to MainActivity and log the user in
                                mainActivity(document.getId());
                            }
                            // Added this condition to ensure signup page only shows if there are no users matching this device
                            if (task.getResult().size() == 0) {
                                enableSignUp();
                            }

                        } else {
                            Log.d(LOGIN_TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    /**
     * This method will validate using the db the string obtained from the qrcode that
     * was scanned to login, if the qrcode is a valid LoginQRCode, they get redirected
     * to the MainActivity/get logged in. Otherwise, a toast will be displayed.
     * @param scannedString the string that is obtained when the qrcode is scanned
     */
    public void checkLoginQRCode(String scannedString, Context context, CameraFragment cameraFragment, String activityCall){
        db.collection(LOGIN_QRCODE_COLLECTION)
                .whereEqualTo("username",scannedString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Login Activity
                                if (activityCall.equals("LoginActivity")) {
                                    updateUserDeviceList(Secure.getString(getApplicationContext().getContentResolver(),
                                            Secure.ANDROID_ID), scannedString);
                                    mainActivity(scannedString);
                                }
                                // Camera Fragment
                                else{
                                    cameraFragment.disablingButtons();
                                    Toast.makeText(context, "Please scan a valid QR Code.", Toast.LENGTH_LONG).show();
                                }
                            }
                            if (task.getResult().size() == 0){
                                // Login Activity no results
                                if (activityCall.equals("LoginActivity")){
                                    enableSignUp();
                                    Toast.makeText(context,"Login QRCode not recognized. Please scan a valid Login QRCode!",Toast.LENGTH_LONG).show();
                                }
                                // CameraFragment no results
                                else{
                                    cameraFragment.checkGameStatusQRCode(scannedString, context);
                                }

                            }
                        } else {
                            Log.d(LOGIN_TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }



    /**
     * This method will check if the username entered for signup exists in the db,
     * if it does then it will show an appropriate message for the user, if it doesn't
     * then it will insert the username along with the contact info into the db.
     * @param userName username entered by the user
     * @param email email entered by the user
     * @param phone phone number entered by the user
     */
    public void checkUsernameExists(String userName, String email, String phone){
        db.collection(USERS_COLLECTION)
                .document(userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                TextInputLayout usernameContainer = (TextInputLayout) findViewById(R.id.username_field_container);
                                usernameContainer.setBoxStrokeColor(Color.RED);
                                usernameContainer.setError("This username already exists!");
                            } else {
                                // Insert username, phone, email into the database.
                                insertUserInfo(userName, email, phone);
                            }
                        } else {
                            Log.d(LOGIN_TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Method to create a user document in the Users collection.
     * @param userName name of the document within the user collection
     * @param email value of the email subcollection within the userName document
     * @param phone value of the phone subcollection within the userName document
     */
    public void insertUserInfo(String userName, String email, String phone){
        db.collection(USERS_COLLECTION)
                .document(userName)
                .set(setUpUserSubCollection(email, phone));

        insertLoginQRCode(userName);

    }

    /**
     * Once a user logs in via a LoginQRCode, this method will run to update the
     * user's device list with the device they just logged in from.
     * @param androidID the device id
     * @param loginQRString the string obtained from the qrcode scanner
     */
    public void updateUserDeviceList(String androidID, String loginQRString){
        Map<String, Object> map = new HashMap<>();
        map.put("devices", FieldValue.arrayUnion(androidID));
        db.collection(USERS_COLLECTION).document(loginQRString).update(map);

    }
    /**
     * Once a user signs up, this method will generate their respective LoginQRCode.
     * @param userName the user's username that will be the string represented by the QRCode
     */
    public void insertLoginQRCode(String userName){
        LoginQRCode loginQRCode = new LoginQRCode(userName);
        db.collection(LOGIN_QRCODE_COLLECTION)
                .document(loginQRCode.getHash())
                .set(setUpLoginQRCodeSubCollection(userName));

        insertGameStatusQRCode(userName);
    }

    /**
     * Once a user signs up, this method will generate their respective LoginQRCode.
     * @param userName the user's username that will be the string represented by the QRCode
     */
    public void insertGameStatusQRCode(String userName){
        GameStatusQRCode gameStatusQRCode = new GameStatusQRCode("gs-"+userName);
        db.collection(GAME_STATUS_QRCODE_COLLECTION)
                .document(gameStatusQRCode.getHash())
                .set(setUpLoginQRCodeSubCollection("gs-"+userName));

        mainActivity(userName);
    }

    /**
     * This method will start the MainActivity activity and will pass in the username that
     * was logged in using the Intent.
     * @param userName the username that will be passed in with the intent
     */
    public void mainActivity (String userName){
        startActivity(new Intent(this, MainActivity.class).putExtra("Username",userName));
    }

    /**
     * This method deals with the UI elements of the sign up page and disables the signup page
     * elements and displays a spinning progress bar instead.
     */
    public void disableSignUp(){

        ProgressBar spinner;
        spinner = (ProgressBar) findViewById(R.id.login_progress_bar);
        spinner.setVisibility(View.VISIBLE);

        ImageView appLogo = (ImageView) findViewById(R.id.app_logo_image);
        appLogo.setVisibility(View.INVISIBLE);

        TextInputLayout usernameContainer = (TextInputLayout) findViewById(R.id.username_field_container);
        TextInputEditText usernameField = (TextInputEditText) findViewById(R.id.username_field);
        usernameContainer.setVisibility(View.INVISIBLE);
        usernameField.setVisibility(View.INVISIBLE);

        TextInputLayout emailContainer = (TextInputLayout) findViewById(R.id.email_field_container);
        TextInputEditText emailField = (TextInputEditText) findViewById(R.id.email_field);
        emailContainer.setVisibility(View.INVISIBLE);
        emailField.setVisibility(View.INVISIBLE);

        TextInputLayout phoneContainer = (TextInputLayout) findViewById(R.id.phone_field_container);
        TextInputEditText phoneField = (TextInputEditText) findViewById(R.id.phone_field);
        phoneContainer.setVisibility(View.INVISIBLE);
        phoneField.setVisibility(View.INVISIBLE);

        Button signUpButton = (Button) findViewById(R.id.btn_sign_up);
        signUpButton.setVisibility(View.INVISIBLE);

        TextView accountExistsText = (TextView) findViewById(R.id.account_exists_text);
        accountExistsText.setVisibility(View.INVISIBLE);

    }
    /**
     * This method deals with the UI elements of the sign up page and enables the signup page.
     */
    public void enableSignUp(){
        // Make progress bar invisible as user has to sign in now
        ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.login_progress_bar);
        spinner.setVisibility(View.INVISIBLE);

        ImageView appLogo = (ImageView) findViewById(R.id.app_logo_image);
        appLogo.setVisibility(View.VISIBLE);

        TextInputLayout usernameContainer = (TextInputLayout) findViewById(R.id.username_field_container);
        TextInputEditText usernameField = (TextInputEditText) findViewById(R.id.username_field);
        usernameContainer.setVisibility(View.VISIBLE);
        usernameField.setVisibility(View.VISIBLE);

        TextInputLayout emailContainer = (TextInputLayout) findViewById(R.id.email_field_container);
        TextInputEditText emailField = (TextInputEditText) findViewById(R.id.email_field);
        emailContainer.setVisibility(View.VISIBLE);
        emailField.setVisibility(View.VISIBLE);

        TextInputLayout phoneContainer = (TextInputLayout) findViewById(R.id.phone_field_container);
        TextInputEditText phoneField = (TextInputEditText) findViewById(R.id.phone_field);
        phoneContainer.setVisibility(View.VISIBLE);
        phoneField.setVisibility(View.VISIBLE);

        Button signUpButton = (Button) findViewById(R.id.btn_sign_up);
        signUpButton.setVisibility(View.VISIBLE);

        SpannableString ss = new SpannableString("Already got an account? Login using your QR Code here.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View textView) {
                startActivity(new Intent(getApplicationContext(), QRScanActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 49, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView accountExistsText = (TextView) findViewById(R.id.account_exists_text);
        accountExistsText.setVisibility(View.VISIBLE);
        accountExistsText.setText(ss);
        accountExistsText.setMovementMethod(LinkMovementMethod.getInstance());
        accountExistsText.setHighlightColor(Color.TRANSPARENT);

    }

    /**
     * This method sets up & returns up the LoginQRCode subcollection that will be added to
     * the LoginQRCode collection with an automatically generated document id.
     * @param userName
     * @return a map of the LoginQRCode subcollection that will be added to the db.
     * @see
     */
    Map<String, Object> setUpLoginQRCodeSubCollection(String userName){
        Map<String, Object> data = new HashMap<>();
        data.put("username",userName);

        return data;
    }

    /**
     * This method sets up & returns up the Users subcollection that will be added to
     * the Users collection with the username as a document id.
     * @param email the value of the email field.
     * @param phone the value of the phone field.
     * @return a map of the Users subcollection that will be added to the db
     */
    Map<String, Object> setUpUserSubCollection(String email, String phone){
        Map<String, Object> data = new HashMap<>();

        data.put("admin",false);

        List<String> devices = new ArrayList<>();
        devices.add(Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID));
        data.put("devices",devices);

        if (email.trim().length()==0) {
            data.put("email",null);
        }
        else{
            data.put("email",email);
        }

        if (phone.trim().length()==0) {
            data.put("phone",null);
        }
        else{
            data.put("phone",phone);
        }

        data.put("scanned_count",0);
        data.put("scanned_highest",0);
        List<String> scanned_qrcodes = new ArrayList<>();
        data.put("scanned_qrcodes",scanned_qrcodes);
        data.put("scanned_sum",0);

        return data;
    }


}
