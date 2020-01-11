package com.android.tacu.module.auctionplus.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusWinLisyModel;

import java.util.List;

/**
 * Created by jiazhen on 2019/6/3.
 */
public class AuctionWinRecordConstract {

    public interface IView extends IBaseMvpView {
        void currencyView(List<CoinListModel.AttachmentBean> attachment);

        void plusWinLisy(AuctionPlusWinLisyModel model);

        void customerCoinByOneCoin(AmountModel model, AuctionPlusWinLisyModel.Bean plusBean);

        void auctionPaySuccess();
    }

    public interface IPresenter {
        void coins(boolean isShowViewing);

        void plusWinLisy(int start, int payStatus, int currency, String beginTime, String endTime, boolean isShowViewing);

        void customerCoinByOneCoin(int currencyId, AuctionPlusWinLisyModel.Bean plusBean);

        void plusPay(String id, int type);
    }
}
