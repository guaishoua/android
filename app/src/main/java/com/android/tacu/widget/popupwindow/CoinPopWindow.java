package com.android.tacu.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CoinPopWindow extends PopupWindow {

    private Context mContext;
    private ScrollIndicatorView baseIndicatorView;
    private RecyclerView recyclerView;

    private UserInfoUtils spUtil;

    private BaseIndicatorAdapter baseApapter;
    private CoinAdapter coinAdapter;

    private SelfModel selfModel;
    private Gson gson = new Gson();

    private Handler mHandler = new Handler();
    private int basePosition = 0;
    private List<String> baseCoinList = new ArrayList<>();//基础币
    private List<MarketNewModel> attachment = new ArrayList<>();//总的数据集

    private String tempBaseCoinString;
    private String tempCoinString;

    public CoinPopWindow(Context context) {
        super(context);
        this.mContext = context;

        spUtil = UserInfoUtils.getInstance();
    }

    public void create(int width, int maxHeight, final TabItemSelect tabItemSelect) {
        setWidth(width);
        setHeight(maxHeight);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.content_bg_color)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10F);
        }

        View view = View.inflate(mContext, R.layout.pop_coin, null);
        baseIndicatorView = view.findViewById(R.id.base_scrollIndicatorView);
        recyclerView = view.findViewById(R.id.recyclerView);

        baseIndicatorView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.content_bg_color_grey));
        baseIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(mContext, R.color.text_default), ContextCompat.getColor(mContext, R.color.text_grey_2)).setSize(14, 14));
        baseIndicatorView.setScrollBar(new TextWidthColorBar(mContext, baseIndicatorView, ContextCompat.getColor(mContext, R.color.text_default), 4));
        baseIndicatorView.setSplitAuto(true);
        baseIndicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                basePosition = select;
                setCoinValueList(select);
            }
        });

        baseApapter = new BaseIndicatorAdapter();
        baseIndicatorView.setAdapter(baseApapter);

        coinAdapter = new CoinAdapter();
        coinAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (tabItemSelect != null) {
                    final MarketNewModel.TradeCoinsBean item = attachment.get(basePosition).tradeCoinsList.get(position);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tabItemSelect.coinClick(item.currencyId, item.baseCurrencyId, item.currencyNameEn, item.baseCurrencyNameEn);
                        }
                    });
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(coinAdapter);

        setContentView(view);
    }

    public void notifyCoinInfo() {
        //自选
        if (spUtil.getLogin()) {
            String selfModelString = SPUtils.getInstance().getString(Constant.SELFCOIN_LIST);
            selfModel = gson.fromJson(selfModelString, SelfModel.class);
        }
        if (selfModel == null) {
            selfModel = new SelfModel();
        }

        //防止侧边栏展开的时候卡顿
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
                List<MarketNewModel> cacheList = gson.fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
                }.getType());

                setBaseCoinsList(cacheList);
            }
        });
    }

    public void setBaseCoinsList(List<MarketNewModel> attach) {
        if (attach != null && attach.size() > 0) {
            attachment.clear();
            for (int i = 0; i < attach.size(); i++) {
                attachment.add(attach.get(i));
            }
            if (attachment != null && attachment.size() > 0) {
                //基础币处理
                baseCoinList.clear();
                for (int i = 0; i < attachment.size(); i++) {
                    if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                        baseCoinList.add(attachment.get(i).name);
                    } else {
                        baseCoinList.add(attachment.get(i).name_en);
                    }
                }
                baseCoinList.add(mContext.getResources().getString(R.string.market_selfselection));

                //基础币没有发生变化就不刷新
                String tempString = gson.toJson(baseCoinList);
                if (TextUtils.isEmpty(tempBaseCoinString) || !TextUtils.equals(tempBaseCoinString, tempString)) {
                    baseApapter.notifyDataSetChanged();
                    tempBaseCoinString = tempString;
                }

                //自选数据
                MarketNewModel selfMarketModel = new MarketNewModel();
                for (int i = 0; i < attachment.size(); i++) {
                    for (int j = 0; j < attachment.get(i).tradeCoinsList.size(); j++) {
                        for (int n = 0; n < selfModel.checkedList.size(); n++) {
                            if (TextUtils.equals(attachment.get(i).tradeCoinsList.get(j).currencyId + "," + attachment.get(i).tradeCoinsList.get(j).baseCurrencyId, selfModel.checkedList.get(n).symbol)) {
                                selfMarketModel.tradeCoinsList.add(attachment.get(i).tradeCoinsList.get(j));
                            }
                        }
                    }
                }
                attachment.add(selfMarketModel);
                setCoinValueList(basePosition < attachment.size() ? basePosition : 0);
            }
        }
    }

    private void setCoinValueList(int position) {
        if (attachment != null && attachment.size() > 0) {
            String tempString = gson.toJson(attachment.get(position).tradeCoinsList);
            if (TextUtils.isEmpty(tempCoinString) || !TextUtils.equals(tempCoinString, tempString)) {
                coinAdapter.setNewData(attachment.get(position).tradeCoinsList);
                tempCoinString = tempString;
            }
        }
    }

    private class BaseIndicatorAdapter extends Indicator.IndicatorAdapter {

        @Override
        public int getCount() {
            return baseCoinList != null ? baseCoinList.size() : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(baseCoinList.get(position));
            int padding = UIUtils.dp2px(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }
    }

    public class CoinAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {
        public CoinAdapter() {
            super(R.layout.item_trade_coin);
        }

        @Override
        protected void convert(BaseViewHolder helper, MarketNewModel.TradeCoinsBean item) {
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
            helper.setText(R.id.tv_current_amount, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            if (item.changeRate >= 0) {
                helper.setText(R.id.tv_change_rate, "+" + BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_change_rate, ContextCompat.getColor(mContext, R.color.color_riseup));
                helper.setTextColor(R.id.tv_current_amount, ContextCompat.getColor(mContext, R.color.color_riseup));
            } else {
                helper.setText(R.id.tv_change_rate, BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_change_rate, ContextCompat.getColor(mContext, R.color.color_risedown));
                helper.setTextColor(R.id.tv_current_amount, ContextCompat.getColor(mContext, R.color.color_risedown));
            }
            helper.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.content_bg_color));
        }
    }

    public interface TabItemSelect {
        void coinClick(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn);
    }
}
