package com.fourpool.teleporter.app.data.google;

import android.os.Parcelable;

import com.fourpool.teleporter.app.gson.AutoGson;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class Result implements Parcelable {
    public static Result of(Geometry geometry, String name) {
        return new AutoParcel_Result(geometry, name);
    }

    public abstract Geometry geometry();

    public abstract String name();
}