package com.example.myapplication.ui.leaderboard;

import android.os.Bundle;
import android.service.notification.NotificationListenerService;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.FragmentLeaderboardBinding;
import com.example.myapplication.ui.profile.QRCodeRecyclerAdapter;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment implements RankingRecyclerAdapter.ItemClickListener{

    private FragmentLeaderboardBinding binding;
    MainActivity activity;
    ArrayAdapter<Player> playerArrayAdapter;

    RankingRecyclerAdapter rankingRecyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LeaderboardViewModel notificationsViewModel =
                new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLeaderboard;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        ArrayList<Player> rankings = new ArrayList<>();
        for (int i=0; i<5; i++) {
            Player player = new Player();
            player.setHighest_score(i+1);
            rankings.add(player);
        }

        RecyclerView recyclerView = binding.leaderboard;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        rankingRecyclerAdapter = new RankingRecyclerAdapter(activity, rankings);
        rankingRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(rankingRecyclerAdapter);

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