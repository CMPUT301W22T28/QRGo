package com.example.myapplication.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.dataClasses.user.Player;

import java.util.ArrayList;

public class LeaderboardViewModel extends ViewModel {
    final String TAG = "LeaderboardViewModel";

    private final MutableLiveData<String> mText;
    private final MutableLiveData<ArrayList<Player>> playerRankingList;

    public LeaderboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the leaderboard fragment");
        playerRankingList = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<Player>> getRankingList() {return playerRankingList;}

    public void setPlayerRankingList(ArrayList<Player> playerList) {
        this.playerRankingList.setValue((playerList));
    }
}