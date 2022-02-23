package com.example.myapplication.dataClasses.qrCode;

public class ScoringQRCode extends QRCode {
    // image
    // private image image; //idk lol
    private String hash;
    private String geolocation;
    private int score = -1;

    @Override
    public String getQRCodeType() {
        return null;
    }

    public int getScore() {

        // return the score of the qr code
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getGeolocation()
    {
        return "undefined";
    }
}
