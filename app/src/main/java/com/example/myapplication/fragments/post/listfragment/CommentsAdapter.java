package com.example.myapplication.fragments.post.listfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.Comment;

import java.util.ArrayList;
import java.util.Locale;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    private ArrayList<Comment> comments;
    private Context context;

    public CommentsAdapter(Context context, ArrayList<Comment> comments) {
        super(context,0,comments);
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.comment_content, null);
        }

        TextView username_text = view.findViewById(R.id.username_text);
        TextView comment_text = view.findViewById(R.id.comment_text);

        // set text for attributes being displayed
        username_text.setText(comments.get(position).getUser());
        comment_text.setText(comments.get(position).getComment());

        return view;
    }

}
