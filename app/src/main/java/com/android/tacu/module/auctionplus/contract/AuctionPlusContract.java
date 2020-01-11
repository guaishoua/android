package com.android.tacu.module.auctionplus.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.auctionplus.modal.AuctionPayStatusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusDataBaseModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListByIdModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusPayInfoModel;

import java.util.List;

/**
 * Created by jiazhen on 2019/6/4.
 */
public class AuctionPlusContract {

    public interface IListView extends IBaseMvpView {
        void auctionPlusList(AuctionPlusListModel model, boolean isTop, boolean isFlag);

        void auctionPlusData(AuctionPlusDataBaseModel model);

        void customerCoinByOneCoin(AmountModel model, AuctionPlusModel auctionModel);

        void auctionPlusListPay(List<AuctionPayStatusModel> list);

        void auctionBuySuccess();

        void auctionPayInfo(AuctionPlusPayInfoModel model, AuctionPlusModel auctionPlusModel, String balance);

        void auctionPaySuccess();
    }

    public interface IDetailView extends IBaseMvpView {
        void listPlusById(AuctionPlusListByIdModel model);

        void customerCoinByOneCoin(AmountModel model, AuctionPlusModel auctionModel, int currencyId);

        void collectCheck(boolean isCollect);

        void collectSuccess();

        void collectError();

        void auctionBuySuccess();

        void auctionPlusListPay(List<AuctionPayStatusModel> list);

        void auctionPayInfo(AuctionPlusPayInfoModel model, AuctionPlusModel auctionPlusModel, String balance);

        void auctionPaySuccess();
    }

    public interface IOfferView extends IBaseMvpView {
        void listPlusById(AuctionPlusListByIdModel model);
    }

    public interface IPresenter {
        /**
         * @param start
         * @param status
         * @param isTop  是否返回到头部
         */
        void auctionPlusList(int start, int size, int status, boolean isTop, boolean isFlag);

        void auctionPlusListByType(int start, int size, int type, boolean isTop, boolean isFlag);

        void auctionPlusListWait(int start, int size, boolean isTop, boolean isFlag);

        void auctionPlusData(String ids);

        void customerCoinByOneCoin(int currencyId, AuctionPlusModel model, int type);

        void auctionPlusListPay(String ids, int type);

        void listPlusById(int start, int size, String id, int type, boolean isShowLoadingView);

        void collectCheck(String id);

        void collect(String id);

        void auctionBuy(String id, String count, int type);

        void auctionPayInfo(AuctionPlusModel auctionPlusModel, String balance, int type);

        void auctionPay(String id, int type, int viewType);
    }
}
