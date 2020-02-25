package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcOrderContract {

    public interface IView extends IBaseMvpView {
        void tradeList(OtcTradeListModel model);
    }

    public interface IPresenter {
        void tradeList(boolean isShowView, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status);
    }
}
