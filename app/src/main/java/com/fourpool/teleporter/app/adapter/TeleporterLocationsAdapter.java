package com.fourpool.teleporter.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fourpool.teleporter.app.data.TeleporterLocation;

import java.util.List;

public class TeleporterLocationsAdapter extends BaseAdapter {
    private final Context context;
    private final List<TeleporterLocation> locations;

    public TeleporterLocationsAdapter(Context context, List<TeleporterLocation> locations) {
        this.context = context;
        this.locations = locations;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TeleporterLocation location = (TeleporterLocation) getItem(position);
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView name = (TextView) v.findViewById(android.R.id.text1);
        name.setText(location.getName());
        return v;
    }

    public void updateLocations(List<TeleporterLocation> locations) {
        this.locations.clear();
        this.locations.addAll(locations);
        notifyDataSetChanged();
    }
}
