package com.android.tacu.module.market.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.market.contract.SelfSelectionEditContract;

/**
 * Created by jiazhen on 2018/8/21.
 */
public class SelfSelectionEditPresenter extends BaseMvpPresenter implements SelfSelectionEditContract.IPresenter {

    @Override
    public void uploadSelfList(String checkJson) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).uploadSelfList(checkJson, 3), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                SelfSelectionEditContract.IView view = (SelfSelectionEditContract.IView) getView();
                view.uploadSelfSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                SelfSelectionEditContract.IView view = (SelfSelectionEditContract.IView) getView();
                view.uploadSelfError();
            }
        });
    }
}
