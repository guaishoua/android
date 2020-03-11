package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;
import com.android.tacu.module.otc.model.SelectStatusModel;

public class OtcMarketBuySellContract {

    public interface IView extends IBaseMvpView {
        void orderList(OtcMarketOrderListModel model);

        void selectStatus(SelectStatusModel model);
    }

    public interface IPresenter {
        void orderList(boolean isShowView, Integer type, Integer currencyId, Integer start, Integer size, Integer payType, Integer buyorsell);

        void selectStatus();
    }
}
