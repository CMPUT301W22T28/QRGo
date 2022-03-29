package com.example.myapplication.fragments.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This Fragment displays the map and allows the user to search for nearby QRCodes.
 *
 * @author CMPUT 301 team 28, Marc-Andre Haley
 *
 * March 10, 2022
 */

/*
 * Sources
 * Location permission: https://developer.android.com/training/permissions/requesting
 * Getting last location: https://developer.android.com/training/location/retrieve-current
 * Checking google play services:
 * https://stackoverflow.com/questions/62787511/programmatically-check-if-android-device-has-google-play
 *
 * Known issue: On new device's first time launching the app, the map fragment might require moving
 * to map fragment twice before showing location and markers
 */
public class MapFragment extends Fragment {

    private MapView mMapView;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private GeoPoint currentLocation;
    private FirebaseFirestore db;
    private IMapController mapController;
    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        flag = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx) == com.google.android.gms.common.ConnectionResult.SUCCESS;

        db = FirebaseFirestore.getInstance();

        // initialize currentLocation so it is not null
        currentLocation = new GeoPoint(53.5461, -113.4938);
        // check permission
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        updateLocation(0);
                    } else {
                        mapController.setCenter(currentLocation);
                        // add myLocation overlay
                        MyLocationNewOverlay myLocationoverlay = new MyLocationNewOverlay(mMapView);
                        myLocationoverlay.enableFollowLocation();
                        myLocationoverlay.enableMyLocation();
                        mMapView.getOverlays().add(myLocationoverlay);
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

        // set up view
        View v = inflater.inflate(R.layout.fragment_map, null);
        mMapView = v.findViewById(R.id.map);
        mMapView.setDestroyMode(false);

        // set up initial map config
        mapController = mMapView.getController();
        mapController.setZoom(16);
        if (flag) { // if google play services available
            updateLocation(0);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag){ // if google play services available
                    updateLocation(1);
                }
            }
        });
    }

    /**
     * Checks location permission and sets current location to device's last known location.
     *
     * @param event
     * indicates in what event the method is called (0 or 1)
     * 1 indicates we need to get nearby codes after getting location
     * anything else indicates we don't need to get nearby codes after getting location
     *
     */
    public void updateLocation(Integer event) {
        final Context context = this.getActivity();

        // always check location permissions
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null){
                        currentLocation.setLatitude(location.getLatitude());
                        currentLocation.setLongitude(location.getLongitude());

                        // if updatelocation is called from search nearby button
                        if (event == 1){
                            getNearbyCodes();
                        }
                    }
                    mapController.setCenter(currentLocation);
                    // add myLocation overlay
                    MyLocationNewOverlay myLocationoverlay = new MyLocationNewOverlay(mMapView);
                    myLocationoverlay.enableFollowLocation();
                    myLocationoverlay.enableMyLocation();
                    mMapView.getOverlays().add(myLocationoverlay);
                }
            });
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Searches the database for QRcodes in a 1km radius from users last known location.
     * Displays these QR codes as markers on the map.
     */
    public void getNearbyCodes(){
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
                                    marker.setPosition(new GeoPoint(lat, lng));
                                    marker.setId(doc.getId());
                                    String title = String.format("Score: %d", doc.getLong("score").intValue());
                                    marker.setTitle(title);
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    mMapView.getOverlays().add(marker);
                                    mMapView.invalidate();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}