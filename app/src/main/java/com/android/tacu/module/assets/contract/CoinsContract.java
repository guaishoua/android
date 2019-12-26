package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.SelectTakeCoinAddressModel;

/**
 * Created by xiaohong on 2018/8/21.
 */

public class CoinsContract {

    public interface ITakeCoinView extends IBaseMvpView {
        void showCoinListAddress(SelectTakeCoinAddressModel attachment);

        void success();

        void delCoinAddress();

        void takeCoinSuccess();

        void addressSuccess();
    }

    public interface IRechargePresenter {
        void selectTakeCoin(int currentyId);

        void updateCoinAddress(int currentyId, String walletAddressId);

        void emailTakeCoin(int type);

        void takeCoin(String actionId, String address, String amount, int currentyId, String fdPwd, String note, String emailCode, String gAuth, String msgCode);

        void insertTakeAddress(String address, int currentyId, String note);
    }
}
