package com.android.tacu.module.auth.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.model.OtcSectionModel;
import com.android.tacu.module.main.model.OwnCenterModel;

import java.util.List;

public class AuthMerchantPresenter extends BaseMvpPresenter implements AuthMerchantContract.IPresenter {

    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                AuthMerchantContract.IView view = (AuthMerchantContract.IView) getView();
                view.ownCenter(model.attachment);
            }
        });
    }

    @Override
    public void BondAccount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).BondAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                AuthMerchantContract.IView view = (AuthMerchantContract.IView) getView();
                view.BondAccount(o.attachment);
            }
        });
    }

    @Override
    public void countTrade() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).countTrade(), new NetDisposableObserver<BaseModel<Integer>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<Integer> o) {
                AuthMerchantContract.IOrdinarView view = (AuthMerchantContract.IOrdinarView) getView();
                view.countTrade(o.attachment);
            }
        });
    }

    @Override
    public void getOssSetting() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                AuthMerchantContract.IOrdinarView wView = (AuthMerchantContract.IOrdinarView) getView();
                wView.getOssSetting(model.attachment);
            }
        });
    }

    @Override
    public void applyMerchant(String video) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).applyMerchant(video), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                AuthMerchantContract.IOrdinarView view = (AuthMerchantContract.IOrdinarView) getView();
                view.applyMerchantSuccess();
            }
        });
    }

    @Override
    public void applyMerchantAuth() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).applyMerchantAuth(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                AuthMerchantContract.IAuthView view = (AuthMerchantContract.IAuthView) getView();
                view.applyMerchantAuthSuccess();
            }
        });
    }

    @Override
    public void selectOtcSection() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectOtcSection(), new NetDisposableObserver<BaseModel<List<OtcSectionModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<OtcSectionModel>> o) {
                AuthMerchantContract.IView view = (AuthMerchantContract.IView) getView();
                view.selectOtcSection(o.attachment);
            }
        });
    }
}
