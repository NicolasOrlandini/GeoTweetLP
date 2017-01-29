package leon.orlandini.com.geotweet;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import leon.orlandini.com.geotweet.classes.Tweet;

public class TweetListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        Intent intent = getIntent();
        String contenuTextbox = intent.getExtras().getString("contenuTextbox"); //on get le hashtag depuis l'activity
        //Toast.makeText(getApplicationContext(), contenuTextbox, Toast.LENGTH_LONG).show();

        //on récupère les derniers tweets via le hashtag
        final SearchTimeline timeline = new SearchTimeline.Builder()
                .query("#" + contenuTextbox)
                .untilDate(new Date()) // depuis la date du jour
                .build();

        //on récupère les tweets via le username
        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName("SinguFuret")
                .build();

        //on ajout dans la listview
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        setListAdapter(adapter);

    }
}
