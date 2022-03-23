package com.example.myapplication.fragments.leaderboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.FragmentLeaderboardBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 *
 * This Fragment displays the rankings of all the players and can be sorted by highest, count, and sum.
 *
 * @author CMPUT 301 team 28, Sankalp Saini
 *
 * March 11, 2022
 */

/*
 * Sources
 *
 * TabLayout: https://developer.android.com/reference/com/google/android/material/tabs/TabLayout.TabView
 *
 */

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

        //initialize binding for FragmentLeaderboard
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        //initialize lists to use
        this.myRankingList = new ArrayList<>();
        this.listForUsernames = new ArrayList<>();

        tabLayout = binding.sortTabs;
        recyclerView = binding.leaderboard;

        //initialize recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        rankingRecyclerAdapter = new RankingRecyclerAdapter(activity, myRankingList);
        rankingRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(rankingRecyclerAdapter);

        //gets the username of the current profile and assigns it
        Log.d("ProfileFragment", requireActivity().getIntent().getStringExtra("Username"));
        this.myUsername = requireActivity().getIntent().getStringExtra("Username");

        // set the view listeners
        setViewListeners();

        //pulls data from Database (starting with scanned_highest)
        getRankingsFromDatabase("scanned_highest", "Highest");

        //listens to which tabs were pressed and calls getRankingsFromDatabase() to sort accordingly
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                   @Override
                   public void onTabSelected(TabLayout.Tab tab) {
                       if (tab.getText().equals("Highest")) {
                           getRankingsFromDatabase("scanned_highest", "Highest");
                       }
                       if (tab.getText().equals("Count")) {
                           Log.d(TAG, "Current tab: " + tab.getText());
                           getRankingsFromDatabase("scanned_count", "Count");
                       }
                       if (tab.getText().equals("Sum")) {
                           Log.d(TAG, "Current tab: " + tab.getText());
                           getRankingsFromDatabase("scanned_sum", "Sum");
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

    /**
     * Gets rankings from the database, based on specific sort, and displays it onto the screen
     *
     * @param tabSort indicates what type of sort is needed based on the tab that is clicked
     *
     */
    private void getRankingsFromDatabase(String tabSort, String tabLabel) {

        // database initalized
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // creates collection reference to "Users"
        CollectionReference usersRef = db.collection("Users");

        LeaderboardFragment leaderboardFragment = this;

        leaderboardViewModel.setPersonalUsername(myUsername);

        // gathers data from database and order's (in DESCENDING order) by specific tab choice
        usersRef.orderBy(tabSort, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    // clears the list of players (ranks) and the list of usernames
                    myRankingList.clear();
                    listForUsernames.clear();
                    // for every value in the document (inside of "Users")
                    for (QueryDocumentSnapshot document : value) {
                        // get Id and add list to listForUsernames
                        String username = document.getId();
                        listForUsernames.add(username);
                        // determine if the user is an Admin
                        Boolean isAdmin = document.getBoolean("admin");
                        Double rankingScore = document.getDouble(tabSort);
                        // create new player and add it to myRankingList
                        if (isAdmin == null) {
                            isAdmin = false;
                        }
                        Player player = new Player(username, isAdmin);
                        if (rankingScore != null) {
                            player.setRankingScore(rankingScore.intValue(), tabLabel);
                        }
                        else {
                            player.setRankingScore(0, tabLabel);
                        }
                        myRankingList.add(player);
                    }
                    // myScore is updated using listForUsernames (this is for the top player card)
                    myScore = listForUsernames.indexOf(myUsername) + 1;
                    // update the view model
                    leaderboardViewModel.setPersonalScore(myScore.toString());
                    leaderboardViewModel.setPlayerRankingList(myRankingList);
                }
                else {
                    // throw exception if any issues getting documents
                    Toast.makeText(activity.getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", error);
                }
            }
        });

    }

    /**
     * Sets and initializes all of the view listeners that are to be used
     */
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

    /**
     * Contains items that react after being clicked
     *
     * @param view indicates the view that is being used
     *
     * @param position indicates what position you user clicks on
     */
    @Override
    public void onItemClick(View view, int position) {
        // displays what row was clicked on
        Toast.makeText(activity.getApplicationContext(), "You clicked on row number " + position, Toast.LENGTH_SHORT).show();
    }

    /**
     * Establishes what occurs on destroying the fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}