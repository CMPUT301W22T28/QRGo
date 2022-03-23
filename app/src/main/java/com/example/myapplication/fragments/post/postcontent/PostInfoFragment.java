package com.example.myapplication.fragments.post.postcontent;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;

import com.example.myapplication.databinding.FragmentCommentsAndScannedByBinding;
import com.example.myapplication.databinding.FragmentPostInfoBinding;

public class PostInfoFragment extends Fragment {

    private PostInfoViewModel mViewModel;

    FragmentPostInfoBinding binding;

    public static PostInfoFragment newInstance() {
        return new PostInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(PostInfoViewModel.class);

        binding = FragmentPostInfoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // stuff in here
    }
}