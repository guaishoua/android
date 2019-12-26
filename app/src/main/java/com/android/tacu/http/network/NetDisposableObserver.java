package com.android.tacu.http.network;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.exceptions.ResponseException;

import io.reactivex.observers.DisposableObserver;

public abstract class NetDisposableObserver<T> extends DisposableObserver<T> {

    private IBaseMvpView mBaseMvpView;
    private boolean isShowLoadingView;

    public NetDisposableObserver(IBaseMvpView baseMvpView) {
        this.mBaseMvpView = baseMvpView;
        this.isShowLoadingView = true;
    }

    public NetDisposableObserver(IBaseMvpView baseMvpView, boolean isShowLoadingView) {
        this.mBaseMvpView = baseMvpView;
        this.isShowLoadingView = isShowLoadingView;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (this.mBaseMvpView != null && this.isShowLoadingView) {
            this.mBaseMvpView.showLoadingView();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (this.mBaseMvpView != null) {
            this.mBaseMvpView.hideLoadingView();
            this.mBaseMvpView.hideRefreshView();
            if (throwable instanceof ResponseException) {
                ResponseException responseException = (ResponseException) throwable;
                if (responseException.status == ApiStatus.ERROR_TOKEN) {
                    this.mBaseMvpView.tokenInvalid();
                } else if (responseException.status == ApiStatus.ERROR_TOAST) {
                    this.mBaseMvpView.showToastError(responseException.message);
                }
            }
        }
    }

    @Override
    public void onComplete() {
        if (this.mBaseMvpView != null) {
            this.mBaseMvpView.hideLoadingView();
            this.mBaseMvpView.hideRefreshView();
        }
    }
}
