package com.example.myapplication.fragments.post.listfragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.Comment;
import com.example.myapplication.databinding.FragmentCommentsBinding;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.databinding.FragmentProfileBinding;
import com.example.myapplication.fragments.post.postcontent.PostInfoFragment;
import com.example.myapplication.fragments.post.postcontent.PostInfoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsFragment extends Fragment implements AddCommentFragment.OnFragmentInteractionListener{
    private ArrayList<Comment> comments = new ArrayList<>();
    FragmentCommentsBinding binding;
    CommentsAdapter commentsAdapter;

    private String username;
    private String qrHash;

    private FirebaseFirestore db;

    private static final String USER = "USER";
    private static final String QR = "QR";

    public static CommentsFragment newInstance(String username, String qrHash) {
        Bundle args = new Bundle();
        args.putString(USER, username);
        args.putString(QR, qrHash);

        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // listen to fab to show fragment. Code from labs
        final FloatingActionButton addCityButton = binding.floatingActionButton;
        addCityButton.setOnClickListener((view) -> {
            new AddCommentFragment().show(getActivity().getSupportFragmentManager(),"ADD_CITY");
        });

        binding = FragmentCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        username = getArguments().getString(USER);
        qrHash = getArguments().getString(QR);

        setupListView();

        // stuff in here
        setViewListeners();
    }

    private void setupListView() {
        ListView listView = binding.commentList;

        commentsAdapter = new CommentsAdapter(getContext(), this.comments);
        listView.setAdapter(commentsAdapter);
    }

    private void setViewListeners() {
        CommentsViewModel commentsViewModel = new ViewModelProvider(requireActivity()).get(CommentsViewModel.class);

        commentsViewModel.getCommentsLiveData().observe(getViewLifecycleOwner(), newComments -> {
            comments.clear();
            comments.addAll(newComments);
            commentsAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onOkPressed(String comment) {
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
}