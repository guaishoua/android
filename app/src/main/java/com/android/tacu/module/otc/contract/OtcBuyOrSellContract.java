package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;

import java.util.List;

public class OtcBuyOrSellContract {

    public interface IView extends IBaseMvpView {
        void selectBank(List<PayInfoModel> list);

        void uselectUserInfo(Integer type, String imageUrl);

        void otcAmount(OtcAmountModel model);

        void orderListOne(OtcMarketOrderAllModel model);
    }

    public interface IPresenter {
        void selectBank();

        void uselectUserInfo(Integer type, String headImg);

        void otcAmount(int currencyId);

        void orderListOne(Integer orderId);
    }
}
