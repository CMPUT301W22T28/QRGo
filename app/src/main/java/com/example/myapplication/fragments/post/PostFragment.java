package com.example.myapplication.fragments.post;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.dataClasses.asyncdata.QRGoEventListener;
import com.example.myapplication.dataClasses.qrCode.ScoringQRCode;
import com.example.myapplication.databinding.FragmentPostBinding;
import com.example.myapplication.fragments.post.listfragment.CommentsFragment;
import com.example.myapplication.fragments.post.listfragment.ScannedByFragment;
import com.example.myapplication.fragments.post.postcontent.PostInfoFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PostFragment extends Fragment implements QRGoEventListener<ScoringQRCode> {

    private PostViewModel postViewModel;

    private FragmentPostBinding binding;

    private String qrHash;
    private String username;

    TabLayout tabLayout;

    private static final String ARG_QR = "argQR";
    private static final String ARG_USER = "argUser";
    private static final String POST_COLLECTION = "Posts";
    private static final String TAG = "PostFragment";

    private final StorageReference storageRef = FirebaseStorage.getInstance("gs://qrgo-e62ee.appspot.com/").getReference();

    public static PostFragment newInstance(String qrHash, String username) {
        Bundle args = new Bundle();
        args.putString(ARG_QR, qrHash);
        args.putString(ARG_USER, username);

        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        binding = FragmentPostBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = binding.postTabLayout;

        assert getArguments() != null;
        qrHash = getArguments().getString("argQR");
        username = getArguments().getString("argUser");

        // need to get postId from user and QRHash here, call

        PostInfoFragment postInfoFragment = PostInfoFragment.newInstance(qrHash);
        CommentsFragment commentsFragment = CommentsFragment.newInstance(qrHash);
        ScannedByFragment scannedByFragment = ScannedByFragment.newInstance(qrHash);


        // launch post info by default
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.post_host_fragment, postInfoFragment, "postInfoFragment")
                .addToBackStack(null)
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Post")) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.post_host_fragment, postInfoFragment, "postInfoFragment")
                            .addToBackStack(null)
                            .commit();
                }
                if (tab.getText().equals("Comments")) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.post_host_fragment, commentsFragment, "commentsFragment")
                            .addToBackStack(null)
                            .commit();
                }
                if (tab.getText().equals("Scanned by")) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.post_host_fragment, scannedByFragment, "scannedByFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void getPostFromDatabase() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(POST_COLLECTION)
                .whereEqualTo("username", username)
                .whereEqualTo("qrcode_hash", qrHash)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().size() == 1) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // show the post stuff

                                    String url = document.getString("url");
                                    if (url != null) {
                                        // get the photo
                                        StorageReference myPhoto = storageRef.getStorage().getReferenceFromUrl(url);

                                        final long ONE_MEGABYTE = 1024*1024;
                                        myPhoto.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                // TODO: we got the photo, send to picture view
                                            }
                                        });
                                    }
                                    else {
                                        Log.d(TAG, "url for image is null");
                                    }
                                }
                            }
                            else {
                                Log.d(TAG, "more posts with same conditions: "+task.getResult());
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    private void getQRCodeAndCommentsFromDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference scoringQRCodeDocRef = db.collection("ScoringQRCodes").document(qrHash);

        PostFragment postFragment = this;

        scoringQRCodeDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot != null && snapshot.exists()) {

                    //region getting the time
                    Timestamp timestamp = snapshot.getTimestamp("last_scanned");
                    // check if exists
                    if (timestamp != null) {

                    }
                    else {
                        // set null
                    }
                    // endregion

                    //region getting the latitude
                    Double latitude = snapshot.getDouble("latitude");
                    // check if exists
                    if (latitude != null) {
                        // fill view model
                    }
                    else {
                        // set null
                    }
                    // endregion

                    //region getting the longitude
                    Double longitude = snapshot.getDouble("longitude");
                    // check if exists
                    if (longitude != null) {
                        // fill view model
                    }
                    else {
                        // set null
                    }
                    // endregion

                    //region get scanned_by
                    ArrayList<String> scannedByList = new ArrayList<>();

                    Object obj = snapshot.get("scanned_by");
                    if (obj != null) {
                        Iterable<?> ar = (Iterable<?>) obj;

                        for (Object x : ar) {
                            scannedByList.add((String) x);
                        }
                    }
                    else {
                        Log.d(TAG, "scanned_by array is null...");
                    }
                    //endregion

                    //region get num_scanned_by
                    Long nScannedBy = snapshot.getLong("num_scanned_by");
                    // check if exists
                    if (nScannedBy == null || nScannedBy != scannedByList.size()) {
                        // update value in scannedQRCodes
                        scoringQRCodeDocRef.update("num_scanned_by", scannedByList.size());
                    }
                    //endregion

                    // TODO: update the view models with the above information

                    //region get comment ids
                    ArrayList<String> commentIds = new ArrayList<>();

                    Object object = snapshot.get("scanned_by");
                    if (object != null) {
                        Iterable<?> ar = (Iterable<?>) object;

                        for (Object x : ar) {
                            scannedByList.add((String) x);
                        }
                    }
                    else {
                        Log.d(TAG, "scanned_by array is null...");
                    }
                    //endregion

                    CollectionReference commentColReference = db.collection("Comments");

                    // TODO: once comment class is made, import and add to array list here:
                    // AsyncList<Comment> asyncList = new AsyncList<>(commentIds.size(), postFragment);


                    for (String commentId : commentIds) {
                        commentColReference.document(commentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        String comment = document.getString("comment");
                                        String username = document.getString("username");

                                        // TODO: fill comment out and pass it to the asyncList
                                    }
                                }
                            }
                        });


                    }




                }
            }
        });


    }

    @Override
    public void onQrCodeListDoneFillingEvent(ArrayList<ScoringQRCode> qrCodes) {

    }
}