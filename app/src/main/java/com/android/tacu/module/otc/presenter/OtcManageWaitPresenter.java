package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcManageWaitContract;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcManageWaitPresenter extends BaseMvpPresenter implements OtcManageWaitContract.IPresenter {
    @Override
    public void tradeList(boolean isShowView, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).tradeList(orderId, currencyId, start, size, buyorsell, status), new NetDisposableObserver<BaseModel<OtcTradeListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcTradeListModel> o) {
                OtcManageWaitContract.IView view = (OtcManageWaitContract.IView) getView();
                view.tradeList(o.attachment);
            }
        });
    }

    @Override
    public void confirmOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageWaitContract.IView view = (OtcManageWaitContract.IView) getView();
                view.confirmOrderSuccess();
            }
        });
    }

    @Override
    public void confirmCancelOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmCancelOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageWaitContract.IView view = (OtcManageWaitContract.IView) getView();
                view.confirmCancelOrderSuccess();
            }
        });
    }

    @Override
    public void payOrder(String orderId, String payImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).payOrder(orderId, payImg), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageWaitContract.IView wView = (OtcManageWaitContract.IView) getView();
                wView.payOrderSuccess();
            }
        });
    }

    @Override
    public void finishOrder(String orderId, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).finishOrder(orderId, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageWaitContract.IView wView = (OtcManageWaitContract.IView) getView();
                wView.finishOrderSuccess();
            }
        });
    }

    @Override
    public void currentTime() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).currentTime(), new NetDisposableObserver<BaseModel<Long>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<Long> model) {
                OtcManageWaitContract.IView view = (OtcManageWaitContract.IView) getView();
                view.currentTime(model.attachment);
            }
        });
    }
}
