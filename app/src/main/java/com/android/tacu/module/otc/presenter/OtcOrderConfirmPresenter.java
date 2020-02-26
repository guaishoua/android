package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.contract.OtcOrderConfirmContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcTradeModel;

public class OtcOrderConfirmPresenter extends BaseMvpPresenter implements OtcOrderConfirmContract.IPresenter {
    @Override
    public void confirmOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderConfirmContract.IView view = (OtcOrderConfirmContract.IView) getView();
                view.confirmOrderSuccess();
            }
        });
    }

    @Override
    public void confirmCancelOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmCancelOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderConfirmContract.IView view = (OtcOrderConfirmContract.IView) getView();
                view.confirmCancelOrderSuccess();
            }
        });
    }

    @Override
    public void selectTradeOne(String orderNo) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectTradeOne(orderNo), new NetDisposableObserver<BaseModel<OtcTradeModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcTradeModel> model) {
                OtcOrderConfirmContract.IView view = (OtcOrderConfirmContract.IView) getView();
                view.selectTradeOne(model.attachment);
            }
        });
    }

    @Override
    public void userBaseInfo(Integer queryUid) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).userBaseInfo(queryUid), new NetDisposableObserver<BaseModel<OtcMarketInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcMarketInfoModel> model) {
                OtcOrderConfirmContract.IView view = (OtcOrderConfirmContract.IView) getView();
                view.userBaseInfo(model.attachment);
            }
        });
    }

    @Override
    public void currentTime() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).currentTime(), new NetDisposableObserver<BaseModel<Long>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<Long> model) {
                OtcOrderConfirmContract.IView view = (OtcOrderConfirmContract.IView) getView();
                view.currentTime(model.attachment);
            }
        });
    }

    @Override
    public void OtcAccount(Integer currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                OtcOrderConfirmContract.IView view = (OtcOrderConfirmContract.IView) getView();
                view.OtcAccount(o.attachment);
            }
        });
    }

    @Override
    public void orderListOne(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).orderListOne(orderId), new NetDisposableObserver<BaseModel<OtcMarketOrderAllModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcMarketOrderAllModel> model) {
                OtcOrderConfirmContract.IView view = (OtcOrderConfirmContract.IView) getView();
                view.orderListOne(model.attachment);
            }
        });
    }
}
