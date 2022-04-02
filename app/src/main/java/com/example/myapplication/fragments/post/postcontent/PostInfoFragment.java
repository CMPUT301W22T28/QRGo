package com.example.myapplication.fragments.post.postcontent;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.databinding.FragmentPostInfoBinding;
import com.example.myapplication.fragments.post.listfragment.ScannedByViewModel;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * Fragment that shows information about the post and the image
 *
 * @author Marc-Andre Haley, Walter Ostrander
 *
 * @see PostInfoViewModel
 *
 * March 22, 2022
 *
 */

public class PostInfoFragment extends Fragment {

    FragmentPostInfoBinding binding;
    private final String TAG = "PostInfoFragment";
    private int widthHeight = 0;

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

        final TextView imageNotAvailableTextView = binding.imageNotAvailableText;
        postInfoViewModel.getImageNotAvailableText().observe(getViewLifecycleOwner(), imageNotAvailableTextView::setText);

        final TextView locationTextView = binding.lastLocation;
        postInfoViewModel.getGeoLocation().observe(getViewLifecycleOwner(), locationTextView::setText);

        final TextView scoreTextView = binding.scoreText;
        postInfoViewModel.getScore().observe(getViewLifecycleOwner(), scoreTextView::setText);

        final TextView scannedByTextView = binding.scannedByText;
        postInfoViewModel.getScannedByText().observe(getViewLifecycleOwner(), scannedByTextView::setText);

        // set the image every time it changes
        final ImageView imageView = binding.cameraImageHolder;
        postInfoViewModel.getImage().observe(getViewLifecycleOwner(), bitmap -> {
            Log.d(TAG, String.valueOf(bitmap));
            if (imageView.getWidth() > 0) {
                widthHeight = imageView.getWidth();
            }
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, widthHeight, widthHeight, false));
        });
    }
}