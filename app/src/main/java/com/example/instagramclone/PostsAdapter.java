package com.example.instagramclone;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.Parse;
import com.parse.ParseFile;

import java.util.List;

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

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewUsername;
        private ImageView mImageViewImage;
        private TextView mTextViewDescription;
        private ImageView mImageViewProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewUsername = itemView.findViewById(R.id.textViewUsername);
            mTextViewDescription = itemView.findViewById(R.id.textViewDescription);
            mImageViewImage = itemView.findViewById(R.id.imageViewImage);
            mImageViewProfile = itemView.findViewById(R.id.imageViewProfile);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            String username = post.getUser().getUsername();
            mTextViewUsername.setText(username);
            String description = "<b>" + username + "</b>" + " " + post.getDescription();
            mTextViewDescription.setText(Html.fromHtml(description));

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
        }
    }
}
