package com.example.myapplication.ui.profile;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;



public class AsyncQrCodeList {
    int asyncTaskCount = 0;
    final int numTasksToReach;
    ProfileViewModel profileViewModel;
    private final ArrayList<ScoringQRCode> qrCodes = new ArrayList<>();

    public AsyncQrCodeList(int max, ProfileViewModel profileViewModel) {
        numTasksToReach = max;
        this.profileViewModel = profileViewModel;
    }

    public void addToArray(ScoringQRCode qrCode) {
        synchronized (this) {
            asyncTaskCount++;
            qrCodes.add(qrCode);
            if (asyncTaskCount >= numTasksToReach) {
                profileViewModel.setMutableProfileQrCodes(qrCodes);
            }
        }
    }


}
