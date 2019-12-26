package com.android.tacu.socket;

import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.transaction.model.RecordModel;
import com.android.tacu.module.transaction.model.UserAccountModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 观察者实体类
 * Created by jiazhen on 2018/8/1.
 */
public class ObserverModel {

    private String mEventType;
    private CoinAllList mCoinAllList;
    private LoginAfterChangeTradeCoin mTradeCoin;
    private UserAccount mUserAccount;
    private Entrust mEntrust;
    private TradeHistory mTradeHistory;

    public String getEventType() {
        return mEventType;
    }

    public void setEventType(String mEventType) {
        this.mEventType = mEventType;
    }

    public CoinAllList getCoinAllList() {
        return mCoinAllList;
    }

    public void setCoinAllList(CoinAllList mCoinAllList) {
        this.mCoinAllList = mCoinAllList;
    }

    public LoginAfterChangeTradeCoin getTradeCoin() {
        return mTradeCoin;
    }

    public void setTradeCoin(LoginAfterChangeTradeCoin mTradeCoin) {
        this.mTradeCoin = mTradeCoin;
    }

    public UserAccount getUserAccount() {
        return mUserAccount;
    }

    public void setUserAccount(UserAccount mUserAccount) {
        this.mUserAccount = mUserAccount;
    }

    public Entrust getEntrust() {
        return mEntrust;
    }

    public void setEntrust(Entrust mEntrust) {
        this.mEntrust = mEntrust;
    }

    public TradeHistory getTradeHistory() {
        return mTradeHistory;
    }

    public void setTradeHistory(TradeHistory mTradeHistory) {
        this.mTradeHistory = mTradeHistory;
    }

    public static class CoinAllList {
        private List<MarketNewModel> marketModelList = new ArrayList<>();

        public List<MarketNewModel> getMarketModelList() {
            return marketModelList;
        }

        public void setMarketModelList(List<MarketNewModel> marketModelList) {
            this.marketModelList = marketModelList;
        }
    }

    public static class LoginAfterChangeTradeCoin {
        private CurrentTradeCoinModel coinModel;

        public CurrentTradeCoinModel getCoinModel() {
            return coinModel;
        }

        public void setCoinModel(CurrentTradeCoinModel coinModel) {
            this.coinModel = coinModel;
        }
    }

    public static class UserAccount {
        private UserAccountModel accountModel;

        public UserAccountModel getAccountModel() {
            return accountModel;
        }

        public void setAccountModel(UserAccountModel accountModel) {
            this.accountModel = accountModel;
        }
    }

    public static class Entrust{
        private RecordModel recordModel;

        public RecordModel getRecordModel() {
            return recordModel;
        }

        public void setRecordModel(RecordModel recordModel) {
            this.recordModel = recordModel;
        }
    }

    public static class TradeHistory {
        private JSONObject data;

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }
    }
}
