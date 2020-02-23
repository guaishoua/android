package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.model.OtcPublishParam;
import com.android.tacu.module.otc.model.OtcSelectFeeModel;

public class OtcManageBuySellContract {

    public interface IView extends IBaseMvpView {
        void selectFee(OtcSelectFeeModel model);

        void BondAccount(OtcAmountModel model);

        void orderSuccess();
    }

    public interface IPresenter {
        void selectFee(Integer currencyId);

        void BondAccount(int currencyId);

        void order(OtcPublishParam param);
    }
}
