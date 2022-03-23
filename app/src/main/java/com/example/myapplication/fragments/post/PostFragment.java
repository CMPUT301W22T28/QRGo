package com.example.myapplication.fragments.post;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentPostBinding;
import com.example.myapplication.fragments.post.listfragment.CommentsFragment;
import com.example.myapplication.fragments.post.listfragment.ScannedByFragment;
import com.example.myapplication.fragments.post.postcontent.PostInfoFragment;
import com.google.android.material.tabs.TabLayout;

public class PostFragment extends Fragment {

    private PostViewModel postViewModel;

    private FragmentPostBinding binding;

    TabLayout tabLayout;

    private static final String ARG_QR = "argQR";
    private static final String ARG_USER = "argUser";

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
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = binding.postTabLayout;

        String qrHash = getArguments().getString("argQR");
        String username = getArguments().getString("argUser");

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
}