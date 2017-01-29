package leon.orlandini.com.geotweet;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.LinearLayout;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;
import java.util.ArrayList;
import leon.orlandini.com.geotweet.classes.Tweet;

public class TweetListActivity extends ListActivity {

    private ArrayList<Tweet> listTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        Intent intent = getIntent();
        String contenuTextbox = intent.getExtras().getString("contenuTextbox"); //on get le hashtag depuis l'activity

        final SearchTimeline timeline = new SearchTimeline.Builder()
                .query("#" + contenuTextbox)
                .build();


        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(timeline)
                .build();
        setListAdapter(adapter);

    }
}
