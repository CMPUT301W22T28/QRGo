package com.example.myapplication.fragments.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.dataClasses.asyncdata.QRGoEventListener;
import com.example.myapplication.dataClasses.asyncdata.AsyncList;
import com.example.myapplication.activity.QRScanActivity;
import com.example.myapplication.activity.QRShowActivity;
import com.example.myapplication.dataClasses.qrCode.GameStatusQRCode;
import com.example.myapplication.dataClasses.qrCode.LoginQRCode;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.dataClasses.user.Player;
import com.example.myapplication.databinding.FragmentProfileBinding;
import com.example.myapplication.fragments.post.PostFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The fragment for the profile. It shows profile information such as username, scanned qr codes and their scores.
 * @author Walter Ostrander
 * @see ProfileViewModel
 * @see QRGoEventListener
 *
 * March 12, 2022
 */
public class ProfileFragment extends Fragment implements QRCodeRecyclerAdapter.ItemClickListener, QRGoEventListener<ScoringQRCode> {
    private final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private MainActivity activity;
    private ProfileViewModel profileViewModel;
    private ArrayList<ScoringQRCode> myQrCodes;
    private Button deleteProfileButton;
    private Button profileContactButton;
    private String viewedUser = null;
    private Player myPlayerProfile;
    private QRCodeRecyclerAdapter scoringQRCodeAdapter;
    private Boolean isAdmin = null;
    private boolean doNotUpdate = false;

    public static ProfileFragment newInstance(Boolean isAdmin, String username) {
        Bundle args = new Bundle();
        args.putBoolean("isAdmin", isAdmin);
        args.putString("Username", username);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Initially called when the profile fragment is created.
     * @param inflater The inflater for this view.
     * @param container The container for this view, defined in the xml.
     * @param savedInstanceState A saved state if there is one.
     * @return The root View.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    /**
     * Called every after the view is initialized
     * @param view The profile fragment view that was initialized.
     * @param savedInstanceState A saved instance.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        // receives the isAdmin from search

        try { this.isAdmin = getArguments().getBoolean("isAdmin");}
        catch(Exception e) { this.isAdmin = false; }

        // initialises the delete profile button
        deleteProfileButton = (Button) binding.deleteProfileButton;
        profileContactButton = (Button) binding.profileContactButton;

        // getting the recycler view ready
        setupRecyclerView();

        // set the view listeners
        setViewListeners();

        // send the data to the view listeners
        getProfileFromDatabase();

        // check to see if the delete button can be visible
        if (isAdmin == null) {
            deleteAllowed();
        }
        else if (isAdmin == true) {
            deleteProfileButton.setVisibility(View.VISIBLE);
        }

        profileContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactDialog contactDialog = new ContactDialog();
                contactDialog.show(getParentFragmentManager(),"ContactDialog");
            }
        });

        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("Users").document(viewedUser).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println("User Deleted Successfully!");
                    }
                });

                db.collection("LoginQRCode").whereEqualTo("username", viewedUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            DocumentReference documentReference = document.getReference();
                            documentReference.delete();
                        }
                    }
                });

                db.collection("GameStatusQRCode").whereEqualTo("username", viewedUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            DocumentReference documentReference = document.getReference();
                            documentReference.delete();
                        }
                    }
                });

                db.collection("Comments").whereEqualTo("username", viewedUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            DocumentReference documentReference = document.getReference();
                            documentReference.delete();
                        }
                    }
                });

                db.collection("Posts").whereEqualTo("username", viewedUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            DocumentReference documentReference = document.getReference();
                            documentReference.delete();
                        }
                    }
                });

                db.collection("ScoringQRCodes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("scanned_by", FieldValue.arrayRemove(viewedUser));
                                db.collection("ScoringQRCodes").document(document.getId()).update(map);
                            }
                        }
                    }
                });

                getParentFragmentManager().popBackStackImmediate();
                Toast.makeText(activity.getApplicationContext(), "Profile Deleted Successfully", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        requireActivity().getViewModelStore().clear();

    }

    /**
     * Method to start the QRShowActivity activity after the user clicks one of the generated qrcode
     * buttons
     * @param username the string to be represented by the qrcode
     * @param qrCodeType the type of qrcode the user requested to generate
     * @see QRShowActivity
     */
    public void qrShowActivity (String username, String qrCodeType){
        Bundle extras = new Bundle();
        extras.putString("Username", username);
        extras.putString("qrCodeType",qrCodeType);
        Intent intent = new Intent(getContext(), QRShowActivity.class);
        intent.putExtras(extras);
        startActivityForResult(intent, 8);
    }

    /**
     * Method that implements everything necessary to get all the data to fill out the profile.
     * The data is stored on Firestore, so it makes fetches from there and passes them to the
     * ProfileViewModel. Furthermore, it uses the array of qrCode hashes from the user's profile
     * Document on Firestore, then creates an AsyncQrCodeListEvent and passes all the qr code information
     * into there.
     */
    private void getProfileFromDatabase() {
        Log.d("ProfileFragment", requireActivity().getIntent().getStringExtra("Username"));

        try { this.viewedUser = getArguments().getString("Username");}
        catch(Exception e) { this.viewedUser = requireActivity().getIntent().getStringExtra("Username"); }

        enableDisableQRCodeButtons(requireActivity().getIntent().getStringExtra("Username") ,this.viewedUser);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference MyUserDocRef = db.collection("Users").document(this.viewedUser);

        ProfileFragment profileFragment = this;

        MyUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot != null && snapshot.exists() && !doNotUpdate) {
                    Boolean isAdmin = snapshot.getBoolean("admin");

                    if (myPlayerProfile == null) {
                        if (isAdmin == null || !isAdmin) {
                            myPlayerProfile = new Player(viewedUser, false);
                        }
                        else {
                            myPlayerProfile = new Player(viewedUser, true);
                        }
                    }

                    String myEmail = snapshot.getString("email");
                    String myPhone = snapshot.getString("phone");
                    // region setting text views in profile top bar
                    profileViewModel.setUsername(viewedUser);
                    profileViewModel.setEmail(myEmail);
                    profileViewModel.setPhone(myPhone);

                    // endregion
                    Long scannedCount = snapshot.getLong("scanned_count");

                    // get qrcodes
                    Object obj = snapshot.get("scanned_qrcodes");
                    Iterable<?> ar = (Iterable<?>) obj;
                    ArrayList<String> qrCodeHashes = new ArrayList<>();
                    assert ar != null;
                    for (Object x : ar) {
                        qrCodeHashes.add((String) x);
                    }

                    AsyncList<ScoringQRCode> asyncList = new AsyncList<>(qrCodeHashes.size(), profileFragment);
                    CollectionReference scoringQrCodeColRef = db.collection("ScoringQRCodes");

                    Log.d("walter", "hash size: "+qrCodeHashes.size() +", qrCodeCount: "+ myPlayerProfile.getQRCodeCount());

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
                                            ScoringQRCode tempQrCode = new ScoringQRCode(hash, true);
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
                                            tempQrCode.setLatitude(latitude);

                                            // setting longitude
                                            longitude = document.getDouble("longitude");
                                            tempQrCode.setLongitude(longitude);

                                            // adding qrCode to array
                                            asyncList.addToArray(tempQrCode);
                                        }
                                    } else {
                                        Log.d(TAG, "Cached ScoringQRCodeDocument document with hash: "+hash+" failed with exception: ", task.getException());
                                    }
                                }
                            });
                        }
                    }
                    else {
                        resetAndFillQRCodes(myQrCodes);
                    }

                    if (scannedCount == null || qrCodeHashes.size() != scannedCount.intValue()) {
                        MyUserDocRef.update(
                                "scanned_count", qrCodeHashes.size()
                        );
                    }
                } else {
                    doNotUpdate = false;
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    /**
     * This method sets up the recycler view to hold all the qr codes, along with creating dividers
     * between the recycler elements.
     */
    private void setupRecyclerView() {

        // testing the custom array adapter
        this.myQrCodes = new ArrayList<>();

        RecyclerView recyclerView = binding.scoringQrCodeList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        scoringQRCodeAdapter = new QRCodeRecyclerAdapter(activity, this.myQrCodes);
        scoringQRCodeAdapter.setClickListener(this);
        recyclerView.setAdapter(scoringQRCodeAdapter);
    }

    /**
     * Method that fetches the livedata and attaches listeners. Whenever there is a change in any of the data,
     * it gets passed on to it's respective view. For example, when the username changes, it's textview will
     * change with it.
     */
    private void setViewListeners() {
        if (myPlayerProfile != null) {
            myPlayerProfile.resetQRCodeList();
        }

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        final TextView usernameTextView = binding.profileUsername;
        profileViewModel.getUsername().observe(getViewLifecycleOwner(), usernameTextView::setText);

        final TextView totalScoreTextView = binding.profileTotalScore;
        profileViewModel.getTotalScore().observe(getViewLifecycleOwner(), totalScoreTextView::setText);

        final TextView QRCodeCountTextView = binding.profileQrCodeCount;
        profileViewModel.getQRCodeCount().observe(getViewLifecycleOwner(), QRCodeCountTextView::setText);

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

        Button showLoginQRCode = binding.showLoginQrcodeButton;

        String myUsername = requireActivity().getIntent().getStringExtra("Username");

        showLoginQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginQRCode loginQRCode = new LoginQRCode(myUsername);
                qrShowActivity(loginQRCode.getScannedString(), loginQRCode.getQRCodeType());
            }
        });

        Button showGameStatusQRCode =  binding.showGamestatusQrcodeButton;

        showGameStatusQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameStatusQRCode gameStatusQRCode = new GameStatusQRCode("gs-"myUsername);
                qrShowActivity(gameStatusQRCode.getScannedString(), gameStatusQRCode.getQRCodeType());
            }
        });
    }

    /**
     * Method that checks to see if the delete button should be visible or not. This is based on whether or
     * not the user is registered as an Administrator
     */
    private void deleteAllowed() {
        Log.d("ProfileFragment", requireActivity().getIntent().getStringExtra("Username"));

        try { this.viewedUser = getArguments().getString("Username");}
        catch(Exception e) { this.viewedUser = requireActivity().getIntent().getStringExtra("Username"); }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference MyUserDocRef = db.collection("Users").document(this.viewedUser);

        MyUserDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    isAdmin = value.getBoolean("admin");
                    if (isAdmin == null) {
                        isAdmin = false;
                    }
                }
                else {
                    // throw exception if any issues getting document
                    Toast.makeText(activity.getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting document: ", error);
                }
            }
        });
    }

    /**
     * To implement later, a method called when a qr code is clicked in the list of Scoring Qr Codes.
     * @param view The view that was clicked.
     * @param position The position of the qr Code clicked in the recycler view.
     */
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity.getApplicationContext(), "You clicked on row number " + position, Toast.LENGTH_SHORT).show();

        ScoringQRCode qrCode = myPlayerProfile.getQRCodes().get(position);

        String currentUser = requireActivity().getIntent().getStringExtra("Username");
//        PostFragment postFragment = PostFragment.newInstance(qrCode.getHash(), viewedUser, currentUser);

        //region passing arguments while navigating fragments

        // all of this is define in "mobile_navigation.xml", the class ProfileFragmentDirections is created automatically.
        ProfileFragmentDirections.ActionNavigationProfileToNavigationPost action = ProfileFragmentDirections.actionNavigationProfileToNavigationPost(
                qrCode.getHash(), // the hash of the qr code of the post.
                viewedUser, // the username of the person who's profile you are on
                currentUser, // the username of the person viewing the post
                isAdmin); // boolean lets the fragment know if the currentUser is an admin

        NavHostFragment.findNavController(this).navigate(action);
        //endregion
    }

    /**
     * Called when the Fragment is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /**
     * An event listener, called when AsynQrCodeList is done filling with the prescribed
     * number of Qr Codes.
     * @param qrCodes The list of qr codes that was filled asynchronously
     */
    @Override
    public void onListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes) {
        resetAndFillQRCodes(qrCodes);
    }

    public void resetAndFillQRCodes(ArrayList<ScoringQRCode> qrCodes) {
        // fill the profile view with qrcodes
        myPlayerProfile.resetQRCodeList();
        for (ScoringQRCode qrCode: qrCodes) {
            myPlayerProfile.addScoringQRCode(qrCode);
        }
        profileViewModel.setMutableProfileQRCodes(qrCodes);
        updateHighestAndSumQrCode();
    }

    /**
     * This method is called after the onQrCodeListDoneFillingEvent is called. It
     * uses the gathered ScoringQrCodes to calculate the highest score and the sum of all
     * the qr codes. It
     */
    public void updateHighestAndSumQrCode() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // setting persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference MyUserDocRef = db.collection("Users").document(this.viewedUser);

        int newHighestQrCode = 0;
        int newSumQrCodes = 0;
        for (ScoringQRCode qrCode: myPlayerProfile.getQRCodes())
        {
            newSumQrCodes += qrCode.getScore();
            newHighestQrCode = Math.max(newHighestQrCode, qrCode.getScore());
        }
        Log.d(TAG,"total: "+newSumQrCodes+", highest: "+newSumQrCodes+", numQrCodes: "+myPlayerProfile.getQRCodes().size());



        if ((profileViewModel.getTopQRCodeScore().getValue() != null && Integer.parseInt(profileViewModel.getTopQRCodeScore().getValue()) != newHighestQrCode)
        || (profileViewModel.getTotalScore().getValue() != null && Integer.parseInt(profileViewModel.getTotalScore().getValue()) != newSumQrCodes)) {
            doNotUpdate = true;
            MyUserDocRef.update(
                    "scanned_highest", newHighestQrCode,
                    "scanned_sum", newSumQrCodes
            );
        }
//        MyUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    // Document found in the offline cache
//                    DocumentSnapshot document = task.getResult();
//
//                    if (document != null && document.exists()) {
//                        Long docSumQrCodes;
//                        Long docHighestQrCode;
//
//                        docSumQrCodes = document.getLong("scanned_sum");
//                        if (docSumQrCodes != null) {
//                            if (docSumQrCodes.intValue() != sumQrCodes) {
//                                MyUserDocRef.update(
//                                        "scanned_sum", sumQrCodes
//                                );
//                            }
//                        }
//
//                        docHighestQrCode = document.getLong("scanned_highest");
//                        if (docHighestQrCode != null) {
//                            if (docHighestQrCode.intValue() != highestQrCode) {
//                                MyUserDocRef.update(
//                                        "scanned_highest", highestQrCode
//                                );
//                            }
//                        }
//                    }
//                }
//            }
//        });

        myPlayerProfile.setTotalScore(newSumQrCodes);
        myPlayerProfile.setHighestScore(newHighestQrCode);

        profileViewModel.setTopQRCodeScore( myPlayerProfile.getHighestScore());
        profileViewModel.setTotalScore(myPlayerProfile.getTotalScore());
    }

    public ProfileViewModel getViewModel() {
        return profileViewModel;
    }

    public void enableDisableQRCodeButtons(String loggedInUsername, String viewedUsername){
        if (!loggedInUsername.equals(viewedUsername)){
            Button showLoginQRCode = binding.showLoginQrcodeButton;
            Button showGameStatusQRCode =  binding.showGamestatusQrcodeButton;

            showLoginQRCode.setVisibility(View.GONE);
            showGameStatusQRCode.setVisibility(View.GONE);


        }
    }
}