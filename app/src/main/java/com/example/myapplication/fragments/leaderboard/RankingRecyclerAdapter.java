package com.example.myapplication.fragments.leaderboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.user.Player;

import java.util.ArrayList;

/**
 *
 * This is a the recycler adapter class for the ranking list
 *
 * @author CMPUT 301 team 28, Sankalp Saini
 *
 * March 11, 2022
 */

/*
 * Sources
 *
 * RecyclerView: https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView
 *
 */

public class RankingRecyclerAdapter extends RecyclerView.Adapter<RankingRecyclerAdapter.ViewHolder> {

    private ArrayList<Player> rankings;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView scoreLabel;
        TextView score;
        TextView username;

        // stores and recycles views as they are scrolled off screen
        ViewHolder(View itemView) {
            super(itemView);
            scoreLabel = itemView.findViewById(R.id.leaderboard_score_text);
            score = itemView.findViewById(R.id.put_leaderboard_score_here);
            username = itemView.findViewById(R.id.put_leaderboard_username_here);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // data passed into the contructor
    RankingRecyclerAdapter(Context context, ArrayList<Player> rankings) {
        this.mInflater = LayoutInflater.from(context);
        this.rankings = rankings;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.leaderboard_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Player player = rankings.get(position);
        holder.score.setText((Integer.toString(player.getRankingScore())));
        holder.scoreLabel.setText(player.getRankingLabel());
        holder.username.setText(player.getUsername());
    }

    /**
     * Returns the size of the ranking list
     *
     * @return int the size of the ranking list
     *
     */
    @Override
    public int getItemCount() {
        return rankings.size();
    }

    /**
     * Adds a player to the ranking list
     *
     * @param player that needs to be added to the list
     *
     */
    public void addRanking(Player player) {
        rankings.add(player);
        notifyItemInserted(0);
    }

    /**
     * Returns the id of the player
     *
     * @return player the player id
     *
     */
    Player getItem(int id) {
        return rankings.get(id);
    }

    /**
     * Allows the click events to be caught
     *
     * @param itemClickListener event click
     *
     */
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * Implements method to respond to onClick events
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
