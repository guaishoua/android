package com.android.tacu.base;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public interface IBaseMvpView extends IMvpView {

    void showLoadingView();

    void hideLoadingView();

    void hideRefreshView();

    void tokenInvalid();

    void showToast(String msg);

    void showToastSuccess(String msg);

    void showToastError(String msg);
}