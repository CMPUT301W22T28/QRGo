package com.example.myapplication.fragments.post.postcontent;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PostInfoViewModel extends ViewModel {
    private final MutableLiveData<String> geoLocation;
    private final MutableLiveData<Bitmap> image;
    private final MutableLiveData<String> score;
    private final MutableLiveData<String> scannedByText;
    private final MutableLiveData<String> imageNotAvailableText;

    public PostInfoViewModel() {
        geoLocation = new MutableLiveData<>();
        image = new MutableLiveData<>();
        score = new MutableLiveData<>();
        scannedByText = new MutableLiveData<>();
        imageNotAvailableText = new MutableLiveData<>();
    }

    public void setGeoLocation(String newLocation) {
        geoLocation.setValue(newLocation);
    }

    public void setImage(Bitmap newImage) {
        image.setValue(newImage);
    }

    public void setScore(int newScore) {
        this.score.setValue(String.valueOf(newScore));
    }

    public void setScannedByText(int numScannedBy) {
        this.scannedByText.setValue("Scanned by "+numScannedBy+" users");
    }

    public void setImageNotAvailableText(String message){
        this.imageNotAvailableText.setValue(message);
    }

    public LiveData<String> getGeoLocation() {
        return geoLocation;
    }

    public LiveData<Bitmap> getImage() {
        return image;
    }

    public LiveData<String> getScore() {
        return score;
    }

    public LiveData<String> getScannedByText() {
        return scannedByText;
    }

    public LiveData<String> getImageNotAvailableText() {
        return imageNotAvailableText;
    }
}