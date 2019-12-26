package com.android.tacu.module.main.contract;

import com.android.tacu.base.IBaseMvpView;

/**
 * Created by jiazhen on 2018/12/5.
 */
public class ZXingLoginContract {

    public interface IView extends IBaseMvpView {
        void sendZxingSuccess();
    }

    public interface IPresenter {
        void sendZxing(String uuid);
    }
}
