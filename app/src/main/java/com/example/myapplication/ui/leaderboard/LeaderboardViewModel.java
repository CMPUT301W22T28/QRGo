package com.example.myapplication.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.dataClasses.user.Player;

import java.util.ArrayList;

public class LeaderboardViewModel extends ViewModel {
    final String TAG = "LeaderboardViewModel";

    private final MutableLiveData<String> personalUsername;
    private final MutableLiveData<String> personalScore;
    private final MutableLiveData<ArrayList<Player>> playerRankingList;

    public LeaderboardViewModel() {
        personalUsername = new MutableLiveData<>();
        personalScore = new MutableLiveData<>();
        playerRankingList = new MutableLiveData<>();
    }

    public LiveData<String> getPersonalUsername() {
        return personalUsername;
    }

    public void setPersonalUsername(String personalUsername) {
        this.personalUsername.setValue(personalUsername);
    }

    public LiveData<String> getPersonalScore() {
        return personalScore;
    }

    public void setPersonalScore(String personalScore) {
        this.personalScore.setValue("Ranking: " + personalScore);
    }

    public LiveData<ArrayList<Player>> getRankingList() {return playerRankingList;}

    public void setPlayerRankingList(ArrayList<Player> playerList) {
        this.playerRankingList.setValue((playerList));
    }
}