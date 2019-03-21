package com.chengfu.android.fuplayer.demo;

import android.app.Application;

import timber.log.Timber;

public class APP extends Application {

    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
