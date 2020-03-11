package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcMarketBuySellContract;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;

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
    public void selectStatus() {

    }
}
