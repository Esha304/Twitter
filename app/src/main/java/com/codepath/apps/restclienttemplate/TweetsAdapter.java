package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.w3c.dom.Text;

import java.util.List;

import javax.annotation.Nonnull;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

         ImageView ivProfileImage;
         TextView tvBody;
         TextView tvScreenName;
        ImageView ivImage;
        TextView tvTime;

         public ViewHolder(@Nonnull View itemView){
             super (itemView);
             ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
             tvBody = itemView.findViewById(R.id.tvBody);
             tvScreenName = itemView.findViewById(R.id.tvScreenName);
             ivImage = itemView.findViewById(R.id.ivImage);
             tvTime = itemView.findViewById(R.id.tvTime);
         }

        public void bind(Tweet tweet) {
             tvBody.setText(tweet.body);
             tvTime.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
             tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            if(tweet.foundImage){
                Glide.with(context).load(tweet.imageUrl).into(ivImage);
                ivImage.setVisibility(View.VISIBLE);
            }
            else{
                ivImage.setVisibility(View.GONE);
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

}
