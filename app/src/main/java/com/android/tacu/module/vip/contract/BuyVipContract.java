package com.android.tacu.module.vip.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.vip.model.VipDetailModel;
import com.android.tacu.module.vip.model.VipDetailRankModel;

import java.util.List;

public class BuyVipContract {

    public interface IView extends IBaseMvpView {
        void selectVip(VipDetailModel model);

        void selectVipDetail(List<VipDetailRankModel> list);

        void customerCoinByOneCoin(Double value);
    }

    public interface IChildView extends IBaseMvpView {
        void buyVipSuccess();

        void cancleVipSuccess();
    }

    public interface IPresenter {
        void selectVip();

        void selectVipDetail();

        void buyVip(Integer type, String fdPassword);

        void customerCoinByOneCoin(int currencyId);

        void cancleVip();
    }
}
