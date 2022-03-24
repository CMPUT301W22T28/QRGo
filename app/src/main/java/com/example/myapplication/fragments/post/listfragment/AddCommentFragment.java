package com.example.myapplication.fragments.post.listfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.Comment;
import com.example.myapplication.fragments.post.PostFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
 * This fragment lets the user add a game by specify a name and number of rolls of 6 sided die
 * ISSUE: If the user enters input in the number of rolls text edit that is not 1,2 or 3 the app will not work. I'm not sure how to validate input.
 */

public class AddCommentFragment extends DialogFragment {
    private EditText commentText;

    private FirebaseFirestore db;

    private static final String USER = "USER";
    private static final String QR = "QR";

    public static AddCommentFragment newInstance(String username, String qrHash) {
        Bundle args = new Bundle();
        args.putString(USER, username);
        args.putString(QR, qrHash);

        AddCommentFragment fragment = new AddCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_comment, null);
        db = FirebaseFirestore.getInstance();
        String username = getArguments().getString(USER);
        String qrHash = getArguments().getString(QR);
        commentText = view.findViewById(R.id.comment_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Comment")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comment = commentText.getText().toString();

                        // add comment to database
                        Map<String, Object> data = new HashMap<>();
                        data.put("comment", comment);
                        data.put("username", username);
                        DocumentReference newCommentRef = db.collection("Comments").document();
                        newCommentRef.set(data);

                        // add comment id to QR code comment list
                        String commentId = newCommentRef.getId().toString();
                        db.collection("ScoringQRCodes").document(qrHash)
                                .update("comment_ids", FieldValue.arrayUnion(commentId));
                    }
                }).create();
    }
}
