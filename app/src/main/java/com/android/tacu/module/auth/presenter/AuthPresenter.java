package com.android.tacu.module.auth.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.auth.contract.AuthContract;
import com.android.tacu.module.auth.model.AliModel;
import com.android.tacu.module.auth.model.SelectAuthLevelModel;

/**
 * Created by xiaohong on 2018/8/24.
 */

public class AuthPresenter extends BaseMvpPresenter implements AuthContract.IAuthPresenter {

    public AuthPresenter() {
    }

    @Override
    public void selectAuthLevel() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).selectAuthLevel(), new NetDisposableObserver<BaseModel<SelectAuthLevelModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<SelectAuthLevelModel> model) {
                AuthContract.IAuthView view = (AuthContract.IAuthView) getView();
                view.selectAuthLevel(model.attachment);
            }
        });
    }

    @Override
    public void getVerifyToken() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ALI, Api.class).aliToken(), new NetDisposableObserver<BaseModel<AliModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<AliModel> model) {
                AuthContract.IAuthView view = (AuthContract.IAuthView) getView();
                view.getVerifyToken(model.attachment);
            }
        });
    }

    @Override
    public void vedioAuth() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ALI, Api.class).vedioAuth(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                AuthContract.IAuthView view = (AuthContract.IAuthView) getView();
                view.vedioAuth();
            }
        });
    }
}
