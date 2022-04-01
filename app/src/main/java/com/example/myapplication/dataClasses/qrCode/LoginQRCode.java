package com.example.myapplication.dataClasses.qrCode;

/**
 * Represents a Login QR Code
 *
 * @author Walter Ostrander, Amro Amanuddein
 *
 * March 10 2022
 */
public class LoginQRCode extends QRCode {

    private String hash;
    private String scannedString;
    private final String typeOfQrCode = "Login";


    public LoginQRCode(String scannedString) {
        this.scannedString = scannedString;
        this.hash = stringToSHA256(scannedString);
    }

    public String getScannedString() {
        return scannedString;
    }

    public String getHash() {
        return this.hash;
    }
    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }

}
