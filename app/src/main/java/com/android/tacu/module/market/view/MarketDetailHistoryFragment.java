package com.android.tacu.module.market.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.socket.AppSocket;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.market.model.ContentBean;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.TradeHistoryModel;
import com.android.tacu.module.market.model.TradeHistoryModelTwo;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.DateUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

/**
 * 币种详情页 交易记录
 * Created by jiazhen on 2018/9/26.
 */
public class MarketDetailHistoryFragment extends BaseFragment implements ISocketEvent, Observer {

    @BindView(R.id.lin_layout)
    LinearLayout linearLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.tv_price)
    TextView tv_price;

    private int currencyId;
    private int baseCurrencyId;
    private int bgColor;

    private Gson gson = new Gson();
    private MarketHistoryAdapter historyAdapter;

    //20条空白数据
    private List<ContentBean> falseBeanList = new ArrayList<>();

    private TradeHistoryModel tradeHistoryModel;
    private CurrentTradeCoinModel currentTradeCoinModel;

    public static MarketDetailHistoryFragment newInstance(int currencyId, int baseCurrencyId, int Color) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putInt("baseCurrencyId", baseCurrencyId);
        bundle.putInt("bgColor", Color);
        MarketDetailHistoryFragment fragment = new MarketDetailHistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            baseCurrencyId = bundle.getInt("baseCurrencyId");
            bgColor = bundle.getInt("bgColor", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_marketdetail_history;
    }

    @Override
    protected void initData(View view) {
        setSocketEvent(this, this, SocketConstant.TRADEHISTORY);
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

        historyAdapter = new MarketHistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(historyAdapter);

        if (bgColor != 0) {
            linearLayout.setBackgroundColor(bgColor);
            tv_time.setTextColor(ContextCompat.getColor(getContext(), R.color.text_grey_2));
            tv_amount.setTextColor(ContextCompat.getColor(getContext(), R.color.text_grey_2));
            tv_price.setTextColor(ContextCompat.getColor(getContext(), R.color.text_grey_2));
        }

        initFalseData();
    }

    @Override
    public void socketConnectEventAgain() {
        if (baseAppSocket != null) {
            baseAppSocket.tradeHistory(currencyId, baseCurrencyId);
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
                        case SocketConstant.TRADEHISTORY:
                            ObserverModel.TradeHistory tradeHistory = model.getTradeHistory();
                            if (tradeHistory != null) {
                                JSONObject jsonObject = tradeHistory.getData();
                                if (jsonObject != null && !TextUtils.isEmpty(jsonObject.toString())) {
                                    String type = jsonObject.optString("type");
                                    if (TextUtils.equals(type, "1")) {
                                        tradeHistoryModel = gson.fromJson(jsonObject.toString(), TradeHistoryModel.class);
                                    } else if (TextUtils.equals(type, "2")) {
                                        TradeHistoryModelTwo tradeHistoryModelTwo = gson.fromJson(jsonObject.toString(), TradeHistoryModelTwo.class);
                                        if (tradeHistoryModelTwo != null) {
                                            tradeHistoryModel.content.add(0, tradeHistoryModelTwo.content);
                                        }
                                    }
                                    if (tradeHistoryModel != null) {
                                        if (tradeHistoryModel.content.size() >= 20) {
                                            historyAdapter.setNewData(tradeHistoryModel.content.subList(0, 20));
                                        } else {
                                            historyAdapter.setNewData(tradeHistoryModel.content);
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        });
    }

    public void setValue(int currencyId, int baseCurrencyId) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        socketConnectEventAgain();
    }

    public void setCurrentTradeCoinModel(CurrentTradeCoinModel currentTradeCoinModel) {
        this.currentTradeCoinModel = currentTradeCoinModel;
        if (tradeHistoryModel != null && tradeHistoryModel.content != null && tradeHistoryModel.content.size() > 0) {
            historyAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 预加载20条数据 占位
     */
    private void initFalseData() {
        for (int i = 0; i < 20; i++) {
            ContentBean bean = new ContentBean();
            falseBeanList.add(bean);
        }
        historyAdapter.setNewData(falseBeanList);
    }

    public class MarketHistoryAdapter extends BaseQuickAdapter<ContentBean, BaseViewHolder> {

        public MarketHistoryAdapter() {
            super(R.layout.item_marketdetail_history);
        }

        @Override
        protected void convert(BaseViewHolder helper, ContentBean item) {
            if (!TextUtils.isEmpty(item.time)) {
                helper.setText(R.id.tv_time, DateUtils.getStrToStr(item.time, DateUtils.FORMAT_DATE_GMT, DateUtils.FORMAT_DATE_HMS));
            }
            if (currentTradeCoinModel != null) {
                if (item.current != 0) {
                    helper.setText(R.id.tv_price, BigDecimal.valueOf(item.current).setScale(currentTradeCoinModel.currentTradeCoin.pointPrice, RoundingMode.DOWN).toPlainString());
                }
                if (item.amount != 0) {
                    helper.setText(R.id.tv_amount, BigDecimal.valueOf(item.amount).setScale(currentTradeCoinModel.currentTradeCoin.pointNum, RoundingMode.DOWN).toPlainString());
                }
            } else {
                if (item.current != 0) {
                    helper.setText(R.id.tv_price, String.valueOf(item.current));
                }
                if (item.amount != 0) {
                    helper.setText(R.id.tv_amount, String.valueOf(item.amount));
                }
            }
            if (bgColor != 0) {
                helper.itemView.setBackgroundColor(bgColor);
                helper.setTextColor(R.id.tv_time, ContextCompat.getColor(getContext(), R.color.text_grey_2));
                helper.setTextColor(R.id.tv_amount, ContextCompat.getColor(getContext(), R.color.text_grey_2));
            }
            if (item.buyOrSell == 1) {
                helper.setTextColor(R.id.tv_price, ContextCompat.getColor(getContext(), R.color.color_riseup));
            } else if (item.buyOrSell == 2) {
                helper.setTextColor(R.id.tv_price, ContextCompat.getColor(getContext(), R.color.color_risedown));
            }
        }
    }
}
