package com.example.myapplication.fragments.post.postcontent;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PostInfoViewModel extends ViewModel {
    private final MutableLiveData<String> title;
    private final MutableLiveData<Bitmap> image;
    private final MutableLiveData<String> score;
    private final MutableLiveData<String> scannedByText;

    public PostInfoViewModel() {
        title = new MutableLiveData<>();
        image = new MutableLiveData<>();
        score = new MutableLiveData<>();
        scannedByText = new MutableLiveData<>();
    }

    public void setTitle(String newTitle) {
        title.setValue(newTitle);
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

    public LiveData<String> getTitle() {
        return title;
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
}