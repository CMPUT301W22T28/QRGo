package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ui.camera.CameraFragment;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class LoginScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_scan);

        scannerView = (ZXingScannerView) findViewById(R.id.scanner_view);

        Dexter.withActivity(this)
                .withPermission("android.permission.CAMERA")
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(LoginScanActivity.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(), "You must accept!", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
    public void loginActivity (String scanResult){
        startActivity(new Intent(this, LoginActivity.class).putExtra("LoginQRCode",scanResult));
    }

    public void cameraFragment(String scanResult) {

//        Intent parentIntent = NavUtils.getParentActivityIntent(this);
//        parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(parentIntent.putExtra("ScoringQRCode", scanResult));
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ScoringQRCode", scanResult);
        setResult(RESULT_OK, returnIntent);
        finish();

        //startActivity(new Intent(this, MainActivity.class).putExtra("ScoringQRCode", scanResult));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {

        if (getIntent().getStringExtra("Prev").equals("CameraFragment")){

            cameraFragment(rawResult.getText());
        }
        else{
            loginActivity(rawResult.getText());
        }
    }
}