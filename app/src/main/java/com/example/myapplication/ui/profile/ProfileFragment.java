package com.example.myapplication.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.databinding.FragmentProfileBinding;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements QRCodeRecyclerAdapter.ItemClickListener{

    private FragmentProfileBinding binding;
    MainActivity activity;
    ArrayAdapter<ScoringQRCode> qrCodeArrayAdapter;
    private ProfileViewModel profileViewModel;


    private String myUsername = null;

    QRCodeRecyclerAdapter scoringQRCodeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        setViews();

        this.myUsername = activity.getMyUsername();

        // testing the custom array adapter
        ArrayList<ScoringQRCode> qrCodes = new ArrayList<>();
        for (int i = 0; i<20; i++) {
            ScoringQRCode code = new ScoringQRCode("hash");
            qrCodes.add(code);
        }


        RecyclerView recyclerView = binding.scoringQrCodeList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        scoringQRCodeAdapter = new QRCodeRecyclerAdapter(activity, qrCodes);
        scoringQRCodeAdapter.setClickListener(this);
        recyclerView.setAdapter(scoringQRCodeAdapter);

    }

    private void setViews() {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        final TextView usernameTextView = binding.profileUsername;
        profileViewModel.getUsername().observe(getViewLifecycleOwner(), usernameTextView::setText);

        final TextView totalScoreTextView = binding.profileTotalScore;
        profileViewModel.getTopQRCodeScore().observe(getViewLifecycleOwner(), totalScoreTextView::setText);

        final TextView QRCodeCountTextView = binding.profileQrCodeCount;
        profileViewModel.getQrCodeCount().observe(getViewLifecycleOwner(), QRCodeCountTextView::setText);

        final TextView topQRCodeTextView = binding.profileTopQrCode;
        profileViewModel.getTopQRCodeScore().observe(getViewLifecycleOwner(), topQRCodeTextView::setText);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity.getApplicationContext(), "You clicked on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}