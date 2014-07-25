package com.fourpool.teleporter.app.data.google;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface GooglePlacesService {
    @GET("/maps/api/place/autocomplete/json")
    Observable<AutoCompleteResponse> autocomplete(@Query("input") String input, @Query("sensor") String sensor, @Query("location") String location, @Query("radius") String radius);

    @GET("/maps/api/place/details/json")
    Observable<DetailsResponse> details(@Query("reference") String reference);
}
