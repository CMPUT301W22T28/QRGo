package com.example.myapplication.dataClasses.qrCode;

public class ScoringQRCode extends QRCode {
    // image
    // private image image; //idk lol
    private String hash;

    @Override
    public String getQRCodeType() {
        return null;
    }

    public int getScore() {

        // return the score of the qr code
        return -1;
    }
}
