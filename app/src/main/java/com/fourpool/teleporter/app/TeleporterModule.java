package com.fourpool.teleporter.app;

import android.app.Application;

import com.fourpool.teleporter.app.data.google.GooglePlacesService;
import com.fourpool.teleporter.app.fragment.HomeFragment;
import com.fourpool.teleporter.app.gson.AutoParcelAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(
        injects = {
                MainActivity.class,
                HomeFragment.class
        }
)
public class TeleporterModule {
    private final TeleporterApplication app;

    public TeleporterModule(TeleporterApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new AutoParcelAdapterFactory())
                .create();
    }

    @Provides
    @Singleton
    public GooglePlacesService provideGooglePlacesService(Gson gson) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://maps.googleapis.com")
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(request -> request.addQueryParam("key", app.getString(R.string.google_places_key)))
                .build();

        return restAdapter.create(GooglePlacesService.class);
    }
}
