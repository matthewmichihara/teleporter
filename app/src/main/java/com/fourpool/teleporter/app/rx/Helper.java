package com.fourpool.teleporter.app.rx;

import android.widget.AutoCompleteTextView;

import com.fourpool.teleporter.app.data.google.Prediction;

import rx.Observable;
import rx.subjects.PublishSubject;

public final class Helper {
    public static Observable<Prediction> itemClicks(AutoCompleteTextView autoCompleteTextView) {
        final PublishSubject<Prediction> subject = PublishSubject.create();

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> subject.onNext((Prediction) parent.getItemAtPosition(position)));

        return subject;
    }
}
