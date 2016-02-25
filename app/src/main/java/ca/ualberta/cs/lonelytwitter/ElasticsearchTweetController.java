package ca.ualberta.cs.lonelytwitter;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by esports on 2/17/16.
 */
public class ElasticsearchTweetController {
    // have to use DroidClient because when android started used an alt version of apache http stuff
    // so have to use android library
    private static JestDroidClient client;

    //TODO: A function that gets tweets
    // static is operating on the class not the objects created
    public static ArrayList<NormalTweet> getTweets() {
        verifyConfig();
        //TODO: DO THIS
        return null;
    }

    public static class GetTweetsTask extends AsyncTask<String,Void,ArrayList<NormalTweet>> {

        @Override
        protected ArrayList<NormalTweet> doInBackground(String... params) {
            verifyConfig();

            // will eventually hold the tweets we get back from Elasticsearch
            ArrayList<NormalTweet> tweets = new ArrayList<NormalTweet>();

            //NOTE: A HUGE ASSUMPTION IS ABOUT THE BE MADE!
            // Assume that only one string is passed in.
            // If you're reusing this for search, do it however you want.
            String search_string = params[0];

            Search search = new Search.Builder(search_string).addIndex("testing").addType("tweet").build();
            try {
                SearchResult execute = client.execute(search);
                if (execute.isSucceeded()) {
                    List<NormalTweet> foundTweets = execute.getSourceAsObjectList(NormalTweet.class);
                    tweets.addAll(foundTweets);
                } else {
                    Log.i("TODO", "Search was unsuccessful; do something!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return tweets;
        }
    }

    //TODO: A function that adds a tweet
    public static void addTweet(NormalTweet tweet) {
        verifyConfig();

        Index index = new Index.Builder(tweet).index("testing").type("tweet").build();

        try {
            DocumentResult execute = client.execute(index);
            if (execute.isSucceeded()) {
                tweet.setId(execute.getId());
            } else {
                Log.e("TODO", "Our insert of tweet failed, oh no!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class AddTweetTask extends AsyncTask<NormalTweet,Void,Void> {

        @Override
        //... means there are more than one, an array
        protected Void doInBackground(NormalTweet... params) {

            verifyConfig();

            for (NormalTweet tweet : params) {
                Index index = new Index.Builder(tweet).index("testing").type("tweet").build();

                try {
                    DocumentResult execute = client.execute(index);
                    if (execute.isSucceeded()) {
                        tweet.setId(execute.getId());
                    } else {
                        Log.e("TODO", "Our insert of tweet failed, oh no!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    // If no client, add one.
    public static void verifyConfig() {
        // to make a factory have to make configs to put in factory to make stuff
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
