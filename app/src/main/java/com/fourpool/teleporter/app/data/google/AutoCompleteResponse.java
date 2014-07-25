package com.fourpool.teleporter.app.data.google;


import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fourpool.teleporter.app.gson.AutoGson;

import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
@AutoGson
public abstract class AutoCompleteResponse implements Parcelable {
    public static AutoCompleteResponse of(String status, List<Prediction> predictions, @Nullable String error_message) {
        return new AutoParcel_AutoCompleteResponse(status, predictions, error_message);
    }

    public abstract String status();

    public abstract List<Prediction> predictions();

    @Nullable public abstract String error_message();
}