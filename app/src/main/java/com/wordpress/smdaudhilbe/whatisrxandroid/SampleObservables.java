package com.wordpress.smdaudhilbe.whatisrxandroid;

import android.os.SystemClock;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mohammed-2284 on 02/06/15.
 */
public class SampleObservables {

    public static Observable<String> fakeApiCall(final long delay) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                SystemClock.sleep(delay);
                String fakeJson = "{\"result\": 42}";
                subscriber.onNext(fakeJson);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<String> numberStrings(int from, int to, final long delay) {
        return Observable.range(from, to).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return integer.toString();
            }
        }).doOnNext(new Action1<String>() {
            @Override
            public void call(String s) {
                SystemClock.sleep(delay);
            }
        }).subscribeOn(Schedulers.newThread());
    }
}