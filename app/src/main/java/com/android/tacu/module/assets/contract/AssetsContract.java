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

        void BondAccount(OtcAmountModel model);

        void otcAmount(OtcAmountModel model);

        void c2cAmount(OtcAmountModel model);
    }

    public interface IAssetsInfoView extends IBaseMvpView {
        void showContent(AssetDetailsModel attachment);

        void otcAmount(OtcAmountModel model);

        void c2cAmount(OtcAmountModel model);
    }

    public interface ICurrencyView extends IBaseMvpView {
        void currencyView(List<CoinListModel.AttachmentBean> attachment);
    }

    public interface IAssetsPresenter {
        void getAssetDetails(boolean isShowLoadingView, int type);

        void coins(boolean isShowLoadingView);

        void BondAccount(boolean isShowLoadingView, int currencyId);

        //flag 0=资产中心 1=资产信息详情
        void otcAmount(int flag, boolean isShowLoadingView, int currencyId);

        //flag 0=资产中心 1=资产信息详情
        void c2cAmount(int flag, boolean isShowLoadingView, int currencyId);
    }
}
