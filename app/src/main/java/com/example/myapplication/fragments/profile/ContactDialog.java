package com.example.myapplication.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.databinding.DialogContactInfoBinding;

import javax.annotation.Nullable;

public class ContactDialog extends DialogFragment {
    private static final String TAG = "ContactDialog";
    private ProfileViewModel profileViewModel;
    private DialogContactInfoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DialogContactInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViewListeners();
    }

    private void setViewListeners() {

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        final TextView usernameTextView = binding.profileUsername;
        profileViewModel.getUsername().observe(getViewLifecycleOwner(), usernameTextView::setText);

        final TextView emailTextView = binding.profileEmail;
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), emailTextView::setText);

        final TextView phoneTextView = binding.profilePhone;
        profileViewModel.getPhone().observe(getViewLifecycleOwner(), phoneTextView::setText);
    }
}
