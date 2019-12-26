package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.ExchangeContract;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.ExchangeModel;
import com.android.tacu.module.assets.model.ExchangeShowModel;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class ExchangePresenter extends BaseMvpPresenter implements ExchangeContract.IPresenter {
    @Override
    public void getUSDT(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).getUSDT(currencyId), new NetDisposableObserver<BaseModel<ExchangeModel>>((IBaseMvpView) getView(), true) {
            @Override
            public void onNext(BaseModel<ExchangeModel> exchangeModel) {
                ExchangeContract.IExchangeView view = (ExchangeContract.IExchangeView) getView();
                view.getUSTPrice(exchangeModel.attachment);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void confirmMessage(String fdPwd, String currencyId, String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).confirmMessage(fdPwd, currencyId, amount), new NetDisposableObserver<BaseModel<ExchangeShowModel>>((IBaseMvpView) getView(), true) {
            @Override
            public void onNext(BaseModel<ExchangeShowModel> model) {
                ExchangeContract.IExchangeView view = (ExchangeContract.IExchangeView) getView();
                view.confirmMessage(model.attachment);
            }
        });
    }

    @Override
    public void exchangeCoinUSDTToCode(String fdPwd, String currencyId, String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).exchangeCoinUSDTToCode(fdPwd, currencyId, amount), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), true) {
            @Override
            public void onNext(BaseModel model) {
                ExchangeContract.IExchangeView view = (ExchangeContract.IExchangeView) getView();
                view.exchangeCoinUSDTToCode();
            }
        });
    }

    @Override
    public void customerCoinByOneCoin(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<AmountModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(AmountModel o) {
                ExchangeContract.IExchangeView view = (ExchangeContract.IExchangeView) getView();
                view.customerCoinByOneCoin(o);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }
}
