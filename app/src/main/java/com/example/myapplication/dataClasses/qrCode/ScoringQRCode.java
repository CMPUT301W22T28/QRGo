package com.example.myapplication.dataClasses.qrCode;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

/**
 * Used to represent a Scoring QR code. Contains getters/setters for the object's attributes.
 *
 * @author Walter Ostrander
 * @author Marc-Andre Haley
 * @author Amro Amanuddein
 *
 * March 10 2022
 */
public class ScoringQRCode extends QRCode {

    private String hash = "";
    private Double latitude = null;
    private Double longitude = null;
    private int score = -1;
    private final String typeOfQrCode = "scoring";

    /**
     * constructor for ScoringQRCode class
     * @param scannedString
     * hash of QR code object
     */
    public ScoringQRCode(String scannedString) {
        this.hash = stringToSHA256(scannedString);
        this.score = calculateScore();
    }

    /**
     * alternate constructor, for when the hash has already been set.
     * @param hash the hash of the qr code
     * @param alreadyHashed not important, only used to overload.
     */
    public ScoringQRCode(String hash, boolean alreadyHashed) {
        this.hash = hash;
        this.score = calculateScore();
    }

    /**
     * Getter for QRcode hash
     * @return hash
     * QRCode hash (also used as ID)
     */
    public String getHash(){
        return hash;
    }

    /**
     * Calculates and returns score of QR code
     * Not yet implemented
     * @return score
     * Score of QR code object
     */
    private int calculateScore() {
        int n = 0;
        // not using this.score since it is used in constructor
        int score_local = 0;
        for (int i = 1; i < this.hash.length(); i++){
            if (this.hash.charAt(i) == this.hash.charAt(i-1)){
                n++;
            }
            else{
                score_local += Math.pow( (int) this.hash.charAt(i), n);
                n = 0;
            }
        }
        return score_local;
    }

    /**
     * Setter for score
     * @param score
     * Integer to be set as score
     */
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }

    /**
     * Getter for the score
     * @return score
     * Score of QR code object
     */
    public int getScore() {
        return score;
    }

    /**
     * Setter for longitude of QR code
     * @param lon
     * double to be set as longitude
     */
    public void setLongitude(Double lon) {
        this.longitude = lon;
    }

    /**
     * Setter for latitude of QR code
     * @param lat
     * double to be set as latitude
     */
    public void setLatitude(Double lat) {
        this.latitude = lat;
    }

    /**
     * Getter for latitude of QR code
     * @return latitude
     */
    public Double getLatitude(){
        return latitude;
    }

    /**
     * Getter for longitude of QR code
     * @return longitude
     */
    public Double getLongitude(){
        return longitude;
    }

    /**
     * Gets geohash of QR code based on geolocation
     * @return geohash
     */
    public String getGeoHash(){
        return GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
    }

    /**
     * Gets geolocation of QR code and returns it as [lat,lon]
     * @return geoLocation
     */
    public String getGeolocationString()
    {
        if (this.longitude != null && this.latitude != null) {
            return  "[" + this.latitude + ", " + this.longitude+"]";
        }
        else {
            return "Not specified";
        }

    }
}
