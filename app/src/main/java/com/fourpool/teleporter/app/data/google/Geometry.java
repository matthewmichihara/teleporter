package com.fourpool.teleporter.app.data.google;

import android.os.Parcelable;

import com.fourpool.teleporter.app.gson.AutoGson;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class Geometry implements Parcelable {
    public static Geometry of(Location location) {
        return new AutoParcel_Geometry(location);
    }

    public abstract Location location();
}
