package com.fourpool.teleporter.app;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class TeleporterApplication extends Application {
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(new TeleporterModule(this));
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }

    public static TeleporterApplication get(Context context) {
        return (TeleporterApplication) context.getApplicationContext();
    }
}
