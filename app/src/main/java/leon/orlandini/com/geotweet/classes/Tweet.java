package leon.orlandini.com.geotweet.classes;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Tweet
 *
 * @author Nicolas Orlandini
 * @version 2017.0.4
 *
 * Date de création : 27/01/2017
 * Dernière modification : 22/01/2017
 * Modifié par : Nicolas Orlandini
 */

public class Tweet {

    private String username;
    private String message;
    private String image_url;
    private LatLng position;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public LatLng getPosition() { return position; }
    public void setPosition(LatLng position) { this.position = position; }

    public Tweet(String username, String message, String url) {
        this.username = username;
        this.message = message;
        this.image_url = url;
    }
}
