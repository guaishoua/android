package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.MoneyFlowContract;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.MoneyFlowModel;

/**
 * Created by xiaohong on 2018/10/20.
 */

public class MoneyFlowPresenter extends BaseMvpPresenter implements MoneyFlowContract.IFlowPresenter {
    @Override
    public void coins(boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).coins(), new NetDisposableObserver<CoinListModel>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(CoinListModel coinsList) {
                MoneyFlowContract.IView view = (MoneyFlowContract.IView) getView();
                view.currencyView(coinsList.attachment);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void selectTakeList(int start, int size, String type, Integer currentyId, String beginTime, String endTime, boolean isLoadding) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).selectTakeList(start, size, type, currentyId, beginTime, endTime), new NetDisposableObserver<BaseModel<MoneyFlowModel>>((IBaseMvpView) getView(), isLoadding) {
            @Override
            public void onNext(BaseModel<MoneyFlowModel> takeModel) {
                MoneyFlowContract.IView view = (MoneyFlowContract.IView) getView();
                view.showTakeCoinList(takeModel.attachment);
            }
        });
    }
}