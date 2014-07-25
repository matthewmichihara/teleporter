package com.fourpool.teleporter.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fourpool.teleporter.app.adapter.TeleporterLocationsAdapter;
import com.fourpool.teleporter.app.data.SavedTeleporterLocations;
import com.fourpool.teleporter.app.data.TeleporterLocation;
import com.fourpool.teleporter.app.fragment.HomeFragment;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private TeleporterLocationsAdapter adapter;

    private PublishSubject<TeleporterLocation> locationChosenFromDrawerStream = PublishSubject.create();

    @Inject
    SavedTeleporterLocations savedTeleporterLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TeleporterApplication app = TeleporterApplication.get(this);
        app.inject(this);

        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        adapter = new TeleporterLocationsAdapter(this, savedTeleporterLocations.getTeleporterLocations());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeleporterLocation location = (TeleporterLocation) parent.getItemAtPosition(position);
                locationChosenFromDrawerStream.onNext(location);
                mDrawerLayout.closeDrawers();
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        HomeFragment mHomeFragment = HomeFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.content_frame, mHomeFragment).commit();

        savedTeleporterLocations.getLocationChangedStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<TeleporterLocation>>() {
                    @Override
                    public void call(List<TeleporterLocation> teleporterLocations) {
                        adapter.updateLocations(teleporterLocations);
                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public PublishSubject<TeleporterLocation> getLocationChosenFromDrawerStream() {
        return locationChosenFromDrawerStream;
    }
}
