package com.example.myapplication.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public class QRCodeRecyclerAdapter extends RecyclerView.Adapter<QRCodeRecyclerAdapter.ViewHolder> {

    private ArrayList<ScoringQRCode> qrCodes;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    QRCodeRecyclerAdapter(Context context, ArrayList<ScoringQRCode> qrCodes) {
        this.mInflater = LayoutInflater.from(context);
        this.qrCodes = qrCodes;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_qr_code_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScoringQRCode qrCode = qrCodes.get(position);
        holder.score.setText(Integer.toString(qrCode.getScore()));
        holder.geolocation.setText(qrCode.getGeolocation());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return qrCodes.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView score;
        TextView geolocation;

        ViewHolder(View itemView) {
            super(itemView);
            score = itemView.findViewById(R.id.put_qr_score_here);
            geolocation = itemView.findViewById(R.id.put_geolocation_here);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ScoringQRCode getItem(int id) {
        return qrCodes.get(id);
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
