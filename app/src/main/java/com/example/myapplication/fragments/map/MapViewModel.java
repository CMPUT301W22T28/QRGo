package com.example.myapplication.fragments.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 *
 * ViewModel for map fragment. Unused in this part of the implementation.
 *
 * @author CMPUT 301 team 28, Marc-Andre Haley
 *
 * March 10, 2022
 */
public class MapViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the map fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}