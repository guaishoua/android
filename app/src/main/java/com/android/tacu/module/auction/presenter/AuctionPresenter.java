package com.android.tacu.module.auction.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.exceptions.ResponseException;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.auction.contract.AuctionContract;
import com.android.tacu.module.auction.model.AuctionLogsListModel;
import com.android.tacu.module.auction.model.AuctionModel;
import com.android.tacu.module.main.model.OwnCenterModel;

public class AuctionPresenter extends BaseMvpPresenter implements AuctionContract.IPresenter {

    /**
     * @param type 1=详情 2=我的拍卖
     */
    @Override
    public void auctionDetail(final int type, boolean isShowView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONNEW, Api.class).auctionList(), new NetDisposableObserver<BaseModel<AuctionModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<AuctionModel> model) {
                if (type == 1) {
                    AuctionContract.IDetailsView view = (AuctionContract.IDetailsView) getView();
                    view.auctionDetail(model.attachment);
                } else if (type == 2) {
                    AuctionContract.IMyView view = (AuctionContract.IMyView) getView();
                    view.auctionDetail(model.attachment);
                }
            }
        });
    }

    /**
     * @param type 1=详情 2=我的拍卖
     */
    @Override
    public void currentTime(final int type, boolean isShowView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.OTCTACU, Api.class).currentTime(), new NetDisposableObserver<BaseModel<Long>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<Long> model) {
                if (type == 1) {
                    AuctionContract.IDetailsView view = (AuctionContract.IDetailsView) getView();
                    view.currentTime(model.attachment);
                } else if (type == 2) {
                    AuctionContract.IMyView view = (AuctionContract.IMyView) getView();
                    view.currentTime(model.attachment);
                }
            }
        });
    }

    @Override
    public void ownCenter() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).ownCenter(), new NetDisposableObserver<BaseModel<OwnCenterModel>>((IBaseMvpView) getView(), false, false) {
            @Override
            public void onNext(BaseModel<OwnCenterModel> model) {
                AuctionContract.IDetailsView view = (AuctionContract.IDetailsView) getView();
                view.ownCenterSuccess(model.attachment);
            }
        });
    }

    @Override
    public void auctionBuy(Integer id, String fdPassword) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONNEW, Api.class).auctionBuy(id, fdPassword), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), true, false) {
            @Override
            public void onNext(BaseModel model) {
                AuctionContract.IDetailsView view = (AuctionContract.IDetailsView) getView();
                view.auctionBuySuccess();
            }

            @Override
            protected void onErrorServer(ResponseException responseException) {
                super.onErrorServer(responseException);

                AuctionContract.IDetailsView view = (AuctionContract.IDetailsView) getView();
                if (responseException.status == 1001) {
                    view.auctionBuyFailure();
                } else {
                    view.showToastError(responseException.message);
                }
            }
        });
    }

    /**
     * @param type  1=详情 2=列表
     * @param id
     * @param start
     * @param size
     */
    @Override
    public void auctionLogsAll(final int type, Integer id, Integer start, Integer size) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONNEW, Api.class).auctionLogsAll(id, 0, start, size), new NetDisposableObserver<BaseModel<AuctionLogsListModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuctionLogsListModel> model) {
                if (type == 1) {
                    AuctionContract.IDetailsView view = (AuctionContract.IDetailsView) getView();
                    view.auctionLogsAll(model.attachment);
                } else if (type == 2) {
                    AuctionContract.IRecordView view = (AuctionContract.IRecordView) getView();
                    view.auctionLogsAll(model.attachment);
                }
            }
        });
    }

    @Override
    public void auctionLogs(Integer id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONNEW, Api.class).auctionLogs(id, 0, 1, 10), new NetDisposableObserver<BaseModel<AuctionLogsListModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuctionLogsListModel> model) {
                AuctionContract.IMyView view = (AuctionContract.IMyView) getView();
                view.auctionLogs(model.attachment);
            }
        });
    }
}
