package com.android.tacu.module.login.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.login.contract.RegisterContract;

/**
 * Created by jiazhen on 2018/8/16.
 */
public class RegisterPresenter extends BaseMvpPresenter implements RegisterContract.IPresenter {

    @Override
    public void sendEmailForRegister(String phoneCode, String email, String token) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).sendEmailForRegister(phoneCode, email, token), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                RegisterContract.IView view = (RegisterContract.IView) getView();
                view.showCodeMsg();
            }
        });
    }

    @Override
    public void register(String phoneCode, String email, String pwd, String vercode, String inviteId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).register(phoneCode, email, pwd, vercode, inviteId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                RegisterContract.IView view = (RegisterContract.IView) getView();
                view.success();
            }
        });
    }
}
