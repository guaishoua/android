package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;

public class OtcOrderCreateContract {

    public interface IView extends IBaseMvpView {
        void otcTradeSuccess();
    }

    public interface IPresenter {
        void otcTrade(Integer orderId, String fdPassword, Integer payType, String num, String amount);
    }
}
