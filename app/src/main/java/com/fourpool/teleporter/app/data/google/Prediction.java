package com.fourpool.teleporter.app.data.google;

import android.os.Parcelable;

import com.fourpool.teleporter.app.gson.AutoGson;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class Prediction implements Parcelable {
    public static Prediction of(String description, String id, String reference) {
        return new AutoParcel_Prediction(description, id, reference);
    }

    public abstract String description();

    public abstract String id();

    public abstract String reference();

    /**
     * The autocomplete search view uses this.
     */
    @Override
    public String toString() {
        return description();
    }
}
