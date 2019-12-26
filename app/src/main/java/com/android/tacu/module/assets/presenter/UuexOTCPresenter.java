package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.UuexOTCContract;
import com.android.tacu.module.assets.model.AmountModel;

public class UuexOTCPresenter extends BaseMvpPresenter implements UuexOTCContract.IPresenter {
    @Override
    public void transOut(String amount, int currencyId, String fdPassword, String sAuthCode, String gAuthCode) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UUEX, Api.class).transOut(amount, currencyId, 13, fdPassword, sAuthCode, gAuthCode), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                UuexOTCContract.IView view = (UuexOTCContract.IView) getView();
                view.transOutSuccess();
            }
        });
    }

    @Override
    public void transIn(String amount, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UUEX, Api.class).transIn(amount, currencyId, 12), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                UuexOTCContract.IView view = (UuexOTCContract.IView) getView();
                view.transInSuccess();
            }
        });
    }

    @Override
    public void coinNum(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UUEX, Api.class).coinNum(currencyId), new NetDisposableObserver<BaseModel<String>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<String> model) {
                UuexOTCContract.IView view = (UuexOTCContract.IView) getView();
                view.coinNum(model.attachment);
            }
        });
    }

    @Override
    public void customerCoinByOneCoin(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<AmountModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(AmountModel o) {
                UuexOTCContract.IView view = (UuexOTCContract.IView) getView();
                view.customerCoinByOneCoin(o);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void emailTakeCoin(int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).emailTakeCoin(type), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel coinsAddress) {
                UuexOTCContract.IView view = (UuexOTCContract.IView) getView();
                view.success();
            }
        });
    }
}
