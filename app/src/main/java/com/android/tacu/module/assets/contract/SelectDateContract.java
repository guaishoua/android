package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.CoinListModel;

import java.util.List;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class SelectDateContract {
    public interface IselectDateView extends IBaseMvpView {
        void showCoinsList(List<CoinListModel.AttachmentBean> attachment);
    }

    public interface IPresenter {
        void coins(boolean isShowLoadingView);
    }
}
