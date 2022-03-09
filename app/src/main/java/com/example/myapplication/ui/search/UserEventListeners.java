package com.example.myapplication.ui.search;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public interface UserEventListeners {
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes);
}
