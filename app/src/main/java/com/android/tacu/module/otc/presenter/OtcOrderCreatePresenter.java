package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcOrderCreateContract;

public class OtcOrderCreatePresenter extends BaseMvpPresenter implements OtcOrderCreateContract.IPresenter {

    @Override
    public void otcTrade(Integer orderId, String fdPassword, Integer payType, String num, String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).otcTrade(orderId, fdPassword, payType, num, amount), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderCreateContract.IView view = (OtcOrderCreateContract.IView) getView();
                view.otcTradeSuccess();
            }
        });
    }
}
