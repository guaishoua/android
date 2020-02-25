package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;

public class OtcOrderDetailContract {

    public interface IView extends IBaseMvpView {
        void selectTradeOne(boolean isFirst, OtcTradeModel model);

        void userBaseInfo(int buyOrSell, OtcMarketInfoModel model);

        void currentTime(Long time);
    }

    public interface IPresenter {
        void selectTradeOne(boolean isFirst, String orderNo);

        // 1=买 2=卖
        void userBaseInfo(int buyOrSell, Integer queryUid);

        void currentTime();
    }
}
