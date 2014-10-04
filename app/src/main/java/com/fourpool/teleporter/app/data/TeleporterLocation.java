package com.fourpool.teleporter.app.data;

import android.os.Parcelable;

import com.fourpool.teleporter.app.gson.AutoGson;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class TeleporterLocation implements Parcelable {
    public static TeleporterLocation of(String name, double lat, double lng) {
        return new AutoParcel_TeleporterLocation(name, lat, lng);
    }

    public abstract String name();

    public abstract double lat();

    public abstract double lng();
}
