package com.example.myapplication.ui.leaderboard;

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

public class RankingArrayAdapter extends ArrayAdapter<Player> {
    private Context context;
    private ArrayList<Player> rankings;

    public RankingArrayAdapter(@NonNull Context context, ArrayList<Player> rankings) {
        super(context, 0, rankings);
        this.context = context;
        this.rankings = rankings;

    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.leaderboard_list_item, parent, false);
        }

        Player player = rankings.get(position);

        TextView score = view.findViewById(R.id.put_leaderboard_score_here);
        TextView username = view.findViewById(R.id.put_leaderboard_username_here);

        score.setText(Integer.toString(player.getHighestScore()));
        username.setText(player.getUsername());

        return view;
    }
}
