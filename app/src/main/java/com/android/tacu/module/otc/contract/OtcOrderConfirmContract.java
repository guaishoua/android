package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcTradeModel;

public class OtcOrderConfirmContract {

    public interface IView extends IBaseMvpView {
        void confirmOrderSuccess();

        void confirmCancelOrderSuccess();

        void selectTradeOne(OtcTradeModel model);

        void userBaseInfo(OtcMarketInfoModel model);

        void currentTime(Long time);

        void OtcAccount(OtcAmountModel model);

        void orderListOne(OtcMarketOrderAllModel model);
    }

    public interface IPresenter {
        void confirmOrder(String orderId);

        void confirmCancelOrder(String orderId);

        void selectTradeOne(String orderNo);

        void userBaseInfo(Integer queryUid);

        void currentTime();

        void OtcAccount(Integer currencyId);

        void orderListOne(String orderId);
    }
}
