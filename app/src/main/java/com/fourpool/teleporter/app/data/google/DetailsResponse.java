package com.fourpool.teleporter.app.data.google;

import android.os.Parcelable;

import com.fourpool.teleporter.app.gson.AutoGson;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class DetailsResponse implements Parcelable {
    public static DetailsResponse of(String status, Result result) {
        return new AutoParcel_DetailsResponse(status, result);
    }

    public abstract String status();

    public abstract Result result();
}

