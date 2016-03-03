package com.example.ronan.final_year_project;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;

/**
 * Created by Ronan on 03/03/2016.
 */
public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.i(TAG, "Created");
        // [Optional] Power your app with Local Datastore. For more info, go to
        // https://parse.com/docs/android/guide#local-datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        super.onCreate();
    }
}
