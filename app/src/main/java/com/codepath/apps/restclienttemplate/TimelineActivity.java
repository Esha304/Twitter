 package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

 public class TimelineActivity extends AppCompatActivity {

     public static final String TAG = "TimeLineActivity";
     TwitterClient client;
     RecyclerView rvTweets;
     List<Tweet> tweets;
     TweetsAdapter adapter;
     private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        button = (Button) findViewById(R.id.logoutbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutButton();
            }
        });

        //Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeLine();
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
            Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();
            return true;
        }
         return super.onOptionsItemSelected(item);
     }

     private void populateHomeTimeLine() {
         client.getHomeTimeline(new JsonHttpResponseHandler() {
             @Override
             public void onSuccess(int statusCode, Headers headers, JSON json) {
                 Log.i(TAG, "onSuccess!");
                 JSONArray jsonArray = json.jsonArray;
                 try {
                     tweets.addAll(Tweet.fromJsonArray(jsonArray));
                     adapter.notifyDataSetChanged();
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