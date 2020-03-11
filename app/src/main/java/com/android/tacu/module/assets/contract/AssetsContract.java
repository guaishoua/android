package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.OtcAmountModel;

import java.util.List;

/**
 * Created by xiaohong on 2018/8/20.
 */

public class AssetsContract {
    public interface IAssetsView extends IBaseMvpView {
        void showContent(AssetDetailsModel attachment);

        void showContentError();

        void BondAccount(OtcAmountModel model);

        void otcAmount(OtcAmountModel model);
    }

    public interface ICurrencyView extends IBaseMvpView {
        void currencyView(List<CoinListModel.AttachmentBean> attachment);
    }

    public interface IAssetsPresenter {
        void getAssetDetails(boolean isShowLoadingView);

        void coins(boolean isShowLoadingView);

        void BondAccount(boolean isShowLoadingView, int currencyId);

        void otcAmount(boolean isShowLoadingView, int currencyId);
    }
}
