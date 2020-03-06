package com.android.tacu.module.splash.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.splash.contract.TradeMatchContract;
import com.android.tacu.module.splash.model.TradeWinListModel;

public class TradeMatchPresenter extends BaseMvpPresenter implements TradeMatchContract.IPresenter {
    @Override
    public void upload(String version, String channel) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UPLOAD, Api.class).update(), new NetDisposableObserver<BaseModel<UploadModel>>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel<UploadModel> model) {
                TradeMatchContract.IView view = (TradeMatchContract.IView) getView();
                view.upload(model.attachment);
            }
        });
    }

    @Override
    public void tradeWinList() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).tradeWinList(), new NetDisposableObserver<BaseModel<TradeWinListModel>>((IBaseMvpView) getView(), true, false) {
            @Override
            public void onNext(BaseModel<TradeWinListModel> model) {
                TradeMatchContract.IView view = (TradeMatchContract.IView) getView();
                view.tradeWinList(model.attachment);
            }
        });
    }
}
