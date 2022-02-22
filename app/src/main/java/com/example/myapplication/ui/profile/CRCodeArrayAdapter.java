package com.example.myapplication.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;

import java.util.ArrayList;

public class CRCodeArrayAdapter extends ArrayAdapter<ScoringQRCode> {
    private Context context;
    private ArrayList<ScoringQRCode> qrCodes;

    public CRCodeArrayAdapter(@NonNull Context context, ArrayList<ScoringQRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.context = context;
        this.qrCodes = qrCodes;

    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if(view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.profile_qr_code_list_item, parent,false);
        }



        return view;
    }
}
