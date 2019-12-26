package com.android.tacu.module.auth.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.auth.contract.RealNameContract;
import com.android.tacu.module.auth.model.AwsModel;
import com.android.tacu.module.auth.model.UserInfoModel;

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
    public void authNew(String country, String firstName, String secondName, String idNumber, String birthday, String gender, String isChina, int step, String singnTime, String outofTime, int isAllTime, int currentLocation) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).authnew(country, firstName, secondName, idNumber, birthday, gender, isChina, step, singnTime, outofTime, isAllTime, currentLocation), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
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
    public void getOssSetting() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.getOssSetting(model.attachment);
            }
        });
    }

    @Override
    public void getAwsSetting() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getAwsSetting(), new NetDisposableObserver<BaseModel<AwsModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<AwsModel> model) {
                RealNameContract.IRealTwoView view = (RealNameContract.IRealTwoView) getView();
                view.getAwsSetting(model.attachment);
            }
        });
    }
}
