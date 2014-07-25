package com.fourpool.teleporter.app.rx;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.fourpool.teleporter.app.data.google.Prediction;

import rx.Observable;
import rx.subjects.PublishSubject;

public final class Helper {
    public static Observable<String> textChanged(AutoCompleteTextView autoCompleteTextView) {
        final PublishSubject<String> subject = PublishSubject.create();
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                subject.onNext(s.toString());
            }
        });

        return subject;
    }

    public static Observable<Prediction> itemClick(AutoCompleteTextView autoCompleteTextView) {
        final PublishSubject<Prediction> subject = PublishSubject.create();
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                subject.onNext((Prediction) parent.getItemAtPosition(position));
            }
        });

        return subject;
    }
}
