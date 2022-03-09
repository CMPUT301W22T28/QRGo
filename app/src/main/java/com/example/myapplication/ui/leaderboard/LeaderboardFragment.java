package com.example.myapplication.ui.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.FragmentLeaderboardBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment implements RankingRecyclerAdapter.ItemClickListener{
    private final String TAG = "LeaderboardFragment";
    private FragmentLeaderboardBinding binding;
    MainActivity activity;
    ArrayAdapter<Player> playerArrayAdapter;
    private LeaderboardViewModel leaderboardViewModel;

    private ArrayList<Player> rankingList;
    private RecyclerView recyclerView;

    RankingRecyclerAdapter rankingRecyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LeaderboardViewModel leaderboardViewModel =
                new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        // getting the recycler view ready
        //setupRecyclerView();

        // set the view listeners
        setViewListeners();

        // send the data to the view listeners
        getRankingsFromDatabase();

        /*ArrayList<Player> rankings = new ArrayList<>();
        for (int i=0; i<5; i++) {
            Player player = new Player("Uri the trainer who trains", false);
            player.setHighestScore(i+1);
            rankings.add(player);
        }

        RecyclerView recyclerView = binding.leaderboard;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        rankingRecyclerAdapter = new RankingRecyclerAdapter(activity, rankings);
        rankingRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(rankingRecyclerAdapter);*/

    }

    private void getRankingsFromDatabase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final CollectionReference usersRef = db.collection("Users");

        LeaderboardFragment leaderboardFragment = this;

        //usersRef.orderBy("scanned_highest", Query.Direction.DESCENDING);

        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot value,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                leaderboardViewModel

            }
        });

    }

    /*private void setupRecyclerView() {
        this.rankingList = new ArrayList<>();

        //recyclerView = binding.scoringQrCodeList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new
    }*/

    private void setViewListeners() {
        leaderboardViewModel  = new ViewModelProvider(requireActivity()).get(LeaderboardViewModel.class);

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