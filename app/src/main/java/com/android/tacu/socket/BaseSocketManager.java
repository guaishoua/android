package com.android.tacu.socket;

import android.text.TextUtils;

import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.transaction.model.RecordModel;
import com.android.tacu.module.transaction.model.UserAccountModel;
import com.android.tacu.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.List;
import java.util.Observable;

/**
 * Created by jiazhen on 2018/8/1.
 */

public class BaseSocketManager extends Observable implements IEmitterListener {

    private Gson gson = new Gson();

    private void coinAllList(List<MarketNewModel> marketModelList) {
        setChanged();
        ObserverModel model = new ObserverModel();
        ObserverModel.CoinAllList coinAllList = new ObserverModel.CoinAllList();
        model.setEventType(SocketConstant.LOGINAFTERNEW);
        coinAllList.setMarketModelList(marketModelList);
        model.setCoinAllList(coinAllList);
        notifyObservers(model);
    }

    private void loginAfterChangeTradeCoin(CurrentTradeCoinModel coinModel) {
        setChanged();
        ObserverModel model = new ObserverModel();
        ObserverModel.LoginAfterChangeTradeCoin tradeCoin = new ObserverModel.LoginAfterChangeTradeCoin();
        model.setEventType(SocketConstant.LOGINAFTERCHANGETRADECOIN);
        tradeCoin.setCoinModel(coinModel);
        model.setTradeCoin(tradeCoin);
        notifyObservers(model);
    }

    private void userAccount(UserAccountModel accountModel) {
        setChanged();
        ObserverModel model = new ObserverModel();
        ObserverModel.UserAccount userAccount = new ObserverModel.UserAccount();
        model.setEventType(SocketConstant.USERACCOUNT);
        userAccount.setAccountModel(accountModel);
        model.setUserAccount(userAccount);
        notifyObservers(model);
    }

    private void entrust(RecordModel recordModel) {
        setChanged();
        ObserverModel model = new ObserverModel();
        ObserverModel.Entrust entrust = new ObserverModel.Entrust();
        model.setEventType(SocketConstant.ENTRUST);
        entrust.setRecordModel(recordModel);
        model.setEntrust(entrust);
        notifyObservers(model);
    }

    private void tradeHistory(JSONObject data) {
        setChanged();
        ObserverModel model = new ObserverModel();
        ObserverModel.TradeHistory tradeHistory = new ObserverModel.TradeHistory();
        model.setEventType(SocketConstant.TRADEHISTORY);
        tradeHistory.setData(data);
        model.setTradeHistory(tradeHistory);
        notifyObservers(model);
    }

    @Override
    public void emitterListenerResut(String key, Object... args) {
        try {
            JSONObject data;
            String value = String.valueOf(args[0]);

            if (key != SocketConstant.LOGINAFTERNEW) {
                LogUtils.i("Socket", "key:" + key + " ,,value:" + value);
            }
            if (!TextUtils.isEmpty(value)) {
                switch (key) {
                    case SocketConstant.LOGINAFTERNEW:
                        LogUtils.i("loginAfter", "key:" + SocketConstant.LOGINAFTERNEW + " ,,value:" + value);
                        List<MarketNewModel> marketModelList = gson.fromJson(value, new TypeToken<List<MarketNewModel>>() {
                        }.getType());
                        coinAllList(marketModelList);
                        break;
                    case SocketConstant.LOGINAFTERCHANGETRADECOIN:
                        CurrentTradeCoinModel coinModel = gson.fromJson(value, CurrentTradeCoinModel.class);
                        loginAfterChangeTradeCoin(coinModel);
                        break;
                    case SocketConstant.USERACCOUNT:
                        UserAccountModel accountModel = gson.fromJson(value, UserAccountModel.class);
                        userAccount(accountModel);
                        break;
                    case SocketConstant.ENTRUST:
                        RecordModel recordModel = gson.fromJson(value, RecordModel.class);
                        entrust(recordModel);
                        break;
                    case SocketConstant.TRADEHISTORY:
                        data = (JSONObject) args[0];
                        tradeHistory(data);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
