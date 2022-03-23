package com.example.myapplication.fragments.post.listfragment;

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

public class CommentsAndScannedByFragment extends Fragment {

    private CommentsAndScannedByViewModel mViewModel;

    private FragmentCommentsAndScannedByBinding binding;

    public static CommentsAndScannedByFragment newInstance() {
        return new CommentsAndScannedByFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(CommentsAndScannedByViewModel.class);
        binding = FragmentCommentsAndScannedByBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // stuff in here
    }
}