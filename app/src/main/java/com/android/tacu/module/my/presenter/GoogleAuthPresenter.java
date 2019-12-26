package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.model.GoogleAuthModel;
import com.android.tacu.module.my.contract.GoogleAuthContact;

/**
 * Created by jiazhen on 2018/8/27.
 */
public class GoogleAuthPresenter extends BaseMvpPresenter implements GoogleAuthContact.IPresenter {

    @Override
    public void getSecretKey() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSecretKey(), new NetDisposableObserver<BaseModel<GoogleAuthModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<GoogleAuthModel> model) {
                GoogleAuthContact.IGAView view = (GoogleAuthContact.IGAView) getView();
                view.getSecretKey(model.attachment);
            }
        });
    }

    @Override
    public void bindgaSendMsg(int type, String sliderToken) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).bingGaSendMsg(type, sliderToken), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                GoogleAuthContact.IView view = (GoogleAuthContact.IView) getView();
                view.sendMsgSuccess();
            }
        });
    }

    @Override
    public void bindGoogleAuth(String gaPwd, String pwd, String vercode, int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).bindGoogleAuth(gaPwd, pwd, vercode, type), new NetDisposableObserver<BaseModel<Object>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<Object> auth) {
                GoogleAuthContact.IView view = (GoogleAuthContact.IView) getView();
                view.success();
            }
        });
    }

    @Override
    public void closeGoogleAuth(String clientPassword, String loginPassword, String vercode, int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).closeGoogleAuth(clientPassword, loginPassword, vercode, type), new NetDisposableObserver<BaseModel<Object>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<Object> auth) {
                GoogleAuthContact.IView view = (GoogleAuthContact.IView) getView();
                view.colseSuccess();
            }
        });
    }
}
