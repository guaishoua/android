package com.android.tacu.module.transaction.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.transaction.model.ShowOrderListModel;

public class CurrentEntrustConstract {

    public interface IView extends IBaseMvpView {
        void showOrderList(ShowOrderListModel model);

        void showOrderListFail();

        void cancelOrder();

        void cancelOrderList();
    }

    public interface IPresenter {
        void showOrderList(int start, int size, Integer currencyId, Integer baseCurrencyId);

        void cancel(String orderNo, String fdPassword);

        void cancelList(String orderNo, String fdPassword, String selectedParams);
    }
}
