package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;

public class OtcManageContract {

    public interface IView extends IBaseMvpView {
        void ownCenter(OwnCenterModel model);

        void merchantoff();

        void merchanton();
    }

    public interface IChildView extends IBaseMvpView {
        void orderListOwn(OtcMarketOrderListModel model);

        void cancelOrderSuccess();

        void unshow();

        void show();
    }

    public interface IPresenter {
        void ownCenter();

        void orderListOwn(boolean isShowView, Integer start, Integer size, Integer buyorsell);

        void cancelOrder(String orderId);

        void merchantoff();

        void merchanton();

        void unshow(String orderId);

        void show(String orderId);
    }
}
