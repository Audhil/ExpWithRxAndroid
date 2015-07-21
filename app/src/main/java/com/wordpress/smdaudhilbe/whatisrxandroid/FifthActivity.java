package com.wordpress.smdaudhilbe.whatisrxandroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnListViewScrollEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;

/**
 * Created by mohammed-2284 on 03/06/15.
 */
public class FifthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);
    }

    //  Fragment
    public static class ListFragment extends Fragment {

        private ArrayAdapter<String> adapter;

        public ListFragment() {
            setRetainInstance(true);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.list_fragment, container, false);
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
            ListView listView = (ListView) view.findViewById(android.R.id.list);
            listView.setAdapter(adapter);

            AppObservable.bindFragment(this, SampleObservables.numberStrings(1, 100, 250)).observeOn(AndroidSchedulers.mainThread()).lift(new BindAdapter()).subscribe();
            final ProgressBar progressBar = (ProgressBar) view.findViewById(android.R.id.progress);
            AppObservable.bindFragment(this, WidgetObservable.listScrollEvents(listView))
                    .subscribe(new Action1<OnListViewScrollEvent>() {
                        @Override
                        public void call(OnListViewScrollEvent event) {
                            if (event.totalItemCount() == 0) {
                                return;
                            }
                            int progress = (int) ((100.0 * (event.firstVisibleItem() + event.visibleItemCount())) / event.totalItemCount());
                            progressBar.setProgress(progress);
                        }
                    });
            return view;
        }

        //  bindAdapter
        class BindAdapter implements Observable.Operator<String, String> {

            @Override
            public Subscriber<? super String> call(Subscriber<? super String> subscriber) {
                return new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        adapter.add(s);
                    }
                };
            }
        }
    }
}