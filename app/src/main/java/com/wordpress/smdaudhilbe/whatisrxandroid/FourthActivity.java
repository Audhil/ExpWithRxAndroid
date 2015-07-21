package com.wordpress.smdaudhilbe.whatisrxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import static rx.android.app.AppObservable.bindActivity;

/**
 * Created by mohammed-2284 on 03/06/15.
 */

/**
 * Activity that binds to a counting sequence and is able to listen in and out to that
 * sequence by pressing a toggle button. The button disables itself once the sequence
 * finishes.
 */
public class FourthActivity extends AppCompatActivity implements Observer<String> {

    private TextView txtView;
    private Observable<String> source;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        txtView = (TextView) findViewById(android.R.id.text1);

        // in a production app, you would use dependency injection, fragments, or other
        // means to preserve the observable, but this will suffice here
        source = (Observable<String>) getLastCustomNonConfigurationInstance();

        if (source == null) {
            source = SampleObservables.numberStrings(1, 100, 200).publish();
            ((ConnectableObservable) source).connect();
        }
        subscribeToSequence();
    }

    private void subscribeToSequence() {
        subscription = bindActivity(this, source).subscribe(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return source;
    }

    @fromXML
    public void onSequenceToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            subscription.unsubscribe();
        } else {
            subscribeToSequence();
        }
    }

    @Override
    public void onCompleted() {
        TextView button = (TextView) findViewById(R.id.toggle_button);
        button.setText("Completed");
        button.setEnabled(false);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(String s) {
        txtView.setText(s);
    }
}