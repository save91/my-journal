package com.example.saverio.myjournal.ui.common;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saverio.myjournal.R;
import com.example.saverio.myjournal.data.database.PostEntry;
import com.squareup.picasso.Picasso;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostAdapterViewHolder> {
    private final String TAG = "PostAdapter";

    private PostEntry[] mPostsData;
    private PostAdapterOnClickHandler mClickHandler;

    public interface PostAdapterOnClickHandler {
        void onClick(String param);
    }

    public PostAdapter(PostAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class PostAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mPostTextView;
        public final ImageView mPostThunbnail;

        public PostAdapterViewHolder(View view) {
            super(view);
            mPostTextView = view.findViewById(R.id.tv_post_title);
            mPostThunbnail = view.findViewById(R.id.iv_post_thundbail);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = this.getAdapterPosition();
            PostEntry post = mPostsData[position];
            mClickHandler.onClick(Integer.toString(post.getId()));
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new PostAdapterViewHolder that holds the View for each list item
     */
    @Override
    public PostAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.post_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new PostAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param position              The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(PostAdapterViewHolder postAdapterViewHolder, int position) {
        PostEntry post = mPostsData[position];

        postAdapterViewHolder.mPostTextView.setText(post.getTitle());
        Uri uri = Uri.parse(post.getThumbnailUrl());
        Log.d(TAG, "post.getThumbnailUrl(): " + post.getThumbnailUrl());
        Picasso.with(postAdapterViewHolder.mPostThunbnail.getContext()).load(uri)
                .into(postAdapterViewHolder.mPostThunbnail);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if (null == mPostsData) return 0;
        return mPostsData.length;
    }

    /**
     * This method is used to set the posts on a PostAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new PostAdapter to display it.
     *
     * @param postsData The new weather data to be displayed.
     */
    public void setPostsData(PostEntry[] postsData) {
        mPostsData = postsData;
        notifyDataSetChanged();
    }

}
