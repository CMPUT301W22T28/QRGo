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
import com.example.myapplication.fragments.post.PostFragment;

import javax.annotation.Nullable;

public class ContactDialog extends DialogFragment {
    private static final String TAG = "ContactDialog";
    private ProfileViewModel profileViewModel;
    private DialogContactInfoBinding binding;

    public static ContactDialog newInstance(String username, String email, String phone) {
        Bundle args = new Bundle();
        args.putString("ARG_USER", username);
        args.putString("ARG_EMAIL", email);
        args.putString("ARG_PHONE", phone);

        ContactDialog fragment = new ContactDialog();
        fragment.setArguments(args);
        return fragment;
    }

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

        String username = getArguments().getString("ARG_USER");
        String email = getArguments().getString("ARG_EMAIL");
        String phone = getArguments().getString("ARG_PHONE");

        final TextView usernameTextView = binding.profileUsername;
        if (username != null) {
            usernameTextView.setText(username);
        }

        final TextView emailTextView = binding.profileEmail;
        if (email != null) {
            emailTextView.setText(email);
        }

        final TextView phoneTextView = binding.profilePhone;
        if (phone != null) {
            phoneTextView.setText(phone);
        }

        //setViewListeners();
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
