package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.contract.OtcBuyOrSellContract;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;

public class OtcBuyOrSellPresenter extends BaseMvpPresenter implements OtcBuyOrSellContract.IPresenter {

    @Override
    public void otcAmount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                OtcBuyOrSellContract.IView view = (OtcBuyOrSellContract.IView) getView();
                view.otcAmount(o.attachment);
            }
        });
    }

    @Override
    public void orderListOne(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).orderListOne(orderId), new NetDisposableObserver<BaseModel<OtcMarketOrderAllModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcMarketOrderAllModel> model) {
                OtcBuyOrSellContract.IView view = (OtcBuyOrSellContract.IView) getView();
                view.orderListOne(model.attachment);
            }
        });
    }
}
