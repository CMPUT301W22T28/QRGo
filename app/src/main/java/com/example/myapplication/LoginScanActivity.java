package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
        // TODO: Change qr detection grid
        scannerView = (ZXingScannerView) findViewById(R.id.scanner_view);
        scannerView.setLaserEnabled(false);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        scannerView.stopCameraPreview();
        scannerView.stopCamera();
        loginActivity(rawResult.getText());
    }
}