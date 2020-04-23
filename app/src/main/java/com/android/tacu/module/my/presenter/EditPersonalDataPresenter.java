package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.my.contract.EditPersonalDataContact;

import java.util.List;

public class EditPersonalDataPresenter extends BaseMvpPresenter implements EditPersonalDataContact.IPresenter {
    @Override
    public void updateUserInfo(String nickname, String headImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).updateUserInfo(nickname, headImg), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                EditPersonalDataContact.IView view = (EditPersonalDataContact.IView) getView();
                view.updateUserInfoSuccess();
            }
        });
    }

    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                EditPersonalDataContact.IView view = (EditPersonalDataContact.IView) getView();
                view.ownCenter(model.attachment);
            }
        });
    }

    @Override
    public void selectBank() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                EditPersonalDataContact.IView view = (EditPersonalDataContact.IView) getView();
                view.selectBank(o.attachment);
            }
        });
    }

    @Override
    public void getOssSetting(final String fileLocalNameAddress) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                EditPersonalDataContact.IView view = (EditPersonalDataContact.IView) getView();
                view.getOssSetting(model.attachment, fileLocalNameAddress);
            }
        });
    }
}
