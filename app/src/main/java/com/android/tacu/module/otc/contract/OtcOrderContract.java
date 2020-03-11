package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcOrderContract {

    public interface IView extends IBaseMvpView {
        void tradeList(boolean isClean, OtcTradeListModel model);

        void confirmOrderSuccess();

        void finishOrderSuccess();

        void currentTime(Long time);
    }

    public interface IPresenter {
        void tradeList(boolean isShowView, boolean isClean, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status);

        void confirmOrder(String orderId);

        void finishOrder(String orderId, String fdPassword);

        void currentTime();
    }
}
