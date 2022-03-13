package com.example.myapplication.fragments.profile;

import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

/**
 * An interface that defines a new event listener
 * @author Walter
 * @see AsyncQrCodeList
 *
 * May 12, 2022
 */
public interface ProfileEventListeners {

    /**
     * Defines a event listener that will trigger when there are a defined number of qr codes in
     * the list.
     * @param qrCodes The list of qr codes that was filled asynchronously
     */
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes);
}
