package com.android.tacu.http.network;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.exceptions.ResponseException;

import io.reactivex.observers.DisposableObserver;

public abstract class NetDisposableObserver<T> extends DisposableObserver<T> {

    private IBaseMvpView mBaseMvpView;
    private boolean isShowLoadingView;
    private boolean isToast;

    public NetDisposableObserver(IBaseMvpView baseMvpView) {
        this.mBaseMvpView = baseMvpView;
        this.isShowLoadingView = true;
        this.isToast = true;
    }

    public NetDisposableObserver(IBaseMvpView baseMvpView, boolean isShowLoadingView) {
        this.mBaseMvpView = baseMvpView;
        this.isShowLoadingView = isShowLoadingView;
        this.isToast = true;
    }

    public NetDisposableObserver(IBaseMvpView baseMvpView, boolean isShowLoadingView, boolean isToast) {
        this.mBaseMvpView = baseMvpView;
        this.isShowLoadingView = isShowLoadingView;
        this.isToast = isToast;
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
                if (responseException.type == 1) {
                    onErrorServer(responseException);

                    if (responseException.status == ApiStatus.ERROR_TOKEN) {
                        this.mBaseMvpView.tokenInvalid();
                    } else if (this.isToast) {
                        this.mBaseMvpView.showToastError(responseException.message);
                    }
                } else if (responseException.type == 0) {
                    onErrorSystem(responseException);
                }
            }
        }
    }

    @Override
    public void onComplete() {
        if (this.mBaseMvpView != null) {
            if (this.isShowLoadingView) {
                this.mBaseMvpView.hideLoadingView();
            }
            this.mBaseMvpView.hideRefreshView();
        }
    }

    /**
     * 服务端返回的错误
     * @param responseException
     */
    protected void onErrorServer(ResponseException responseException) {
    }

    /**
     * 系统的错误，比如无网络情况
     * @param responseException
     */
    protected void onErrorSystem(ResponseException responseException) {

    }
}
