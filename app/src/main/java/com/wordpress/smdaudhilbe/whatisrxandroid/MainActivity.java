package com.wordpress.smdaudhilbe.whatisrxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import rx.Observable;
import rx.Subscription;
import rx.android.lifecycle.LifecycleEvent;
import rx.android.lifecycle.LifecycleObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Problem:
 * You have a data source (where that data is potentially expensive to obtain), and you want to
 * emit this data into a fragment. However, you want to gracefully deal with rotation changes and
 * not lose any data already emitted.
 * <p/>
 * You also want your UI to update accordingly to the data being emitted.
 *
 * @author zsiegel (zsiegel87@gmail.com)
 */

public class MainActivity extends AppCompatActivity {

//    String[] stringsIs = new String[]{"one", "two"};
    private Button buttonIs;
    private Subscription subscription;

    private BehaviorSubject<LifecycleEvent> lifecycleSubject = BehaviorSubject.create();

    private Observable<LifecycleEvent> lifeCycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Observable.from(stringsIs).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                System.out.println("getIs : " + s);
//            }
//        });

        setTitle("jack and jill");
        initView();
    }

    private void initView() {
        buttonIs = (Button) findViewById(R.id.buttonIs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
        subscription = LifecycleObservable.bindActivityLifecycle(lifeCycle(), ViewObservable.clicks(buttonIs)).subscribe(new Action1<OnClickEvent>() {
            @Override
            public void call(OnClickEvent onClickEvent) {
                Toast.makeText(getApplicationContext(), "bindActivityLifecycle Clicked!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop : isSubscription UnSubscribed : " + subscription.isUnsubscribed());
        subscription.unsubscribe();
        System.out.println("onStop : isSubscription UnSubscribed : " + subscription.isUnsubscribed());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy : isSubscription UnSubscribed : " + subscription.isUnsubscribed());
    }

//    not at all working
//    @fromXML
//    public void buttonClicked(View view) {
//        Toast.makeText(getApplicationContext(), "buttonClicked!!", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}