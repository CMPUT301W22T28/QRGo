package com.example.myapplication.fragments.post.listfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * View model class for ScannedByFragment
 *
 * @author Walter Ostrander
 *
 * @see ScannedByFragment
 *
 * March 22, 2022
 */

public class ScannedByViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<ArrayList<String>> scannedByLiveDataList;

    public ScannedByViewModel() {
        scannedByLiveDataList = new MutableLiveData<>();
    }

    public void setScannedByLiveDataList(ArrayList<String> scannedByList) {
        scannedByLiveDataList.setValue(scannedByList);
    }

    public LiveData<ArrayList<String>> getScannedByLiveData() {
        return scannedByLiveDataList;
    }
}