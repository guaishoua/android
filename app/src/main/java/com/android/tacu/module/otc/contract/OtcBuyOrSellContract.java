package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;

import java.util.List;

public class OtcBuyOrSellContract {

    public interface IView extends IBaseMvpView {
        void otcAmount(OtcAmountModel model);

        void orderListOne(OtcMarketOrderAllModel model);

        void selectBank(List<PayInfoModel> list, String num, final String amount);

        void selectMerchantBank(List<PayInfoModel> list, String num, final String amount);

        void otcTradeSuccess();
    }

    public interface IPresenter {
        void otcAmount(int currencyId);

        void orderListOne(String orderId);

        void selectBank(String num, String amount);

        void selectMerchantBank(String merchantId, String num, final String amount);

        void otcTrade(String orderId, Integer payType, String num, String amount);
    }
}
