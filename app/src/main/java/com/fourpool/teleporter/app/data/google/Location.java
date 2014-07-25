package com.fourpool.teleporter.app.data.google;

import android.os.Parcelable;

import com.fourpool.teleporter.app.gson.AutoGson;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class Location implements Parcelable {
    public static Location of(double lat, double lng) {
        return new AutoParcel_Location(lat, lng);
    }

    public abstract double lat();

    public abstract double lng();
}
