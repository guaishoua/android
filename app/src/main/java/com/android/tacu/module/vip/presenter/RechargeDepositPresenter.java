package com.android.tacu.module.vip.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.vip.contract.RechargeDepositContract;
import com.android.tacu.module.vip.model.BondRecordModel;

import java.util.List;

/**
 * Created by xiaohong on 2018/8/24.
 */
public class RechargeDepositPresenter extends BaseMvpPresenter implements RechargeDepositContract.IPresenter {

    @Override
    public void customerCoinByOneCoin(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<BaseModel<Double>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<Double> o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.customerCoinByOneCoin(o.attachment);
            }
        });
    }

    @Override
    public void BondAccount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).BondAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.BondAccount(o.attachment);
            }
        });
    }

    @Override
    public void CcToBond(String amount, int currencyId, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).CcToBond(amount, currencyId, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.CcToBondSuccess();
            }
        });
    }

    @Override
    public void BondToCc(String amount, int currencyId, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).BondToCc(amount, currencyId, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.BondToCcSuccess();
            }
        });
    }

    @Override
    public void selectBondRecord() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBondRecord(null), new NetDisposableObserver<BaseModel<List<BondRecordModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<BondRecordModel>> o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.selectBondRecord(o.attachment);
            }
        });
    }
}
