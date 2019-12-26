package com.android.tacu.utils;

import android.text.TextUtils;
import android.util.SparseArray;

import com.android.tacu.api.Constant;
import com.android.tacu.module.main.model.ConvertModel;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.utils.user.UserInfoUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 汇率
 * Created by jiazhen on 2018/11/8.
 */
public class ConvertMoneyUtils {

    public static class CoinExchangeValue {
        int id;
        double rate;

        public CoinExchangeValue(int id, double rate) {
            this.id = id;
            this.rate = rate;
        }
    }

    private static UserInfoUtils spUtil = UserInfoUtils.getInstance();
    private static String convertMoneyString = spUtil.getConvertMoney();
    private static ConvertModel convertModel = null;
    private static ConvertModel.ConvertCurrencyListBean currentConvertBean = null;
    private static SparseArray<Double> convertSparseArray = new SparseArray<>();
    //运营活动 BTC/USDT的单价
    private static double currentAmount = 0;

    public static boolean hasValue() {
        if (convertModel != null && TextUtils.equals(spUtil.getConvertMoney(), convertMoneyString)) {
            return true;
        }
        return false;
    }

    /**
     * 缓存中取汇率相关的数据
     */
    public static void setSpConvertBean() {
        String convertString = SPUtils.getInstance().getString(Constant.CONVERT_CACHE);
        ConvertModel model = new Gson().fromJson(convertString, ConvertModel.class);
        if (model != null) {
            convertModel = model;
            ConvertModel.ConvertCurrencyListBean bean;
            for (int i = 0; i < model.convertCurrencyList.size(); i++) {
                bean = model.convertCurrencyList.get(i);
                if (TextUtils.equals(convertMoneyString, bean.name)) {
                    currentConvertBean = bean;
                }
            }
        }
    }

    /**
     * 接口中取汇率相关的数据
     */
    public static void setHttpConvertBean(ConvertModel model) {
        if (model != null) {
            convertModel = model;
            ConvertModel.ConvertCurrencyListBean bean;
            for (int i = 0; i < model.convertCurrencyList.size(); i++) {
                bean = model.convertCurrencyList.get(i);
                if (TextUtils.equals(convertMoneyString, bean.name)) {
                    currentConvertBean = bean;
                }
            }
        }
    }

    /**
     * 缓存中取基础币相对于牟定币的比值
     */
    public static void setSpBaseCoinScale() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = new Gson().fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());
        if (cacheList != null && cacheList.size() > 0) {
            convertSparseArray.clear();
            for (int i = 0; i < cacheList.size(); i++) {
                MarketNewModel marketModel = cacheList.get(i);
                if (marketModel.baseCoinList != null && marketModel.baseCoinList.size() > 0) {
                    for (int n = 0; n < marketModel.baseCoinList.size(); n++) {
                        convertSparseArray.put(marketModel.baseCoinList.get(n).currencyId, marketModel.baseCoinList.get(n).baseExchangeAmount);
                        if (TextUtils.equals(marketModel.baseCoinList.get(n).currencyNameEn, "USDT")) {
                            for (int j = 0; j < marketModel.tradeCoinsList.size(); j++) {
                                if (TextUtils.equals(marketModel.tradeCoinsList.get(j).currencyNameEn, "BTC")) {
                                    currentAmount = marketModel.tradeCoinsList.get(j).currentAmount;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 接口中取基础币相对于牟定币的比值
     */
    public static void setHttpBaseCoinScale(List<MarketNewModel> list) {
        if (list != null && list.size() > 0) {
            convertSparseArray.clear();
            for (int i = 0; i < list.size(); i++) {
                MarketNewModel marketModel = list.get(i);
                cacheDateToRateBase(marketModel.tradeCoinsAutoList);
                cacheDateToRateBase(marketModel.tradeCoinsFixedList);
                if (marketModel.baseCoinList != null && marketModel.baseCoinList.size() > 0) {
                    for (int n = 0; n < marketModel.baseCoinList.size(); n++) {
                        convertSparseArray.put(marketModel.baseCoinList.get(n).currencyId, marketModel.baseCoinList.get(n).baseExchangeAmount);
                        if (TextUtils.equals(marketModel.baseCoinList.get(n).currencyNameEn, "USDT")) {
                            for (int j = 0; j < marketModel.tradeCoinsList.size(); j++) {
                                if (TextUtils.equals(marketModel.tradeCoinsList.get(j).currencyNameEn, "BTC")) {
                                    currentAmount = marketModel.tradeCoinsList.get(j).currentAmount;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static Map<Integer, Map<Integer, CoinExchangeValue>> currency2currencyChangeRateMap = new HashMap<>();

    public static void cacheDateToRateBase(List<MarketNewModel.TradeCoinsBean> list) {
        for (MarketNewModel.TradeCoinsBean bean : list) {
            CoinExchangeValue changeRate = new CoinExchangeValue(bean.baseCurrencyId, bean.currentAmount);

            Map<Integer, CoinExchangeValue> changeRateMap = currency2currencyChangeRateMap.get(bean.currencyId);
            if (changeRateMap == null) {
                changeRateMap = new HashMap<>();
                currency2currencyChangeRateMap.put(bean.currencyId, changeRateMap);
            }
            changeRateMap.put(changeRate.id, changeRate);
        }
    }

    /**
     * 切换汇率
     */
    public static void changeConvertMoney(String convertMoney) {
        if (convertModel != null) {
            convertMoneyString = convertMoney;
            ConvertModel.ConvertCurrencyListBean bean;
            for (int i = 0; i < convertModel.convertCurrencyList.size(); i++) {
                bean = convertModel.convertCurrencyList.get(i);
                if (TextUtils.equals(convertMoneyString, bean.name)) {
                    currentConvertBean = bean;
                }
            }
        }
    }

    /**
     * 根据当前基础币数量来计算人民币数量
     *
     * @param baseCurrentId 基础币Id
     * @param number        基础币数量
     * @return
     */
    public static String getMcM(int baseCurrentId, double number) {
        if (currentConvertBean != null && getMcMValue(baseCurrentId, number) != null) {
            return currentConvertBean.sign + getMcMValue(baseCurrentId, number).toPlainString();
        }
        return "";
    }

    public static BigDecimal getMcMValue(int baseCurrentId, double number) {
        if (currentConvertBean != null && convertSparseArray.get(baseCurrentId) != null) {
            double baseExchangeAmount = convertSparseArray.get(baseCurrentId);
            return BigDecimal.valueOf(baseExchangeAmount * currentConvertBean.price * number).setScale(currentConvertBean.priceDot, BigDecimal.ROUND_HALF_UP);
        }

        if (currency2currencyChangeRateMap.containsKey(baseCurrentId)) {
            for (CoinExchangeValue value : currency2currencyChangeRateMap.get(baseCurrentId).values()) {
                if (currentConvertBean != null && convertSparseArray.get(value.id) != null) {
                    double baseExchangeAmount = convertSparseArray.get(value.id);
                    return BigDecimal.valueOf(baseExchangeAmount * currentConvertBean.price * value.rate * number).setScale(currentConvertBean.priceDot, BigDecimal.ROUND_HALF_UP);
                }
            }
        }

        return null;
    }

    public static double getCurrentAmount() {
        return currentAmount;
    }
}
