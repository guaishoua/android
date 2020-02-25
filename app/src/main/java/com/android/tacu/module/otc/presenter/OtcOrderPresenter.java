package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcOrderContract;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcOrderPresenter extends BaseMvpPresenter implements OtcOrderContract.IPresenter {
    @Override
    public void tradeList(boolean isShowView, String orderId, Integer currencyId, Integer start, Integer size, Integer buyorsell, Integer status) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).tradeList(orderId, currencyId, start, size, buyorsell, status), new NetDisposableObserver<BaseModel<OtcTradeListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcTradeListModel> o) {
                OtcOrderContract.IView view = (OtcOrderContract.IView) getView();
                view.tradeList(o.attachment);
            }
        });
    }
}
