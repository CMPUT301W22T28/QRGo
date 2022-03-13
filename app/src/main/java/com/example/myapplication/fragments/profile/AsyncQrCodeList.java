package com.example.myapplication.fragments.profile;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;


public class AsyncQrCodeList {
    ProfileEventListeners profileEventListeners;

    int asyncTaskCount = 0;
    final int numTasksToReach;
    private final ArrayList<ScoringQRCode> qrCodes = new ArrayList<>();

    public AsyncQrCodeList(int max, ProfileEventListeners profileEventListeners) {
        numTasksToReach = max;
        this.profileEventListeners = profileEventListeners;
        qrCodes.clear();
    }

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
