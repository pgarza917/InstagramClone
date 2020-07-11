package com.example.instagramclone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telecom.Call;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.fragments.DetailsFragment;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public PostsAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> posts) {
        mPosts.addAll(posts);
        notifyDataSetChanged();
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextViewUsername;
        private ImageView mImageViewImage;
        private TextView mTextViewDescription;
        private ImageView mImageViewProfile;
        private TextView mTextViewTime;
        private ImageButton mImageButtonLike;
        private TextView mTextViewLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewUsername = itemView.findViewById(R.id.textViewUsername);
            mTextViewDescription = itemView.findViewById(R.id.textViewDescription);
            mImageViewImage = itemView.findViewById(R.id.imageViewImage);
            mImageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            mTextViewTime = itemView.findViewById(R.id.textViewTime);
            mImageButtonLike = itemView.findViewById(R.id.imageButtonLike);
            mTextViewLikes = itemView.findViewById(R.id.textViewLikes);

            itemView.setOnClickListener(this);
            mImageButtonLike.setOnClickListener(this);

            itemView.setOnTouchListener(new OnDoubleTapListener(mContext) {
                @Override
                public void onDoubleTap(MotionEvent e) {
                    final int position = getAdapterPosition();
                    // Getting the current post from the List of posts using the adapter position
                    Post post = mPosts.get(position);
                    // Retrieving the array of users that have liked the current post
                    List<ParseUser> likes = (ArrayList<ParseUser>) post.get("likes");

                    List<ParseUser> users = new ArrayList<>();
                    users.add(ParseUser.getCurrentUser());

                    // If the current user clicked the like button and has already liked the post before,
                    // we want to update our Parse database by removing the user from the list of users
                    // that have liked the post. Else, if the user clicked "like" and has not previously
                    // liked the post, we want to add the user to the array of users for the current post
                    if(likes != null && listHasUser(likes, ParseUser.getCurrentUser())) {
                        post.removeAll(Post.KEY_LIKES, users);
                    } else {
                        post.addUnique(Post.KEY_LIKES, ParseUser.getCurrentUser());
                    }
                    post.saveInBackground();
                    notifyItemChanged(position);
                }
            });
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            String username = post.getUser().getUsername();
            mTextViewUsername.setText(username);
            String description = "<b>" + username + "</b>" + " " + post.getDescription();
            mTextViewDescription.setText(Html.fromHtml(description));
            String createdAt = post.getCreatedAt().toString();
            String timeStamp = getRelativeTimeAgo(createdAt);
            mTextViewTime.setText(timeStamp);

            // Use Glide to load post image from DB into image view
            // Also confirm that the post has a valid image in DB to load
            ParseFile image = post.getImage();
            if(image != null) {
                mImageViewImage.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(image.getUrl()).into(mImageViewImage);
            } else {
                mImageViewImage.setVisibility(View.GONE);
            }

            // Use Glide again to load profile image from DB into image view
            // Confirm that the post has a valid image in DB to load
            ParseFile profileImage = post.getUser().getParseFile(Post.KEY_PROFILE_IMAGE);
            if(profileImage != null) {
                mImageViewProfile.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(profileImage.getUrl()).circleCrop().into(mImageViewProfile);
            } else {
                mImageViewProfile.setVisibility(View.GONE);
            }

            // Retrieving the list of users that have liked the current Post
            List<ParseUser> likes = (ArrayList<ParseUser>) post.get("likes");
            int resource;

            // Constructing the correct like count string. Taking into account singular and plural
            // and accounting for posts with no likes, i.e. likes List will be null
            int size = (likes == null) ? 0 : likes.size();
            String likesAppendedWord = (size == 1) ? "like" : "likes";
            String likesCount = Integer.toString(size) + " " + likesAppendedWord;
            mTextViewLikes.setText(likesCount);

            // If the current user has liked the current post, change the color of the like button
            // to red. Otherwise change the color of the heart to black
            if (likes != null && listHasUser(likes, ParseUser.getCurrentUser())) {
                mImageButtonLike.setColorFilter(ContextCompat.getColor(mContext, R.color.colorRed));
                resource = R.drawable.ufi_heart_active;
            } else {
                mImageButtonLike.setColorFilter(ContextCompat.getColor(mContext, R.color.colorBlack));
                resource = R.drawable.ufi_heart;
            }

            // User Glide to load "heart" image into the like image button
            Glide.with(mContext)
                    .load(resource)
                    .fitCenter()
                    .into(mImageButtonLike);
        }

        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            // Getting the current post from the List of posts using the adapter position
            Post post = mPosts.get(position);
            if(position != RecyclerView.NO_POSITION) {
                // If anywhere on the item is tapped, launch a new details fragment
                Fragment fragment = new DetailsFragment();

                // Convert the Parse User post object into a parcelable PostParcel object
                PostParcel postParcel = new PostParcel();
                postParcel = postParcel.postParcelFromPostParse(post);

                // Bundle the necessary data to passed to the new fragment
                Bundle passedData = new Bundle();
                passedData.putParcelable(PostParcel.class.getSimpleName(), Parcels.wrap(postParcel));
                fragment.setArguments(passedData);

                ((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, fragment).commit();
            }
        }

        // Method for determining if a ParseUser is in a List of Parse Users by comparing the
        // objectIds. The contains method of the List Collection checks if it is the exact same
        // object, which causes errors on reloading the app with like functionality.
        public boolean listHasUser(List<ParseUser> users, ParseUser user) {
            int i = 0;
            String currentUserId = user.getObjectId();
            while(i < users.size()) {
                if(users.get(i).getObjectId().equals(currentUserId)) {
                    return true;
                }
                i++;
            }
            return false;
        }
    }
}
