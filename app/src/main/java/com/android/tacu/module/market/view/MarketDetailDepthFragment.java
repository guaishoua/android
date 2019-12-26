package com.android.tacu.module.market.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.MarketDepthModel;
import com.android.tacu.module.market.model.PriceModal;
import com.android.tacu.module.transaction.model.RecordModel;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.FormatterUtils;
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

/**
 * 币种详情页的 深度
 * Created by jiazhen on 2018/9/26.
 */
public class MarketDetailDepthFragment extends BaseFragment implements ISocketEvent, Observer {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TextView text_rise;
    private TextView text_down;
    private TextView tv_buy_high;
    private TextView tv_buy_sell;
    private TextView tv_sell_low;
    private BuySellChartView chart_view;

    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    private MarketDepthAdapter depthAdapter;

    //20条空白数据
    private List<MarketDepthModel> falseBeanList = new ArrayList<>();

    private List<MarketDepthModel> depthModelList = new ArrayList<>();
    private CurrentTradeCoinModel currentTradeCoinModel;

    public static MarketDetailDepthFragment newInstance(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putInt("baseCurrencyId", baseCurrencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putString("baseCurrencyNameEn", baseCurrencyNameEn);
        MarketDetailDepthFragment fragment = new MarketDetailDepthFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            baseCurrencyId = bundle.getInt("baseCurrencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
            baseCurrencyNameEn = bundle.getString("baseCurrencyNameEn");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_marketdetail_depth;
    }

    @Override
    protected void initData() {
        setSocketEvent(this, this, SocketConstant.ENTRUST);

        depthAdapter = new MarketDepthAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(depthAdapter);

        initHeader();
        initFalseData();
    }

    @Override
    public void socketConnectEventAgain() {
        if (baseAppSocket != null) {
            baseAppSocket.entrust(currencyId, baseCurrencyId);
        }
    }

    @Override
    public void update(final Observable observable, final Object object) {
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (observable instanceof BaseSocketManager) {
                    ObserverModel model = (ObserverModel) object;
                    switch (model.getEventType()) {
                        case SocketConstant.ENTRUST:
                            ObserverModel.Entrust entrust = model.getEntrust();
                            if (entrust != null) {
                                entrustInfo(entrust.getRecordModel());
                            }
                            break;
                    }
                }
            }
        });
    }

    public void setValue(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;
        socketConnectEventAgain();
    }

    private void initHeader() {
        View viewHeader = View.inflate(getContext(), R.layout.view_marketdetail_depth_header, null);
        text_rise = viewHeader.findViewById(R.id.text_rise);
        text_down = viewHeader.findViewById(R.id.text_down);
        tv_buy_high = viewHeader.findViewById(R.id.tv_buy_high);
        tv_buy_sell = viewHeader.findViewById(R.id.tv_buy_sell);
        tv_sell_low = viewHeader.findViewById(R.id.tv_sell_low);
        chart_view = viewHeader.findViewById(R.id.chart_view);

        depthAdapter.setHeaderView(viewHeader);
    }

    private void entrustInfo(RecordModel recordModel) {
        if (recordModel != null) {
            int num = 0;
            if (recordModel.buy.size() >= recordModel.sell.size()) {
                num = recordModel.buy.size();
            } else {
                num = recordModel.sell.size();
            }
            if (num > 0) {
                depthModelList.clear();
                int buyNum = recordModel.buy.size();
                int sellNum = recordModel.sell.size();
                for (int i = 0; i < num; i++) {
                    MarketDepthModel depthModel = new MarketDepthModel();
                    if (i < buyNum) {
                        depthModel.setBuyCurrent(recordModel.buy.get(i).current);
                        depthModel.setBuyNumber(recordModel.buy.get(i).number);
                        depthModel.setBuyDepth(BigDecimal.valueOf(recordModel.buy.get(i).current).doubleValue() * BigDecimal.valueOf(recordModel.buy.get(i).number).doubleValue() / recordModel.entrustScale);
                    }
                    if (i < sellNum) {
                        depthModel.setSellCurrent(recordModel.sell.get(i).current);
                        depthModel.setSellNumber(recordModel.sell.get(i).number);
                        depthModel.setSellDepth(BigDecimal.valueOf(recordModel.sell.get(i).current).doubleValue() * BigDecimal.valueOf(recordModel.sell.get(i).number).doubleValue() / recordModel.entrustScale);
                    }
                    depthModelList.add(depthModel);
                }
                if (depthModelList != null) {
                    if (depthModelList != null && depthModelList.size() >= 20) {
                        depthAdapter.setNewData(depthModelList.subList(0, 20));
                    } else {
                        for (int i = depthModelList.size() - 1; i < 20; i++) {
                            MarketDepthModel bean = new MarketDepthModel();
                            depthModelList.add(bean);
                        }
                        depthAdapter.setNewData(depthModelList);
                    }
                }
            }

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
                tv_buy_high.setText(BigDecimal.valueOf(buyHighPrice).setScale(currentTradeCoinModel.currentTradeCoin.pointPrice, RoundingMode.DOWN).toPlainString());
            }
            if (recordModel.sell != null && recordModel.sell.size() > 0) {
                List<Double> sellPriceList = new ArrayList<>();
                for (int i = 0; i < recordModel.sell.size(); i++) {
                    sellPriceList.add(recordModel.sell.get(i).current);
                }
                sellLowPrice = Collections.min(sellPriceList, comp);
                tv_sell_low.setText(BigDecimal.valueOf(sellLowPrice).setScale(currentTradeCoinModel.currentTradeCoin.pointPrice, RoundingMode.DOWN).toPlainString());
            }
            if (buyHighPrice != 0) {
                double dis = sellLowPrice - buyHighPrice;
                double disValue = dis / buyHighPrice;
                tv_buy_sell.setText(BigDecimal.valueOf(dis).setScale(currentTradeCoinModel.currentTradeCoin.pointPrice, RoundingMode.DOWN).toPlainString() + " | " + BigDecimal.valueOf(disValue * 100).setScale(2, BigDecimal.ROUND_DOWN).toPlainString() + "%");
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
            chart_view.setItems(buyList, sellList, currentTradeCoinModel != null ? currentTradeCoinModel.currentTradeCoin.pointPrice : 2, currentTradeCoinModel != null ? currentTradeCoinModel.currentTradeCoin.pointNum : 2, currencyNameEn, baseCurrencyNameEn);
        }
    }

    public void setCurrentTradeCoinModel(CurrentTradeCoinModel currentTradeCoinModel) {
        this.currentTradeCoinModel = currentTradeCoinModel;
        if (depthModelList != null && depthModelList.size() > 0) {
            depthAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 预加载20条数据 占位
     */
    private void initFalseData() {
        for (int i = 0; i < 20; i++) {
            MarketDepthModel bean = new MarketDepthModel();
            falseBeanList.add(bean);
        }
        depthAdapter.setNewData(falseBeanList);
    }

    public class MarketDepthAdapter extends BaseQuickAdapter<MarketDepthModel, BaseViewHolder> {

        public MarketDepthAdapter() {
            super(R.layout.item_marketdetail_depth);
        }

        @Override
        protected void convert(BaseViewHolder helper, MarketDepthModel item) {
            if (currentTradeCoinModel != null) {
                if (item.getBuyNumber() != 0) {
                    helper.setText(R.id.tv_buyamount, BigDecimal.valueOf(item.getBuyNumber()).setScale(currentTradeCoinModel.currentTradeCoin.pointNum, RoundingMode.DOWN).toPlainString());
                } else {
                    helper.setText(R.id.tv_buyamount, "--");
                }
                if (item.getSellNumber() != 0) {
                    helper.setText(R.id.tv_sellamount, BigDecimal.valueOf(item.getSellNumber()).setScale(currentTradeCoinModel.currentTradeCoin.pointNum, RoundingMode.DOWN).toPlainString());
                } else {
                    helper.setText(R.id.tv_sellamount, "--");
                }
                if (item.getBuyCurrent() != 0) {
                    helper.setText(R.id.tv_buyprice, BigDecimal.valueOf(item.getBuyCurrent()).setScale(currentTradeCoinModel.currentTradeCoin.pointPrice, RoundingMode.DOWN).toPlainString());
                } else {
                    helper.setText(R.id.tv_buyprice, "--");
                }
                if (item.getSellCurrent() != 0) {
                    helper.setText(R.id.tv_sellprice, BigDecimal.valueOf(item.getSellCurrent()).setScale(currentTradeCoinModel.currentTradeCoin.pointPrice, RoundingMode.DOWN).toPlainString());
                } else {
                    helper.setText(R.id.tv_sellprice, "--");
                }
            } else {
                if (item.getBuyNumber() != 0) {
                    helper.setText(R.id.tv_buyamount, FormatterUtils.getFormatValue(item.getBuyNumber()));
                } else {
                    helper.setText(R.id.tv_buyamount, "--");
                }
                if (item.getSellNumber() != 0) {
                    helper.setText(R.id.tv_sellamount, FormatterUtils.getFormatValue(item.getSellNumber()));
                } else {
                    helper.setText(R.id.tv_sellamount, "--");
                }
                if (item.getBuyCurrent() != 0) {
                    helper.setText(R.id.tv_buyprice, FormatterUtils.getFormatValue(item.getBuyCurrent()));
                } else {
                    helper.setText(R.id.tv_buyprice, "--");
                }
                if (item.getSellCurrent() != 0) {
                    helper.setText(R.id.tv_sellprice, FormatterUtils.getFormatValue(item.getSellCurrent()));
                } else {
                    helper.setText(R.id.tv_sellprice, "--");
                }
            }

            if (item.getBuyDepth() != 0) {
                helper.setProgress(R.id.progressBar_buy, BigDecimal.valueOf(item.getBuyDepth()).intValue());
            } else {
                helper.setProgress(R.id.progressBar_buy, 0);
            }
            if (item.getSellDepth() != 0) {
                helper.setProgress(R.id.progressBar_sell, BigDecimal.valueOf(item.getSellDepth()).intValue());
            } else {
                helper.setProgress(R.id.progressBar_sell, 0);
            }
        }
    }
}
