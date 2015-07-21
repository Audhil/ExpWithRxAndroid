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

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by mohammed-2284 on 02/06/15.
 */

/**
 * Problem:
 * You have a data source (where that data is potentially expensive to obtain), and you want to
 * emit this data into a fragment. However, you want to gracefully deal with rotation changes and
 * not lose any data already emitted.
 * <p/>
 * Solution:
 * Combine {@link android.app.Fragment#setRetainInstance(boolean)} with
 * {@link rx.android.schedulers.AndroidSchedulers#mainThread()} and {@link rx.Observable#cache()}
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("UIBinding");
        setContentView(R.layout.activity_second);
        Toast.makeText(getApplicationContext(),"audhil : activity",Toast.LENGTH_SHORT).show();
    }

    //  Fragment
    public static class RetainFragment extends Fragment {

        private Button button;
        Func1<String, String> PARSE_JSON = new Func1<String, String>() {

            @Override
            public String call(String json) {
                try {
                    JSONObject jObj = new JSONObject(json);
                    return String.valueOf(jObj.getInt("result"));
                } catch (JSONException e) {
                    System.out.println("PARSE_JSON : exception : " + e.toString());
                    throw new RuntimeException(e);
                }
            }
        };
        private Observable<String> observableIs;
        Subscription subscriptionIs = Subscriptions.empty();

        public RetainFragment() {
            setRetainInstance(true);
            System.out.println("RetainFragment : Constructor()");
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getActivity().setProgressBarIndeterminateVisibility(true);
            return inflater.inflate(R.layout.activity_main, null, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            button = (Button) getView().findViewById(R.id.buttonIs);

            button.setOnClickListener(btnClickListener);
        }

        @Override
        public void onResume() {
            super.onResume();
            subscribeNow();
        }

        @Override
        public void onPause() {
            subscriptionIs.unsubscribe();
            super.onPause();
        }

        View.OnClickListener btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEveryThing();
            }
        };

        //  after button click event
        private void startEveryThing() {
            observableIs = SampleObservables.fakeApiCall(5000).map(PARSE_JSON).observeOn(AndroidSchedulers.mainThread()).cache();
            subscribeNow();
        }

        private void subscribeNow() {
            if (observableIs != null) {
                final TextView txtViewIs = (TextView) getView().findViewById(R.id.textViewIs);
                subscriptionIs = observableIs.map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        txtViewIs.setText(s);
                        return true;
                    }
                }).startWith(false).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        setRequestInProgress(aBoolean);
                    }
                });
            }
        }

        private void setRequestInProgress(boolean aBoolean) {
            System.out.println("setRequestInProgress : aBoolean : " + aBoolean);
            getActivity().setProgressBarIndeterminateVisibility(!aBoolean);
            button.setEnabled(aBoolean);
        }
    }
}