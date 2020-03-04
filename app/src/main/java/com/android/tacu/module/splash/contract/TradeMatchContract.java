package com.android.tacu.module.splash.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.UploadModel;

public class TradeMatchContract {

    public interface IView extends IBaseMvpView {
        void upload(UploadModel model);
    }

    public interface IPresenter {
        void upload(String version, String channel);
    }
}
