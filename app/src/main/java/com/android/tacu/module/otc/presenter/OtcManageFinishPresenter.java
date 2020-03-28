package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.otc.contract.OtcManageFinishContract;
import com.android.tacu.module.otc.model.OtcTradeListModel;

public class OtcManageFinishPresenter extends BaseMvpPresenter implements OtcManageFinishContract.IPresenter {
    @Override
    public void tradeListByOrder(boolean isShowView, String orderId, Integer start, Integer size) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).tradeListByOrder(orderId, start, size), new NetDisposableObserver<BaseModel<OtcTradeListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcTradeListModel> model) {
                OtcManageFinishContract.IView view = (OtcManageFinishContract.IView) getView();
                view.tradeListByOrder(model.attachment);
            }
        });
    }
}
