package com.android.tacu.module.main.view;

import com.android.tacu.module.market.model.MarketNewModel;

import java.util.List;

public interface TradeDataBridge {

    List<MarketNewModel.TradeCoinsBean> getTradeList();
}
