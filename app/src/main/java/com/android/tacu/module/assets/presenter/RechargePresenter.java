package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.RechargeContract;
import com.android.tacu.module.assets.model.CoinAddressModel;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class RechargePresenter extends BaseMvpPresenter implements RechargeContract.IPresenter {

    @Override
    public void selectUserAddress(int walletType, int currentyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).selectUserAddress(walletType, currentyId), new NetDisposableObserver<BaseModel<CoinAddressModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<CoinAddressModel> coinModel) {
                RechargeContract.IRechargeView view = (RechargeContract.IRechargeView) getView();
                view.showCoinsAddress(coinModel.attachment);
            }
        });
    }

    @Override
    public void rechargeGrin(String txid, String amount) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).rechargeGrin(txid, amount), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                RechargeContract.IRechargeView view = (RechargeContract.IRechargeView) getView();
                view.showRechargeSuccess(model.message);
            }
        });
    }
}
