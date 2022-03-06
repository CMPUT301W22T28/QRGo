package com.example.myapplication.dataClasses.user;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public class Player {

    private String username;
    private ArrayList<ScoringQRCode> scannedQRCodes = new ArrayList<>();
    private int totalScore;
    private int highestScore;
    private final boolean isUserSet;
    private boolean isAdmin = false;

    // if player is not set yet, isUserSet remains false
    public Player() {
        this.isUserSet = false;
    }

    public Player(String username, boolean isAdmin) {
        this.username = username;

        this.isAdmin = isAdmin;
        this.isUserSet = true;
    }

    public void addScoringQRCode(ScoringQRCode qrCode) {
        scannedQRCodes.add(qrCode);
    }

    public String getUsername() {
        return username;
    }

    public boolean isUserSet() {
        return isUserSet;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public int getQRCodeCount() {
        return scannedQRCodes.size();
    }
}
