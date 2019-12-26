package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AmountModel;

public class UuexOTCContract {

    public interface IView extends IBaseMvpView {
        void transOutSuccess();

        void transInSuccess();

        void coinNum(String num);

        void customerCoinByOneCoin(AmountModel model);

        void success();
    }

    public interface IPresenter {
        void transOut(String amount, int currencyId, String fdPassword, String sAuthCode, String gAuthCode);

        void transIn(String amount, int currencyId);

        void coinNum(int currencyId);

        void customerCoinByOneCoin(int currencyId);

        void emailTakeCoin(int type);
    }
}
