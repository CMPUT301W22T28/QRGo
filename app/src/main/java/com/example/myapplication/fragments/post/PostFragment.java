package com.example.myapplication.fragments.post;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.myapplication.R;
import com.example.myapplication.dataClasses.Comment;
import com.example.myapplication.dataClasses.asyncdata.AsyncList;
import com.example.myapplication.dataClasses.asyncdata.QRGoEventListener;
import com.example.myapplication.databinding.FragmentPostBinding;
import com.example.myapplication.fragments.post.listfragment.CommentsFragment;
import com.example.myapplication.fragments.post.listfragment.CommentsViewModel;
import com.example.myapplication.fragments.post.listfragment.ScannedByFragment;
import com.example.myapplication.fragments.post.listfragment.ScannedByViewModel;
import com.example.myapplication.fragments.post.postcontent.PostInfoFragment;
import com.example.myapplication.fragments.post.postcontent.PostInfoViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Fragment that provides tab navigation between info, comments and scanned by fragments.
 * Updates all these fragments data in real time using their view models.
 *
 * @author CMPUT 301 team 28, Marc-Andre Haley & Walter Ostrander
 *
 * @see CommentsViewModel
 * @see PostInfoViewModel
 * @see ScannedByViewModel
 *
 * March 22, 2022
 */

public class PostFragment extends Fragment implements QRGoEventListener<Comment> {

    private FragmentPostBinding binding;

    private String qrHash;
    private String postOwner; // username of post owner
    private String username; // main user
    private Boolean isAdmin;
    private boolean userHasCode;
    private FirebaseFirestore db;

    TabLayout tabLayout;

    private static final String ARG_QR = "argQR";
    private static final String ARG_POST_USER = "argPostUser";
    private static final String ARG_USER = "argUser";
    private static final String ARG_ADMIN = "argAdmin";
    private static final String POST_COLLECTION = "Posts";
    private static final String USER_COLLECTION = "Users";
    private static final String TAG = "PostFragment";

    private ScannedByViewModel scannedByViewModel;
    private PostInfoViewModel postInfoViewModel;
    private CommentsViewModel commentsViewModel;

    private final StorageReference storageRef = FirebaseStorage.getInstance("gs://qrgo-e62ee.appspot.com/").getReference();

    public static PostFragment newInstance(String qrHash, String postOwner, String username, Boolean isAdmin) {
        Bundle args = new Bundle();
        args.putString(ARG_QR, qrHash);
        args.putString(ARG_POST_USER, postOwner);
        args.putString(ARG_USER, username);
        args.putBoolean(ARG_ADMIN, isAdmin);

        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater, container, false);

        postInfoViewModel = new ViewModelProvider(requireActivity()).get(PostInfoViewModel.class);
        scannedByViewModel = new ViewModelProvider(requireActivity()).get(ScannedByViewModel.class);
        commentsViewModel = new ViewModelProvider(requireActivity()).get(CommentsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = binding.postTabLayout;

        assert getArguments() != null;
        qrHash = getArguments().getString(ARG_QR);
        postOwner = getArguments().getString(ARG_POST_USER);
        username = getArguments().getString(ARG_USER);
        isAdmin = getArguments().getBoolean(ARG_ADMIN);

        Log.d(TAG, "User: "+username+ ", post owner: "+postOwner);

        // need to get postId from user and QRHash here, call

        PostInfoFragment postInfoFragment = PostInfoFragment.newInstance(qrHash, postOwner, username, isAdmin);
        CommentsFragment commentsFragment = CommentsFragment.newInstance(username, qrHash);
        ScannedByFragment scannedByFragment = ScannedByFragment.newInstance();

        // launch post info by default
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.post_host_fragment, postInfoFragment, "postInfoFragment")
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Post")) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.post_host_fragment, postInfoFragment, "postInfoFragment")
                            .commit();
                }
                if (tab.getText().equals("Comments")) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.post_host_fragment, commentsFragment, "commentsFragment")
                            .commit();
                }
                if (tab.getText().equals("Scanned by")) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.post_host_fragment, scannedByFragment, "scannedByFragment")
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

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(USER_COLLECTION).document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> QRCodes = (List<String>) document.get("scanned_qrcodes");
                        // check if user has qrCode
                        userHasCode = QRCodes.contains(qrHash);
                        if (userHasCode) {
                            getAndSetPostImage();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        if (qrHash != null) {
            getAndSetPost();
        }
    }

    /**
     *
     * Gets post image from database and sets it in the postInfoViewModel
     *
     * @see PostInfoViewModel
     *
     */
    private void getAndSetPostImage() {
        // get view model to use

        postInfoViewModel.setImageNotAvailableText("");

        Log.d(TAG, "post owna: "+postOwner + ", hash: "+qrHash);

        db.collection(POST_COLLECTION)
                .whereEqualTo("username", postOwner)
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
                                                // source: https://stackoverflow.com/questions/13854742/byte-array-of-image-into-imageview
                                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                postInfoViewModel.setImage(bmp);
                                                Log.d(TAG, "bmp: "+bmp);

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

    /**
     *
     * Gets a document snapshot of the QR code in post and sets all the info associated with it.
     *
     */
    private void getAndSetPost(){

        DocumentReference scoringQRCodeDocRef = db.collection("ScoringQRCodes").document(qrHash);

        scoringQRCodeDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
            } else if (snapshot != null && snapshot.exists()) {
                if (getActivity() == null) {

                }
                getAndSetPostScannedBy(snapshot, scoringQRCodeDocRef);
                getAndSetPostInfo(snapshot);
                getAndSetPostComments(snapshot);
            }
        });
    }

    /**
     *
     * Gets Scanned by list from database and sets it in ScannedByViewModel.
     * Updates the number of scanned by field in database.
     *
     * @param QRCodeSnapshot document QRCodeSnapshot of the current QR code
     * @param QRCodeDocRef document reference of QR code
     *
     * @see ScannedByViewModel
     *
     */
    private void getAndSetPostScannedBy(DocumentSnapshot QRCodeSnapshot, DocumentReference QRCodeDocRef){

        Log.d("tagtag", "here:"+String.valueOf(getActivity()));

        //region get scanned_by
        ArrayList<String> scannedByList = new ArrayList<>();

        Object obj = QRCodeSnapshot.get("scanned_by");
        if (obj != null) {
            Iterable<?> ar = (Iterable<?>) obj;

            for (Object x : ar) {
                scannedByList.add((String) x);
            }
            scannedByViewModel.setScannedByLiveDataList(scannedByList);
        }
        else {
            Log.d(TAG, "scanned_by array is null...");
        }
        //endregion

        //region get num_scanned_by
        Long nScannedBy = QRCodeSnapshot.getLong("num_scanned_by");
        // check if exists
        if (nScannedBy == null || nScannedBy != scannedByList.size()) {
            // update value in scannedQRCodes
            QRCodeDocRef.update("num_scanned_by", scannedByList.size());
        }
    }

    /**
     *
     * Gets post info from database and sets it in the postInfoViewModel
     *
     * @param snapshot document snapshot of the current QR code
     *
     * @see PostInfoViewModel
     *
     */
    private void getAndSetPostInfo(DocumentSnapshot snapshot){

        // set score
        postInfoViewModel.setScore(snapshot.getLong("score").intValue());

        // endregion
        String geolocationString = "[";

        //region getting the latitude
        Double latitude = snapshot.getDouble("latitude");
        // check if exists
        if (latitude == null) {
            // fill view model
            geolocationString+="null";
        }
        else{
            geolocationString+=latitude+", ";
        }
        // endregion

        //region getting the longitude
        Double longitude = snapshot.getDouble("longitude");
        // check if exists
        if (longitude == null) {
            // fill view model
            geolocationString += "null]";
        }
        else {
            geolocationString += longitude + "]";
        }
        // endregion
        postInfoViewModel.setGeoLocation(geolocationString);

        //region get num_scanned_by
        Long nScannedBy = snapshot.getLong("num_scanned_by");
        // check if exists
        if (nScannedBy != null) {
            // set value
            postInfoViewModel.setScannedByText(nScannedBy.intValue());
        }
        //endregion
    }

    /**
     *
     * Gets comments associated with QR code from database and sets it in the CommentsViewModel
     *
     * @param snapshot document snapshot of the current QR code
     *
     * @see CommentsViewModel
     *
     */
    private void getAndSetPostComments(DocumentSnapshot snapshot){
        //region get comment ids
        ArrayList<String> commentIds = new ArrayList<>();

        Object object = snapshot.get("comment_ids");
        if (object != null) {
            Iterable<?> ar = (Iterable<?>) object;

            for (Object x : ar) {
                commentIds.add((String) x);
            }
        }
        else {
            Log.d(TAG, "scanned_by array is null...");
        }
        //endregion

        CollectionReference commentColReference = db.collection("Comments");

        AsyncList<Comment> asyncList = new AsyncList<>(commentIds.size(), this);

        for (String commentId : commentIds) {
            commentColReference.document(commentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String commentText = document.getString("comment");
                            String username = document.getString("username");

                            // TODO: fill comment out and pass it to the asyncList
                            if (commentText == null) {
                                commentText = "";
                            }
                            if (username == null) {
                                username = "";
                            }
                            Comment comment = new Comment(commentText, username);

                            asyncList.addToArray(comment);
                        }
                    }
                }
            });
        }
    }


    @Override
    public void onListDoneFillingEvent(ArrayList<Comment> comments) {
        if (isAdded()) {

            commentsViewModel.setComments(comments);
        }
    }

}