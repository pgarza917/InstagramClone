package com.example.instagramclone.fragments;

import android.graphics.PostProcessor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Post;
import com.example.instagramclone.PostParcel;
import com.example.instagramclone.PostsAdapter;
import com.example.instagramclone.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private TextView mTextViewUsername;
    private ImageView mImageViewImage;
    private TextView mTextViewDescription;
    private ImageView mImageViewProfile;
    private TextView mTextViewTime;
    private TextView mTextViewLikes;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextViewUsername = view.findViewById(R.id.textViewUsername);
        mTextViewDescription = view.findViewById(R.id.textViewDescription);
        mImageViewImage = view.findViewById(R.id.imageViewImage);
        mImageViewProfile = view.findViewById(R.id.imageViewProfile);
        mTextViewTime = view.findViewById(R.id.textViewTime);
        mTextViewLikes = view.findViewById(R.id.textViewLikes);

        // Get the passed Post object
        PostParcel post = Parcels.unwrap(getArguments().getParcelable(PostParcel.class.getSimpleName()));

        // Bind the post data to the view elements
        String username = post.getUsername();
        mTextViewUsername.setText(username);
        String description = "<b>" + username + "</b>" + " " + post.getDescription();
        mTextViewDescription.setText(Html.fromHtml(description));
        mTextViewTime.setText(post.getTimeStamp());

        // Use Glide to load post image from DB into image view
        // Also confirm that the post has a valid image in DB to load

        if(post.getPostImageUrl() != null) {
            mImageViewImage.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(post.getPostImageUrl()).into(mImageViewImage);
        } else {
            mImageViewImage.setVisibility(View.GONE);
        }

        // Use Glide again to load profile image from DB into image view
        // Confirm that the post has a valid image in DB to load

        if(post.getProfileImageUrl() != null) {
            mImageViewProfile.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(post.getProfileImageUrl()).circleCrop().into(mImageViewProfile);
        } else {
            mImageViewProfile.setVisibility(View.GONE);
        }
        // Constructing the correct like count string. Taking into account singular and plural
        // and accounting for posts with no likes, i.e. likes List will be null

        String likesAppendedWord = (post.getLikeCount() == 1) ? "like" : "likes";
        String likesCount = Integer.toString(post.getLikeCount()) + " " + likesAppendedWord;
        mTextViewLikes.setText(likesCount);

    }

    // getRelativeTimeAgo("Thu Jul 09 17:20:55 EDT 2020")
    public String getRelativeTimeAgo(String rawJsonDate) {
        String instaFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(instaFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}