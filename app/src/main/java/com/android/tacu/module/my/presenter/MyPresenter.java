package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.my.contract.MyContract;

/**
 * Created by jiazhen on 2018/8/20.
 */
public class MyPresenter extends BaseMvpPresenter implements MyContract.IMyPresenter {

    @Override
    public void upload(String version, String channel) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UPLOAD, Api.class).update(), new NetDisposableObserver<BaseModel<UploadModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<UploadModel> model) {
                MyContract.IMyView view = (MyContract.IMyView) getView();
                view.upload(model.attachment);
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
}
