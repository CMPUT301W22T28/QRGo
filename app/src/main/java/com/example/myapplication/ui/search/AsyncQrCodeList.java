package com.example.myapplication.ui.search;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;


public class AsyncQrCodeList {
    UserEventListeners userEventListeners;

    int asyncTaskCount = 0;
    final int numTasksToReach;
    private final ArrayList<ScoringQRCode> qrCodes = new ArrayList<>();

    public AsyncQrCodeList(int max, UserEventListeners userEventListeners) {
        numTasksToReach = max;
        this.userEventListeners = userEventListeners;
        qrCodes.clear();
    }

    public void addToArray(ScoringQRCode qrCode) {
        synchronized (this) {
            asyncTaskCount++;
            qrCodes.add(qrCode);
            if (asyncTaskCount >= numTasksToReach) {
                userEventListeners.onQrCodeListDoneFillingEvent(qrCodes);
            }
        }
    }


}
