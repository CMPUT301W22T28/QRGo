package com.example.myapplication.dataClasses.qrCode;

/**
 * Represents a Login QR Code
 *
 * @author Walter Ostrander
 *
 * March 10 2022
 */
public class LoginQRCode extends QRCode {

    private String hash;

    private final String typeOfQrCode = "login";
    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }

}
