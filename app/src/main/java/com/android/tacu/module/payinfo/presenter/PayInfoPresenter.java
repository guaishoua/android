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
import com.android.tacu.module.payinfo.contract.PayInfoListContract;

import java.util.List;

public class PayInfoPresenter extends BaseMvpPresenter implements PayInfoListContract.IPresenter {

    @Override
    public void selectBank() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                PayInfoListContract.IView view = (PayInfoListContract.IView) getView();
                view.selectBank(o.attachment);
            }
        });
    }

    @Override
    public void insert(Integer type, String bankName, String openBankName, String bankCard, String weChatNo, String weChatImg, String aliPayNo, String aliPayImg, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).insertBank(type, bankName, openBankName, bankCard, weChatNo, weChatImg, aliPayNo, aliPayImg, fdPassword), new NetDisposableObserver<BaseModel<PayInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<PayInfoModel> o) {
                PayInfoListContract.IDetailView yView = (PayInfoListContract.IDetailView) getView();
                yView.insertSuccess();
            }
        });
    }

    @Override
    public void delete(Integer id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).deleteBank(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                PayInfoListContract.IDetailView yView = (PayInfoListContract.IDetailView) getView();
                yView.deleteSuccess();
            }
        });
    }

    @Override
    public void getOssSetting(final String fileLocalNameAddress) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                PayInfoListContract.IDetailView wView = (PayInfoListContract.IDetailView) getView();
                wView.getOssSetting(model.attachment, fileLocalNameAddress);
            }
        });
    }

    @Override
    public void uselectUserInfo(String headImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).uselectUserInfo(headImg), new NetDisposableObserver<BaseModel<String>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<String> model) {
                PayInfoListContract.IDetailView wView = (PayInfoListContract.IDetailView) getView();
                wView.uselectUserInfo(model.attachment);
            }
        });
    }
}
