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
import com.android.tacu.module.assets.model.TransInfoCoinModal;

/**
 * set
 * Created by jiazhen on 2018/8/20.
 */
public class AssetsPresenter extends BaseMvpPresenter implements AssetsContract.IAssetsPresenter {

    @Override
    public void getAssetDetails(boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).getAsAsets(), new NetDisposableObserver<BaseModel<AssetDetailsModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<AssetDetailsModel> model) {
                AssetsContract.IAssetsView iAssetsView = (AssetsContract.IAssetsView) getView();
                iAssetsView.showContent(model.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                AssetsContract.IAssetsView view = (AssetsContract.IAssetsView) getView();
                view.showContentError();
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
    public void transInfoCoin() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UUEX, Api.class).TransInfoCoin(), new NetDisposableObserver<BaseModel<TransInfoCoinModal>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<TransInfoCoinModal> model) {
                AssetsContract.IAssetsView view = (AssetsContract.IAssetsView) getView();
                view.transInfoCoin(model.attachment);
            }
        });
    }
}
