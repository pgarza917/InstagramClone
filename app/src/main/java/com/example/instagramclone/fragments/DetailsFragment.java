package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.PostParcel;
import com.example.instagramclone.R;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *  DetailsFragment is a subclass of {@link Fragment}. It handles much of
 *  the functionality of the screen users go to when they tap on a post
 *  within the posts screen to view more details about the post. Specifically,
 *  this class handles the following features:
 *      - Allowing users to view the profile picture of post's creator, the
 *      username of the post's creator, the uploaded picture of the post, the
 *      number of likes the post has, the post's caption, and the post's timestamp
 *      - Enlarging the post's uploaded picture for better visibility
 */
public class DetailsFragment extends Fragment {

    private TextView mUsernameTextView;
    private ImageView mPostPictureImageView;
    private TextView mDescriptionTextView;
    private ImageView mProfilePictureImageView;
    private TextView mTimestampTextView;
    private TextView mLikesCountTextView;

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

        mUsernameTextView = view.findViewById(R.id.textViewUsername);
        mDescriptionTextView = view.findViewById(R.id.textViewDescription);
        mPostPictureImageView = view.findViewById(R.id.imageViewImage);
        mProfilePictureImageView = view.findViewById(R.id.imageViewProfile);
        mTimestampTextView = view.findViewById(R.id.textViewTime);
        mLikesCountTextView = view.findViewById(R.id.textViewLikes);

        // Get the passed Post object
        PostParcel post = Parcels.unwrap(getArguments().getParcelable(PostParcel.class.getSimpleName()));

        // Bind the post data to the view elements
        String username = post.getUsername();
        mUsernameTextView.setText(username);
        String description = "<b>" + username + "</b>" + " " + post.getDescription();
        mDescriptionTextView.setText(Html.fromHtml(description));
        mTimestampTextView.setText(post.getTimeStamp());

        // Use Glide to load post image from DB into image view
        // Also confirm that the post has a valid image in DB to load

        if(post.getPostImageUrl() != null) {
            mPostPictureImageView.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(post.getPostImageUrl()).into(mPostPictureImageView);
        } else {
            mPostPictureImageView.setVisibility(View.GONE);
        }

        // Use Glide again to load profile image from DB into image view
        // Confirm that the post has a valid image in DB to load

        if(post.getProfileImageUrl() != null) {
            mProfilePictureImageView.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(post.getProfileImageUrl()).circleCrop().into(mProfilePictureImageView);
        } else {
            mProfilePictureImageView.setVisibility(View.GONE);
        }
        // Constructing the correct like count string. Taking into account singular and plural
        // and accounting for posts with no likes, i.e. likes List will be null

        String likesAppendedWord = (post.getLikeCount() == 1) ? "like" : "likes";
        String likesCount = Integer.toString(post.getLikeCount()) + " " + likesAppendedWord;
        mLikesCountTextView.setText(likesCount);

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