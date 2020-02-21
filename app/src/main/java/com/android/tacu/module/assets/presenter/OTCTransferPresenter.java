package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.OTCTransferContract;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.OtcAmountModel;

public class OTCTransferPresenter extends BaseMvpPresenter implements OTCTransferContract.IPresenter {
    @Override
    public void transOut(String amount, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcToCC(amount, currencyId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                OTCTransferContract.IView view = (OTCTransferContract.IView) getView();
                view.transOutSuccess();
            }
        });
    }

    @Override
    public void transIn(String amount, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).CcToOtc(amount, currencyId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                OTCTransferContract.IView view = (OTCTransferContract.IView) getView();
                view.transInSuccess();
            }
        });
    }

    @Override
    public void customerCoinByOneCoin(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<AmountModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(AmountModel o) {
                OTCTransferContract.IView view = (OTCTransferContract.IView) getView();
                view.customerCoinByOneCoin(o);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void otcAmount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                OTCTransferContract.IView view = (OTCTransferContract.IView) getView();
                view.otcAmount(o.attachment);
            }
        });
    }
}
