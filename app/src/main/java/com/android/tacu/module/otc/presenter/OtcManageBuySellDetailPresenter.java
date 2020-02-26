package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.contract.OtcManageBuySellDetailContract;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcManageBuySellDetailPresenter extends BaseMvpPresenter implements OtcManageBuySellDetailContract.IPresenter {
    @Override
    public void orderListOne(final boolean isShowView, final boolean isTop, String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).orderListOne(orderId), new NetDisposableObserver<BaseModel<OtcMarketOrderAllModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcMarketOrderAllModel> model) {
                OtcManageBuySellDetailContract.IDetailView view = (OtcManageBuySellDetailContract.IDetailView) getView();
                view.orderListOne(isShowView, isTop, model.attachment);
            }
        });
    }

    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                OtcManageBuySellDetailContract.IRecordView view = (OtcManageBuySellDetailContract.IRecordView) getView();
                view.ownCenter(model.attachment);
            }
        });
    }

    @Override
    public void tradeListByOrder(boolean isShowView, String orderId, Integer start, Integer size) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).tradeListByOrder(orderId, start, size), new NetDisposableObserver<BaseModel<OtcTradeListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcTradeListModel> model) {
                OtcManageBuySellDetailContract.IRecordView view = (OtcManageBuySellDetailContract.IRecordView) getView();
                view.tradeListByOrder(model.attachment);
            }
        });
    }

    @Override
    public void tradeList(boolean isShowView, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).tradeList(orderId, currencyId, start, size, buyorsell, status), new NetDisposableObserver<BaseModel<OtcTradeListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcTradeListModel> o) {
                OtcManageBuySellDetailContract.IDetailView view = (OtcManageBuySellDetailContract.IDetailView) getView();
                view.tradeList(o.attachment);
            }
        });
    }

    @Override
    public void confirmOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageBuySellDetailContract.IDetailView view = (OtcManageBuySellDetailContract.IDetailView) getView();
                view.confirmOrderSuccess();
            }
        });
    }

    @Override
    public void confirmCancelOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmCancelOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageBuySellDetailContract.IDetailView view = (OtcManageBuySellDetailContract.IDetailView) getView();
                view.confirmCancelOrderSuccess();
            }
        });
    }

    @Override
    public void currentTime() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).currentTime(), new NetDisposableObserver<BaseModel<Long>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<Long> model) {
                OtcManageBuySellDetailContract.IDetailView view = (OtcManageBuySellDetailContract.IDetailView) getView();
                view.currentTime(model.attachment);
            }
        });
    }
}
