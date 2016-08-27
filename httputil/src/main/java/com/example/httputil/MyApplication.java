package com.example.httputil;

import android.app.Application;
import android.content.Context;

/**
 * Created by LeiYang on 2016/8/27 0027.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }

}
