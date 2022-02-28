package com.example.myapplication.dataClasses.user;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public class Player {

    private String username;
    private ArrayList<ScoringQRCode> scanned_codes;
    private int sum_score;
    private int highest_score;
    private int most_codes_score;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSum_score() {
        return sum_score;
    }

    public void setSum_score(int sum_score) {
        this.sum_score = sum_score;
    }

    public int getHighest_score() {
        return highest_score;
    }

    public void setHighest_score(int highest_score) {
        this.highest_score = highest_score;
    }

    public int getMost_codes_score() {
        return most_codes_score;
    }

    public void setMost_codes_score(int most_codes_score) {
        this.most_codes_score = most_codes_score;
    }
}
