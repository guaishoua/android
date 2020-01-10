package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.CoinListModel;

import java.util.List;

/**
 * Created by xiaohong on 2018/8/20.
 */

public class AssetsContract {
    public interface IAssetsView extends IBaseMvpView {
        void showContent(AssetDetailsModel attachment);

        void showContentError();
    }

    public interface ICurrencyView extends IBaseMvpView {
        void currencyView(List<CoinListModel.AttachmentBean> attachment);
    }

    public interface IAssetsPresenter {
        void getAssetDetails(boolean isShowLoadingView);

        void coins(boolean isShowLoadingView);
    }
}
