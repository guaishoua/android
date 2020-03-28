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
        void selectBondFreerate(OtcSelectFeeModel model);

        void orderSuccess();
    }

    public interface IPresenter {
        void selectBank();

        void selectBondFreerate(boolean isShowView);

        void order(OtcPublishParam param);
    }
}
