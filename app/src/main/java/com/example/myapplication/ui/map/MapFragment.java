package com.example.myapplication.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentMapBinding;

import com.example.myapplication.R;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {

    //private FragmentMapBinding binding;
    private MapView mMapView;
    FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private GeoPoint currentLocation;
    //private Boolean locationPermission;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        db = FirebaseFirestore.getInstance();

        // initialize currentlocation so it is not null
        currentLocation = new GeoPoint(53.5461, -113.4938);
        // check permission
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        //locationPermission = Boolean.TRUE;
                        updateLocation();
                    } else {
                        //locationPermission = Boolean.FALSE;
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //final Context context = this.getActivity();

        //MapViewModel mapViewModel =
        //new ViewModelProvider(this).get(MapViewModel.class);

        //binding = FragmentMapBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();

        View v = inflater.inflate(R.layout.fragment_map, null);
        mMapView = v.findViewById(R.id.map);
        mMapView.setDestroyMode(false);

        IMapController mapController = mMapView.getController();
        mapController.setZoom(15);
        GeoPoint startPoint = new GeoPoint(53.5461, -113.4938);
        mapController.setCenter(startPoint);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Context context = this.getActivity();
        super.onViewCreated(view, savedInstanceState);

        final Button searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. get location
                updateLocation();
            }
        });
    }

    public void updateLocation() {
        final Context context = this.getActivity();
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            //locationPermission = Boolean.TRUE;
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null){
                        currentLocation.setLatitude(location.getLatitude());
                        currentLocation.setLongitude(location.getLongitude());

                        final GeoLocation center = new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

                        // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
                        // a separate query for each pair.
                        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, 1000);
                        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                        for (GeoQueryBounds b : bounds) {
                            Query q = db.collection("ScoringQRCodes")
                                    .orderBy("geoHash")
                                    .startAt(b.startHash)
                                    .endAt(b.endHash);

                            tasks.add(q.get());
                        }

                        //final List<OverlayItem> markers = new ArrayList<OverlayItem>();
                        // Collect all the query results together into a single list
                        Tasks.whenAllComplete(tasks)
                                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                                        for (Task<QuerySnapshot> task : tasks) {
                                            QuerySnapshot snap = task.getResult();
                                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                                double lat = doc.getDouble("latitude");
                                                double lng = doc.getDouble("longitude");

                                                // We have to filter out a few false positives due to GeoHash
                                                // accuracy, but most will match
                                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                                if (distanceInM <= 1000) {
                                                    //matchingDocs.add(doc);
                                                    // create marker and listener for each doc
                                                    Marker marker = new Marker(mMapView);
                                                    marker.setPosition(new GeoPoint(lat,lng));
                                                    marker.setId(doc.getId());
                                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    mMapView.getOverlays().add(marker);

//                                            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
//                                                @Override
//                                                public boolean onMarkerClick(Marker marker, MapView mapView) {
//                                                    return false;
//                                                }
//                                            });
                                                    //markers.add(new OverlayItem(doc.getId(), "Title","marker", new GeoPoint(lat,lng)));
                                                }
                                            }
                                        }

                                        // matchingDocs contains the results

                                    }
                                });
                    }
                }
            });
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }
}