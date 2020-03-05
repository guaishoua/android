package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.RecordContract;
import com.android.tacu.module.assets.model.ChargeModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.TakeCoinListModel;

/**
 * Created by xiaohong on 2018/8/29.
 */

public class RecordPresenter extends BaseMvpPresenter implements RecordContract.IRecordPresenter {
    /**
     * 充币记录
     */
    @Override
    public void selectListByUuid(String beginTime, String endTime, int size, Integer currencyId, int start, int status, boolean isLoadding) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).selectListByUuid(beginTime, endTime, size, currencyId, start, status), new NetDisposableObserver<BaseModel<ChargeModel>>((IBaseMvpView) getView(), isLoadding) {
            @Override
            public void onNext(BaseModel<ChargeModel> chargeModel) {
                RecordContract.IRecordView view = (RecordContract.IRecordView) getView();
                view.showChargeCoinList(chargeModel.attachment);
            }
        });
    }

    /**
     * 提币记录
     */
    @Override
    public void selectTakeList(String beginTime, String endTime, int size, Integer currentyId, int start, int status, boolean isLoadding) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).takeList(beginTime, endTime, size, currentyId, start, status), new NetDisposableObserver<BaseModel<TakeCoinListModel>>((IBaseMvpView) getView(), isLoadding) {
            @Override
            public void onNext(BaseModel<TakeCoinListModel> takeModel) {
                RecordContract.IRecordView view = (RecordContract.IRecordView) getView();
                view.showTakeCoinList(takeModel.attachment);
            }
        });
    }

    @Override
    public void coins(boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).coins(), new NetDisposableObserver<CoinListModel>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(CoinListModel coinsList) {
                RecordContract.IRecordView view = (RecordContract.IRecordView) getView();
                view.currencyView(coinsList.attachment);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }
}
