package com.example.myapplication.dataClasses;

import com.google.firebase.Timestamp;

public class Comment {

    private String comment;
    private String user;

    public Comment(String comment, String user) {
        this.comment = comment;
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }
}
