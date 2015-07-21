package com.wordpress.smdaudhilbe.whatisrxandroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.observables.ConnectableObservable;
import rx.subscriptions.Subscriptions;

/**
 * Created by mohammed-2284 on 02/06/15.
 */

/**
 * Problem:
 * You have a background sequence which keeps emitting items (either a limited or unlimited number)
 * and your UI component should be able to "listen in" to the sequence, i.e. it's okay to miss
 * in-flight items when going e.g. through a screen rotation or being otherwise detached from the
 * screen for a limited period of time. (Another example is a "page out" in a fragment ViewPager.)
 * <p/>
 * This is useful if you need behavior that mimics event buses. Think of a publishing
 * Observable as a channel or queue on an event bus.
 * <p/>
 * Solution:
 * Combine {@link android.app.Fragment#setRetainInstance(boolean)} with
 * {@link rx.android.schedulers.AndroidSchedulers#mainThread()} and {@link rx.Observable#publish()}
 */
public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    //  Fragment
    public static class ListeningFragment extends Fragment {

        private ConnectableObservable<String> strings;
        private Subscription subscription = Subscriptions.empty();

        public ListeningFragment() {
            setRetainInstance(true);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            strings = SampleObservables.numberStrings(1, 50, 250).publish();
            strings.connect();  //  triggers the sequence
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_main, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            final TextView textView = (TextView) view.findViewById(R.id.textViewIs);

            //  re-connect to sequence
            subscription = AppObservable.bindFragment(this, strings).subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    Toast.makeText(getActivity(), "Done!!!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getActivity(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(String s) {
                    textView.setText(s);
                }
            });

            Button buttonIs = (Button) view.findViewById(R.id.buttonIs);
            buttonIs.setOnClickListener(buttonClickListener);
        }

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Bang! Bang!", Toast.LENGTH_SHORT).show();
//                strings.connect();  //  triggers the sequence
            }
        };

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            subscription.unsubscribe(); //  stop listening
        }
    }
}