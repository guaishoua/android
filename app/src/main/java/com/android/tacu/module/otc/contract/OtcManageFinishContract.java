package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcManageFinishContract {

    public interface IView extends IBaseMvpView {
        void tradeListByOrder(OtcTradeListModel model);
    }

    public interface IPresenter {
        void tradeListByOrder(boolean isShowView, String orderId, Integer start, Integer size);
    }
}
