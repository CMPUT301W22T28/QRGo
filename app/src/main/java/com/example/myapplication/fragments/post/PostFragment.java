package com.example.myapplication.fragments.post;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentProfileBinding;
import com.example.myapplication.databinding.PostFragmentBinding;
import com.example.myapplication.fragments.profile.ProfileViewModel;

public class PostFragment extends Fragment {

    private PostViewModel postViewModel;

    private PostFragmentBinding binding;

    public static PostFragment newInstance() {
        return new PostFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        binding = PostFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // stuff for view model for this fragment

    }
}