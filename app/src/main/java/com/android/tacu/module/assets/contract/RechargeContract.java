package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.CoinAddressModel;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class RechargeContract {
    public interface IRechargeView extends IBaseMvpView {
        void showCoinsAddress(CoinAddressModel attachment);

        void showRechargeSuccess(String msg);
    }

    public interface IPresenter {
        void selectUserAddress(int walletType, int currentyId);

        void rechargeGrin(String txid, String amount);
    }
}
