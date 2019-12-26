package com.android.tacu.module.transaction.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.transaction.contract.CurrentEntrustConstract;
import com.android.tacu.module.transaction.model.ShowOrderListModel;

public class CurrentEntrustPresenter extends BaseMvpPresenter implements CurrentEntrustConstract.IPresenter {

    @Override
    public void showOrderList(int start, int size, Integer currencyId, Integer baseCurrencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).trOrderListByCustomer(null, null, start, 20, "0,1", 0, currencyId, baseCurrencyId, null), new NetDisposableObserver<BaseModel<ShowOrderListModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<ShowOrderListModel> moder) {
                CurrentEntrustConstract.IView view = (CurrentEntrustConstract.IView) getView();
                view.showOrderList(moder.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                CurrentEntrustConstract.IView view = (CurrentEntrustConstract.IView) getView();
                view.showOrderListFail();
            }
        });
    }

    @Override
    public void cancel(String orderNo, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ORDER, Api.class).cancel(orderNo, fdPassword, null), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel moder) {
                CurrentEntrustConstract.IView view = (CurrentEntrustConstract.IView) getView();
                view.cancelOrder();
            }
        });
    }

    @Override
    public void cancelList(String orderNo, String fdPassword, String selectedParams) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ORDER, Api.class).cancelList(orderNo, fdPassword, null, selectedParams), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel moder) {
                CurrentEntrustConstract.IView view = (CurrentEntrustConstract.IView) getView();
                view.cancelOrderList();
            }
        });
    }
}
