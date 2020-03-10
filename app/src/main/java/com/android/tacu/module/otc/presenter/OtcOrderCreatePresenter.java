package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcOrderCreateContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;

import java.util.List;

public class OtcOrderCreatePresenter extends BaseMvpPresenter implements OtcOrderCreateContract.IPresenter {

    @Override
    public void userBaseInfo(final boolean isBuy, Integer queryUid) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).userBaseInfo(queryUid), new NetDisposableObserver<BaseModel<OtcMarketInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcMarketInfoModel> model) {
                OtcOrderCreateContract.IView view = (OtcOrderCreateContract.IView) getView();
                view.userBaseInfo(isBuy, model.attachment);
            }
        });
    }

    @Override
    public void selectBank() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                OtcOrderCreateContract.IView view = (OtcOrderCreateContract.IView) getView();
                view.selectBank(o.attachment);
            }
        });
    }

    @Override
    public void otcTrade(String orderId, Integer payType, String num, String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).otcTrade(orderId, payType, num, amount), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderCreateContract.IView view = (OtcOrderCreateContract.IView) getView();
                view.otcTradeSuccess();
            }
        });
    }

    @Override
    public void disclaimer() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).disclaimer(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel o) {
                OtcOrderCreateContract.IView view = (OtcOrderCreateContract.IView) getView();
                view.disclaimerSuccess();
            }
        });
    }
}
