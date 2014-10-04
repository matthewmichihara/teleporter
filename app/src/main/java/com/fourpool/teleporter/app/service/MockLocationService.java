package com.fourpool.teleporter.app.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.fourpool.teleporter.app.MainActivity;
import com.fourpool.teleporter.app.R;
import com.fourpool.teleporter.app.data.TeleporterLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import hugo.weaving.DebugLog;

public class MockLocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private static final String TAG = MockLocationService.class.getSimpleName();
    public static final String EXTRA_MOCK_LOCATION = TAG + "EXTRA_MOCK_LOCATION";

    private LocationClient locationClient;

    private TeleporterLocation mockLocation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override @DebugLog
    public void onCreate() {
        super.onCreate();
        Log.e("mattm", "here");
        locationClient = new LocationClient(this, this, this);
        locationClient.connect();

        Log.e("mattm", "here2");

    }

    @Override @DebugLog
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("matm", "here 3");
        if (intent == null) return START_STICKY;
        Log.e("mattm", "here4");
        mockLocation = intent.getParcelableExtra(EXTRA_MOCK_LOCATION);

        if (locationClient.isConnected()) {
            setMockLocation(mockLocation);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        locationClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            locationClient.setMockMode(true);
            setMockLocation(mockLocation);
        } catch (SecurityException e) {
            Log.e("mattm", "security exception ", e);
            Toast.makeText(this, "Enable mock locations in developer settings", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @DebugLog
    private void setMockLocation(TeleporterLocation mockLocation) {
        Location location = createMockAndroidLocation(mockLocation);
        locationClient.setMockLocation(location);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.notification_title))
                .setOngoing(true)
                .setContentText(mockLocation.name());

        Intent intent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));

        startForeground(1, builder.build());
    }

    private static Location createMockAndroidLocation(TeleporterLocation teleporterLocation) {
        double lat = teleporterLocation.lat();
        double lng = teleporterLocation.lng();

        Location location = new Location("flp");
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setAccuracy(1.0f);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        return location;
    }
}
