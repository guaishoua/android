package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.C2CTransferContract;
import com.android.tacu.module.assets.model.OtcAmountModel;

public class C2CTransferPresenter extends BaseMvpPresenter implements C2CTransferContract.IPresenter {
    @Override
    public void transOut(String amount, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.C2C, Api.class).C2cToCC(amount, currencyId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                C2CTransferContract.IView view = (C2CTransferContract.IView) getView();
                view.transOutSuccess();
            }
        });
    }

    @Override
    public void transIn(String amount, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.C2C, Api.class).CcToC2c(amount, currencyId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                C2CTransferContract.IView view = (C2CTransferContract.IView) getView();
                view.transInSuccess();
            }
        });
    }

    @Override
    public void customerCoinByOneCoin(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<BaseModel<Double>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<Double> o) {
                C2CTransferContract.IView view = (C2CTransferContract.IView) getView();
                view.customerCoinByOneCoin(o.attachment);
            }
        });
    }

    @Override
    public void c2cAmount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.C2C, Api.class).C2cAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                C2CTransferContract.IView view = (C2CTransferContract.IView) getView();
                view.c2cAmount(o.attachment);
            }
        });
    }
}
