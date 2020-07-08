package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Post;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostsFragment extends Fragment {

    public static final String TAG = PostsFragment.class.getSimpleName();

    private RecyclerView mRecyclerViewPosts;
    protected PostsAdapter mAdapter;
    protected List<Post> mAllPosts;

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

        mRecyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);

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
                // Update the posts data set and notify the adapter of change
                mAllPosts.addAll(posts);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}