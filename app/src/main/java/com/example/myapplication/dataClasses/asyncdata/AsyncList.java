package com.example.myapplication.dataClasses.asyncdata;

import java.util.ArrayList;

/**
 * This class lets a person add elements to a list asynchronously. So that
 * asynchronous database fetches can happen, then notify a listener that all the
 * elements have been added to a list
 * @author Walter
 * @see QRGoEventListener
 *
 * March 12, 2022
 */
public class AsyncList<T> {
    private final QRGoEventListener<T> QRGoEventListener;
    private int index = 0;
    private final int maxNumElements;
    private final ArrayList<T> arrayToFill = new ArrayList<>();

    /**
     * Constructor of AsyncList
     * @param max Number of elements to accepts until it triggers the event
     * @param QRGoEventListener The event listener to trigger
     */
    public AsyncList(int max, QRGoEventListener<T> QRGoEventListener) {
        maxNumElements = max;
        this.QRGoEventListener = QRGoEventListener;
        arrayToFill.clear();
    }

    /**
     * A method to add elements to a list safely from different threads.
     * @param element The element to add to the list.
     */
    public void addToArray(T element) {
        synchronized (arrayToFill) {
            index++;
            arrayToFill.add(element);
            if (index >= maxNumElements) {
                QRGoEventListener.onListDoneFillingEvent(arrayToFill);
            }
        }
    }
}
