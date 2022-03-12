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

/**
 * The recycler adapter for the profile fragment, adapts scoring qr codes to fit in the profile
 * array list
 * @author Walter
 * @see ProfileFragment
 */
public class QrCodeRecyclerAdapter extends RecyclerView.Adapter<QrCodeRecyclerAdapter.ViewHolder> {
    private ArrayList<ScoringQRCode> qrCodes;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    /**
     * A class that stores and recycles views as they are scrolled off screen.
     * @author Walter
     * @see QrCodeRecyclerAdapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView scoreTextView;
        TextView geolocationTextView;

        /**
         * The constructor for the view holder.
         * @param itemView The view inside each recycler view element.
         */
        ViewHolder(View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.put_qr_score_here);
            geolocationTextView = itemView.findViewById(R.id.put_geolocation_here);
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
     * The constructor for the Recycler adapter
     * @param context the context of the qr code adapter
     * @param qrCodes the list of qr codes to inflate inside the recycler view
     */
    QrCodeRecyclerAdapter(Context context, ArrayList<ScoringQRCode> qrCodes) {
        this.mInflater = LayoutInflater.from(context);
        this.qrCodes = qrCodes;
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
        View view = mInflater.inflate(R.layout.profile_qr_code_list_item, parent, false);
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
        ScoringQRCode qrCode = qrCodes.get(position);
        holder.scoreTextView.setText(Integer.toString(qrCode.getScore()));
        holder.geolocationTextView.setText(qrCode.getGeolocation());
    }

    /**
     * Total number of columns inside the recycler view.
     * @return The size of the qr code list.
     */
    @Override
    public int getItemCount() {
        return qrCodes.size();
    }

    /**
     * A method to add qr codes to the view model.
     * @param qrCode
     */
    public void addQrCode(ScoringQRCode qrCode) {
        qrCodes.add(qrCode);
        notifyItemInserted(0);
    }

    /**
     * Convenience method for getting data at click position.
     * @param id The position of the qr code to get.
     * @return The qr code at position id.
     */
    ScoringQRCode getItem(int id) {
        return qrCodes.get(id);
    }

    /**
     * Allows clicks events to be caught.
     * @param itemClickListener the itemClickListener to set.
     */
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * Parent activity will implement this method to respond to click events.
     * @author Walter
     * @see QrCodeRecyclerAdapter
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
