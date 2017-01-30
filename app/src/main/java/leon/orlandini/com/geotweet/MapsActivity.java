package leon.orlandini.com.geotweet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

import leon.orlandini.com.geotweet.classes.Authenticated;
import leon.orlandini.com.geotweet.classes.Tweet;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /// Constantes
    private static final String TWITTER_KEY = "zwgAc9M97DQWmaKPCVcjHNGKm";
    private static final String TWITTER_SECRET = "C8iRCONJGHX7XpDu59pd1CviOc1JsLUUPwpClLknfPRTVA4iEs";
    private final static String TWEETER_TOKEN_URL = "https://api.twitter.com/oauth2/token";
    private final static String TWEETER_SEARCH_TWEET_URL = "https://api.twitter.com/1.1/search/tweets.json?q=";
    private final static String NUMBER_TWEET = "&count=100";

    /// Variables
    private GoogleMap mMap;
    private ArrayList<Tweet> tweets = new ArrayList<>();
    private EditText editText;
    private String token;
    private String secret;
    private String contenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        token = intent.getExtras().getString("Token");
        secret = intent.getExtras().getString("Secret");

        editText = (EditText) findViewById(R.id.txtHashtag);

        Button btnValider = (Button) findViewById(R.id.btnValider);

        btnValider.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clearMarkers();
                clearTweets();
                exectuteGetTweet();
            }
        });

        Button btnTweetList = (Button) findViewById(R.id.btnToTweetList);

        btnTweetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contenu = editText.getText().toString();
                navigationToTweetList(contenu);
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        /// Désactivation de l'affichage des plans intérieurs
        mMap.setIndoorEnabled (false);

        /// Settings
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        /// Bouton de zoom et dezoom activé
        uiSettings.setIndoorLevelPickerEnabled(true);
        /// Activation de la boussole
        uiSettings.setCompassEnabled(true);
        /// La toolbar est désactivé
        uiSettings.setMapToolbarEnabled(false);
        /// Le joueur ne pourra pas incliner la carte
        uiSettings.setTiltGesturesEnabled(false);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View myContentView = getLayoutInflater().inflate( R.layout.custommarker, null);

                TextView tweet = ((TextView) myContentView.findViewById(R.id.title));
                tweet.setText(marker.getTitle());

                TextView pseudo = ((TextView) myContentView.findViewById(R.id.snippet));
                pseudo.setText(marker.getSnippet());

                ImageView image = ((ImageView) myContentView.findViewById(R.id.image));

                image.setImageDrawable(chargerImageProfil(marker.getTitle()));

                return myContentView;
            }
        });
    }

    /**
     * Récupération de l'image de profil correspondant au tweet
     * @param username nom d'utilisateur pour lequel l'image doit être récupéré
     * @return retourne le Drawable créé à partir de l'image
     */
    public Drawable chargerImageProfil(String username) {
        Log.e("nbTweets", String.valueOf(tweets.size()));
        for (Tweet tweet:tweets ) {
            if (Objects.equals(tweet.getUsername(), username)){
                try {
                    InputStream is = (InputStream) new URL(tweet.getImage_url()).getContent();

                    Log.e("ImageURL", tweet.getImage_url());
                    return Drawable.createFromStream(is, tweet.getUsername());
                } catch (Exception e) {
                    Log.e("ImageURL", "Impossible de télécharger l'image", e);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Exécution de la tâche asynchrone de récupération des tweets
     */
    public void exectuteGetTweet()
    {
        MyTask task = new MyTask();

        EditText txtHashtag = (EditText) findViewById(R.id.txtHashtag);
        task.hashtag = txtHashtag.getText().toString();
        /// démarrer la tâche asynchrone
        task.execute();
    }

    /**
     * Suppression des marqueurs de la map
     */
    private void clearMarkers() {
        mMap.clear();
    }

    /**
     * Clean de la liste de tweets
     */
    private void clearTweets() {
        tweets.clear();
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        String hashtag;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MapsActivity.this,  "", "Chargement en cours, veuillez patienter...", true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                getTweets(TWEETER_SEARCH_TWEET_URL + hashtag + NUMBER_TWEET);

            } catch (Exception e) {
                Log.e("TwitterFeedActivity", "Erreur de récupération des tweets", e);
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();

            if (tweets.isEmpty()){
                Toast.makeText(getApplicationContext(), "Personne ne parle de vous !", Toast.LENGTH_LONG).show();
            }
            else{
                for (Tweet tweet:tweets) {
                    addMarker(tweet);
                }
            }
        }

    }

    /**
     * Récupération de la liste des tweets ayant une localisation
     * @param url url de récupération des tweets
     * @throws IOException
     * @throws JSONException
     * @throws IllegalStateException
     */
    private void getTweets(String url) throws IOException, JSONException, IllegalStateException {

        String resultat;

        /// Authentification
        Authenticated auth = authentifier();

        /// Vérification du token
        if (auth != null && auth.getToken_type().equals("bearer")) {

            /// Création d'une requête
            HttpGet httpGet = new HttpGet(url);

            /// Construction de la requête et ajout du bearer token dans le header
            httpGet.setHeader("Authorization", "Bearer " + auth.getAccess_token());
            httpGet.setHeader("Content-Type", "application/json");

            /// Récupération du résultat
            resultat = getResponseBody(httpGet);

            Log.e("Resultat", resultat);

            parseTweets(resultat);
        }
    }

    /**
     * Athentification Facebook
     * @return objet Authenticated
     * @throws IOException exception
     */
    private Authenticated authentifier() throws IOException {
        // URL encode the consumer key et secret
        String urlApiKey = URLEncoder.encode(TWITTER_KEY, "UTF-8");
        String urlApiSecret = URLEncoder.encode(TWITTER_SECRET, "UTF-8");

        /// Concaténation de la consumer key et de la secret
        String combined = urlApiKey + ":" + urlApiSecret;

        /// Conversion des clés en 64 bits
        String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

        /// Requete POST, obtention du bearer token
        HttpPost httpPost = new HttpPost(TWEETER_TOKEN_URL);
        httpPost.setHeader("Authorization", "Basic " + base64Encoded);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
        String rawAuthorization = getResponseBody(httpPost);
        return jsonToAuthenticated(rawAuthorization);
    }


    /**
     * Récupération du corp de la réponse
     * @param request requête http
     * @return résultat
     * @throws IOException exception
     */
    private String getResponseBody(HttpRequestBase request) throws  IOException {
        StringBuilder sb = new StringBuilder();

        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String reason = response.getStatusLine().getReasonPhrase();

        if (statusCode == 200) {

            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();

            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            String line = null;
            while ((line = bReader.readLine()) != null) {
                sb.append(line);
            }
        } else {
            sb.append(reason);
        }
        return sb.toString();
    }


    /**
     * Parse Ahthentification
     * @param rawAuthorization
     * @return objet Authenticated
     */
    private Authenticated jsonToAuthenticated(String rawAuthorization) {
        Authenticated auth = null;
        if (rawAuthorization != null && rawAuthorization.length() > 0) {
            try {
                Gson gson = new Gson();
                auth = gson.fromJson(rawAuthorization, Authenticated.class);
            } catch (IllegalStateException ex) {
                Toast.makeText(getApplicationContext(), "Impossible de se connecter " + ex, Toast.LENGTH_LONG).show();
            }
        }
        return auth;
    }

    /**
     * Parse du Json contenant les tweets
     * @param resultat
     */
    private void parseTweets(String resultat){
        try{
            JSONObject root = new JSONObject(resultat);
            JSONArray sessions = root.getJSONArray("statuses");

            for (int i = 0; i < sessions.length(); i++) {
                JSONObject session = sessions.getJSONObject(i);

                String message = session.getString("text");
                String username  = session.getJSONObject("user").getString("screen_name");

                String image_url = session.getJSONObject("user").getString("profile_image_url");

                if(!sessions.getJSONObject(i).isNull("geo")){
                    JSONObject point = sessions.getJSONObject(i).getJSONObject("geo");

                    double latitude   = point.getJSONArray("coordinates").getDouble(0);
                    double longitude = point.getJSONArray("coordinates").getDouble(1);

                    LatLng location = new LatLng(latitude, longitude);

                    Log.e("Position", "Latitude : " + location.latitude + "/Longitude : " + location.longitude);

                    Tweet tweet = new Tweet(username, message, image_url);
                    tweet.setPosition(location);
                    Log.e("Position du tweet ", "Latitude : " + tweet.getPosition().latitude + "/Longitude : " + tweet.getPosition().longitude);
                    tweets.add(tweet);
                }
            }
        }
        catch (Exception ex) {
            Log.e("JsonError","Erreur de parse du JSON !",ex);
        }
    }

    /**
     * Ajout de marqueur
     * @param tweet Tweet
     */
    public void addMarker(Tweet tweet){

        mMap.addMarker(new MarkerOptions()
                .position(tweet.getPosition())
                .title(tweet.getUsername())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .visible(true)
                .snippet(tweet.getMessage()));
    }

    public void navigationToTweetList(String contenu){
        Intent intent = new Intent(this, TweetListActivity.class);

        //String bilboBaggings = new Gson().toJson(tweets);

        //intent.putExtra("tweetList", listT);
        intent.putExtra("contenuTextbox", contenu);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
