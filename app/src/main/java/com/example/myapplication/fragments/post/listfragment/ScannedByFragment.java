package com.example.myapplication.fragments.post.listfragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.databinding.FragmentScannedByBinding;

import java.util.ArrayList;

public class ScannedByFragment extends Fragment {
    FragmentScannedByBinding binding;
    ArrayAdapter<String> scannedByAdapter;

    ArrayList<String> scannedByList = new ArrayList<>();

    public static ScannedByFragment newInstance() {

        return new ScannedByFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScannedByBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup the list view for peoples names
        setupListView();

        //
        setViewListeners();
    }

    private void setupListView() {
        scannedByAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, scannedByList);
        ListView listView = binding.userList;
        listView.setAdapter(scannedByAdapter);
    }

    private void setViewListeners() {
        ScannedByViewModel scannedByViewModel = new ViewModelProvider(requireActivity()).get(ScannedByViewModel.class);

        scannedByViewModel.getScannedByLiveData().observe(getViewLifecycleOwner(), newUsernames -> {
            scannedByList.clear();
            scannedByList.addAll(newUsernames);
            scannedByAdapter.notifyDataSetChanged();
        });
    }
}