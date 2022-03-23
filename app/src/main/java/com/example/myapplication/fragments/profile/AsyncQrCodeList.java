package com.example.myapplication.fragments.profile;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

/**
 * This class lets a person add qr codes to a list asynchronously. So that
 * asynchronous database fetches can happen, then notify a listener that all the
 * qr codes have been added to a list
 * @author Walter
 * @see ProfileEventListeners
 *
 * March 12, 2022
 */
public class AsyncQrCodeList {
    private ProfileEventListeners profileEventListeners;
    private int asyncTaskCount = 0;
    private int numTasksToReach;
    private ArrayList<ScoringQRCode> qrCodes = new ArrayList<>();

    /**
     * Constructor of AsyncQrCodeList
     * @param max Number of qr codes to accepts until it triggers the event
     * @param profileEventListeners The event listener to trigger
     */
    public AsyncQrCodeList(int max, ProfileEventListeners profileEventListeners) {
        numTasksToReach = max;
        this.profileEventListeners = profileEventListeners;
        qrCodes.clear();
    }

    /**
     * A method to add qr codes to a list safely from different threads.
     * @param qrCode The qr code to add to the list.
     */
    public void addToArray(ScoringQRCode qrCode) {
        synchronized (this) {
            asyncTaskCount++;
            qrCodes.add(qrCode);
            if (asyncTaskCount >= numTasksToReach) {
                profileEventListeners.onQrCodeListDoneFillingEvent(qrCodes);
            }
        }
    }
}
