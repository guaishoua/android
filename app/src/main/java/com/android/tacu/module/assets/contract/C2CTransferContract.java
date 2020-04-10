package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;

public class C2CTransferContract {

    public interface IView extends IBaseMvpView {
        void transOutSuccess();

        void transInSuccess();

        void customerCoinByOneCoin(Double value);

        void c2cAmount();
    }

    public interface IPresenter {
        void transOut(String amount, int currencyId);

        void transIn(String amount, int currencyId);

        void customerCoinByOneCoin(int currencyId);

        void c2cAmount(int currencyId);
    }
}
