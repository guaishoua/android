package com.android.tacu.module.login.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.login.contract.LoginContract;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.market.model.SelfModel;

/**
 * Created by jiazhen on 2018/8/13.
 */
public class LoginPresenter extends BaseMvpPresenter implements LoginContract.IPresenter {

    @Override
    public void login(String email, String pwd, String token) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).login(email, pwd, token), new NetDisposableObserver<BaseModel<LoginModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<LoginModel> model) {
                LoginContract.IView view = (LoginContract.IView) getView();
                view.showContent(model);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                LoginContract.IView view = (LoginContract.IView) getView();
                view.ownCenterSuccess(model.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                LoginContract.IView view = (LoginContract.IView) getView();
                view.ownCenterError();
            }
        });
    }

    @Override
    public void getSelfList() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSelfList(), new NetDisposableObserver<BaseModel<SelfModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<SelfModel> model) {
                LoginContract.IView view = (LoginContract.IView) getView();
                view.getSelfSelectionValueSuccess(model.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                LoginContract.IView view = (LoginContract.IView) getView();
                view.getSelfSelectionValueError();
            }
        });
    }
}
