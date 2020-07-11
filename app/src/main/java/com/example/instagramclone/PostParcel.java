package com.example.instagramclone;

import android.text.format.DateUtils;

import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class PostParcel {

    public String mUsername;
    public String mPostImageUrl;
    public String mProfileImageUrl;
    public String mDescription;
    public String mTimeStamp;
    public int mLikeCount;

    // Empty constructor needed for Parcel
    public PostParcel() {}

    // Extracts all the necessary details from Parse User object Post and creates a PostParcel
    // object out of it
    public PostParcel postParcelFromPostParse(Post post) {
        PostParcel postParcel = new PostParcel();

        ParseFile profileImage = post.getUser().getParseFile(Post.KEY_PROFILE_IMAGE);
        postParcel.mProfileImageUrl = (profileImage == null) ? null : profileImage.getUrl();

        ParseFile image = post.getImage();
        postParcel.mPostImageUrl = (image == null) ? null : image.getUrl();

        postParcel.mUsername = post.getUser().getUsername();
        postParcel.mDescription = post.getDescription();
        postParcel.mTimeStamp = getRelativeTimeAgo(post.getCreatedAt().toString());

        List<ParseUser> likes = (ArrayList<ParseUser>) post.get("likes");
        postParcel.mLikeCount = (likes == null) ? 0 : likes.size();

        return postParcel;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPostImageUrl() {
        return mPostImageUrl;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public int getLikeCount() {
        return mLikeCount;
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
