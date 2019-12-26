package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.MoneyFlowModel;

import java.util.List;

/**
 * Created by xiaohong on 2018/8/29.
 */

public class MoneyFlowContract {
    public interface IView extends IBaseMvpView {
        void showTakeCoinList(MoneyFlowModel attachment);

        void currencyView(List<CoinListModel.AttachmentBean> attachment);
    }

    public interface IFlowPresenter {
        void coins(boolean isShowLoadingView);

        void selectTakeList(int start, int size, String type, Integer currentyId, String beginTime, String endTime, boolean isLoadding);
    }
}
