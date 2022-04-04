package com.example.myapplication.fragments.profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
    private DialogContactInfoBinding binding;

    /**
     * Called to instantiate the Dialog
     * @param username username of the user profile being viewed.
     * @param email email address of the user profile being viewed.
     * @param phone phone number of the user profile being viewed.
     */
    public static ContactDialog newInstance(String username, String email, String phone) {
        Bundle args = new Bundle();
        args.putString("ARG_USER", username);
        args.putString("ARG_EMAIL", email);
        args.putString("ARG_PHONE", phone);

        ContactDialog fragment = new ContactDialog();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initially called when the contact dialog is created.
     * @param inflater The inflater for this view.
     * @param container The container for this view, defined in the xml.
     * @param savedInstanceState A saved state if there is one.
     * @return The root View.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DialogContactInfoBinding.inflate(inflater, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return binding.getRoot();
    }

    /**
     * Called every after the view is initialized
     * @param view The profile fragment view that was initialized.
     * @param savedInstanceState A saved instance.
     */
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

    }
}
