package com.example.myapplication.fragments.post.postcontent;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.dataClasses.asyncdata.QRGoEventListener;
import com.example.myapplication.databinding.FragmentPostInfoBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Fragment that shows information about the post and the image
 *
 * @author Marc-Andre Haley, Walter Ostrander, Sankalp Saini
 *
 * @see PostInfoViewModel
 *
 * March 22, 2022
 *
 */
public class PostInfoFragment extends Fragment {
    private MainActivity activity;

    FragmentPostInfoBinding binding;
    private final String TAG = "PostInfoFragment";
    private int widthHeight = 0;

    Button deletePostButton;

    private String qrHash;
    private String postOwner; // username of post owner
    private String username; // main user
    private Boolean isAdmin;
    private String commentUsername;

    private static final String ARG_QR = "argQR";
    private static final String ARG_POST_USER = "argPostUser";
    private static final String ARG_USER = "argUser";
    private static final String ARG_ADMIN = "argAdmin";

    public static PostInfoFragment newInstance(String qrHash, String postOwner, String username, Boolean isAdmin) {
        Bundle args = new Bundle();
        args.putString(ARG_QR, qrHash);
        args.putString(ARG_POST_USER, postOwner);
        args.putString(ARG_USER, username);
        args.putBoolean(ARG_ADMIN, isAdmin);

        PostInfoFragment fragment = new PostInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentPostInfoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        assert getArguments() != null;
        qrHash = getArguments().getString(ARG_QR);
        postOwner = getArguments().getString(ARG_POST_USER);
        username = getArguments().getString(ARG_USER);
        isAdmin = getArguments().getBoolean(ARG_ADMIN);

        setViewListeners();
        // stuff in here

        deletePostButton = (Button) binding.deletePostButton;
        // check to see if the delete button can be visible

        if ((username.equals(postOwner)) || isAdmin) {
            deletePostButton.setVisibility(View.VISIBLE);
        }

        System.out.println("in PostInfoFragment"+isAdmin);

        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (isAdmin){
                    System.out.println("THIS IS AN ADMIN POST DELETE");

                    db.collection("ScoringQRCodes").document(qrHash).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Object obj = documentSnapshot.get("comment_ids");
                            Iterable<?> ar = (Iterable<?>) obj;
                            ArrayList<String> comments = new ArrayList<>();
                            assert ar != null;
                            for (Object comment : ar) {
                                comments.add((String) comment);
                            }
                            for (String comment : comments) {
                                db.collection("Comments").document(comment).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        System.out.println("COMMENT: "+comment+" deleted successfully");
                                    }
                                });
                            }
                            DocumentReference documentReference = documentSnapshot.getReference();
                            documentReference.delete();
                        }
                    });

                    db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                for (DocumentSnapshot document : documents) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("scanned_qrcodes", FieldValue.arrayRemove(qrHash));
                                    db.collection("Users").document(document.getId()).update(map);
                                }

                                getParentFragmentManager().popBackStackImmediate();
                                requireActivity().getViewModelStore().clear();
                                Toast.makeText(activity.getApplicationContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    db.collection("Posts").whereEqualTo("qrcode_hash", qrHash).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                DocumentReference documentReference = document.getReference();
                                documentReference.delete();
                            }
                        }
                    });
                }

                else if (username.equals(postOwner)) {
                    System.out.println("THIS IS AN USER POST DELETE");
                    //db.collection("Comments").

                    db.collection("Posts").whereEqualTo("qrcode_hash", qrHash).whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                DocumentReference documentReference = document.getReference();
                                documentReference.delete();
                            }
                        }
                    });

                    db.collection("Users").document(postOwner).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("scanned_qrcodes", FieldValue.arrayRemove(qrHash));
                            db.collection("Users").document(postOwner).update(map);

                            getParentFragmentManager().popBackStackImmediate();
                            requireActivity().getViewModelStore().clear();
                            Toast.makeText(activity.getApplicationContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT).show();

                        }
                    });

                    db.collection("ScoringQRCodes").document(qrHash).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("scanned_by", FieldValue.arrayRemove(username));
                            db.collection("ScoringQRCodes").document(documentSnapshot.getId()).update(map);
                        }
                    });
                }


            }
        });
    }

    /**
     * Method that fetches the livedata and attaches listeners. Whenever there is a change in any of the data,
     * it gets passed on to it's respective view. For example, when the username changes, it's textview will
     * change with it.
     */
    private void setViewListeners() {
        PostInfoViewModel postInfoViewModel = new ViewModelProvider(requireActivity()).get(PostInfoViewModel.class);

        final TextView imageNotAvailableTextView = binding.imageNotAvailableText;
        postInfoViewModel.getImageNotAvailableText().observe(getViewLifecycleOwner(), imageNotAvailableTextView::setText);

        final TextView locationTextView = binding.lastLocation;
        postInfoViewModel.getGeoLocation().observe(getViewLifecycleOwner(), locationTextView::setText);

        final TextView scoreTextView = binding.scoreText;
        postInfoViewModel.getScore().observe(getViewLifecycleOwner(), scoreTextView::setText);

        final TextView scannedByTextView = binding.scannedByText;
        postInfoViewModel.getScannedByText().observe(getViewLifecycleOwner(), scannedByTextView::setText);

        // set the image every time it changes
        final ImageView imageView = binding.cameraImageHolder;
        postInfoViewModel.getImage().observe(getViewLifecycleOwner(), bitmap -> {
            Log.d(TAG, String.valueOf(bitmap));
            if (imageView.getWidth() > 0) {
                widthHeight = imageView.getWidth();
            }

            if (widthHeight > 0) {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, widthHeight, widthHeight, false));
            }
        });
    }
}