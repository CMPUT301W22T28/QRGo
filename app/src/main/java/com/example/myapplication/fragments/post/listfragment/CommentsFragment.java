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

import java.util.ArrayList;

public class CommentsFragment extends Fragment {
    private ArrayList<Comment> comments = new ArrayList<>();
    FragmentCommentsBinding binding;
    CommentsAdapter commentsAdapter;

    private static final String ARG_POST = "argPost";

    public static CommentsFragment newInstance(String postId) {
        Bundle args = new Bundle();
        args.putString(ARG_POST, postId);

        CommentsFragment fragment = new CommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

}