package com.example.myapplication.fragments.profile;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public interface ProfileEventListeners {
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes);
}
