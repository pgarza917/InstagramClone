package com.example.instagramclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

/**
 *  ProfileFragment is a subclass of {@link Fragment}. It handles the
 *  functionality of the screen users go to (using the bottom navigation
 *  bar) view details about their "Instagram" profile. Specifically, this
 *  class handles the following features:
 *      - Displaying the current user's profile picture, number of posts
 *      they've submitted, and username via a Parse database query
 *      - Allowing users to logout by tapping on a button
 *      - Displaying all the posts the current user has submitted and allowing
 *      the current user to interact with their posts by setting up the
 *      Recycler View in which these posts will be shown
 *      - Allowing users to swipe from the top of the Recycler view that
 *      displays their posts to show the most-updated details about their
 *      posts, e.g. likes, by re-querying the Parse database
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    private RecyclerView mPostsRecyclerView;
    protected SwipeRefreshLayout mSwipeContainer;
    protected PostsAdapter mAdapter;
    protected List<Post> mAllPostsList;
    private ImageView mProfileImageView;
    private TextView mUsernameTextView;
    private Button mLogoutButton;
    private TextView mPostsCountTextView;
    private ProgressBar mProgressBar;

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

        mProgressBar = view.findViewById(R.id.progessbarLoadingProfile);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        mPostsRecyclerView = view.findViewById(R.id.recyclerViewPosts);
        mProfileImageView = view.findViewById(R.id.imageViewProfileImage);
        mUsernameTextView = view.findViewById(R.id.textViewUsername);
        mLogoutButton = view.findViewById(R.id.buttonLogout);
        mPostsCountTextView = view.findViewById(R.id.textViewPostsCount);

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
        mAllPostsList = new ArrayList<>();
        mAdapter = new PostsAdapter(getContext(), mAllPostsList);
        // 2. Create the data source
        // 3. Set the adapter on the Recycler View
        mPostsRecyclerView.setAdapter(mAdapter);
        // 4. Set the layout manager on the Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mPostsRecyclerView.setLayoutManager(layoutManager);

        queryPosts();

        String username = ParseUser.getCurrentUser().getUsername();
        mUsernameTextView.setText(username);

        ParseFile profileImage = ParseUser.getCurrentUser().getParseFile(Post.KEY_PROFILE_IMAGE);
        if(profileImage != null) {
            mProfileImageView.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(mProfileImageView);
        } else {
            mProfileImageView.setVisibility(View.GONE);
        }

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
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

                int postsCount = posts.size();
                String stringPostsCount = Integer.toString(postsCount);
                mPostsCountTextView.setText(stringPostsCount);

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}
