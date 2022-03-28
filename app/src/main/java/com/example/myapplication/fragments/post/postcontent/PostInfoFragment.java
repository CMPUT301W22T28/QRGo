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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.FragmentPostInfoBinding;
import com.example.myapplication.fragments.post.PostFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostInfoFragment extends Fragment {

    FragmentPostInfoBinding binding;

    Button deletePostButton;

    private String qrHash;
    private String postOwner; // username of post owner
    private String username; // main user
    private Boolean isAdmin;

    private static final String ARG_QR = "argQR";
    private static final String ARG_POST_USER = "argPostUser";
    private static final String ARG_USER = "argUser";
    private static final String ARG_ADMIN = "argAdmin";

    public static PostInfoFragment newInstance(String qrHash, String postOwner, String username, Boolean isAdmin) {
        Bundle args = new Bundle();
        args.putString(ARG_QR, qrHash);
        args.putString(ARG_POST_USER, postOwner);
        args.putString(ARG_USER, username);
        args.putBoolean(ARG_ADMIN, isAdmin);

        PostInfoFragment fragment = new PostInfoFragment();
        fragment.setArguments(args);
        return fragment;
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

        assert getArguments() != null;
        qrHash = getArguments().getString(ARG_QR);
        postOwner = getArguments().getString(ARG_POST_USER);
        username = getArguments().getString(ARG_USER);
        isAdmin = getArguments().getBoolean(ARG_ADMIN);

        deletePostButton = (Button) binding.deletePostButton;
        // check to see if the delete button can be visible

        if ((username.equals(postOwner)) || isAdmin) {
            deletePostButton.setVisibility(View.VISIBLE);
        }

        setViewListeners();
        // stuff in here
    }

    private void setViewListeners() {
        PostInfoViewModel postInfoViewModel = new ViewModelProvider(requireActivity()).get(PostInfoViewModel.class);

        final TextView locationTextView = binding.lastLocation;
        postInfoViewModel.getGeoLocation().observe(getViewLifecycleOwner(), locationTextView::setText);

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