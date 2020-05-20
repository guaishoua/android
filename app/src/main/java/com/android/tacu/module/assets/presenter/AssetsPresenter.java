package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.AssetsContract;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.OtcAmountModel;

/**
 * set
 * Created by jiazhen on 2018/8/20.
 */
public class AssetsPresenter extends BaseMvpPresenter implements AssetsContract.IAssetsPresenter {

    /**
     * @param isShowLoadingView
     * @param type              1代表AssetsFragment 2代表AssetsInfoActivity
     */
    @Override
    public void getAssetDetails(boolean isShowLoadingView, final int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).getAsAsets(), new NetDisposableObserver<BaseModel<AssetDetailsModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<AssetDetailsModel> model) {
                if (type == 1) {
                    AssetsContract.IAssetsView iAssetsView = (AssetsContract.IAssetsView) getView();
                    iAssetsView.showContent(model.attachment);
                } else if (type == 2) {
                    AssetsContract.IAssetsInfoView iAssetsInfoView = (AssetsContract.IAssetsInfoView) getView();
                    iAssetsInfoView.showContent(model.attachment);
                }
            }
        });
    }

    @Override
    public void coins(boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).coins(), new NetDisposableObserver<CoinListModel>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(CoinListModel coinsList) {
                AssetsContract.ICurrencyView view = (AssetsContract.ICurrencyView) getView();
                view.currencyView(coinsList.attachment);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void BondAccount(boolean isShowLoadingView, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).BondAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                AssetsContract.IAssetsView view = (AssetsContract.IAssetsView) getView();
                view.BondAccount(o.attachment);
            }
        });
    }

    @Override  //flag 0=资产中心 1=资产信息详情
    public void otcAmount(final int flag, boolean isShowLoadingView, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                if (flag == 0) {
                    AssetsContract.IAssetsView view = (AssetsContract.IAssetsView) getView();
                    view.otcAmount(o.attachment);
                } else if (flag == 1) {
                    AssetsContract.IAssetsInfoView view = (AssetsContract.IAssetsInfoView) getView();
                    view.otcAmount(o.attachment);
                }
            }
        });
    }

    @Override //flag 0=资产中心 1=资产信息详情
    public void c2cAmount(final int flag, boolean isShowLoadingView, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.C2C, Api.class).C2cAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                if (flag == 0) {
                    AssetsContract.IAssetsView view = (AssetsContract.IAssetsView) getView();
                    view.c2cAmount(o.attachment);
                } else if (flag == 1) {
                    AssetsContract.IAssetsInfoView view = (AssetsContract.IAssetsInfoView) getView();
                    view.c2cAmount(o.attachment);
                }
            }
        });
    }
}
