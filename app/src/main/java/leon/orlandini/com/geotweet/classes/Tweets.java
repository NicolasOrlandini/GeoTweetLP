package leon.orlandini.com.geotweet.classes;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Singu_Admin on 29/01/2017.
 */

public class Tweets extends ArrayList<Tweet> {

    private ArrayList<Tweet> listTweet;

    public Tweets(ArrayList<Tweet> tweets) {
        listTweet = new ArrayList<Tweet>();
    }

    public ArrayList<Tweet> getListTweet() {
        return listTweet;
    }

    public void setListTweet(ArrayList<Tweet> listTweet) {
        this.listTweet = listTweet;
    }

}
