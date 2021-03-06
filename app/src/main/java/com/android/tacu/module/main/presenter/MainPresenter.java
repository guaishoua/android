package com.android.tacu.module.main.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.exceptions.ResponseException;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.main.contract.MainContract;
import com.android.tacu.module.main.model.AliModel;
import com.android.tacu.module.main.model.ConvertModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.market.model.SelfModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/8/13.
 */
public class MainPresenter extends BaseMvpPresenter implements MainContract.IPresenter {

    @Override
    public void upload(String version, String channel, final boolean isTip) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UPLOAD, Api.class).update(), new NetDisposableObserver<BaseModel<UploadModel>>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel<UploadModel> model) {
                MainContract.IView view = (MainContract.IView) getView();
                view.upload(model.attachment, isTip);
            }
        });
    }

    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                MainContract.IView view = (MainContract.IView) getView();
                view.ownCenter(model.attachment);
            }
        });
    }

    @Override
    public void getSelfList() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSelfList(), new NetDisposableObserver<BaseModel<SelfModel>>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel<SelfModel> model) {
                MainContract.IView view = (MainContract.IView) getView();
                view.getSelfSelectionValue(model.attachment);
            }
        });
    }

    @Override
    public void logout() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).logout(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel o) {
            }
        });
    }

    @Override
    public void getConvertModel() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).getConvertList(), new NetDisposableObserver<BaseModel<ConvertModel>>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel<ConvertModel> model) {
                MainContract.IView view = (MainContract.IView) getView();
                view.convertMoney(model.attachment);
            }
        });
    }

    @Override
    public void selectBank() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                MainContract.IView view = (MainContract.IView) getView();
                view.selectBank(o.attachment);
            }
        });
    }

    @Override
    public void getVerifyToken() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ALI, Api.class).aliToken(), new NetDisposableObserver<BaseModel<AliModel>>((IBaseMvpView) getView(), true, false) {
            @Override
            public void onNext(BaseModel<AliModel> model) {
                MainContract.IView view = (MainContract.IView) getView();
                view.getVerifyToken(model.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable instanceof ResponseException) {
                    ResponseException responseException = (ResponseException) throwable;
                    MainContract.IView view = (MainContract.IView) getView();
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
                MainContract.IView view = (MainContract.IView) getView();
                view.vedioAuth();
            }
        });
    }
}
