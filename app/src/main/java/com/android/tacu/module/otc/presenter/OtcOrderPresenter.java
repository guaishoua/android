package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcOrderContract;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcOrderPresenter extends BaseMvpPresenter implements OtcOrderContract.IPresenter {
    @Override
    public void tradeList(boolean isShowView, final boolean isClean, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).tradeList(orderId, currencyId, start, size, buyorsell, status), new NetDisposableObserver<BaseModel<OtcTradeListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcTradeListModel> o) {
                OtcOrderContract.IView view = (OtcOrderContract.IView) getView();
                view.tradeList(isClean, o.attachment);
            }
        });
    }

    @Override
    public void confirmOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderContract.IView view = (OtcOrderContract.IView) getView();
                view.confirmOrderSuccess();
            }
        });
    }

    @Override
    public void finishOrder(String orderId, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).finishOrder(orderId, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderContract.IView view = (OtcOrderContract.IView) getView();
                view.finishOrderSuccess();
            }
        });
    }

    @Override
    public void currentTime() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).currentTime(), new NetDisposableObserver<BaseModel<Long>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<Long> model) {
                OtcOrderContract.IView view = (OtcOrderContract.IView) getView();
                view.currentTime(model.attachment);
            }
        });
    }
}
