package com.android.tacu.module.auth.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.auth.model.OtcSectionModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;

import java.util.List;

public class AuthMerchantContract {

    public interface IView extends IBaseMvpView {
        void ownCenter(OwnCenterModel model);

        void BondAccount(OtcAmountModel model);

        void selectOtcSection(List<OtcSectionModel> list);

        void countTrade(Integer num);

        void userBaseInfo(OtcMarketInfoModel model);
    }

    public interface IOrdinarView extends IBaseMvpView {

        void getOssSetting(AuthOssModel model);

        void applyMerchantSuccess();

        void quitMerchantSuccess();
    }

    public interface IAuthView extends IBaseMvpView {
        void applyMerchantAuthSuccess();

        void downMerchantAuthSuccess();

        void quitMerchantSuccess();
    }

    public interface IPresenter {
        void ownCenter();

        void BondAccount(int currencyId);

        void countTrade();

        void getOssSetting();

        void applyMerchant(String video);

        void applyMerchantAuth();

        void selectOtcSection();

        void downMerchantAuth();

        void quitMerchant(int type);

        void userBaseInfo(Integer queryUid);
    }
}
