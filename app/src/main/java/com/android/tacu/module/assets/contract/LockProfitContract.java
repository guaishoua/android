package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.CashChexAndRateModel;
import com.android.tacu.module.assets.model.LockChexAmountModel;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class LockProfitContract {

    public interface IView extends IBaseMvpView {
        void setLockChexAmount(LockChexAmountModel model);

        void setCashChexAndRate(CashChexAndRateModel model);

        void lockChexSuccess();
    }

    public interface IPresenter {
        void getLockChexAmount(boolean isShow);

        void getCashChexAndRate(boolean isShow);

        void lockChex(String num, int dateVal, String fdPwd);
    }
}
