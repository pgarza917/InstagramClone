package com.example.instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 *  ParseApplication is a subclass of {@link Application}. It handles the
 *  setup from the client-side to allow successful communication between
 *  the current InstagramClone app client and the Parse database
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Registering our Parse model
        ParseObject.registerSubclass(Post.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("pablo-parstagram") // should correspond to APP_ID env variable
                .clientKey("pablogarza917parstagramfbu2020")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://pablo-parstagram.herokuapp.com/parse/").build());
    }
}
