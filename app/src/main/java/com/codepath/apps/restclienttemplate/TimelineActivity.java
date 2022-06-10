 package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.apis.TwitterApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

 public class TimelineActivity extends AppCompatActivity {

     public static final String TAG = "TimeLineActivity";
     private SwipeRefreshLayout swipeContainer;
     private final int REQUEST_CODE = 20;
     TwitterClient client;
     RecyclerView rvTweets;
     List<Tweet> tweets;
     TweetsAdapter adapter;
     private Button button;
     private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        //Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager lln = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(lln);
        rvTweets.setAdapter(adapter);
        client = TwitterApp.getRestClient(this);
        populateHomeTimeLine(null);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //tweets.clear();
                populateHomeTimeLine(null);
                //fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        button = (Button) findViewById(R.id.logoutbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutButton();
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(lln) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                //loadNextDataFromApi(page);
                Tweet lastTweetBeingDisplayed = tweets.get(tweets.size()-1);
                String maxId = lastTweetBeingDisplayed.id;
                populateHomeTimeLine(maxId);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }

     void onLogoutButton() {
         // forget who's logged in
         TwitterApp.getRestClient(this).clearAccessToken();

         // navigate backwards to Login screen
         Intent i = new Intent(this, LoginActivity.class);
         i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
         i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
         startActivity(i);
     }


     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.compose){
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
         return super.onOptionsItemSelected(item);
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
         }
         super.onActivityResult(REQUEST_CODE, resultCode, data);
     }

     private void populateHomeTimeLine(String maxId) {
         client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
             @Override
             public void onSuccess(int statusCode, Headers headers, JSON json) {
                 Log.i(TAG, "onSuccess!");
                 JSONArray jsonArray = json.jsonArray;
                 try {
                     tweets.addAll(Tweet.fromJsonArray(jsonArray));
                     //List<Tweet> networkTweet = Tweet.fromJsonArray(jsonArray);
                     //adapter.addAll(networkTweet);

                     adapter.notifyDataSetChanged();
                     swipeContainer.setRefreshing(false); // hide the loading spinner
                 } catch (JSONException e) {
                     Log.e(TAG, "Json Exception", e);
                 }
             }

             @Override
             public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                 Log.e(TAG, "onFailure!"+ response, throwable);
             }
         });
     }
 }