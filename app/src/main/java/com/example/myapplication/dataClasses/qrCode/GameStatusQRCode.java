package com.example.myapplication.dataClasses.qrCode;

/**
 * Represents a Game Status QR Code
 *
 * @author Walter Ostrander
 *
 * March 10 2022
 */
public class GameStatusQRCode extends QRCode {
    private String hash;

    private final String typeOfQrCode = "profile";

    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }
}
