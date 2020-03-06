package com.android.tacu.module.splash.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.splash.model.TradeWinListModel;

public class TradeMatchContract {

    public interface IView extends IBaseMvpView {
        void upload(UploadModel model);

        void tradeWinList(TradeWinListModel model);
    }

    public interface IPresenter {
        void upload(String version, String channel);

        void tradeWinList();
    }
}
