package com.android.tacu.module.payinfo.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.payinfo.contract.PayInfoContract;

import java.util.List;

public class PayInfoPresenter extends BaseMvpPresenter implements PayInfoContract.IPresenter {

    @Override
    public void selectBank(boolean isShowView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                PayInfoContract.IView view = (PayInfoContract.IView) getView();
                view.selectBank(o.attachment);
            }
        });
    }

    @Override
    public void insert(Integer type, String name, String bankName, String openBankName, String bankCard, String weChatNo, String weChatImg, String aliPayNo, String aliPayImg, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).insertBank(type, name, bankName, openBankName, bankCard, weChatNo, weChatImg, aliPayNo, aliPayImg, fdPassword), new NetDisposableObserver<BaseModel<PayInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<PayInfoModel> o) {
                PayInfoContract.IDetailView yView = (PayInfoContract.IDetailView) getView();
                yView.insertSuccess();
            }
        });
    }

    @Override
    public void delete(Integer id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).deleteBank(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                PayInfoContract.IDetailView yView = (PayInfoContract.IDetailView) getView();
                yView.deleteSuccess();
            }
        });
    }

    @Override
    public void getOssSetting(final String fileLocalNameAddress) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                PayInfoContract.IDetailView wView = (PayInfoContract.IDetailView) getView();
                wView.getOssSetting(model.attachment, fileLocalNameAddress);
            }
        });
    }

    @Override
    public void uselectUserInfo(String headImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).uselectUserInfo(headImg), new NetDisposableObserver<BaseModel<String>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<String> model) {
                PayInfoContract.IDetailView wView = (PayInfoContract.IDetailView) getView();
                wView.uselectUserInfo(model.attachment);
            }
        });
    }

    @Override
    public void cancelBank(final Integer id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).cancelBank(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                PayInfoContract.IView view = (PayInfoContract.IView) getView();
                view.cancelBankSuccess(id);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);

                PayInfoContract.IView view = (PayInfoContract.IView) getView();
                view.cancelBankFailure(id);
            }
        });
    }

    @Override
    public void openBank(final Integer id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).openBank(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                PayInfoContract.IView view = (PayInfoContract.IView) getView();
                view.openBankSuccess(id);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);

                PayInfoContract.IView view = (PayInfoContract.IView) getView();
                view.openBankFailure(id);
            }
        });
    }
}
