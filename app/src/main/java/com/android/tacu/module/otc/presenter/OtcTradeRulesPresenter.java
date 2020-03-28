package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcTradeRulesContract;

public class OtcTradeRulesPresenter extends BaseMvpPresenter implements OtcTradeRulesContract.IPresenter {
    @Override
    public void disclaimer() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).disclaimer(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), true, false) {
            @Override
            public void onNext(BaseModel o) {
                OtcTradeRulesContract.IView view = (OtcTradeRulesContract.IView) getView();
                view.disclaimerSuccess();
            }
        });
    }
}
