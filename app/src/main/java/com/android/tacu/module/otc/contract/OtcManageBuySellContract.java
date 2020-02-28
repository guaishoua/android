package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcPublishParam;
import com.android.tacu.module.otc.model.OtcSelectFeeModel;

import java.util.List;

public class OtcManageBuySellContract {

    public interface IView extends IBaseMvpView {
        void selectBank(List<PayInfoModel> list);
    }

    public interface IChildView extends IBaseMvpView {
        void selectFee(OtcSelectFeeModel model);

        void BondAccount(OtcAmountModel model);

        void orderSuccess();
    }

    public interface IPresenter {
        void selectBank();

        void selectFee(Integer currencyId);

        void BondAccount(int currencyId);

        void order(OtcPublishParam param);
    }
}
