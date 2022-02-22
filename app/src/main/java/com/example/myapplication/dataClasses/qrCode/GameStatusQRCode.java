package com.example.myapplication.dataClasses.qrCode;

public class GameStatusQRCode extends QRCode {
    private String hash;

    private final String typeOfQrCode = "profile";

    @Override
    public String getQRCodeType() {
        return typeOfQrCode;
    }
}
