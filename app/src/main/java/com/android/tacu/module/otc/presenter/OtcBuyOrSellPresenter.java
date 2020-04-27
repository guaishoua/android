package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcBuyOrSellContract;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;

import java.util.List;

public class OtcBuyOrSellPresenter extends BaseMvpPresenter implements OtcBuyOrSellContract.IPresenter {

    @Override
    public void otcAmount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                OtcBuyOrSellContract.IView view = (OtcBuyOrSellContract.IView) getView();
                view.otcAmount(o.attachment);
            }
        });
    }

    @Override
    public void orderListOne(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).orderListOne(orderId), new NetDisposableObserver<BaseModel<OtcMarketOrderAllModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcMarketOrderAllModel> model) {
                OtcBuyOrSellContract.IView view = (OtcBuyOrSellContract.IView) getView();
                view.orderListOne(model.attachment);
            }
        });
    }

    @Override
    public void selectBank(final String num, final String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                OtcBuyOrSellContract.IView view = (OtcBuyOrSellContract.IView) getView();
                view.selectBank(o.attachment, num, amount);
            }
        });
    }

    @Override
    public void selectMerchantBank(String merchantId, final String num, final String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectMerchantBank(merchantId), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                OtcBuyOrSellContract.IView view = (OtcBuyOrSellContract.IView) getView();
                view.selectMerchantBank(o.attachment, num, amount);
            }
        });
    }

    @Override
    public void otcTrade(String orderId, Integer payType, String num, String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).otcTrade(orderId, payType, num, amount), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcBuyOrSellContract.IView view = (OtcBuyOrSellContract.IView) getView();
                view.otcTradeSuccess();
            }
        });
    }
}
