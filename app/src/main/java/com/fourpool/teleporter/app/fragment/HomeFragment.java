package com.fourpool.teleporter.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.fourpool.teleporter.app.MainActivity;
import com.fourpool.teleporter.app.R;
import com.fourpool.teleporter.app.TeleporterApplication;
import com.fourpool.teleporter.app.adapter.PredictionArrayAdapter;
import com.fourpool.teleporter.app.data.SavedTeleporterLocations;
import com.fourpool.teleporter.app.data.TeleporterLocation;
import com.fourpool.teleporter.app.data.google.AutoCompleteResponse;
import com.fourpool.teleporter.app.data.google.GooglePlacesService;
import com.fourpool.teleporter.app.data.google.Prediction;
import com.fourpool.teleporter.app.rx.Helper;
import com.fourpool.teleporter.app.service.MockLocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.subjects.PublishSubject;

import static rx.Observable.combineLatest;
import static rx.Observable.merge;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class HomeFragment extends Fragment {
    @InjectView(R.id.map) MapView mapView;
    @InjectView(R.id.search) AutoCompleteTextView searchView;

    @Inject GooglePlacesService googlePlacesService;
    @Inject SavedTeleporterLocations savedTeleporterLocations;
    Observable<TeleporterLocation> locationChosenFromDrawerStream;

    private final PublishSubject<Long> saveButtonClickStream = PublishSubject.create();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        locationChosenFromDrawerStream = ((MainActivity) activity).getLocationChosenFromDrawerStream();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TeleporterApplication app = TeleporterApplication.get(getActivity());
        app.inject(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, view);

        final GoogleMap map = initMap(getActivity(), mapView, savedInstanceState);

        final Observable<String> searchTextChangedObservable = Helper.textChanged(searchView);
        final Observable<Prediction> searchSuggestedItemClickObservable = Helper.itemClick(searchView);

        final Observable<AutoCompleteResponse> autoCompleteRequestStream
                = searchTextChangedObservable
                .filter(query -> !TextUtils.isEmpty(query))
                .flatMap(query -> googlePlacesService.autocomplete(query, "true", "0,0", "20000000"));

        final Observable<TeleporterLocation> locationChosenFromSearchStream = searchSuggestedItemClickObservable
                .flatMap(prediction -> googlePlacesService.details(prediction.reference())
                        .map(detailsResponse -> {
                            String name = detailsResponse.result().name();
                            double lat = detailsResponse.result().geometry().location().lat();
                            double lng = detailsResponse.result().geometry().location().lng();

                            return TeleporterLocation.of(name, lat, lng);
                        }));

        final Observable<TeleporterLocation> mockLocationStream = merge(locationChosenFromDrawerStream, locationChosenFromSearchStream);

        autoCompleteRequestStream.subscribeOn(io())
                .observeOn(mainThread())
                .subscribe(response -> {
                    PredictionArrayAdapter adapter = new PredictionArrayAdapter(getActivity(), response.predictions());
                    searchView.setAdapter(adapter);
                });

        merge(locationChosenFromDrawerStream, locationChosenFromSearchStream)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe(teleporterLocation -> {
                    double lat = teleporterLocation.lat();
                    double lng = teleporterLocation.lng();

                    LatLng latLng = new LatLng(lat, lng);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(17)
                            .build();

                    Location location = new Location("flp");
                    location.setLatitude(lat);
                    location.setLongitude(lng);
                    location.setAccuracy(1.0f);
                    location.setTime(System.currentTimeMillis());
                    location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

                    Intent intent = new Intent(getActivity(), MockLocationService.class);
                    intent.putExtra(MockLocationService.EXTRA_MOCK_LOCATION, teleporterLocation);
                    getActivity().startService(intent);

                    map.addMarker(new MarkerOptions().position(latLng).title(teleporterLocation.name()));
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                });

        combineLatest(saveButtonClickStream, mockLocationStream, Pair::new)
                .distinctUntilChanged(pair -> pair.first)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe(
                        pair -> {
                            TeleporterLocation location = (TeleporterLocation) pair.second;

                            List<TeleporterLocation> savedLocations = savedTeleporterLocations.getTeleporterLocations();
                            if (!savedLocations.contains(location)) {
                                savedLocations.add(location);
                            }

                            savedTeleporterLocations.setTeleporterLocations(savedLocations);
                        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mapView.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveButtonClickStream.onNext(System.currentTimeMillis());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static GoogleMap initMap(Context context, MapView mapView, Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);

        GoogleMap map = mapView.getMap();
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(context);

        return map;
    }
}
