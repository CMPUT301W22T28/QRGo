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
    private final String typeOfQrCode = "login";


    public LoginQRCode(String scannedString) {
        this.hash = stringToSHA256(scannedString);
    }

    public String getHash() {
        return this.hash;
    }
    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }

}
