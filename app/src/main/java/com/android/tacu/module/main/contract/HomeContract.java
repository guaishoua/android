package com.android.tacu.module.main.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.HomeModel;
import com.android.tacu.module.main.model.UuexSignModal;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.NoticeModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/9/6.
 */
public class HomeContract {

    public interface IView extends IBaseMvpView {
        void home(HomeModel homeModel);

        void showNoticeList(List<NoticeModel> list);

        void uuexSign(UuexSignModal modal);
    }

    public interface IPresenter {
        void getHome(boolean isShowLoadingView);

        void getNoticeInfo();

        void uuexSign();

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
