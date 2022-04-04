package com.example.myapplication.dataClasses.user;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

/**
 * This class is a representation of the player. It holds information necessary such as type of user, their score,
 * if they are a player or an admin.
 * @author Walter Ostrander
 * @author Sankalp
 * @see ScoringQRCode
 *
 * May 12, 2022
 */
public class Player {
    private String username;
    private final ArrayList<ScoringQRCode> scannedQRCodes = new ArrayList<>();
    private int totalScore;
    private int highestScore;
    private int rankingScore;
    private String rankingNumber;
    private String rankingLabel;
    private boolean isAdmin = false;

    /**
     * Constructor of the player class.
     * @param username the username of the player
     * @param isAdmin whether or not the player has admin privilages
     */
    public Player(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    /**
     * Clears the qrCode list so that you can add a new list of qr codes.
     */
    public void resetQRCodeList() {
        scannedQRCodes.clear();
    }

    /**
     * Adds a scoring qr code to the list of qr codes.
     * @param qrCode A new qr code to add to the current list and display on profile.
     */
    public void addScoringQRCode(ScoringQRCode qrCode) {
        scannedQRCodes.add(qrCode);
    }

    /**
     * Method that gets the username.
     * @return The current username of the player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns whether or not the user is an admin.
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Sets the username to a new username.
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

     /**
     * Allows someone to get the score that is being ranked.
     * @return The current ranking score.
     */
     public int getRankingScore() {
        return rankingScore;
    }

    /**
     * Allows someone to get the label that is to be displayed on the leaderboard fragment.
     * @return The current ranking label.
     */
    public String getRankingLabel() {
        return rankingLabel;
    }

    /**
     * Allows someone to get the number of the element on the list that is to be displayed on the leaderboard fragment.
     * @return The current ranking number.
     */
    public String getRankingNumber() { return rankingNumber; }
  
    /**
     * Allows the user to set the ranking score to a new score that will be displayed.
     * @param rankingScore The new ranking score.
     */
    public void setRankingScore(int rankingScore, String tabLabel, String rankingNumber) {
        this.rankingScore = rankingScore;
        this.rankingLabel = tabLabel + " Score - ";
        this.rankingNumber = rankingNumber;
    }

    /**
     * Allows someone to get the current total score.
     * @return The current total score.
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Allows the user to set the total score to a new total score.
     * @param totalScore The new total score.
     */
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * Allows someone to get the current highest score of the player.
     * @return The highest score of the player.
     */
    public int getHighestScore() {
        return highestScore;
    }

    /**
     * Allows someone to set the highest score of the player.
     * @param highestScore the new highest score of the player.
     */
    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    /**
     * Queries the number of qr codes that the player has.
     * @return The number of qr codes that the player has.
     */
    public int getQRCodeCount() {
        return scannedQRCodes.size();
    }

    /**
     * Allows someone to get the list of qr codes the player has.
     * @return The list of qr codes on the player profile.
     */
    public ArrayList<ScoringQRCode> getQRCodes() {
        return this.scannedQRCodes;
    }
}
