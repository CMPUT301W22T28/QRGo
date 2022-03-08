package com.example.myapplication.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    final String TAG = "ProfileViewModel";

    private final MutableLiveData<String> username;
    private final MutableLiveData<String> totalScore;
    private final MutableLiveData<String> qrCodeCount;
    private final MutableLiveData<String> topQRCodeScore;

    private final MutableLiveData<ArrayList<ScoringQRCode>> mutableProfileQrCodes;


    public ProfileViewModel() {
        username = new MutableLiveData<>();
        totalScore = new MutableLiveData<>();
        qrCodeCount = new MutableLiveData<>();
        topQRCodeScore = new MutableLiveData<>();
        mutableProfileQrCodes = new MutableLiveData<>();
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getTotalScore() {
        return totalScore;
    }

    public LiveData<String> getQrCodeCount() {
        return qrCodeCount;
    }

    public LiveData<String> getTopQRCodeScore() {
        return topQRCodeScore;
    }

    public LiveData<ArrayList<ScoringQRCode>> getQrCodes() { return mutableProfileQrCodes; }

    public void setUsername(String username) {
        if (!String.valueOf(this.username.getValue()).equals(username)) {
            this.username.setValue(username);
        }
    }

    public void setTotalScore(int totalScore) {
        if (!String.valueOf(this.totalScore.getValue()).equals(String.valueOf(totalScore))) {
            this.totalScore.setValue(String.valueOf(totalScore));
        }
    }

    public void setTopQRCodeScore(int topQRCodeScore) {
        if (!String.valueOf(this.topQRCodeScore.getValue()).equals(String.valueOf(topQRCodeScore))) {
            this.topQRCodeScore.setValue(String.valueOf(topQRCodeScore));
        }
    }


    public void setMutableProfileQrCodes(ArrayList<ScoringQRCode> qrCodes) {
        this.mutableProfileQrCodes.setValue(qrCodes);
        this.qrCodeCount.setValue(String.valueOf(qrCodes.size()));
    }
}