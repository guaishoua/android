package com.android.tacu.module.assets.presenter;


import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.CoinsContract;
import com.android.tacu.module.assets.model.SelectTakeCoinAddressModel;

/**
 * Created by xiaohong on 2018/8/21.
 */

public class CoinsPresenter extends BaseMvpPresenter implements CoinsContract.IRechargePresenter {

    @Override
    public void selectTakeCoin(int currentyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).selectTakeCoin(currentyId), new NetDisposableObserver<BaseModel<SelectTakeCoinAddressModel>>((IBaseMvpView) getView(), true) {
            @Override
            public void onNext(BaseModel<SelectTakeCoinAddressModel> o) {
                CoinsContract.ITakeCoinView view = (CoinsContract.ITakeCoinView) getView();
                view.showCoinListAddress(o.attachment);
            }
        });
    }

    @Override
    public void updateCoinAddress(int currentyId, String walletAddressId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).updateCoinAddress(currentyId, walletAddressId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                CoinsContract.ITakeCoinView view = (CoinsContract.ITakeCoinView) getView();
                view.delCoinAddress();
            }
        });
    }

    @Override
    public void emailTakeCoin(int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).emailTakeCoin(type), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel coinsAddress) {
                CoinsContract.ITakeCoinView view = (CoinsContract.ITakeCoinView) getView();
                view.success();
            }
        });
    }

    @Override
    public void takeCoin(String actionId, String address, String amount, int currentyId, String fdPwd, String note, String emailCode, String gAuth, String msgCode) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).takeCoin(actionId, address, amount, currentyId, fdPwd, note, emailCode, gAuth, msgCode), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                CoinsContract.ITakeCoinView view = (CoinsContract.ITakeCoinView) getView();
                view.takeCoinSuccess();
            }
        });
    }

    @Override
    public void insertTakeAddress(String address, int currentyId, String note) {
//        后期接口修改之后记得去掉，交易密码
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).insertTakeAddress(address, currentyId, note), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                CoinsContract.ITakeCoinView view = (CoinsContract.ITakeCoinView) getView();
                view.addressSuccess();
            }
        });
    }
}