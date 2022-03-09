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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment implements RankingRecyclerAdapter.ItemClickListener{
    private final String TAG = "LeaderboardFragment";
    private FragmentLeaderboardBinding binding;
    MainActivity activity;
    ArrayAdapter<Player> playerArrayAdapter;
    private LeaderboardViewModel leaderboardViewModel;

    private ArrayList<Player> myRankingList;
    private ArrayList<String> listForUsernames;
    private RecyclerView recyclerView;

    private String myUsername = null;
    private Integer myScore;

    RankingRecyclerAdapter rankingRecyclerAdapter;
    TabLayout tabLayout;

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
        this.listForUsernames = new ArrayList<>();

        tabLayout = binding.sortTabs;
        recyclerView = binding.leaderboard;

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        rankingRecyclerAdapter = new RankingRecyclerAdapter(activity, myRankingList);
        rankingRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(rankingRecyclerAdapter);

        Log.d("ProfileFragment", requireActivity().getIntent().getStringExtra("Username"));
        this.myUsername = requireActivity().getIntent().getStringExtra("Username");

        // set the view listeners
        setViewListeners();

        getRankingsFromDatabase("scanned_highest");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                   @Override
                   public void onTabSelected(TabLayout.Tab tab) {
                       //Log.d(TAG, "Current tab: " + tab.getText());
                       if (tab.getText().equals("Highest")) {
                           Log.d(TAG, "here");
                           getRankingsFromDatabase("scanned_highest");
                       }
                       if (tab.getText().equals("Count")) {
                           Log.d(TAG, "Current tab: " + tab.getText());
                           getRankingsFromDatabase("scanned_count");
                       }
                       if (tab.getText().equals("Sum")) {
                           Log.d(TAG, "Current tab: " + tab.getText());
                           getRankingsFromDatabase("scanned_sum");
                       }
                   }

                   @Override
                   public void onTabUnselected(TabLayout.Tab tab) {

                   }

                   @Override
                   public void onTabReselected(TabLayout.Tab tab) {

                   }
               });

    }

    private void getRankingsFromDatabase(String tabSort) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        /*FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);*/

        CollectionReference usersRef = db.collection("Users");

        LeaderboardFragment leaderboardFragment = this;

        leaderboardViewModel.setPersonalUsername(myUsername);

        usersRef.orderBy(tabSort, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    myRankingList.clear();
                    listForUsernames.clear();
                    for (QueryDocumentSnapshot document : value) {
                        String username = document.getId();
                        listForUsernames.add(username);
                        boolean isAdmin = document.getBoolean("admin");
                        Double rankingScore = document.getDouble(tabSort);
                        Player player = new Player(username, isAdmin);
                        player.setRankingScore(rankingScore.intValue());
                        myRankingList.add(player);

                    }
                    myScore = listForUsernames.indexOf(myUsername) + 1;
                    leaderboardViewModel.setPersonalScore(myScore.toString());
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

        final TextView personalProfileUsername = binding.personalPlayerCardUsername;
        leaderboardViewModel.getPersonalUsername().observe(getViewLifecycleOwner(), personalProfileUsername::setText);

        final TextView personalProfileScore = binding.personalPlayerCardScore;
        leaderboardViewModel.getPersonalScore().observe(getViewLifecycleOwner(), personalProfileScore::setText);

        leaderboardViewModel.getRankingList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Player>>() {
           @SuppressLint("NotifyDataSetChanged")
           @Override
           public void onChanged(ArrayList<Player> rankings) {
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