package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.ChargeModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.TakeCoinListModel;

import java.util.List;


/**
 * Created by xiaohong on 2018/8/29.
 */

public class RecordContract {
    public interface IRecordView extends IBaseMvpView {
        void showChargeCoinList(ChargeModel attachment);

        void showTakeCoinList(TakeCoinListModel attachment);

        void currencyView(List<CoinListModel.AttachmentBean> attachment);
    }

    public interface IRecordPresenter {
        void selectListByUuid(String beginTime, String endTime, int size, Integer currencyId, int start, int status, boolean isLoadding);

        void selectTakeList(String beginTime, String endTime, int size, Integer currentyId, int start, int status, boolean isLoadding);

        void coins(boolean isShowLoadingView);
    }
}

