package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;

public class OtcBuyOrSellContract {

    public interface IView extends IBaseMvpView {
        void otcAmount(OtcAmountModel model);

        void orderListOne(OtcMarketOrderAllModel model);
    }

    public interface IPresenter {

        void otcAmount(int currencyId);

        void orderListOne(String orderId);
    }
}
