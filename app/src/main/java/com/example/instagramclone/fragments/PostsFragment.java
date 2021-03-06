package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.instagramclone.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 *  PostsFragment is a subclass of {@link Fragment}. It handles
 *  some functionality of the "home" screen for users, i.e. a feed of the
 *  last 20 posts submitted to the Parse database that the InstagramClone
 *  app uses. Specifically, this class handles the following features:
 *      - Querying the Parse database for the last 20 submitted posts
 *      - Displaying the retrieved, most-recent 20 posts by setting up
 *      the Recycler View in which these posts will be shown
 *      - Allowing users to swipe from the top of their screens to refresh
 *      by handling the re-querying of the database
 */
public class PostsFragment extends Fragment {

    public static final String TAG = PostsFragment.class.getSimpleName();

    private RecyclerView mPostsRecyclerView;
    protected SwipeRefreshLayout mSwipeContainer;
    protected PostsAdapter mAdapter;
    protected List<Post> mAllPostsList;
    protected ProgressBar mProgresBar;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mProgresBar= view.findViewById(R.id.progressBarLoadingPosts);
        mProgresBar.setVisibility(ProgressBar.VISIBLE);

        mPostsRecyclerView = view.findViewById(R.id.recyclerViewPosts);

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
    }

    // Creates and executes a query for all the post objects in our Parse DB
    protected void queryPosts() {
        // Specify which class to query from Parse DB
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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
                mProgresBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}