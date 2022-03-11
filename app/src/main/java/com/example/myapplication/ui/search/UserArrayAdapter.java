package com.example.myapplication.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.user.Player;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<Player> {
    private Context context;
    private ArrayList<Player> users;

    public UserArrayAdapter(@NonNull Context context, ArrayList<Player> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;

    }

    public UserArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public void filterList(ArrayList<Player> filterllist) {
        // below line is to add our filtered
        // list in our course array list.
        users = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
        }

        Player player = users.get(position);

        TextView username = view.findViewById(R.id.username);

        username.setText(player.getUsername());

        return view;
    }
}
