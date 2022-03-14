package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @author Amro Amanuddein
 * @see LoginActivity
 * @see com.example.myapplication.fragments.camera.CameraFragment
 */
public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_scan);
        // TODO: Change qr detection grid size
        scannerView = (ZXingScannerView) findViewById(R.id.scanner_view);
        scannerView.setLaserEnabled(false);

        Dexter.withActivity(this)
                .withPermission("android.permission.CAMERA")
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(QRScanActivity.this);
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

    /**
     * Starts the LoginActivity activity and passes the result of the QRScan with it
     * @param scanResult
     */
    public void loginActivity (String scanResult){
        startActivity(new Intent(this, LoginActivity.class).putExtra("LoginQRCode",scanResult));
    }

    /**
     * Starts the camera fragment and passes the result of the QRScan with it
     * @param scanResult result of the QRScan
     */
    public void cameraFragment(String scanResult) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ScoringQRCode", scanResult);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        scannerView.stopCameraPreview();
        scannerView.stopCamera();
        // This if statement is to check which activity/fragment launched this activity to redirect accordingly
        if (getIntent().getStringExtra("Prev") != null && getIntent().getStringExtra("Prev").equals("CameraFragment")){
            cameraFragment(rawResult.getText());
        }
        else{
            loginActivity(rawResult.getText());
        }
    }
}