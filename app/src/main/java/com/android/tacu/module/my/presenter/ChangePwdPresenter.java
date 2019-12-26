package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.my.contract.ChangePwdContact;

/**
 * Created by xiaohong on 2018/8/28.
 */

public class ChangePwdPresenter extends BaseMvpPresenter implements ChangePwdContact.IPresenter {

    @Override
    public void resetPwdInUserCenter(String newPwd, String pwd) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).resetPwdInUserCenter(newPwd, pwd), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                ChangePwdContact.Iview view = (ChangePwdContact.Iview) getView();
                view.bindStatus();
            }
        });
    }

    @Override
    public void bindPhoneSendMsg(String phoneCode, String phone, int type, String token) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).bindPhoneSendMsg(phoneCode, phone, type, token), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                ChangePwdContact.ITradeView view = (ChangePwdContact.ITradeView) getView();
                view.bindSendMsg();
            }
        });
    }

    @Override
    public void validCode(int type, String vercode) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).validCode(type, vercode), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                ChangePwdContact.ITradeView view = (ChangePwdContact.ITradeView) getView();
                view.validCode();
            }
        });
    }

    @Override
    public void bindFdpwd(String newPwd, String oldPwd, String loginPassWord) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).bindFdpwd(newPwd, oldPwd, loginPassWord), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                ChangePwdContact.Iview view = (ChangePwdContact.Iview) getView();
                view.bindStatus();
            }
        });
    }
}
