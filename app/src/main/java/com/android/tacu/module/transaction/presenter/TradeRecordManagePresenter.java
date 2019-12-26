package com.android.tacu.module.transaction.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.transaction.contract.TradeRecordManageContract;
import com.android.tacu.module.transaction.model.ShowOrderListModel;
import com.android.tacu.module.transaction.model.ShowTradeListModel;

/**
 * Created by jiazhen on 2018/10/13.
 */
public class TradeRecordManagePresenter extends BaseMvpPresenter implements TradeRecordManageContract.IPresenter {

    @Override
    public void showOrderList(int start, int size, String status, int buyOrSell, Integer currencyId, Integer baseCurrencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).trOrderListByCustomer(null, null, start, size, status, buyOrSell, currencyId, baseCurrencyId, null), new NetDisposableObserver<BaseModel<ShowOrderListModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<ShowOrderListModel> moder) {
                TradeRecordManageContract.IView view = (TradeRecordManageContract.IView) getView();
                view.showOrderList(moder.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                TradeRecordManageContract.IView view = (TradeRecordManageContract.IView) getView();
                view.showOrderListFail();
            }
        });
    }

    @Override
    public void showTradeList(int start, int size, int buyOrSell, Integer currencyId, Integer baseCurrencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).trTradeListByCustomer(null, null, start, size, null, buyOrSell, currencyId, baseCurrencyId, null), new NetDisposableObserver<BaseModel<ShowTradeListModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<ShowTradeListModel> moder) {
                TradeRecordManageContract.IView view = (TradeRecordManageContract.IView) getView();
                view.showTradeList(moder.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                TradeRecordManageContract.IView view = (TradeRecordManageContract.IView) getView();
                view.showTradeListFail();
            }
        });
    }

    @Override
    public void cancel(String orderNo, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ORDER, Api.class).cancel(orderNo, fdPassword, null), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel moder) {
                TradeRecordManageContract.IView view = (TradeRecordManageContract.IView) getView();
                view.cancelOrder();
            }
        });
    }

    @Override
    public void cancelList(String orderNo, String fdPassword, String selectedParams) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ORDER, Api.class).cancelList(orderNo, fdPassword, null, selectedParams), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel moder) {
                TradeRecordManageContract.IView view = (TradeRecordManageContract.IView) getView();
                view.cancelOrderList();
            }
        });
    }
}
