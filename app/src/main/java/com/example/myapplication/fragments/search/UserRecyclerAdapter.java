package com.example.myapplication.fragments.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.fragments.profile.ProfileFragment;
import com.example.myapplication.fragments.profile.QRCodeRecyclerAdapter;

import java.util.ArrayList;

/**
 * The recycler adapter for the search fragment, adapts users to fit in the search list
 *
 * @author Ervin Binu Joseph
 * @see SearchFragment
 *
 * May 12, 2022
 */

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    private ArrayList<Player> users;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    /**
     * User recycler adapter constructor
     *
     */
    public UserRecyclerAdapter(Context context, ArrayList<Player> users) {
        this.mInflater = LayoutInflater.from(context);
        this.users = users;
    }

    /**
     * method that Inflates the row layout from xml when needed.
     * @param parent The parent of the recycler view.
     * @param viewType The type of view.
     * @return a new ViewHolder of the view.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.search_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * A method that Binds the data to the TextView in each row.
     * @param holder the view holder
     * @param position the position to put the ViewHolder in.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Player player = users.get(position);
        holder.username.setText(player.getUsername());
    }

    /**
     * Total number of columns inside the recycler view.
     * @return The size of the user list.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * A class that stores and recycles views as they are scrolled off screen.
     *
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView profilePic;
        TextView username;

        /**
         * The constructor for the view holder.
         * @param itemView The view inside each recycler view element.
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilepic);
            username = itemView.findViewById(R.id.username);
            itemView.setOnClickListener(this);
        }

        /**
         * What gets set off when you click an element
         * @param view The view that gets passed when an item in clicked.
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /**
     * Convenience method to set the users list of the recycler view
     * @param filterlist
     * The list to be set up with the recycler view
     */
    public void filterList(ArrayList<Player> filterlist) {
        users = filterlist;
        notifyDataSetChanged();
    }

    /**
     * Convenience method for getting data at click position.
     * @param id The position of the qr code to get.
     * @return The qr code at position id.
     */
    Player getItem(int id) {
        return users.get(id);
    }

    /**
     * Allows clicks events to be caught.
     * @param itemClickListener the itemClickListener to set.
     */
    void setClickListener(UserRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * Parent activity will implement this method to respond to click events.
     *
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
