package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.OtcAmountModel;

public class OTCTransferContract {

    public interface IView extends IBaseMvpView {
        void transOutSuccess();

        void transInSuccess();

        void customerCoinByOneCoin(AmountModel model);

        void otcAmount(OtcAmountModel model);
    }

    public interface IPresenter {
        void transOut(String amount, int currencyId);

        void transIn(String amount, int currencyId);

        void customerCoinByOneCoin(int currencyId);

        void otcAmount(int currencyId);
    }
}
