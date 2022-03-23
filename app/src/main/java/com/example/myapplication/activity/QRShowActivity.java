package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRShowActivity extends AppCompatActivity {
    private final String SHOW_TAG = "QRShowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrshow);
        Log.d(SHOW_TAG, getIntent().getStringExtra("Username"));
        Log.d(SHOW_TAG, getIntent().getStringExtra("qrCodeType"));


        ImageView qrCodeImage = ((ImageView) findViewById(R.id.imageView2));
        QRCodeWriter writer = new QRCodeWriter();

        /**
         * The following code for generating the qrcode was taken from:
         * https://stackoverflow.com/questions/8800919/how-to-generate-a-qr-code-for-an-android-application
         */

        try {
            BitMatrix bitMatrix = writer.encode(getIntent().getStringExtra("Username"), BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
           qrCodeImage.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}