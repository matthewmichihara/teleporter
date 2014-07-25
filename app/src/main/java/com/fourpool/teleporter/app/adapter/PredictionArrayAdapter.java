package com.fourpool.teleporter.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fourpool.teleporter.app.data.google.Prediction;

import java.util.List;

public class PredictionArrayAdapter extends ArrayAdapter<Prediction> implements Filterable {

    public PredictionArrayAdapter(Context context, List<Prediction> objects) {
        super(context, android.R.layout.simple_dropdown_item_1line, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        Prediction prediction = getItem(position);
        TextView title = (TextView) convertView.findViewById(android.R.id.text1);
        title.setText(prediction.description());

        return convertView;
    }
}
