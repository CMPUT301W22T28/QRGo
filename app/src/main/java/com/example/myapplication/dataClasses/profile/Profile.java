package com.example.myapplication.dataClasses.profile;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public class Profile {
    private String userName;

    private int numQRCodes = 0;

    private ArrayList<ScoringQRCode> playerQRCodes;

    public Profile(String userName) {
        this.userName = userName;
    }
}
