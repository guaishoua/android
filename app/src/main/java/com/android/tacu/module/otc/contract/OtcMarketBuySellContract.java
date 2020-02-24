package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;

public class OtcMarketBuySellContract {

    public interface IView extends IBaseMvpView {
        void orderList(OtcMarketOrderListModel model);
    }

    public interface IPresenter {
        void orderList(boolean isShowView, Integer type, Integer currencyId, Integer start, Integer size, Integer payType, Integer buyorsell);
    }
}
