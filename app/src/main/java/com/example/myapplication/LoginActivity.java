package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



public class LoginActivity extends AppCompatActivity {
    private final String USERS_COLLECTION = "Users";
    private final String LOGIN_TAG = "LoginActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String androidId = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        disableSignUp();
        getUsernameFromAndroidId(androidId);


    }

    public void getUsernameFromAndroidId(String androidId){
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

    public void insertUserInfo(){

    }

    public void mainActivity (String userName){
        startActivity(new Intent(this, MainActivity.class).putExtra("Username",userName));
    }

    public void disableSignUp(){

        ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.login_progress_bar);
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
            public void onClick(View textView) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("Username","My link works kid"));
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


}
