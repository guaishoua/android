package com.android.tacu.module.main.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.contract.ZXingLoginContract;

/**
 * Created by jiazhen on 2018/12/5.
 */
public class ZXingLoginPresenter extends BaseMvpPresenter implements ZXingLoginContract.IPresenter {

    @Override
    public void sendZxing(String uuid) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).sendZxing(uuid), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                ZXingLoginContract.IView view = (ZXingLoginContract.IView) getView();
                view.sendZxingSuccess();
            }
        });
    }
}
