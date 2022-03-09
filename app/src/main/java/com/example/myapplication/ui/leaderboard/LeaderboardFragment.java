package com.example.myapplication.ui.leaderboard;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.FragmentLeaderboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment implements RankingRecyclerAdapter.ItemClickListener{
    private final String TAG = "LeaderboardFragment";
    private FragmentLeaderboardBinding binding;
    MainActivity activity;
    ArrayAdapter<Player> playerArrayAdapter;
    private LeaderboardViewModel leaderboardViewModel;

    private ArrayList<Player> myRankingList;
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

        this.myRankingList = new ArrayList<>();

        recyclerView = binding.leaderboard;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        rankingRecyclerAdapter = new RankingRecyclerAdapter(activity, myRankingList);
        rankingRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(rankingRecyclerAdapter);

        // set the view listeners
        setViewListeners();

        // send the data to the view listeners
        getRankingsFromDatabase();

    }

    private void getRankingsFromDatabase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        /*FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);*/

        CollectionReference usersRef = db.collection("Users");

        LeaderboardFragment leaderboardFragment = this;

        usersRef.orderBy("scanned_highest", Query.Direction.DESCENDING)
                //.get()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    /*@Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {*/
                    @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    myRankingList.clear();
                    for (QueryDocumentSnapshot document : value) {
                        String username = document.getId();
                        //String highestScanned = document.getString("scanned_highest");
                        //boolean isAdmin = document.getBoolean("admin");
                        Player player = new Player(username, false);
                        //player.setHighestScore(2);
                        myRankingList.add(player);

                    }
                    //Log.d(TAG, "Current Rankings: " + myRankingList);
                    leaderboardViewModel.setPlayerRankingList(myRankingList);
                }
                else {
                    Toast.makeText(activity.getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", error);
                }
            }
        });

    }

    private void setViewListeners() {
        leaderboardViewModel  = new ViewModelProvider(requireActivity()).get(LeaderboardViewModel.class);

        leaderboardViewModel.getRankingList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Player>>() {
           @SuppressLint("NotifyDataSetChanged")
           @Override
           public void onChanged(ArrayList<Player> rankings) {
               Log.d(TAG, "Current Rankings: " + rankings);
               myRankingList.clear();
               myRankingList.addAll(rankings);
               Log.d(TAG, "New Rankings: " + myRankingList);
               rankingRecyclerAdapter.notifyDataSetChanged();
           }
        });
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