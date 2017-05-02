package com.example.jiji.love;

import android.app.Application;
import android.content.Context;

/**
 * Created by jiji on 2017/4/16.
 */

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
