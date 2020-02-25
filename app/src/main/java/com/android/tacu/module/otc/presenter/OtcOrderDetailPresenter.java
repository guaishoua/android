package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;

public class OtcOrderDetailPresenter extends BaseMvpPresenter implements OtcOrderDetailContract.IPresenter {

    @Override
    public void selectTradeOne(final boolean isFirst, String orderNo) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectTradeOne(orderNo), new NetDisposableObserver<BaseModel<OtcTradeModel>>((IBaseMvpView) getView(), isFirst) {
            @Override
            public void onNext(BaseModel<OtcTradeModel> model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.selectTradeOne(isFirst, model.attachment);
            }
        });
    }

    @Override
    public void userBaseInfo(final int buyOrSell, Integer queryUid) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).userBaseInfo(queryUid), new NetDisposableObserver<BaseModel<OtcMarketInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcMarketInfoModel> model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.userBaseInfo(buyOrSell, model.attachment);
            }
        });
    }

    @Override
    public void currentTime() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).currentTime(), new NetDisposableObserver<BaseModel<Long>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<Long> model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.currentTime(model.attachment);
            }
        });
    }
}
