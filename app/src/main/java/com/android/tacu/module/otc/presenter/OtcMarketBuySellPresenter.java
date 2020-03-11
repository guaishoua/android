package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcMarketBuySellContract;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;
import com.android.tacu.module.otc.model.SelectStatusModel;

public class OtcMarketBuySellPresenter extends BaseMvpPresenter implements OtcMarketBuySellContract.IPresenter {
    @Override
    public void orderList(boolean isShowView, Integer type, Integer currencyId, Integer start, Integer size, Integer payType, Integer buyorsell) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).orderList(type, currencyId, start, size, payType, buyorsell), new NetDisposableObserver<BaseModel<OtcMarketOrderListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcMarketOrderListModel> model) {
                OtcMarketBuySellContract.IView view = (OtcMarketBuySellContract.IView) getView();
                view.orderList(model.attachment);
            }
        });
    }

    @Override
    public void selectStatus(final OtcMarketOrderAllModel marketModel, String merchantId, String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectStatus(merchantId, orderId), new NetDisposableObserver<BaseModel<SelectStatusModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<SelectStatusModel> model) {
                OtcMarketBuySellContract.IView view = (OtcMarketBuySellContract.IView) getView();
                view.selectStatus(marketModel, model.attachment);
            }
        });
    }
}
