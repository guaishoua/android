package com.android.tacu.module.auth.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.main.model.OwnCenterModel;

public class AuthMerchantContract {

    public interface IView extends IBaseMvpView {
        void ownCenter(OwnCenterModel model);

        void BondAccount(OtcAmountModel model);
    }

    public interface IOrdinarView extends IBaseMvpView {
        void countTrade(Integer num);

        void getOssSetting(AuthOssModel model);

        void applyMerchantSuccess();
    }

    public interface IAuthView extends IBaseMvpView {
        void applyMerchantAuthSuccess();
    }

    public interface IPresenter {
        void ownCenter();

        void BondAccount(int currencyId);

        void countTrade();

        void getOssSetting();

        void applyMerchant(String video);

        void applyMerchantAuth();
    }
}
