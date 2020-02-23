package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.contract.OtcManageBuySellContract;
import com.android.tacu.module.otc.model.OtcPublishParam;
import com.android.tacu.module.otc.model.OtcSelectFeeModel;

public class OtcManageBuySellPresenter extends BaseMvpPresenter implements OtcManageBuySellContract.IPresenter {
    @Override
    public void selectFee(Integer currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectFee(currencyId), new NetDisposableObserver<BaseModel<OtcSelectFeeModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<OtcSelectFeeModel> model) {
                OtcManageBuySellContract.IView view = (OtcManageBuySellContract.IView) getView();
                view.selectFee(model.attachment);
            }
        });
    }

    @Override
    public void BondAccount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).BondAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                OtcManageBuySellContract.IView view = (OtcManageBuySellContract.IView) getView();
                view.BondAccount(o.attachment);
            }
        });
    }

    @Override
    public void order(OtcPublishParam param) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).order(param.buyorsell, param.currencyId, param.money, param.timeOut, param.price, param.num, param.amount, param.lowLimit, param.highLimit, param.explain, param.payByCard, param.payWechat, param.payAlipay, param.fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                OtcManageBuySellContract.IView view = (OtcManageBuySellContract.IView) getView();
                view.orderSuccess();
            }
        });
    }
}
