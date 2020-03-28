package com.android.tacu.module.transaction.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.transaction.contract.TradeContract;
import com.android.tacu.module.vip.model.VipDetailRankModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/9/30.
 */
public class TradePresenter extends BaseMvpPresenter implements TradeContract.IPresenter {
    /**
     * 提交自选列表
     *
     * @param checkJson
     */
    @Override
    public void uploadSelfList( String checkJson) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).uploadSelfList(checkJson, 3), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                TradeContract.IView view = (TradeContract.IView) getView();
                view.uploadSelfSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                TradeContract.IView view = (TradeContract.IView) getView();
                view.uploadSelfError();
            }
        });
    }

    @Override
    public void order(int buyOrSell, int currencyId, String fdPassword, String num, String price, int type, int baseCurrencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ORDER, Api.class).order(buyOrSell, currencyId, fdPassword, num, price, type, baseCurrencyId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel moder) {
                TradeContract.IView view = (TradeContract.IView) getView();
                view.buySuccess();
            }
        });
    }

    @Override
    public void updateFdPwdEnabled(int enabled, String fdPwd) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).updateFdPwdEnabled(enabled, fdPwd), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                TradeContract.IView view = (TradeContract.IView) getView();
                view.updateFdPwdSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                TradeContract.IView view = (TradeContract.IView) getView();
                view.updateFdPwdError();
            }
        });
    }

    @Override
    public void selectVipDetail() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectVipDetail(), new NetDisposableObserver<BaseModel<List<VipDetailRankModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<VipDetailRankModel>> model) {
                TradeContract.IView view = (TradeContract.IView) getView();
                view.selectVipDetail(model.attachment);
            }
        });
    }
}
