package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    // Firestore collection names
    private final String USERS_COLLECTION = "Users";
    private final String LOGIN_QRCODE_COLLECTION = "LoginQRCode";
    // Tag for Logcat
    private final String LOGIN_TAG = "LoginActivity";
    private final int MY_CAMERA_REQUEST_CODE = 100;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login
        String res = this.getIntent().getStringExtra("LoginQRCode");
        if (res != null) {
            disableSignUp();
            checkLoginQRCode(res);
        }
        else {
            String androidId = Secure.getString(getApplicationContext().getContentResolver(),
                    Secure.ANDROID_ID);
            getUsernameFromAndroidId(androidId);


            Button signUpButton = (Button) findViewById(R.id.btn_sign_up);
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextInputEditText usernameField = (TextInputEditText) findViewById(R.id.username_field);
                    TextInputEditText emailField = (TextInputEditText) findViewById(R.id.email_field);
                    TextInputEditText phoneField = (TextInputEditText) findViewById(R.id.phone_field);

                    String userNameInput = usernameField.getText().toString().trim();

                    if (userNameInput.length() == 0) {
                        TextInputLayout usernameContainer = (TextInputLayout) findViewById(R.id.username_field_container);
                        usernameContainer.setBoxStrokeColor(Color.RED);
                        usernameContainer.setError("Enter a valid username!");
                    } else {
                        checkUsernameExists(userNameInput, emailField.getText().toString(), phoneField.getText().toString());
                    }
                }
            });
        }
    }

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

    public void checkLoginQRCode(String scannedString){
        db.collection(LOGIN_QRCODE_COLLECTION)
                .whereEqualTo("username",scannedString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // TODO: Add device that was just scanned from to login to USERS.Devices
                                mainActivity(scannedString);
                            }
                            if (task.getResult().size() == 0){
                                enableSignUp();
                                Toast.makeText(getApplicationContext(),"Login QRCode not recognized. Please scan a valid Login QRCode!",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(LOGIN_TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

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


    public void insertUserInfo(String userName, String email, String phone){
        db.collection(USERS_COLLECTION)
                .document(userName)
                .set(setUpUserSubCollection(email, phone));

        insertLoginQRCode(userName);

    }

    public void insertLoginQRCode(String userName){
        db.collection(LOGIN_QRCODE_COLLECTION)
                .add(setUpLoginQRCodeSubCollection(userName));

        mainActivity(userName);
    }

    public void mainActivity (String userName){
        startActivity(new Intent(this, MainActivity.class).putExtra("Username",userName));
    }

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
                startActivity(new Intent(getApplicationContext(), LoginScanActivity.class).putExtra("Prev", "LoginActivity"));
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

    Map<String, Object> setUpLoginQRCodeSubCollection(String userName){
        Map<String, Object> data = new HashMap<>();
        data.put("username",userName);

        return data;
    }

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
