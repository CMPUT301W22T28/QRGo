package com.example.myapplication.ui.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.dataClasses.user.Player;

import java.util.ArrayList;

/**
 *
 * This view model creates a stores and manages all of the UI componenets for the leaderboard
 *
 * @author CMPUT 301 team 28, Sankalp Saini
 *
 * March 11, 2022
 */

/*
 * Sources
 *
 * ViewModel: https://developer.android.com/topic/libraries/architecture/viewmodel
 *
 */

public class LeaderboardViewModel extends ViewModel {
    final String TAG = "LeaderboardViewModel";

    private final MutableLiveData<String> personalUsername;
    private final MutableLiveData<String> personalScore;
    private final MutableLiveData<ArrayList<Player>> playerRankingList;

    /**
     * the constructor for the leaderboard view model, it initializes all the live data for the fragment.
     */
    public LeaderboardViewModel() {
        personalUsername = new MutableLiveData<>();
        personalScore = new MutableLiveData<>();
        playerRankingList = new MutableLiveData<>();
    }

    /**
     * Returns the username of the active profile
     *
     * @return  personalUsername username of the active profile
     *
     */
    public LiveData<String> getPersonalUsername() {
        return personalUsername;
    }

    public void setPersonalUsername(String personalUsername) {
        this.personalUsername.setValue(personalUsername);
    }

    /**
     * Returns the placement of the active profile
     *
     * @return personalScore placement (integer) of the profile
     *
     */
    public LiveData<String> getPersonalScore() {
        return personalScore;
    }

    public void setPersonalScore(String personalScore) {
        this.personalScore.setValue("Ranking: " + personalScore);
    }

    /**
     * Returns the ranked list of players
     *
     * @return  playerRankingList list of players (ranked)
     *
     */
    public LiveData<ArrayList<Player>> getRankingList() {return playerRankingList;}

    /**
     * Creates a playerRankingList based on inputted list
     *
     * @param  playerList playerList is a ranked list of type Player
     *
     */
    public void setPlayerRankingList(ArrayList<Player> playerList) {
        this.playerRankingList.setValue((playerList));
    }
}