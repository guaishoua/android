package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.contract.OtcManageContract;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;

public class OtcManagePresenter extends BaseMvpPresenter implements OtcManageContract.IPresenter {
    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                OtcManageContract.IView view = (OtcManageContract.IView) getView();
                view.ownCenter(model.attachment);
            }
        });
    }

    @Override
    public void orderListOwn(boolean isShowView, Integer start, Integer size, Integer buyorsell) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).orderListOwn(start, size, buyorsell), new NetDisposableObserver<BaseModel<OtcMarketOrderListModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcMarketOrderListModel> model) {
                OtcManageContract.IChildView view = (OtcManageContract.IChildView) getView();
                view.orderListOwn(model.attachment);
            }
        });
    }

    @Override
    public void cancelOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).cancelOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageContract.IChildView view = (OtcManageContract.IChildView) getView();
                view.cancelOrderSuccess();
            }
        });
    }

    @Override
    public void merchantoff() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).merchantoff(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageContract.IView view = (OtcManageContract.IView) getView();
                view.merchantoff();
            }
        });
    }

    @Override
    public void merchanton() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).merchanton(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageContract.IView view = (OtcManageContract.IView) getView();
                view.merchanton();
            }
        });
    }

    @Override
    public void unshow(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).unshow(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageContract.IChildView view = (OtcManageContract.IChildView) getView();
                view.unshow();
            }
        });
    }

    @Override
    public void show(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).show(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcManageContract.IChildView view = (OtcManageContract.IChildView) getView();
                view.show();
            }
        });
    }
}
