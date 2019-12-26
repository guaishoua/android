package com.android.tacu.module.transaction.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.transaction.model.ShowTradeListModel;

public class MyDealContract {

    public interface IView extends IBaseMvpView {
        void showTradeList(ShowTradeListModel model);

        void showTradeListFail();
    }

    public interface IPresenter {
        void showTradeList(int start, int size, int buyOrSell, Integer currencyId, Integer baseCurrencyId);
    }
}
