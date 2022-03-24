package com.example.myapplication.dataClasses.asyncdata;

import java.util.ArrayList;

/**
 * An interface that defines a new event listener
 * @author Walter
 * @see AsyncList
 *
 * March 12, 2022
 */
public interface QRGoEventListener<T> {

    /**
     * Defines a event listener that will trigger when there are a defined number of qr codes in
     * the list.
     * @param qrCodes The list of qr codes that was filled asynchronously
     */
    public void onQrCodeListDoneFillingEvent(ArrayList<T> qrCodes);
}
