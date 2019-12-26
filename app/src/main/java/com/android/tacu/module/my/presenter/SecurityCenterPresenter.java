package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.my.contract.SecurityCenterContract;

/**
 * Created by jiazhen on 2018/8/27.
 */
public class SecurityCenterPresenter extends BaseMvpPresenter implements SecurityCenterContract.IPresenter {

    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                SecurityCenterContract.IView view = (SecurityCenterContract.IView) getView();
                view.ownCenter(model.attachment);
            }
        });
    }

    @Override
    public void updateFdPwdEnabled(int enabled, String fdPwd) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).updateFdPwdEnabled(enabled, fdPwd), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                SecurityCenterContract.IView view = (SecurityCenterContract.IView) getView();
                view.updateFdPwdSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                SecurityCenterContract.IView view = (SecurityCenterContract.IView) getView();
                view.updateFdPwdError();
            }
        });
    }
}
