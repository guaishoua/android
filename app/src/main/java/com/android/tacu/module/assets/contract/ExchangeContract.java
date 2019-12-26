package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.ExchangeModel;
import com.android.tacu.module.assets.model.ExchangeShowModel;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class ExchangeContract {

    public interface IExchangeView extends IBaseMvpView {
        void getUSTPrice(ExchangeModel model);

        void confirmMessage(ExchangeShowModel model);

        void exchangeCoinUSDTToCode();

        void customerCoinByOneCoin(AmountModel model);
    }

    public interface IPresenter {
        void customerCoinByOneCoin(int currencyId);

        void getUSDT(int currencyId);

        void confirmMessage(String fdPwd, String CurrencyId, String amount);

        void exchangeCoinUSDTToCode(String fdPwd, String CurrencyId, String amount);
    }

}
