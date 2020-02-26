package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcManageBuySellDetailContract {

    public interface IDetailView extends IBaseMvpView {
        void orderListOne(boolean isShowView, boolean isTop,OtcMarketOrderAllModel model);

        void tradeList(OtcTradeListModel model);

        void confirmOrderSuccess();

        void confirmCancelOrderSuccess();

        void currentTime(Long time);
    }

    public interface IRecordView extends IBaseMvpView {
        void ownCenter(OwnCenterModel model);

        void tradeListByOrder(OtcTradeListModel model);
    }

    public interface IPresenter {
        void orderListOne(boolean isShowView, boolean isTop, String orderId);

        void ownCenter();

        void tradeListByOrder(boolean isShowView, String orderId, Integer start, Integer size);

        void tradeList(boolean isShowView, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status);

        void confirmOrder(String orderId);

        void confirmCancelOrder(String orderId);

        void currentTime();
    }
}
