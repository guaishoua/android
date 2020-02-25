package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.PayInfoModel;

import java.util.List;

public class OtcOrderCreateContract {

    public interface IView extends IBaseMvpView {
        void selectBank(List<PayInfoModel> list);

        void otcTradeSuccess();
    }

    public interface IPresenter {
        void selectBank();

        void otcTrade(String orderId, String fdPassword, Integer payType, String num, String amount);
    }
}
