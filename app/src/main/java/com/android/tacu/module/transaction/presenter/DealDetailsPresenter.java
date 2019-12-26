package com.android.tacu.module.transaction.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.transaction.contract.DealDetailsConstract;
import com.android.tacu.module.transaction.model.DealDetailsModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/10/16.
 */
public class DealDetailsPresenter extends BaseMvpPresenter implements DealDetailsConstract.IPresenter {
    @Override
    public void dealDetail(String orderNo) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getDealDetail(orderNo), new NetDisposableObserver<BaseModel<List<DealDetailsModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<DealDetailsModel>> moder) {
                DealDetailsConstract.IView view = (DealDetailsConstract.IView) getView();
                view.dealDetail(moder.attachment);
            }
        });
    }
}
