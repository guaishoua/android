package com.android.tacu.module.main.presenter;

import android.text.TextUtils;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.main.contract.HomeContract;
import com.android.tacu.module.main.model.HomeModel;
import com.android.tacu.module.main.model.UuexSignModal;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.NoticeModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jiazhen on 2018/9/6.
 */
public class HomePresenter extends BaseMvpPresenter implements HomeContract.IPresenter {

    @Override
    public void getHome(boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getHome(), new NetDisposableObserver<BaseModel<HomeModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<HomeModel> model) {
                HomeContract.IView view = (HomeContract.IView) getView();
                view.home(model.attachment);
            }
        });
    }

    @Override
    public void getNoticeInfo() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ZEN, Api.class).getNoticeInfo(1, 4, 4), new NetDisposableObserver<BaseModel<List<NoticeModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<NoticeModel>> model) {
                HomeContract.IView view = (HomeContract.IView) getView();
                view.showNoticeList(model.attachment);
            }
        });
    }

    @Override
    public void uuexSign() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.UUEX, Api.class).uuexSign(), new NetDisposableObserver<BaseModel<UuexSignModal>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<UuexSignModal> model) {
                HomeContract.IView view = (HomeContract.IView) getView();
                view.uuexSign(model.attachment);
            }
        });
    }

    /**
     * 最新价排序
     * type : up升序 down降序
     */
    @Override
    public void sortLastPrice(List<MarketNewModel.TradeCoinsBean> tradesList, final String type) {
        Collections.sort(tradesList, new Comparator<MarketNewModel.TradeCoinsBean>() {
            @Override
            public int compare(MarketNewModel.TradeCoinsBean o1, MarketNewModel.TradeCoinsBean o2) {
                if (o1.currentAmount > o2.currentAmount) {
                    if (TextUtils.equals(type, "up")) {
                        return 1;
                    } else if (TextUtils.equals(type, "down")) {
                        return -1;
                    }
                    return 1;
                } else if (o1.currentAmount == o2.currentAmount) {
                    return 0;
                }
                if (TextUtils.equals(type, "up")) {
                    return -1;
                } else if (TextUtils.equals(type, "down")) {
                    return 1;
                }
                return -1;
            }
        });
    }

    /**
     * 涨跌幅
     * type : up升序 down降序
     */
    @Override
    public void sortHour(List<MarketNewModel.TradeCoinsBean> tradesList, final String type) {
        Collections.sort(tradesList, new Comparator<MarketNewModel.TradeCoinsBean>() {
            @Override
            public int compare(MarketNewModel.TradeCoinsBean o1, MarketNewModel.TradeCoinsBean o2) {
                if (o1.changeRate > o2.changeRate) {
                    if (TextUtils.equals(type, "up")) {
                        return 1;
                    } else if (TextUtils.equals(type, "down")) {
                        return -1;
                    }
                    return 1;
                } else if (o1.changeRate == o2.changeRate) {
                    return 0;
                }
                if (TextUtils.equals(type, "up")) {
                    return -1;
                } else if (TextUtils.equals(type, "down")) {
                    return 1;
                }
                return -1;
            }
        });
    }

    /**
     * 名称排序
     * type : up升序 down降序
     */
    @Override
    public void sortPair(List<MarketNewModel.TradeCoinsBean> tradesList, final String type) {
        Collections.sort(tradesList, new Comparator<MarketNewModel.TradeCoinsBean>() {
            @Override
            public int compare(MarketNewModel.TradeCoinsBean o1, MarketNewModel.TradeCoinsBean o2) {
                if (TextUtils.equals(type, "up")) {
                    return o1.currencyNameEn.compareTo(o2.currencyNameEn);
                } else if (TextUtils.equals(type, "down")) {
                    return o2.currencyNameEn.compareTo(o1.currencyNameEn);
                }
                return -1;
            }
        });
    }

    /**
     * 成交量排序降序
     */
    @Override
    public void sortVol(List<MarketNewModel.TradeCoinsBean> tradesList, final String type) {
        Collections.sort(tradesList, new Comparator<MarketNewModel.TradeCoinsBean>() {
            @Override
            public int compare(MarketNewModel.TradeCoinsBean o1, MarketNewModel.TradeCoinsBean o2) {
                if (o1.volume > o2.volume) {
                    if (TextUtils.equals(type, "up")) {
                        return 1;
                    } else if (TextUtils.equals(type, "down")) {
                        return -1;
                    }
                    return 1;
                } else if (o1.volume == o2.volume) {
                    return 0;
                }
                if (TextUtils.equals(type, "up")) {
                    return -1;
                } else if (TextUtils.equals(type, "down")) {
                    return 1;
                }
                return -1;
            }
        });
    }

    /**
     * 给list排序
     * 名称/成交量：pairVol 最新价：lastPrice 24H成交量：24Hour
     */
    @Override
    public void sortList(String currentStatus, int pairVolStatus, int lastPriceStatus, int hourStatus, List<MarketNewModel.TradeCoinsBean> tradesList) {
        switch (currentStatus) {
            case "pairVol":
                if (pairVolStatus == 1) {
                    sortVol(tradesList, "down");
                } else if (pairVolStatus == 2) {
                    sortPair(tradesList, "up");
                }
                break;
            case "lastPrice":
                if (lastPriceStatus == 1) {
                    sortLastPrice(tradesList, "down");
                } else if (lastPriceStatus == 2) {
                    sortLastPrice(tradesList, "up");
                }
                break;
            case "24Hour":
                if (hourStatus == 1) {
                    sortHour(tradesList, "down");
                } else if (hourStatus == 2) {
                    sortHour(tradesList, "up");
                }
                break;
        }
    }
}
