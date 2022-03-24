package com.example.myapplication.fragments.post.postcontent;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import com.example.myapplication.dataClasses.Comment;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.databinding.FragmentPostInfoBinding;

import java.util.ArrayList;

public class PostInfoFragment extends Fragment {

    FragmentPostInfoBinding binding;

    public static PostInfoFragment newInstance() {

        return new PostInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentPostInfoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setViewListeners();
        // stuff in here
    }


    private void setViewListeners() {
        PostInfoViewModel postInfoViewModel = new ViewModelProvider(requireActivity()).get(PostInfoViewModel.class);

        final TextView titleTextView = binding.titleText;
        postInfoViewModel.getTitle().observe(getViewLifecycleOwner(), titleTextView::setText);

        final TextView scoreTextView = binding.scoreText;
        postInfoViewModel.getScore().observe(getViewLifecycleOwner(), scoreTextView::setText);

        final TextView scannedByTextView = binding.scannedByText;
        postInfoViewModel.getScannedByText().observe(getViewLifecycleOwner(), scannedByTextView::setText);

        // set the image every time it changes
        final ImageView imageView = binding.cameraImageHolder;
        postInfoViewModel.getImage().observe(getViewLifecycleOwner(), bitmap -> {
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(), false));
        });
    }
}