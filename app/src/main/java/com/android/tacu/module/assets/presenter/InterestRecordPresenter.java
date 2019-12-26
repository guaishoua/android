package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.InterestRecordContract;
import com.android.tacu.module.assets.model.UnclaimedListModel;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class InterestRecordPresenter extends BaseMvpPresenter implements InterestRecordContract.IPresenter {
    @Override
    public void getShanxibaoList(String startTime, String endTime, int start) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.DEI, Api.class).getShanxibaoList(startTime, endTime, start, 10), new NetDisposableObserver<BaseModel<UnclaimedListModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<UnclaimedListModel> model) {
                InterestRecordContract.IView view = (InterestRecordContract.IView) getView();
                view.setShanxibaoListSuccess(model.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                InterestRecordContract.IView view = (InterestRecordContract.IView) getView();
                view.setShanxibaoListError();
            }
        });
    }
}
