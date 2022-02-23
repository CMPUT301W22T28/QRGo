package com.example.myapplication.ui.profile;

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
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public class QRCodeArrayAdapter extends ArrayAdapter<ScoringQRCode> {
    private Context context;
    private ArrayList<ScoringQRCode> qrCodes;

    public QRCodeArrayAdapter(@NonNull Context context, ArrayList<ScoringQRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.context = context;
        this.qrCodes = qrCodes;

    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.profile_qr_code_list_item, parent,false);
        }

        ScoringQRCode qrCode = qrCodes.get(position);

        TextView geolocation = view.findViewById(R.id.put_geolocation_here);
        TextView score = view.findViewById(R.id.put_qr_score_here);

        geolocation.setText(qrCode.getGeolocation());
        score.setText(Integer.toString(qrCode.getScore()));

        return view;
    }
}
