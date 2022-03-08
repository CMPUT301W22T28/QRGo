package com.example.myapplication.dataClasses.qrCode;

import com.google.firebase.firestore.GeoPoint;

public class ScoringQRCode extends QRCode {
    // image
    // private image image; //idk lol
    private String hash = "";
    private Double latitude = null;
    private Double longitude = null;
    private String geolocation = "";
    private int score = -1;

    public ScoringQRCode(String hash) {
        this.hash = hash;
        this.score = calculateScore();
    }

    private int calculateScore() {
        return -1;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String getQRCodeType() {
        return null;
    }

    public int getScore() {
        return score;
    }

    public void setLongitude(Double lon) {
        this.longitude = lon;
    }

    public void setLatitude(Double lat) {
        this.latitude = lat;
    }

    public String getGeolocation()
    {
        if (this.longitude != null && this.latitude != null) {
            return  "[" + this.latitude + ", " + this.longitude+"]";
        }
        else {
            return "Not specified";
        }

    }
}
