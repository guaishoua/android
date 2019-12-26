package com.android.tacu.module.market.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/9/13.
 */
public class MarketContract {

    public interface IMarketListiew extends IBaseMvpView {
    }

    public interface IMarketLiew extends IBaseMvpView {
    }

    public interface ISelfView extends IBaseMvpView {
        void getSelfSelectionValue(SelfModel selfModel);
    }

    public interface IPresenter {
        /**
         * 获取自选列表
         */
        void getSelfList();

        /**
         * 最新价排序
         * type : up升序 down降序
         */
        void sortLastPrice(List<MarketNewModel.TradeCoinsBean> tradesList, String type);

        /**
         * 涨跌幅
         * type : up升序 down降序
         */
        void sortHour(List<MarketNewModel.TradeCoinsBean> tradesList, String type);

        /**
         * 名称排序升序
         */
        void sortPair(List<MarketNewModel.TradeCoinsBean> tradesList, String type);

        /**
         * 成交量排序降序
         */
        void sortVol(List<MarketNewModel.TradeCoinsBean> tradesList, String type);

        /**
         * 给list排序
         * 名称/成交量：pairVol 最新价：lastPrice 24H成交量：24Hour
         */
        void sortList(String currentStatus, int pairVolStatus, int lastPriceStatus, int hourStatus, List<MarketNewModel.TradeCoinsBean> tradesList);
    }
}
