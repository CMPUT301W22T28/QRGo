package com.example.myapplication.dataClasses;

import com.example.myapplication.fragments.post.listfragment.CommentsFragment;

/**
 *
 * Class that represents a comment object.
 *
 * @author CMPUT 301 team 28, Marc-Andre Haley
 *
 * @see CommentsFragment
 *
 * March 22, 2022
 */

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
