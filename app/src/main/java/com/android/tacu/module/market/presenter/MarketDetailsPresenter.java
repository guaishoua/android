package com.android.tacu.module.market.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.market.contract.MarketDetailsContract;
import com.android.tacu.module.market.model.KLineModel;

/**
 * Created by jiazhen on 2018/8/23.
 */
public class MarketDetailsPresenter extends BaseMvpPresenter implements MarketDetailsContract.IPresenter {

    // type=1 k线详情页 type=2 交易
    @Override
    public void getBestexKline(String symbol, long range, final int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.KLINE, Api.class).getKline(symbol, range), new NetDisposableObserver<BaseModel<KLineModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<KLineModel> model) {
                if (type == 1) {
                    MarketDetailsContract.IView view = (MarketDetailsContract.IView) getView();
                    view.success(model.attachment);
                } else {
                    MarketDetailsContract.IKlineView view = (MarketDetailsContract.IKlineView) getView();
                    view.success(model.attachment);
                }
            }
        });
    }

    @Override
    public void uploadSelfList(String checkJson) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).uploadSelfList(checkJson, 3), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                MarketDetailsContract.IView view = (MarketDetailsContract.IView) getView();
                view.uploadSelfSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                MarketDetailsContract.IView view = (MarketDetailsContract.IView) getView();
                view.uploadSelfError();
            }
        });
    }
}
