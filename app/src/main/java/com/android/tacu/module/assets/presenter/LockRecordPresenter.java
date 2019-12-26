package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.LockRecordContract;
import com.android.tacu.module.assets.model.LockChexListModel;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class LockRecordPresenter extends BaseMvpPresenter implements LockRecordContract.IPresenter {

    @Override
    public void getLockChexList(String startTime, String endTime, int start) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getLockChexList(startTime, endTime, start), new NetDisposableObserver<BaseModel<LockChexListModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<LockChexListModel> model) {
                LockRecordContract.IView view = (LockRecordContract.IView) getView();
                view.setLockChexListSuccess(model.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                LockRecordContract.IView view = (LockRecordContract.IView) getView();
                view.setLockChexListError();
            }
        });
    }
}
