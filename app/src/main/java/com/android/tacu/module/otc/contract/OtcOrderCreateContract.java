package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;

import java.util.List;

public class OtcOrderCreateContract {

    public interface IView extends IBaseMvpView {
        void userBaseInfo(boolean isBuy, OtcMarketInfoModel model);

        void selectBank(List<PayInfoModel> list);

        void otcTradeSuccess();
    }

    public interface IPresenter {
        void userBaseInfo(boolean isBuy, Integer queryUid);

        void selectBank();

        void otcTrade(String orderId, Integer payType, String num, String amount);
    }
}
