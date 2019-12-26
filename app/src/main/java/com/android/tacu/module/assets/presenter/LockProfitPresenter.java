package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.LockProfitContract;
import com.android.tacu.module.assets.model.CashChexAndRateModel;
import com.android.tacu.module.assets.model.LockChexAmountModel;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class LockProfitPresenter extends BaseMvpPresenter implements LockProfitContract.IPresenter {
    @Override
    public void getLockChexAmount(boolean isShow) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getLockChexAmount(), new NetDisposableObserver<BaseModel<LockChexAmountModel>>((IBaseMvpView) getView(), isShow) {
            @Override
            public void onNext(BaseModel<LockChexAmountModel> model) {
                LockProfitContract.IView view = (LockProfitContract.IView) getView();
                view.setLockChexAmount(model.attachment);
            }
        });
    }

    @Override
    public void getCashChexAndRate(boolean isShow) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getCashChexAndRate(), new NetDisposableObserver<BaseModel<CashChexAndRateModel>>((IBaseMvpView) getView(), isShow) {
            @Override
            public void onNext(BaseModel<CashChexAndRateModel> model) {
                LockProfitContract.IView view = (LockProfitContract.IView) getView();
                view.setCashChexAndRate(model.attachment);
            }
        });
    }

    @Override
    public void lockChex(String num, int dateVal, String fdPwd) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).lockChex(num, dateVal, fdPwd), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                LockProfitContract.IView view = (LockProfitContract.IView) getView();
                view.lockChexSuccess();
            }
        });
    }
}
