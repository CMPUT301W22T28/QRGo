package com.example.myapplication.dataClasses.qrCode;

public class ScoringQRCode extends QRCode {
    // image
    // private image image; //idk lol
    private String hash;
    private String geolocation;
    private int score = -1;

    public ScoringQRCode(String hash) {
        this.hash = hash;
        this.score = calculateScore();
    }

    private int calculateScore() {
        return -1;
    }

    @Override
    public String getQRCodeType() {
        return null;
    }

    public int getScore() {

        // return the score of the qr code
        return score;
    }

    public String getGeolocation()
    {
        return "undefined";
    }
}
