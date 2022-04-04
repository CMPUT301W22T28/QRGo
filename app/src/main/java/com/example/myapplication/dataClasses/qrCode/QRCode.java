package com.example.myapplication.dataClasses.qrCode;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
     * Converts the scannedString parameter into the encrypted sha256 string
     * @param scannedString the string you want to encrypt
     * @return the hashed string
     */
    public String stringToSHA256(String scannedString){
            byte[] hash = null;
            String hashCode = null;// w  ww  .  j  a va 2 s.c  o m
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                hash = digest.digest(scannedString.getBytes());
            } catch (NoSuchAlgorithmException e) {

            }

            if (hash != null) {
                StringBuilder hashBuilder = new StringBuilder();
                for (int i = 0; i < hash.length; i++) {
                    String hex = Integer.toHexString(hash[i]);
                    if (hex.length() == 1) {
                        hashBuilder.append("0");
                        hashBuilder.append(hex.charAt(hex.length() - 1));
                    } else {
                        hashBuilder.append(hex.substring(hex.length() - 2));
                    }
                }
                hashCode = hashBuilder.toString();
            }

            return hashCode;
    }
    /**
     * gets QR code class type
     * @return QR code class type
     */
    public abstract String getQRCodeType();
}
