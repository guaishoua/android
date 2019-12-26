package com.android.tacu.module.transaction.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.transaction.contract.MyDealContract;
import com.android.tacu.module.transaction.model.ShowTradeListModel;

public class MyDealPresenter extends BaseMvpPresenter implements MyDealContract.IPresenter {

    @Override
    public void showTradeList(int start, int size, int buyOrSell, Integer currencyId, Integer baseCurrencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).trTradeListByCustomer(null, null, start, size, null, buyOrSell, currencyId, baseCurrencyId, null), new NetDisposableObserver<BaseModel<ShowTradeListModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<ShowTradeListModel> moder) {
                MyDealContract.IView view = (MyDealContract.IView) getView();
                view.showTradeList(moder.attachment);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                MyDealContract.IView view = (MyDealContract.IView) getView();
                view.showTradeListFail();
            }
        });
    }
}
