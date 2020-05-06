package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;

public class OtcOrderDetailContract {

    public interface IView extends IBaseMvpView {
        void selectTradeOne(boolean isFirst, OtcTradeModel model);

        void userBaseInfo(OtcMarketInfoModel model);

        void currentTime(Long time);

        void selectPayInfoById(PayInfoModel model);

        void uselectUserInfo(String imageUrl);

        void uselectUserInfoArbitration(int type, String imageUrl);

        void confirmOrderSuccess();

        void confirmCancelOrderSuccess();

        void payOrderSuccess();

        void payCancelOrderSuccess();

        void finishOrderSuccess();
    }

    public interface IAView extends IBaseMvpView {
        void getOssSetting(AuthOssModel model);

        void arbitrationOrderSuccess();

        void beArbitrationOrderSuccess();
    }

    public interface IPresenter {
        void selectTradeOne(boolean isShowView, boolean isFirst, String orderNo);

        // 1=买 2=卖
        void userBaseInfo(Integer queryUid);

        void currentTime();

        void selectPayInfoById(String id);

        void uselectUserInfo(String headImg);

        //type 1=仲裁图片 2=申诉图片
        void uselectUserInfoArbitration(int type, String headImg);

        void getOssSetting();

        void confirmOrder(String orderId);

        void confirmCancelOrder(String orderId);

        void payOrder(String orderId, String payImg);

        void payCancelOrder(String orderId);

        void finishOrder(String orderId, String fdPassword);

        void arbitrationOrder(String id, String arbitrateExp, String arbitrateImg);

        void beArbitrationOrder(String id, String beArbitrateExp, String beArbitrateImg);
    }
}
