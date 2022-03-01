package com.example.myapplication.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private MapView mMapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //final Context context = this.getActivity();

        //MapViewModel mapViewModel =
                //new ViewModelProvider(this).get(MapViewModel.class);

        //binding = FragmentMapBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();

        mMapView = new MapView(inflater.getContext());
        mMapView.setDestroyMode(false);

        IMapController mapController = mMapView.getController();
        mapController.setZoom(12);
        GeoPoint startPoint = new GeoPoint(53.5461, -113.4938);
        mapController.setCenter(startPoint);

        //mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        //final TextView textView = binding.textMap;
        //mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return mMapView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}