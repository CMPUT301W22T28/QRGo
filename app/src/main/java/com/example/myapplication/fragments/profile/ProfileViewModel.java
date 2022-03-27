package com.example.myapplication.fragments.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

/**
 * The view model that holds all the livedata for the profile fragment
 * @author Walter
 * @see ProfileFragment
 *
 * March 12, 2022
 */
public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> username;
    private final MutableLiveData<String> totalScore;
    private final MutableLiveData<String> qrCodeCount;
    private final MutableLiveData<String> topQRCodeScore;
    private final MutableLiveData<ArrayList<ScoringQRCode>> mutableProfileQRCodes;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phone;

    /**
     * the constructor for the profile view model, it initializes all the live data for the fragment.
     */
    public ProfileViewModel() {
        username = new MutableLiveData<>();
        totalScore = new MutableLiveData<>();
        qrCodeCount = new MutableLiveData<>();
        topQRCodeScore = new MutableLiveData<>();
        mutableProfileQRCodes = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phone = new MutableLiveData<>();
    }

    /**
     * Getter for the username livedata.
     * @return The username livedata.
     */
    public LiveData<String> getUsername() {
        return username;
    }

    /**
     * A getter for the total Score livedata.
     * @return The totalScore livedata.
     */
    public LiveData<String> getTotalScore() {
        return totalScore;
    }

    /**
     * A getter for the qrCodeCount.
     * @return the qrCodeCount livedata.
     */
    public LiveData<String> getQRCodeCount() {
        return qrCodeCount;
    }

    /**
     * A getter for the topQrCodeScore livedata.
     * @return the topQrCodeScore livedata.
     */
    public LiveData<String> getTopQRCodeScore() {
        return topQRCodeScore;
    }

    /**
     * A getter for the profile Qr Code live data array.
     * @return The profile qr code array live data.
     */
    public LiveData<ArrayList<ScoringQRCode>> getQrCodes() { return mutableProfileQRCodes; }

    /**
     * A getter for the profile email live data.
     * @return The email live data.
     */
    public LiveData<String> getEmail () { return email; }

    /**
     * A getter for the profile phone live data.
     * @return The phone live data.
     */
    public LiveData<String> getPhone () { return phone; }

    /**
     * Sets the username if it it is not the same as the current username.
     * @param username The new username to change the profile to.
     */
    public void setUsername(String username) {
        if (!String.valueOf(this.username.getValue()).equals(username)) {
            this.username.setValue(username);
        }
    }

    /**
     * Sets the current total score to a new total score if it is not already set.
     * @param totalScore The new total score to set the livedata to.
     */
    public void setTotalScore(int totalScore) {
        if (!String.valueOf(this.totalScore.getValue()).equals(String.valueOf(totalScore))) {
            this.totalScore.setValue(String.valueOf(totalScore));
        }
    }

    /**
     * Sets the top qr code score if it not the same as the current qr code score.
     * @param topQRCodeScore The new top qr code score to set to.
     */
    public void setTopQRCodeScore(int topQRCodeScore) {
        if (!String.valueOf(this.topQRCodeScore.getValue()).equals(String.valueOf(topQRCodeScore))) {
            this.topQRCodeScore.setValue(String.valueOf(topQRCodeScore));
        }
    }

    /**
     * Sets the profile qr code array, along with the qr code count.
     * @param qrCodes The array of qr codes to update the profile to.
     */
    public void setMutableProfileQRCodes(ArrayList<ScoringQRCode> qrCodes) {
        this.mutableProfileQRCodes.setValue(qrCodes);
        this.qrCodeCount.setValue(String.valueOf(qrCodes.size()));
    }

    public void setEmail(String email) {
        if (!String.valueOf(this.email.getValue()).equals(email)) {
            this.email.setValue(email);
        }
    }

    public void setPhone(String phone) {
        if (!String.valueOf(this.phone.getValue()).equals(phone)) {
            this.phone.setValue(phone);
        }
    }
}