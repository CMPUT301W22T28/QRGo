package com.example.myapplication.dataClasses.qrCode;

/**
 * Abstract class for all QR code types
 *
 * @author Walter Ostrander
 *
 * March 10 2022
 */
public abstract class QRCode {
    private String hash;

    /**
     * gets QR code class type
     * @return QR code class type
     */
    public abstract String getQRCodeType();
}
