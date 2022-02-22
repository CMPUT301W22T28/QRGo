package com.example.myapplication.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.databinding.FragmentProfileBinding;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    ArrayAdapter<ScoringQRCode> qrCodeArrayAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfile;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        ArrayList<ScoringQRCode> qrCodes = new ArrayList<>();
        for (int i = 0; i<20; i++) {
            qrCodes.add(new ScoringQRCode());
        }



        qrCodeArrayAdapter = new QRCodeArrayAdapter(requireContext(), qrCodes);
        binding.scoringQrCodeList.setAdapter(qrCodeArrayAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}