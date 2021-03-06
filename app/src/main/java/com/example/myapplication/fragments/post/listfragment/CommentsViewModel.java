package com.example.myapplication.fragments.post.listfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.dataClasses.Comment;

import java.util.ArrayList;

/**
 * View model class for CommentsFragment
 *
 * @author Walter Ostrander
 *
 * @see CommentsFragment
 *
 * March 22, 2022
 */
public class CommentsViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final MutableLiveData<ArrayList<Comment>> commentsLiveData;

    public CommentsViewModel() {
        this.commentsLiveData = new MutableLiveData<>();
    }

    public void setComments(ArrayList<Comment> newComments) {
        commentsLiveData.setValue(newComments);
    }

    public LiveData<ArrayList<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }
}