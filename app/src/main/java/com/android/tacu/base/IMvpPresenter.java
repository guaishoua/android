package com.android.tacu.base;

import com.android.tacu.http.network.NetDisposableObserver;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public interface IMvpPresenter<V extends IMvpView> {

    void attachView(V paramV);

    void detachView();

    void destroy();

    void subscribeNetwork(Observable paramObservable, NetDisposableObserver paramObserver);

    void subscribeNetwork(Observable paramObservable, NetDisposableObserver paramObserver, ObservableTransformer paramTransformer);
}