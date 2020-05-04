package com.android.tacu.module.transaction.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.PriceModal;
import com.android.tacu.module.transaction.model.RecordModel;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.widget.BuySellChartView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

public class DepthFragment extends BaseFragment implements Observer {

    @BindView(R.id.text_rise)
    TextView text_rise;
    @BindView(R.id.text_down)
    TextView text_down;
    @BindView(R.id.tv_buy_high)
    TextView tv_buy_high;
    @BindView(R.id.tv_buy_sell)
    TextView tv_buy_sell;
    @BindView(R.id.tv_sell_low)
    TextView tv_sell_low;
    @BindView(R.id.chart_view)
    BuySellChartView chart_view;

    private String currencyNameEn;
    private String baseCurrencyNameEn;
    private RecordModel recordModel;

    private int pointPrice = 0;
    private int pointNum = 0;

    public static DepthFragment newInstance(String currencyNameEn, String baseCurrencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putString("baseCurrencyNameEn", baseCurrencyNameEn);
        DepthFragment fragment = new DepthFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyNameEn = bundle.getString("currencyNameEn");
            baseCurrencyNameEn = bundle.getString("baseCurrencyNameEn");
        }
        if (TradeFragment.currentTradeCoinModel != null) {
            pointPrice = TradeFragment.currentTradeCoinModel.currentTradeCoin.pointPrice;
            pointNum = TradeFragment.currentTradeCoinModel.currentTradeCoin.pointNum;
        }
        if (TradeFragment.recordModel != null) {
            recordModel = TradeFragment.recordModel;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_depth;
    }

    @Override
    protected void initData(View view) {
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        if (recordModel != null) {
            entrustInfo(recordModel);
        }
        TradeFragment.tradeSocketManager.addObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object object) {
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (observable instanceof BaseSocketManager) {
                        ObserverModel model = (ObserverModel) object;
                        switch (model.getEventType()) {
                            //获取币种信息
                            case SocketConstant.LOGINAFTERCHANGETRADECOIN:
                                ObserverModel.LoginAfterChangeTradeCoin coinInfo = model.getTradeCoin();
                                if (coinInfo != null) {
                                    setCurrentTradeCoinModel(coinInfo.getCoinModel());
                                }
                                break;
                            //获取买卖委托
                            case SocketConstant.ENTRUST:
                                ObserverModel.Entrust entrust = model.getEntrust();
                                if (entrust != null) {
                                    entrustInfo(entrust.getRecordModel());
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setValue(String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;
        chart_view.clearItem();
    }

    private void setCurrentTradeCoinModel(CurrentTradeCoinModel currentTradeCoinModel) {
        if (currentTradeCoinModel != null) {
            if (pointPrice != currentTradeCoinModel.currentTradeCoin.pointPrice || pointNum != currentTradeCoinModel.currentTradeCoin.pointNum) {
                pointPrice = currentTradeCoinModel.currentTradeCoin.pointPrice;
                pointNum = currentTradeCoinModel.currentTradeCoin.pointNum;
                chart_view.setItems(pointPrice != 0 ? pointPrice : 2, pointNum != 0 ? pointNum : 2);
            }
        }
    }

    private void entrustInfo(RecordModel recordModel) {
        if (recordModel != null) {
            //买卖对比
            double buyNum = 0, sellNum = 0;
            if (recordModel.buy != null && recordModel.buy.size() > 0) {
                for (int i = 0; i < recordModel.buy.size(); i++) {
                    buyNum += recordModel.buy.get(i).number;
                }
            }
            if (recordModel.sell != null && recordModel.sell.size() > 0) {
                for (int i = 0; i < recordModel.sell.size(); i++) {
                    sellNum += recordModel.sell.get(i).number;
                }
            }
            float BuyValue = 0;
            if (buyNum == 0 && sellNum == 0) {
                BuyValue = 0.5F;
            } else {
                BuyValue = BigDecimal.valueOf(buyNum / (buyNum + sellNum)).setScale(4, BigDecimal.ROUND_DOWN).floatValue();
            }
            text_rise.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, BuyValue));
            text_down.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1 - BuyValue));

            text_rise.setText(BigDecimal.valueOf(BuyValue * 100).setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "%");
            text_down.setText(BigDecimal.valueOf((1 - BuyValue) * 100).setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "%");

            // 买单最低价
            Comparator<Double> comp = new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    if (o1 > o2) {
                        return 1;
                    } else if (o1 == o2) {
                        return 0;
                    }
                    return -1;
                }
            };
            double buyHighPrice = 0, sellLowPrice = 0;
            if (recordModel.buy != null && recordModel.buy.size() > 0) {
                List<Double> buyPriceList = new ArrayList<>();
                for (int i = 0; i < recordModel.buy.size(); i++) {
                    buyPriceList.add(recordModel.buy.get(i).current);
                }
                buyHighPrice = Collections.max(buyPriceList, comp);
                if (pointPrice != 0) {
                    tv_buy_high.setText(BigDecimal.valueOf(buyHighPrice).setScale(pointPrice, RoundingMode.DOWN).toPlainString());
                } else {
                    tv_buy_high.setText(BigDecimal.valueOf(buyHighPrice).setScale(2, RoundingMode.DOWN).toPlainString());
                }
            }
            if (recordModel.sell != null && recordModel.sell.size() > 0) {
                List<Double> sellPriceList = new ArrayList<>();
                for (int i = 0; i < recordModel.sell.size(); i++) {
                    sellPriceList.add(recordModel.sell.get(i).current);
                }
                sellLowPrice = Collections.min(sellPriceList, comp);
                if (pointPrice != 0) {
                    tv_sell_low.setText(BigDecimal.valueOf(sellLowPrice).setScale(pointPrice, RoundingMode.DOWN).toPlainString());
                } else {
                    tv_sell_low.setText(BigDecimal.valueOf(sellLowPrice).setScale(2, RoundingMode.DOWN).toPlainString());
                }
            }
            if (buyHighPrice != 0) {
                double dis = sellLowPrice - buyHighPrice;
                double disValue = dis / buyHighPrice;
                if (pointPrice != 0) {
                    tv_buy_sell.setText(BigDecimal.valueOf(dis).setScale(pointPrice, RoundingMode.DOWN).toPlainString() + " | " + BigDecimal.valueOf(disValue * 100).setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "%");
                } else {
                    tv_buy_sell.setText(BigDecimal.valueOf(dis).setScale(2, RoundingMode.DOWN).toPlainString() + " | " + BigDecimal.valueOf(disValue * 100).setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "%");
                }
            }

            //买卖的折线图
            List<PriceModal> buyList = new ArrayList<>();
            List<PriceModal> sellList = new ArrayList<>();
            if (recordModel.buy != null && recordModel.buy.size() > 0) {
                double all = 0;
                for (int i = 0; i < recordModel.buy.size(); i++) {
                    if (i == 0) {
                        all = recordModel.buy.get(0).current * recordModel.buy.get(0).number;
                    } else {
                        all += recordModel.buy.get(i).current * recordModel.buy.get(i).number;
                    }
                    buyList.add(new PriceModal(recordModel.buy.get(i).current, recordModel.buy.get(i).number, all));
                }
            }
            if (recordModel.sell != null && recordModel.sell.size() > 0) {
                double all = 0;
                for (int i = 0; i < recordModel.sell.size(); i++) {
                    if (i == 0) {
                        all = recordModel.sell.get(0).number;
                    } else {
                        all += recordModel.sell.get(i).number;
                    }
                    sellList.add(new PriceModal(recordModel.sell.get(i).current, recordModel.sell.get(i).number, all));
                }
            }
            chart_view.setItems(buyList, sellList, pointPrice != 0 ? pointPrice : 2, pointNum != 0 ? pointNum : 2, currencyNameEn, baseCurrencyNameEn);
        }
    }
}
