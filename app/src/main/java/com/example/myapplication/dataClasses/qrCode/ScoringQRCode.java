package com.example.myapplication.dataClasses.qrCode;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

/**
 * Used to represent a Scoring QR code. Contains getters/setters for the object's attributes.
 *
 * @author Walter Ostrander
 *
 * March 10 2022
 */
public class ScoringQRCode extends QRCode {

    private String hash = "";
    private Double latitude = null;
    private Double longitude = null;
    private int score = -1;

    /**
     * constructor for ScoringQRCode class
     * @param hash
     * hash of QR code object
     */
    public ScoringQRCode(String hash) {
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
        return -1;
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
        return null;
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
