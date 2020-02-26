package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;

public class OtcOrderDetailContract {

    public interface IView extends IBaseMvpView {
        void selectTradeOne(boolean isFirst, OtcTradeModel model);

        void userBaseInfo(Integer buyOrSell, OtcMarketInfoModel model);

        void currentTime(Long time);

        void selectPayInfoById(PayInfoModel model);

        void uselectUserInfo(String imageUrl);

        void getOssSetting(AuthOssModel model);

        void payOrderSuccess();
    }

    public interface IPresenter {
        void selectTradeOne(boolean isShowView, boolean isFirst, String orderNo);

        // 1=买 2=卖
        void userBaseInfo(Integer buyOrSell, Integer queryUid);

        void currentTime();

        void selectPayInfoById(String id);

        void uselectUserInfo(String headImg);

        void getOssSetting();

        void payOrder(String orderId, String payImg);
    }
}
