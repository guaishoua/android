package com.android.tacu.module.auth.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.exceptions.ResponseException;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.auth.contract.RealNameContract;
import com.android.tacu.module.auth.model.UserInfoModel;
import com.android.tacu.module.main.model.AliModel;

/**
 * Created by xiaohong on 2018/9/18.
 */

public class RealNamePresenter extends BaseMvpPresenter implements RealNameContract.IRealNamePresenter {

    @Override
    public void authinfoNew() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).authinfoNew(), new NetDisposableObserver<BaseModel<UserInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<UserInfoModel> model) {
                RealNameContract.IRealView view = (RealNameContract.IRealView) getView();
                view.authinfoNew(model.attachment);
            }
        });
    }

    @Override
    public void authNew(String country, String firstName, String secondName, String idNumber, String birthday, String gender, String isChina, int step, String singnTime, String outofTime, int isAllTime) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).authnew(country, firstName, secondName, idNumber, birthday, gender, isChina, step, singnTime, outofTime, isAllTime), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RealNameContract.IRealView view = (RealNameContract.IRealView) getView();
                view.authNewSuccess();
            }
        });
    }

    @Override
    public void authnewPositive(String authnewPositive, String ischina, int step) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).authnewPositive(authnewPositive, ischina, step), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.authNewSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.onError();
            }
        });
    }

    @Override
    public void authnewOpposite(String authnewOpposite, String ischina, int step) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).authnewOpposite(authnewOpposite, ischina, step), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.authNewSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.onError();
            }
        });
    }

    @Override
    public void authnewHand(String authnewHand, String ischina, int step) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).authnewHand(authnewHand, ischina, step), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.authThredSuccess(o);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.onError();
            }
        });
    }

    @Override
    public void getVerifyToken() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ALI, Api.class).aliToken(), new NetDisposableObserver<BaseModel<AliModel>>((IBaseMvpView) getView(), true, false) {
            @Override
            public void onNext(BaseModel<AliModel> model) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.getVerifyToken(model.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable instanceof ResponseException) {
                    ResponseException responseException = (ResponseException) throwable;
                    RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                    view.getVerifyTokenError(responseException.status);
                }
            }
        });
    }

    @Override
    public void vedioAuth() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ALI, Api.class).vedioAuth(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel model) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.vedioAuth();
            }
        });
    }

    @Override
    public void getOssSetting() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.getOssSetting(model.attachment);
            }
        });
    }
}
