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
    private String scannedString;
    private final String typeOfQrCode = "GameStatus";

    public GameStatusQRCode(String scannedString){
        this.scannedString = scannedString;
        this.hash = stringToSHA256(scannedString);
    }

    public String getScannedString() {
        return scannedString;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }
}
