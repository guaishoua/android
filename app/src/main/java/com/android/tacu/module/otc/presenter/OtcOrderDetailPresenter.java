package com.android.tacu.module.otc.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;

public class OtcOrderDetailPresenter extends BaseMvpPresenter implements OtcOrderDetailContract.IPresenter {

    @Override
    public void selectTradeOne(boolean isShowView, final boolean isFirst, String orderNo) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectTradeOne(orderNo), new NetDisposableObserver<BaseModel<OtcTradeModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<OtcTradeModel> model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.selectTradeOne(isFirst, model.attachment);
            }
        });
    }

    @Override
    public void userBaseInfo(final Integer queryUid) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).userBaseInfo(queryUid), new NetDisposableObserver<BaseModel<OtcMarketInfoModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<OtcMarketInfoModel> model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.userBaseInfo(model.attachment);
            }
        });
    }

    @Override
    public void currentTime() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).currentTime(), new NetDisposableObserver<BaseModel<Long>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<Long> model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.currentTime(model.attachment);
            }
        });
    }

    @Override
    public void selectPayInfoById(String id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).selectPayInfoById(id), new NetDisposableObserver<BaseModel<PayInfoModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<PayInfoModel> model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.selectPayInfoById(model.attachment);
            }
        });
    }

    @Override
    public void uselectUserInfo(String headImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).uselectUserInfo(headImg), new NetDisposableObserver<BaseModel<String>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<String> o) {
                OtcOrderDetailContract.IView wView = (OtcOrderDetailContract.IView) getView();
                wView.uselectUserInfo(o.attachment);
            }
        });
    }

    @Override
    public void uselectUserInfoArbitration(final int type, String headImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).uselectUserInfo(headImg), new NetDisposableObserver<BaseModel<String>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<String> o) {
                OtcOrderDetailContract.IView wView = (OtcOrderDetailContract.IView) getView();
                wView.uselectUserInfoArbitration(type, o.attachment);
            }
        });
    }

    @Override
    public void getOssSetting() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getSts(), new NetDisposableObserver<BaseModel<AuthOssModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuthOssModel> model) {
                OtcOrderDetailContract.IAView wView = (OtcOrderDetailContract.IAView) getView();
                wView.getOssSetting(model.attachment);
            }
        });
    }

    @Override
    public void confirmOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.confirmOrderSuccess();
            }
        });
    }

    @Override
    public void confirmCancelOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).confirmCancelOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.confirmCancelOrderSuccess();
            }
        });
    }

    @Override
    public void payOrder(String orderId, String payImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).payOrder(orderId, payImg), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IView wView = (OtcOrderDetailContract.IView) getView();
                wView.payOrderSuccess();
            }
        });
    }

    @Override
    public void payCancelOrder(String orderId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).payCancelOrder(orderId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IView wView = (OtcOrderDetailContract.IView) getView();
                wView.payCancelOrderSuccess();
            }
        });
    }

    @Override
    public void finishOrder(String orderId, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).finishOrder(orderId, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.finishOrderSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.finishOrderFailure();
            }
        });
    }

    @Override
    public void arbitrationOrder(String id, String arbitrateExp, String arbitrateImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).arbitrationOrder(id, arbitrateExp, arbitrateImg), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IAView wView = (OtcOrderDetailContract.IAView) getView();
                wView.arbitrationOrderSuccess();
            }
        });
    }

    @Override
    public void beArbitrationOrder(String id, String beArbitrateExp, String beArbitrateImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).beArbitrationOrder(id, beArbitrateExp, beArbitrateImg), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IAView wView = (OtcOrderDetailContract.IAView) getView();
                wView.beArbitrationOrderSuccess();
            }
        });
    }

    @Override
    public void arbitrationOrderAgain(String id, String beArbitrateExp, String beArbitrateImg) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).arbitrationOrderAgain(id, beArbitrateExp, beArbitrateImg), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IAView wView = (OtcOrderDetailContract.IAView) getView();
                wView.arbitrationOrderAgainSuccess();
            }
        });
    }

    @Override
    public void arbitrationOrderCancel(String id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).arbitrationOrderCancel(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.arbitrationOrderCancelSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);

                OtcOrderDetailContract.IView view = (OtcOrderDetailContract.IView) getView();
                view.arbitrationOrderCancelFailure();
            }
        });
    }
}
