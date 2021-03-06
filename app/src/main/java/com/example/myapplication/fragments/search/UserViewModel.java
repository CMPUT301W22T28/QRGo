package com.example.myapplication.fragments.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> username;

    public UserViewModel() {
        mText = new MutableLiveData<>();
        username = new MutableLiveData<>();
        mText.setValue("This is the search fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (!String.valueOf(this.username.getValue()).equals(username)) {
            this.username.setValue(username);
        }
    }
}