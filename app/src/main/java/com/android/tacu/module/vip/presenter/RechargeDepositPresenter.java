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
import com.android.tacu.module.vip.model.SelectBondModel;

import java.util.List;

/**
 * Created by xiaohong on 2018/8/24.
 */
public class RechargeDepositPresenter extends BaseMvpPresenter implements RechargeDepositContract.IPresenter {

    @Override
    public void customerCoinByOneCoin(boolean isShowView, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<BaseModel<Double>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<Double> o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.customerCoinByOneCoin(o.attachment);
            }
        });
    }

    @Override
    public void BondAccount(boolean isShowView, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).BondAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.BondAccount(o.attachment);
            }
        });
    }

    @Override
    public void otcAmount(boolean isShowView, int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.otcAmount(o.attachment);
            }
        });
    }

    @Override
    public void selectBond() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBond(), new NetDisposableObserver<BaseModel<List<SelectBondModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<SelectBondModel>> o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.selectBond(o.attachment);
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
    public void otcToBond(String amount, int currencyId, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).otcToBond(amount, currencyId, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.otcToBondSuccess();
            }
        });
    }

    @Override
    public void bondToOtc(String amount, int currencyId, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).bondToOtc(amount, currencyId, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RechargeDepositContract.IView view = (RechargeDepositContract.IView) getView();
                view.BondToOtcSuccess();
            }
        });
    }

    @Override
    public void selectBondRecord(boolean isShowview) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBondRecord(null), new NetDisposableObserver<BaseModel<List<BondRecordModel>>>((IBaseMvpView) getView(), isShowview) {
            @Override
            public void onNext(BaseModel<List<BondRecordModel>> o) {
                RechargeDepositContract.IRecordView view = (RechargeDepositContract.IRecordView) getView();
                view.selectBondRecord(o.attachment);
            }
        });
    }

    @Override
    public void cancelBondRecord(String id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).cancelBondRecord(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                RechargeDepositContract.IRecordView view = (RechargeDepositContract.IRecordView) getView();
                view.cancelBondRecordSuccess();
            }
        });
    }
}
