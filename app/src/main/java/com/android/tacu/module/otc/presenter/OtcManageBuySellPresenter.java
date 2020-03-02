package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcManageBuySellContract;
import com.android.tacu.module.otc.model.OtcPublishParam;
import com.android.tacu.module.otc.model.OtcSelectFeeModel;

import java.util.List;

public class OtcManageBuySellPresenter extends BaseMvpPresenter implements OtcManageBuySellContract.IPresenter {
    @Override
    public void selectBank() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectBank(), new NetDisposableObserver<BaseModel<List<PayInfoModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<PayInfoModel>> o) {
                OtcManageBuySellContract.IView view = (OtcManageBuySellContract.IView) getView();
                view.selectBank(o.attachment);
            }
        });
    }

    @Override
    public void selectFee(Integer currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectFee(currencyId), new NetDisposableObserver<BaseModel<OtcSelectFeeModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<OtcSelectFeeModel> model) {
                OtcManageBuySellContract.IChildView view = (OtcManageBuySellContract.IChildView) getView();
                view.selectFee(model.attachment);
            }
        });
    }

    @Override
    public void BondAccount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).BondAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                OtcManageBuySellContract.IChildView view = (OtcManageBuySellContract.IChildView) getView();
                view.BondAccount(o.attachment);
            }
        });
    }

    @Override
    public void OtcAccount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                OtcManageBuySellContract.IChildView view = (OtcManageBuySellContract.IChildView) getView();
                view.OtcAccount(o.attachment);
            }
        });
    }

    @Override
    public void order(OtcPublishParam param) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).order(param.buyorsell, param.currencyId, param.money, param.timeOut, param.price, param.num, param.amount, param.lowLimit, param.highLimit, param.explain, param.payByCard, param.payWechat, param.payAlipay, param.fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                OtcManageBuySellContract.IChildView view = (OtcManageBuySellContract.IChildView) getView();
                view.orderSuccess();
            }
        });
    }
}
