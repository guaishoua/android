package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.BindingPayInfoContract;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.PayInfoModel;

import java.util.List;

public class BindingPayInfoPresenter extends BaseMvpPresenter implements BindingPayInfoContract.IPresenter {
    @Override
    public void selectBank() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                BindingPayInfoContract.IView view = (BindingPayInfoContract.IView) getView();
                view.selectBank(o.attachment);
            }
        });
    }

    @Override
    public void insertBank(final Integer type, String bankName, String openBankName, String openBankAdress, String bankCard, String weChatNo, String weChatImg, String aliPayNo, String aliPayImg, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).insertBank(type, bankName, openBankName, openBankAdress, bankCard, weChatNo, weChatImg, aliPayNo, aliPayImg, fdPassword), new NetDisposableObserver<BaseModel<PayInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<PayInfoModel> o) {
                if (type != null) {
                    switch (type) {
                        case 1://银行卡
                            BindingPayInfoContract.IYhkView yView = (BindingPayInfoContract.IYhkView) getView();
                            yView.insertBankSuccess();
                            break;
                        case 2://微信
                            BindingPayInfoContract.IWxView wView = (BindingPayInfoContract.IWxView) getView();
                            wView.insertBankSuccess();
                            break;
                        case 3://支付宝
                            BindingPayInfoContract.IZfbView zView = (BindingPayInfoContract.IZfbView) getView();
                            zView.insertBankSuccess();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void deleteBank(final Integer type, Integer id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).deleteBank(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                if (type != null) {
                    switch (type) {
                        case 1://银行卡
                            BindingPayInfoContract.IYhkView yView = (BindingPayInfoContract.IYhkView) getView();
                            yView.deleteBankSuccess();
                            break;
                        case 2://微信
                            BindingPayInfoContract.IWxView wView = (BindingPayInfoContract.IWxView) getView();
                            wView.deleteBankSuccess();
                            break;
                        case 3://支付宝
                            BindingPayInfoContract.IZfbView zView = (BindingPayInfoContract.IZfbView) getView();
                            zView.deleteBankSuccess();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void getOssSetting(final Integer type, final String fileLocalNameAddress) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                if (type != null) {
                    switch (type) {
                        case 2://微信
                            BindingPayInfoContract.IWxView wView = (BindingPayInfoContract.IWxView) getView();
                            wView.getOssSetting(model.attachment, fileLocalNameAddress);
                            break;
                        case 3://支付宝
                            BindingPayInfoContract.IZfbView zView = (BindingPayInfoContract.IZfbView) getView();
                            zView.getOssSetting(model.attachment, fileLocalNameAddress);
                            break;
                    }
                }
            }
        });
    }
}
