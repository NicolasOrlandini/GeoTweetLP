package leon.orlandini.com.geotweet.classes;

/**
 * Created by Nicolas on 27/01/2017.
 */

public class Authenticated {
    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    private String access_token;
}
