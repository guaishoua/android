package com.android.tacu.module.vip.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.vip.contract.BuyVipContract;
import com.android.tacu.module.vip.model.VipDetailModel;
import com.android.tacu.module.vip.model.VipDetailRankModel;

import java.util.List;

/**
 * Created by xiaohong on 2018/8/24.
 */
public class BuyVipPresenter extends BaseMvpPresenter implements BuyVipContract.IPresenter {

    @Override
    public void selectVip() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectVip(), new NetDisposableObserver<BaseModel<VipDetailModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<VipDetailModel> model) {
                BuyVipContract.IView view = (BuyVipContract.IView) getView();
                view.selectVip(model.attachment);
            }
        });
    }

    @Override
    public void selectVipDetail() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectVipDetail(), new NetDisposableObserver<BaseModel<List<VipDetailRankModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<VipDetailRankModel>> model) {
                BuyVipContract.IView view = (BuyVipContract.IView) getView();
                view.selectVipDetail(model.attachment);
            }
        });
    }

    @Override
    public void buyVip(Integer type, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).buyVip(type, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                BuyVipContract.IChildView view = (BuyVipContract.IChildView) getView();
                view.buyVipSuccess();
            }
        });
    }

    @Override
    public void otcAmount(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).OtcAccount(currencyId), new NetDisposableObserver<BaseModel<OtcAmountModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OtcAmountModel> o) {
                BuyVipContract.IView view = (BuyVipContract.IView) getView();
                view.otcAmount(o.attachment);
            }
        });
    }

    @Override
    public void cancleVip() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).cancleVip(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                BuyVipContract.IChildView view = (BuyVipContract.IChildView) getView();
                view.cancleVipSuccess();
            }
        });
    }
}
