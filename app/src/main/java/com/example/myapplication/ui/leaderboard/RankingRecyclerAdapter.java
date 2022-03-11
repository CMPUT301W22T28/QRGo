package com.example.myapplication.ui.leaderboard;

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

public class RankingRecyclerAdapter extends RecyclerView.Adapter<RankingRecyclerAdapter.ViewHolder> {

    private ArrayList<Player> rankings;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView score;
        TextView username;

        // stores and recycles views as they are scrolled off screen
        ViewHolder(View itemView) {
            super(itemView);
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

    // data is passed into the constructor
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
        holder.score.setText(Integer.toString(player.getHighestScore()));
        holder.username.setText(player.getUsername());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return rankings.size();
    }

    // add item to the ranking list
    public void addRanking(Player player) {
        rankings.add(player);
        notifyItemInserted(0);
    }

    // convenience method for getting data at click position
    Player getItem(int id) {
        return rankings.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
