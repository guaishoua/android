package com.android.tacu.module.transaction.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.vip.model.VipDetailRankModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/9/30.
 */
public class TradeContract {

    public interface IView extends IBaseMvpView {
        void uploadSelfSuccess();

        void uploadSelfError();

        void buySuccess();

        void updateFdPwdSuccess();

        void updateFdPwdError();

        void selectVipDetail(List<VipDetailRankModel> list);
    }

    public interface IPresenter {
        void uploadSelfList(String checkJson);

        void order(int buyOrSell, int currencyId, String fdPassword, String num, String price, int type, int baseCurrencyId);

        void updateFdPwdEnabled(int enabled, String fdPwd);

        void selectVipDetail();
    }
}
