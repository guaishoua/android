package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.my.contract.BindContact;

/**
 * Created by xiaohong on 2018/8/28.
 */

public class BindPresenter extends BaseMvpPresenter implements BindContact.IPresenter {
    @Override
    public void validCode(int type, String vercode) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).validCode(type, vercode), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                BindContact.IBindView view = (BindContact.IBindView) getView();
                view.verifySuccess();
            }
        });
    }

    @Override
    public void bindPhoneSendMsg(String phoneCode, String phone, int type, String token) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).bindPhoneSendMsg(phoneCode, phone, type, token), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                BindContact.IBindView view = (BindContact.IBindView) getView();
                view.showCodeStatus(model);
            }
        });
    }

    @Override
    public void bindPhone(String phoneCode, String code, String oldVercode, String newPhone, String oldPhone) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).bindPhone(phoneCode, code, oldVercode, newPhone, oldPhone), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                BindContact.IBindView view = (BindContact.IBindView) getView();
                view.bindStatus();
            }
        });
    }
}
