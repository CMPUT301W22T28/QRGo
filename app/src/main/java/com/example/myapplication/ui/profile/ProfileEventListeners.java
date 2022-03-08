package com.example.myapplication.ui.profile;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public interface ProfileEventListeners {
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes);
}
