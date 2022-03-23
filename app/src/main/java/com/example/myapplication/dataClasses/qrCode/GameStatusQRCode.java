package com.example.myapplication.dataClasses.qrCode;

/**
 * Represents a Game Status QR Code
 *
 * @author Walter Ostrander, Amro Amanuddein
 *
 * March 10 2022
 */
public class GameStatusQRCode extends QRCode {
    private String hash;

    private final String typeOfQrCode = "profile";

    public GameStatusQRCode(String scannedString){
        this.hash = stringToSHA256(scannedString);
    }

    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }
}
