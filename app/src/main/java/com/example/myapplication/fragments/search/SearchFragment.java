package com.example.myapplication.fragments.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.FragmentSearchBinding;
import com.example.myapplication.fragments.profile.ProfileFragment;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

/**
 *
 * This Fragment displays registered users based on search term inputted by user of the app.
 *
 * @author Ervin Binu Joseph
 *
 * March 12, 2022
 */

/*
 * Sources
 *
 * SearchView: https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
 *
 */

public class SearchFragment extends Fragment implements UserRecyclerAdapter.ItemClickListener{

    private FragmentSearchBinding binding;
    MainActivity activity;
    ArrayList<Player> users;
    UserRecyclerAdapter userRecyclerAdapter;
    FirebaseFirestore db;
    private Boolean isAdmin;
    private String myUsername = null;

    /**
     * Initially called when the search fragment is created.
     * @param inflater The inflater for this view.
     * @param container The container for this view, defined in the xml.
     * @param savedInstanceState A saved state if there is one.
     * @return The root View.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserViewModel searchViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }


    /**
     * Called every after the view is initialized
     * @param view The profile fragment view that was initialized.
     * @param savedInstanceState A saved instance.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity();
        assert activity != null;

        users = new ArrayList<>();
        getUsersFromDatabase();
        RecyclerView recyclerView = binding.searchList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        userRecyclerAdapter = new UserRecyclerAdapter(activity, users);
        userRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(userRecyclerAdapter);
        searchBar();
        deleteAllowed();
    }

    /**
     * This method sets up the SearchView and accepts search queries to filter through the list of users
     *
     */

    private void searchBar() {
        SearchView searchView = binding.searchBar;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
    }

    /**
     * This method filters the list of users based on text inputted on the search bar
     *
     * @param text
     * search query used to filter out users on the search list
     *
     */
    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Player> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Player user : users) {
            // checking if the entered string matched with any item of our recycler view.
            if (user.getUsername().toLowerCase().startsWith(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(user);
            }
        }
        userRecyclerAdapter.filterList(filteredlist);
    }


    /**
     *This method gets all the registered users from the database and displays it onto the screen
     *
     */
    private void getUsersFromDatabase() {

        // setting persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("Users").addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.e("Firestore Error", error.getMessage());
                return;
            }

            assert value != null;
            for (DocumentChange dc : value.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    Player player = new Player(dc.getDocument().getId(), false);
                    users.add(player);
                }
            }

            userRecyclerAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Method that checks to see if the delete button should be visible or not. This is based on whether or
     * not the user is registered as an Administrator
     */
    private void deleteAllowed() {
        Log.d("ProfileFragment", requireActivity().getIntent().getStringExtra("Username"));
        try { this.myUsername = getArguments().getString("Username");}
        catch(Exception e) { this.myUsername = requireActivity().getIntent().getStringExtra("Username"); }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference MyUserDocRef = db.collection("Users").document(this.myUsername);

        MyUserDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    isAdmin = value.getBoolean("admin");
                    if (isAdmin == null) {
                        isAdmin = false;
                    }
                }
                else {
                    // throw exception if any issues getting document
                    Toast.makeText(activity.getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is called when a user on the search fragment has been clicked
     * @param view The view that was clicked.
     * @param position The position of the qr Code clicked in the recycler view.
     */
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity.getApplicationContext(), "You clicked on row number " + position, Toast.LENGTH_SHORT).show();
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle username = new Bundle();
        String name = userRecyclerAdapter.getItem(position).getUsername();
        username.putString("Username", name);

        username.putBoolean("isAdmin", isAdmin);
        if (name.equals(myUsername)) { username.putBoolean("isAdmin", false); }

        profileFragment.setArguments(username);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, profileFragment, "profileFragment"+name)
                .addToBackStack(null)
                .commit();
    }

    /**
     * This method is called when the fragment is destroyed
     */
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}