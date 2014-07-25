package com.fourpool.teleporter.app.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class SavedTeleporterLocations {
    private static final String KEY_TELEPORTER_LOCATIONS = "teleporter_locations";

    private final PublishSubject<List<TeleporterLocation>> savedLocationsChangedStream = PublishSubject.create();

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    @Inject
    public SavedTeleporterLocations(Application context, Gson gson) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.gson = gson;
    }

    public synchronized List<TeleporterLocation> getTeleporterLocations() {
        String s = sharedPreferences.getString(KEY_TELEPORTER_LOCATIONS, "");
        List<TeleporterLocation> l = gson.fromJson(s, new TypeToken<List<TeleporterLocation>>() {
        }.getType());

        if (l == null) {
            l = new ArrayList<TeleporterLocation>();
        }

        return l;
    }

    public synchronized void setTeleporterLocations(List<TeleporterLocation> locations) {
        String s = gson.toJson(locations);
        sharedPreferences.edit().putString(KEY_TELEPORTER_LOCATIONS, s).commit();

        savedLocationsChangedStream.onNext(locations);
    }

    public Observable<List<TeleporterLocation>> getLocationChangedStream() {
        return savedLocationsChangedStream;
    }
}
