package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.instagramclone.LoginActivity;
import com.example.instagramclone.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    private RecyclerView mRecyclerViewPosts;
    protected SwipeRefreshLayout mSwipeContainer;
    protected PostsAdapter mAdapter;
    protected List<Post> mAllPosts;
    private ImageView mImageViewProfileImage;
    private TextView mTextViewUsername;
    private Button mButtonLogout;

    // Required empty constructor
    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        mImageViewProfileImage = view.findViewById(R.id.imageViewProfileImage);
        mTextViewUsername = view.findViewById(R.id.textViewUsername);
        mButtonLogout = view.findViewById(R.id.buttonLogout);

        mSwipeContainer = view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Recycler View steps:
        // 0. Create layout for one row in the list
        // 1. Create the adapter
        mAllPosts = new ArrayList<>();
        mAdapter = new PostsAdapter(getContext(), mAllPosts);
        // 2. Create the data source
        // 3. Set the adapter on the Recycler View
        mRecyclerViewPosts.setAdapter(mAdapter);
        // 4. Set the layout manager on the Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewPosts.setLayoutManager(layoutManager);

        queryPosts();

        String username = ParseUser.getCurrentUser().getUsername();
        mTextViewUsername.setText(username);



        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    protected void queryPosts() {
        // Specify which class to query from Parse DB
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.orderByDescending(Post.KEY_CREATED);
        // Using findInBackground to pull all Posts from DB
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // The ParseException will not be null if error with populating List with Post objects
                // went wrong with query
                if(e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // The ParseException will be null if List was populated successfully from query
                for(Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                mAdapter.clear();
                // Update the posts data set and notify the adapter of change
                mAdapter.addAll(posts);
                // Now we call setRefreshing(false) to signal refresh has finished
                mSwipeContainer.setRefreshing(false);

                ParseFile profileImage = mAllPosts.get(0).getUser().getParseFile("profileImage");
                if(profileImage != null) {
                    mImageViewProfileImage.setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(mImageViewProfileImage);
                } else {
                    mImageViewProfileImage.setVisibility(View.GONE);
                }
            }
        });
    }
}
