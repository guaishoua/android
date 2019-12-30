package com.android.tacu.module.market.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MarketListVisibleHintEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.market.contract.MarketContract;
import com.android.tacu.EventBus.model.EditStatusEvent;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.presenter.MarketPresenter;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.UIUtils;
import com.google.gson.Gson;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jiazhen on 2018/8/17.
 */
public class MarketListFragment extends BaseFragment<MarketPresenter> implements MarketContract.IMarketListiew, ISocketEvent, Observer {

    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magicIndicator;
    @BindView(R.id.tv_pair)
    TextView tvPair;
    @BindView(R.id.tv_vol)
    TextView tvVol;
    @BindView(R.id.tv_last_price)
    TextView tvLastPrice;
    @BindView(R.id.tv_hour)
    TextView tvHour;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.market_sort)
    View marketSort;
    @BindView(R.id.self_sort)
    View selfSort;

    private QMUIAlphaImageButton btnLeft;
    private IndicatorViewPager indicatorViewPager;

    /**
     * 筛选按钮的箭头 0：不显示 1：向下降序 2：向上升序
     * 注：名称/成交量 成交量降序 名称升序
     */
    private int pairVolStatus = 0;
    private int lastPriceStatus = 0;
    private int hourStatus = 0;
    //名称/成交量：pairVol 最新价：lastPrice 24H成交量：24Hour
    private String currentStatus = "";

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    //打新区过滤完之后数据
    private List<MarketNewModel.TradeCoinsBean> allTradeCoinList = new ArrayList<>();

    private Gson gson = new Gson();

    private Handler mSocketHandler = new Handler();

    public static MarketListFragment newInstance() {
        Bundle bundle = new Bundle();
        MarketListFragment fragment = new MarketListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        EventManage.sendEvent(new BaseEvent<>(EventConstant.MarkListVisibleCode, new MarketListVisibleHintEvent(isVisibleToUser)));
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_market_list;
    }

    @Override
    protected void initData() {
        setSocketEvent(this, this, SocketConstant.LOGINAFTERNEW);

        mTopBar.setTitle(getResources().getString(R.string.market));
        mTopBar.setBackgroundDividerEnabled(true);
        btnLeft = mTopBar.addLeftImageButton(R.drawable.icon_edit_black, R.id.qmui_topbar_item_left_back, 20, 20);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(SelfSelectionEditActivity.class);
            }
        });
        btnLeft.setVisibility(View.GONE);
        mTopBar.addRightImageButton(R.drawable.icon_search_black, R.id.qmui_topbar_item_right, 20, 20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(SearchHistoryActivity.class);
            }
        });

        magicIndicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_bg_color));
        magicIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(getContext(), R.color.tab_default), ContextCompat.getColor(getContext(), R.color.tab_text_color)).setSize(14, 14));
        magicIndicator.setScrollBar(new ColorBar(getContext(), ContextCompat.getColor(getContext(), R.color.tab_default), 4));
        magicIndicator.setSplitAuto(true);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    selfSort.setVisibility(View.VISIBLE);
                    marketSort.setVisibility(View.GONE);
                } else {
                    selfSort.setVisibility(View.GONE);
                    marketSort.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected MarketPresenter createPresenter(MarketPresenter mPresenter) {
        return new MarketPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketHandler != null) {
            mSocketHandler.removeCallbacksAndMessages(null);
            mSocketHandler = null;
        }
    }

    @OnClick(R.id.lin_pair_vol)
    void pairVolClick() {
        clearAllStatus();
        switch (pairVolStatus) {
            case 0:
                setTextRightDrawable(tvVol, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_downblue_arrow));
                setTextRightDrawable(tvPair, null);
                pairVolStatus = 1;
                break;
            case 1:
                setTextRightDrawable(tvVol, null);
                setTextRightDrawable(tvPair, ContextCompat.getDrawable(getContext(), R.drawable.icon_upblue_down_arrow));
                pairVolStatus = 2;
                break;
            case 2:
                setTextRightDrawable(tvVol, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_downblue_arrow));
                setTextRightDrawable(tvPair, null);
                pairVolStatus = 1;
                break;
        }
        currentStatus = "pairVol";
        setFragmentValue(null, true);
    }

    @OnClick(R.id.tv_last_price)
    void lastPriceClick() {
        clearAllStatus();
        switch (lastPriceStatus) {
            case 0:
                setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_downblue_arrow));
                lastPriceStatus = 1;
                break;
            case 1:
                setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_upblue_down_arrow));
                lastPriceStatus = 2;
                break;
            case 2:
                setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_downblue_arrow));
                lastPriceStatus = 1;
                break;
        }
        currentStatus = "lastPrice";
        setFragmentValue(null, true);
    }

    @OnClick(R.id.tv_hour)
    void hourClick() {
        clearAllStatus();
        switch (hourStatus) {
            case 0:
                setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_downblue_arrow));
                hourStatus = 1;
                break;
            case 1:
                setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_upblue_down_arrow));
                hourStatus = 2;
                break;
            case 2:
                setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_downblue_arrow));
                hourStatus = 1;
                break;
        }
        currentStatus = "24Hour";
        setFragmentValue(null, true);
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.EditStatusCode:
                    EditStatusEvent editStatusEvent = (EditStatusEvent) event.getData();
                    if (editStatusEvent != null) {
                        switch (editStatusEvent.getStatus()) {
                            case Constant.BTN_STATUS_NO:
                                btnLeft.setVisibility(View.GONE);
                                break;
                            case Constant.BTN_STATUS_EDIT:
                                btnLeft.setVisibility(View.VISIBLE);
                                break;
                            case Constant.BTN_STATUS_NOTICE:
                                btnLeft.setVisibility(View.GONE);
                                break;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void socketConnectEventAgain() {
        if (baseAppSocket != null) {
            baseAppSocket.coinAllList();
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
                        case SocketConstant.LOGINAFTERNEW:
                            ObserverModel.CoinAllList coinAllList = model.getCoinAllList();
                            sendValue(coinAllList.getMarketModelList());
                            break;
                    }
                }
            }
        });
    }

    public void sendValue(final List<MarketNewModel> marketModelList) {
        mSocketHandler.post(new Runnable() {
            @Override
            public void run() {
                if (marketModelList != null && marketModelList.size() > 0) {
                    saveAllValueList(marketModelList);
                    setMarketTitle(marketModelList);
                    setFragmentValue(marketModelList, false);
                    ConvertMoneyUtils.setHttpBaseCoinScale(marketModelList);
                }
            }
        });
    }

    private void saveAllValueList(List<MarketNewModel> marketModelList) {
        if (marketModelList != null && marketModelList.size() > 0) {
            //将所有币种存成一个list 给自选列表
            allTradeCoinList.clear();
            for (int i = 0; i < marketModelList.size(); i++) {
                marketModelList.get(i).tradeCoinsList.clear();

                if (marketModelList.get(i).tradeCoinsFixedList != null && marketModelList.get(i).tradeCoinsFixedList.size() > 0) {
                    allTradeCoinList.addAll(marketModelList.get(i).tradeCoinsFixedList);
                    marketModelList.get(i).tradeCoinsList.addAll(marketModelList.get(i).tradeCoinsFixedList);
                }

                //将tradeCoinsAutoList按照一定的规则排序
                if (marketModelList.get(i).tradeCoinsAutoList != null && marketModelList.get(i).tradeCoinsAutoList.size() > 0) {
                    switch (marketModelList.get(i).sort_type) {
                        case 1://价格正序
                            mPresenter.sortLastPrice(marketModelList.get(i).tradeCoinsAutoList, "up");
                            break;
                        case 2://价格倒序
                            mPresenter.sortLastPrice(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                        case 3://涨跌幅正序
                            mPresenter.sortHour(marketModelList.get(i).tradeCoinsAutoList, "up");
                            break;
                        case 4://涨跌幅倒序
                            mPresenter.sortHour(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                        case 5://交易币首字母正序
                            mPresenter.sortPair(marketModelList.get(i).tradeCoinsAutoList, "up");
                            break;
                        case 6://交易币首字母倒序
                            mPresenter.sortPair(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                        case 7://24小时成交量正序
                            mPresenter.sortVol(marketModelList.get(i).tradeCoinsAutoList, "up");
                            break;
                        case 8://24小时成交量倒序
                            mPresenter.sortVol(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                    }
                }

                if (marketModelList.get(i).tradeCoinsAutoList != null && marketModelList.get(i).tradeCoinsAutoList.size() > 0) {
                    allTradeCoinList.addAll(marketModelList.get(i).tradeCoinsAutoList);
                    marketModelList.get(i).tradeCoinsList.addAll(marketModelList.get(i).tradeCoinsAutoList);
                }
            }
            //存Socket的缓存
            SPUtils.getInstance().put(Constant.SELECT_COIN_GROUP_CACHE, gson.toJson(marketModelList));
        }
    }

    /**
     * 添加tab
     */
    private void setMarketTitle(List<MarketNewModel> marketModelList) {
        if (marketModelList == null || marketModelList.size() <= 0) {
            return;
        }

        int num = marketModelList.size() + 1;
        if (tabTitle != null && tabTitle.size() > 1 && tabTitle.size() == num) {
            int unEqualNum = 0;
            for (int i = 0; i < marketModelList.size(); i++) {
                if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                    if (!TextUtils.equals(tabTitle.get(i + 1), marketModelList.get(i).name)) {
                        unEqualNum++;
                    }
                } else {
                    if (!TextUtils.equals(tabTitle.get(i + 1), marketModelList.get(i).name_en)) {
                        unEqualNum++;
                    }
                }
            }

            if (unEqualNum == 0) {
                return;
            }
        }

        //获取标题数据
        if (marketModelList != null && marketModelList.size() > 0) {
            tabTitle.clear();
            tabTitle.add(getResources().getString(R.string.market_selfselection));
            for (int i = 0; i < marketModelList.size(); i++) {
                MarketNewModel marketModel = marketModelList.get(i);
                if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                    tabTitle.add(marketModel.name);
                } else {
                    tabTitle.add(marketModel.name_en);
                }
            }
        }

        if (tabTitle != null && tabTitle.size() > 1) {
            fragmentList.clear();
            fragmentList.add(SelfSelectionFragment.newInstance());
            for (int i = 0; i < marketModelList.size(); i++) {
                fragmentList.add(MarketFragment.newInstance());
            }
        }

        if (fragmentList != null && fragmentList.size() > 0) {
            indicatorViewPager = new IndicatorViewPager(magicIndicator, viewpager);
            indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager()));
            viewpager.setOffscreenPageLimit(fragmentList.size() - 1);
            viewpager.setCurrentItem(1, false);
            indicatorViewPager.notifyDataSetChanged();
        }
    }

    /**
     * 给MarketFragment和SelfSelectionFragment赋值
     * isSort: true排序
     */
    private void setFragmentValue(List<MarketNewModel> marketModelList, boolean isSort) {
        if (fragmentList != null && fragmentList.size() > 0) {
            for (int i = 0; i < fragmentList.size(); i++) {
                if (fragmentList.get(i) != null) {
                    if (fragmentList.get(i).getClass().equals(SelfSelectionFragment.class)) {
                        if (allTradeCoinList != null && allTradeCoinList.size() > 0) {
                            if (!isSort) {
                                ((SelfSelectionFragment) fragmentList.get(i)).setCoinValue(allTradeCoinList);
                            }
                        }
                    } else {
                        if (!isSort && marketModelList != null && marketModelList.size() > 0) {
                            //如果不加这个判断 就不走下面那一步 不知道为啥
                            if (!TextUtils.isEmpty(currentStatus)) {
                                mPresenter.sortList(currentStatus, pairVolStatus, lastPriceStatus, hourStatus, marketModelList.get(i - 1).tradeCoinsList);
                            }

                            ((MarketFragment) fragmentList.get(i)).setCoinValue(gson.toJson(marketModelList.get(i - 1)));
                        } else if (isSort) {
                            ((MarketFragment) fragmentList.get(i)).setSortCoinValue(currentStatus, pairVolStatus, lastPriceStatus, hourStatus);
                        }
                    }
                }
            }
        }
    }

    /**
     * 给textview设置右边的Drawable
     *
     * @param tvDrawable
     * @param drawableRight
     */
    private void setTextRightDrawable(TextView tvDrawable, Drawable drawableRight) {
        tvDrawable.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
        tvDrawable.setCompoundDrawablePadding(UIUtils.dp2px(3));
    }

    private void clearAllStatus() {
        setTextRightDrawable(tvPair, null);
        setTextRightDrawable(tvVol, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_down_grey_arrow));
        setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_down_grey_arrow));
        setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_up_down_grey_arrow));
    }

    private class TabAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabTitle != null ? tabTitle.size() : 0;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabTitle.get(position));
            int padding = UIUtils.dp2px(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_UNCHANGED;
        }
    }
}
