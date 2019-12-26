package com.android.tacu.module.transaction.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.transaction.model.ShowOrderListModel;
import com.android.tacu.module.transaction.model.ShowTradeListModel;

/**
 * Created by jiazhen on 2018/10/13.
 */
public class TradeRecordManageContract {

    public interface IView extends IBaseMvpView {
        void showOrderList(ShowOrderListModel model);

        void showOrderListFail();

        void showTradeList(ShowTradeListModel model);

        void showTradeListFail();

        void cancelOrder();

        void cancelOrderList();
    }

    public interface IPresenter {
        void showOrderList(int start, int size, String status, int buyOrSell, Integer currencyId, Integer baseCurrencyId);

        void showTradeList(int start, int size, int buyOrSell, Integer currencyId, Integer baseCurrencyId);

        void cancel(String orderNo, String fdPassword);

        void cancelList(String orderNo, String fdPassword, String selectedParams);
    }
}
