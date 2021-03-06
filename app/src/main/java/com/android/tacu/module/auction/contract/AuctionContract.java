package com.android.tacu.module.auction.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.auction.model.AuctionLogsListModel;
import com.android.tacu.module.auction.model.AuctionModel;
import com.android.tacu.module.main.model.OwnCenterModel;

public class AuctionContract {

    public interface IDetailsView extends IBaseMvpView {
        void auctionDetail(AuctionModel model);

        void currentTime(Long time);

        void ownCenterSuccess(OwnCenterModel model);

        void auctionBuySuccess();

        void auctionBuyFailure();

        void auctionLogsAll(AuctionLogsListModel model);
    }

    public interface IRecordView extends IBaseMvpView {
        void auctionLogsAll(AuctionLogsListModel model);
    }

    public interface IMyView extends IBaseMvpView {
        void auctionDetail(AuctionModel model);

        void currentTime(Long time);

        void auctionLogs(AuctionLogsListModel model);
    }

    public interface IPresenter {
        void auctionDetail(int type, boolean isShowView);

        void currentTime(int type, boolean isShowView);

        void ownCenter();

        void auctionBuy(Integer id, String fdPassword);

        void auctionLogsAll(int type, Integer id, Integer start, Integer size);

        void auctionLogs(Integer id);
    }
}
