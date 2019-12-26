package com.android.tacu.module.market.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.market.model.KLineModel;
import com.android.tacu.module.my.model.InvitedInfoModel;

/**
 * Created by jiazhen on 2018/8/23.
 */
public class MarketDetailsContract {

    public interface IView extends IBaseMvpView {
        void success(KLineModel model);

        void showInvitedInfo(InvitedInfoModel model);

        void uploadSelfSuccess();

        void uploadSelfError();
    }

    public interface IKlineView extends IBaseMvpView {
        void success(KLineModel model);
    }

    public interface IPresenter {
        void getBestexKline(String symbol, long range,int type);

        void getInvitedInfo(int uid);

        void uploadSelfList(String checkJson);
    }
}
