package com.android.tacu.base;

import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jiazhen on 2018/8/7.
 */
public class BaseMvpPresenter<V extends IMvpView> implements IMvpPresenter<V> {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private V mView;

    @Override
    public void attachView(V paramV) {
        this.mView = paramV;
    }

    @Override
    public void detachView() {
        if (isViewAttached()) {
            this.mView = null;
        }
    }

    @Override
    public void destroy() {
        detachView();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }

    @Override
    public void subscribeNetwork(Observable paramObservable, NetDisposableObserver paramObserver) {
        paramObservable.compose(ModelTransformerFactory.getBaseModelTransformer()).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(paramObserver);
        compositeDisposable.add(paramObserver);
    }

    @Override
    public void subscribeNetwork(Observable paramObservable, NetDisposableObserver paramObserver, ObservableTransformer paramTransformer) {
        paramObservable.compose(paramTransformer).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(paramObserver);
        compositeDisposable.add(paramObserver);
    }

    public IMvpView getView() {
        if (this.mView == null)
            return null;
        return this.mView;
    }

    public boolean isViewAttached() {
        return this.mView != null;
    }
}
