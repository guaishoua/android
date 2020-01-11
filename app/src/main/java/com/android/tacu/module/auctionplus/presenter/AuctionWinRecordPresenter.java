package com.android.tacu.module.auctionplus.presenter;


import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.auctionplus.contract.AuctionWinRecordConstract;
import com.android.tacu.module.auctionplus.modal.AuctionPlusWinLisyModel;

/**
 * Created by jiazhen on 2019/6/3.
 */
public class AuctionWinRecordPresenter extends BaseMvpPresenter implements AuctionWinRecordConstract.IPresenter {

    @Override
    public void coins(boolean isShowViewing) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).coins(), new NetDisposableObserver<CoinListModel>((IBaseMvpView) getView(), isShowViewing) {
            @Override
            public void onNext(CoinListModel coinsList) {
                AuctionWinRecordConstract.IView view = (AuctionWinRecordConstract.IView) getView();
                view.currencyView(coinsList.attachment);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void plusWinLisy(int start, int payStatus, int currency, String beginTime, String endTime, boolean isShowViewing) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).plusWinLisy(start, 10, payStatus, currency, beginTime, endTime), new NetDisposableObserver<BaseModel<AuctionPlusWinLisyModel>>((IBaseMvpView) getView(), isShowViewing) {
            @Override
            public void onNext(BaseModel<AuctionPlusWinLisyModel> model) {
                AuctionWinRecordConstract.IView view = (AuctionWinRecordConstract.IView) getView();
                view.plusWinLisy(model.attachment);
            }
        });
    }

    @Override
    public void customerCoinByOneCoin(final int currencyId, final AuctionPlusWinLisyModel.Bean plusBean) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<AmountModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(AmountModel o) {
                AuctionWinRecordConstract.IView view = (AuctionWinRecordConstract.IView) getView();
                view.customerCoinByOneCoin(o, plusBean);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void plusPay(String id, int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionPlusPay(id, type), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                AuctionWinRecordConstract.IView view = (AuctionWinRecordConstract.IView) getView();
                view.auctionPaySuccess();
            }
        });
    }
}
