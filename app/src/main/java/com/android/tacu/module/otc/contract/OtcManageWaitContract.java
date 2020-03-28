package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcManageWaitContract {

    public interface IView extends IBaseMvpView {
        void tradeList(OtcTradeListModel model);

        void confirmOrderSuccess();

        void confirmCancelOrderSuccess();

        void payOrderSuccess();

        void finishOrderSuccess();

        void currentTime(Long time);
    }

    public interface IPresenter {
        void tradeList(boolean isShowView, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status);

        void confirmOrder(String orderId);

        void confirmCancelOrder(String orderId);

        void payOrder(String orderId, String payImg);

        void finishOrder(String orderId, String fdPassword);

        void currentTime();
    }
}
