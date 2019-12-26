package com.android.tacu.module.login.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.login.contract.FindPwdContract;

/**
 * Created by jiazhen on 2018/8/16.
 */
public class FindPwdPresenter extends BaseMvpPresenter implements FindPwdContract.IPresenter {


    @Override
    public void sendMail(String phoneCode, String email, String token) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).sendMail(phoneCode, email, token), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                FindPwdContract.IView view = (FindPwdContract.IView) getView();
                view.sendMailSuccess();
            }
        });
    }

    @Override
    public void resetPwd(String phoneCode, String email, String newPwd, String vercode) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).resetPwd(phoneCode, email, newPwd, vercode), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                FindPwdContract.IView view = (FindPwdContract.IView) getView();
                view.resetSuccess();
            }
        });
    }
}
