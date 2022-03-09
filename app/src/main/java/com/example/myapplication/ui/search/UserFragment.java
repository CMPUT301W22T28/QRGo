package com.example.myapplication.ui.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class UserFragment extends Fragment implements QRCodeRecyclerAdapter.ItemClickListener, UserEventListeners {
    private final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    MainActivity activity;
    ArrayAdapter<ScoringQRCode> qrCodeArrayAdapter;
    private ProfileViewModel profileViewModel;

    private ArrayList<ScoringQRCode> myQrCodes;
    private RecyclerView recyclerView;

    private String myUsername = null;
    private Player myPlayerProfile;

    QRCodeRecyclerAdapter scoringQRCodeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        // getting the recycler view ready
        setupRecyclerView();

        // set the view listeners
        setViewListeners();

        // send the data to the view listeners
        getProfileFromDatabase();
    }

    private void getProfileFromDatabase() {

        Log.d("ProfileFragment", getArguments().getString("username"));
        this.myUsername = getArguments().getString("username");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference MyUserDocRef = db.collection("Users").document(this.myUsername);

        UserFragment userFragment = this;

        MyUserDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Boolean isAdmin = snapshot.getBoolean("admin");

                    if (myPlayerProfile == null) {
                        if (isAdmin == null || !isAdmin) {
                            myPlayerProfile = new Player(myUsername, false);
                        }
                        else {
                            myPlayerProfile = new Player(myUsername, true);
                        }
                    }

                    // region setting text views in profile top bar
                    profileViewModel.setUsername(myUsername);

                    Long scannedCount = snapshot.getLong("scanned_count");

                    // get qrcodes
                    Object obj = snapshot.get("scanned_qrcodes");
                    Iterable<?> ar = (Iterable<?>) obj;
                    ArrayList<String> qrCodeHashes = new ArrayList<>();
                    assert ar != null;
                    for (Object x : ar) {
                        qrCodeHashes.add((String) x);
                    }

                    AsyncQrCodeList asyncQrCodeList = new AsyncQrCodeList(qrCodeHashes.size(), userFragment);
                    CollectionReference scoringQrCodeColRef = db.collection("ScoringQRCodes");

                    if (qrCodeHashes.size() != myPlayerProfile.getQRCodeCount()) {
                        for (String hash : qrCodeHashes) {
                            scoringQrCodeColRef.document(hash).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        // Document found in the offline cache
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {

                                            // data we need to get
                                            ScoringQRCode tempQrCode = new ScoringQRCode(hash);
                                            Long score;
                                            Double longitude;
                                            Double latitude;

                                            // fetching score
                                            score = document.getLong("score");
                                            if (score != null) {
                                                tempQrCode.setScore(score.intValue());
                                            }
                                            else {
                                                tempQrCode.setScore(-2);
                                            }

                                            // setting latitude
                                            latitude = document.getDouble("latitude");
                                            if (latitude != null) {
                                                tempQrCode.setLatitude(latitude);
                                            }
                                            else {
                                                tempQrCode.setLatitude(null);
                                            }

                                            // setting longitude
                                            longitude = document.getDouble("longitude");
                                            if (longitude != null) {
                                                tempQrCode.setLongitude(longitude);
                                            }
                                            else {
                                                tempQrCode.setLongitude(null);
                                            }

                                            // adding qrCode to array
                                            asyncQrCodeList.addToArray(tempQrCode);
                                        }
                                    } else {
                                        Log.d(TAG, "Cached ScoringQRCodeDocument document with hash: "+hash+" failed with exception: ", task.getException());
                                    }
                                }
                            });
                        }
                    }

                    if (scannedCount == null || qrCodeHashes.size() != scannedCount.intValue()) {
                        MyUserDocRef.update(
                                "scanned_count", qrCodeHashes.size()
                        );
                    }


                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    private void setupRecyclerView() {
        this.myUsername = getArguments().getString("username");;

        // testing the custom array adapter
        this.myQrCodes = new ArrayList<>();

        recyclerView = binding.scoringQrCodeList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        scoringQRCodeAdapter = new QRCodeRecyclerAdapter(activity, myQrCodes);
        scoringQRCodeAdapter.setClickListener(this);
        recyclerView.setAdapter(scoringQRCodeAdapter);
    }

    private void setViewListeners() {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        final TextView usernameTextView = binding.profileUsername;
        profileViewModel.getUsername().observe(getViewLifecycleOwner(), usernameTextView::setText);

        final TextView totalScoreTextView = binding.profileTotalScore;
        profileViewModel.getTotalScore().observe(getViewLifecycleOwner(), totalScoreTextView::setText);

        final TextView QRCodeCountTextView = binding.profileQrCodeCount;
        profileViewModel.getQrCodeCount().observe(getViewLifecycleOwner(), QRCodeCountTextView::setText);

        final TextView topQRCodeTextView = binding.profileTopQrCode;
        profileViewModel.getTopQRCodeScore().observe(getViewLifecycleOwner(), topQRCodeTextView::setText);

        profileViewModel.getQrCodes().observe(getViewLifecycleOwner(), new Observer<ArrayList<ScoringQRCode>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(ArrayList<ScoringQRCode> qrCodes) {
                myQrCodes.clear();
                myQrCodes.addAll(qrCodes);
                scoringQRCodeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity.getApplicationContext(), "You clicked on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes) {
        // fill the profile view with qrcodes
        myPlayerProfile.resetQrCodeList();
        for (ScoringQRCode qrCode: qrCodes) {
            myPlayerProfile.addScoringQRCode(qrCode);
        }
        profileViewModel.setMutableProfileQrCodes(qrCodes);
        updateHighestAndSumQrCode();
    }

    public void updateHighestAndSumQrCode() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference MyUserDocRef = db.collection("Users").document(this.myUsername);


        int tempHighestQrCode = 0;
        int tempSumQrCodes = 0;
        for (ScoringQRCode qrCode: myPlayerProfile.getQrCodes())
        {
            tempSumQrCodes += qrCode.getScore();
            tempHighestQrCode = Math.max(tempHighestQrCode, qrCode.getScore());
        }
        Log.d(TAG,"total: "+tempSumQrCodes+", highest: "+tempSumQrCodes+", numQrCodes: "+myPlayerProfile.getQrCodes().size());
        final int sumQrCodes = tempSumQrCodes;
        final int highestQrCode = tempHighestQrCode;

        MyUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();

                    if (document != null && document.exists()) {
                        Long docSumQrCodes;
                        Long docHighestQrCode;

                        docSumQrCodes = document.getLong("scanned_sum");
                        if (docSumQrCodes != null) {
                            if (docSumQrCodes.intValue() != sumQrCodes) {
                                MyUserDocRef.update(
                                        "scanned_sum", sumQrCodes
                                );
                            }
                        }

                        docHighestQrCode = document.getLong("scanned_highest");
                        if (docHighestQrCode != null) {
                            if (docHighestQrCode.intValue() != highestQrCode) {
                                MyUserDocRef.update(
                                        "scanned_highest", highestQrCode
                                );
                            }
                        }
                    }
                }
            }
        });

        myPlayerProfile.setTotalScore(sumQrCodes);
        myPlayerProfile.setHighestScore(highestQrCode);

        profileViewModel.setTopQRCodeScore(tempHighestQrCode);
        profileViewModel.setTotalScore(tempSumQrCodes);
    }


}