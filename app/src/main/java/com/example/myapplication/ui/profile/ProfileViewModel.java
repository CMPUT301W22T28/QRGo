package com.example.myapplication.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.dataClasses.user.Player;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> username;
    private final MutableLiveData<String> totalScore;
    private final MutableLiveData<String> qrCodeCount;
    private final MutableLiveData<String> topQRCodeScore;

    private Player myPlayerProfile;

    public ProfileViewModel() {
        username = new MutableLiveData<>();
        totalScore = new MutableLiveData<>();
        qrCodeCount = new MutableLiveData<>();
        topQRCodeScore = new MutableLiveData<>();
    }

    public void setMyPlayerProfile(Player player) {
        myPlayerProfile = player;
        username.setValue(myPlayerProfile.getUsername());
        totalScore.setValue(String.valueOf(myPlayerProfile.getTotalScore()));
        qrCodeCount.setValue(String.valueOf(myPlayerProfile.getQRCodeCount()));
        topQRCodeScore.setValue(String.valueOf(myPlayerProfile.getHighestScore()));
    }

    public void setTotalScore(int newTotalScore) {
        myPlayerProfile.setTotalScore(newTotalScore);
        totalScore.setValue(String.valueOf(newTotalScore));
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
}