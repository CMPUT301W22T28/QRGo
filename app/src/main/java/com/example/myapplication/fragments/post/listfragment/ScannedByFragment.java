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

public class ScannedByFragment extends Fragment {

    private ScannedByViewModel mViewModel;

    private static final String ARG_QR = "argQR";

    public static ScannedByFragment newInstance(String qrHash) {
        Bundle args = new Bundle();
        args.putString(ARG_QR, qrHash);

        ScannedByFragment fragment = new ScannedByFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanned_by, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // stuff in here
    }

}