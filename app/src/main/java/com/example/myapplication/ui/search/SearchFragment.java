package com.example.myapplication.ui.search;

import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
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
import com.example.myapplication.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements UserRecyclerAdapter.ItemClickListener{

    private FragmentSearchBinding binding;
    MainActivity activity;
    UserArrayAdapter userArrayAdapter;

    ArrayList<Player> users;

    UserRecyclerAdapter userRecyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearch;
        searchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        return root;
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Player> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Player user : users) {
            // checking if the entered string matched with any item of our recycler view.
            if (user.getUsername().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(user);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            // Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            userArrayAdapter.filterList(filteredlist);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        users = new ArrayList<Player>();

        for (int i=0; i<20; i++) {
            Player player = new Player("Sandypants", true);
            users.add(player);
        }

        RecyclerView recyclerView = binding.searchList;
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        userRecyclerAdapter = new UserRecyclerAdapter(activity, users);
        userRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(userRecyclerAdapter);


        // search bar implementation
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