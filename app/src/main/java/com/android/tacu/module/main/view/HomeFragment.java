package com.android.tacu.module.main.view;

import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.HomeNotifyEvent;
import com.android.tacu.EventBus.model.MainDrawerLayoutOpenEvent;
import com.android.tacu.EventBus.model.MainSwitchEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.auction.view.AuctionDetailsActivity;
import com.android.tacu.module.auction.view.MyAddressActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.main.contract.HomeContract;
import com.android.tacu.module.main.model.HomeModel;
import com.android.tacu.module.main.presenter.HomePresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.NoticeModel;
import com.android.tacu.module.market.view.MarketFragment;
import com.android.tacu.module.market.view.SearchHistoryActivity;
import com.android.tacu.module.market.view.SelfSelectionFragment;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.module.my.view.GoogleHintActivity;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.ScreenShareHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.textview.QMUIScrollTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.CustomViewsInfo;
import com.stx.xhb.xbanner.entity.LocalImageInfo;
import com.stx.xhb.xbanner.transformers.Transformer;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

/**
 * 首页
 * Created by jiazhen on 2018/9/6.
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements HomeContract.IView, ISocketEvent, Observer {

    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.refreshlayout_home)
    SmartRefreshLayout refreshHome;
    @BindView(R.id.banner_home)
    XBanner banner_home;
    @BindView(R.id.lin_notice)
    LinearLayout lin_notice;
    @BindView(R.id.text_scroll)
    QMUIScrollTextView text_scroll;
    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magicIndicator;
    @BindView(R.id.img_search)
    ImageView img_search;
    @BindView(R.id.lin_pair_vol)
    LinearLayout lin_pair_vol;
    @BindView(R.id.tv_pair)
    TextView tvPair;
    @BindView(R.id.tv_vol)
    TextView tvVol;
    @BindView(R.id.tv_last_price)
    TextView tvLastPrice;
    @BindView(R.id.tv_hour)
    TextView tvHour;
    @BindView(R.id.vp)
    ViewPager viewpager;
    @BindView(R.id.img_notice_close)
    ImageView img_notice_close;
    @BindView(R.id.tv_auction)
    TextView tv_auction;

    private Gson gson = new Gson();
    private List<NoticeModel> noticeList = new ArrayList<>();
    private List<CustomViewsInfo> bannerImageList = new ArrayList<>();
    private List<LocalImageInfo> bannerLocalList = new ArrayList<>();
    //打新区过滤完之后数据
    private List<MarketNewModel.TradeCoinsBean> allTradeCoinList = new ArrayList<>();
    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private HomeModel homeModel;
    private IndicatorViewPager indicatorViewPager;

    private String noticeCloseCacheString;

    private ScreenShareHelper screenShareHelper;

    /**
     * 筛选按钮的箭头 0：不显示 1：向下降序 2：向上升序
     * 注：名称/成交量 成交量降序 名称升序
     */
    private int pairVolStatus = 0;
    private int lastPriceStatus = 0;
    private int hourStatus = 0;
    //名称/成交量：pairVol 最新价：lastPrice 24H成交量：24Hour
    private String currentStatus = "";

    private Handler mSocketHandler = new Handler();

    public static HomeFragment newInstance() {
        Bundle bundle = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData(View view) {
        setSocketEvent(this, this, SocketConstant.LOGINAFTERNEW);

        initTitle();

        CustomTextHeaderView header = new CustomTextHeaderView(getContext());
        header.setPrimaryColors(ContextCompat.getColor(getContext(), R.color.content_bg_color), ContextCompat.getColor(getContext(), R.color.text_color));
        refreshHome.setRefreshHeader(header);
        refreshHome.setEnableLoadmore(false);
        refreshHome.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.getHome(false);
                mPresenter.getNoticeInfo();
                mPresenter.auctionTotal();
            }
        });

        banner_home.setPageTransformer(Transformer.Default);

        magicIndicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_bg_color));
        magicIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(getContext(), R.color.tab_default), ContextCompat.getColor(getContext(), R.color.tab_text_color)).setSize(16, 16));
        magicIndicator.setScrollBar(new TextWidthColorBar(getContext(), magicIndicator, ContextCompat.getColor(getContext(), R.color.tab_default), 4));
        magicIndicator.setSplitAuto(false);

        initCache();
    }

    @Override
    protected HomePresenter createPresenter(HomePresenter mPresenter) {
        return new HomePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getHome(homeModel == null);
        mPresenter.getNoticeInfo();
        mPresenter.auctionTotal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketHandler != null) {
            mSocketHandler.removeCallbacksAndMessages(null);
            mSocketHandler = null;
        }
        if (screenShareHelper != null) {
            screenShareHelper.destory();
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshHome != null && refreshHome.isRefreshing()) {
            refreshHome.finishRefresh();
        }
    }

    @OnClick(R.id.tv_auction)
    void auctionClick() {
        if (spUtil.getLogin()) {
            jumpTo(AuctionDetailsActivity.class);
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    @OnClick(R.id.tv_recharge)
    void rechargeClick() {
        if (spUtil.getLogin()) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.MainSwitchCode, new MainSwitchEvent(Constant.MAIN_ASSETS)));
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    @OnClick(R.id.tv_takecoin)
    void takeCoinClick() {
        if (spUtil.getLogin()) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.MainSwitchCode, new MainSwitchEvent(Constant.MAIN_ASSETS)));
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    @OnClick(R.id.tv_otc)
    void otcClick() {
    }

    @OnClick(R.id.tv_help)
    void helpClick() {
        jumpTo(WebviewActivity.createActivity(getContext(), Constant.ZENDESK_HELP));
    }

    @OnClick(R.id.img_notice_close)
    void imgNotceClose() {
        lin_notice.setVisibility(View.GONE);
        SPUtils.getInstance().put(Constant.HOME_NOTICE_CLOSE_CACHE, gson.toJson(noticeList));
    }

    @OnClick(R.id.img_search)
    void searchClick() {
        jumpTo(SearchHistoryActivity.class);
    }

    @OnClick(R.id.lin_pair_vol)
    void linPairVolClick() {
        pairVolClick();
    }

    @OnClick(R.id.tv_last_price)
    void lastPrice() {
        lastPriceClick();
    }

    @OnClick(R.id.tv_hour)
    void hour() {
        hourClick();
    }

    @Override
    public void home(HomeModel model) {
        this.homeModel = model;
        if (model != null) {
            SPUtils.getInstance().put(Constant.HOME_CACHE, gson.toJson(model));
        }
        setBannerValue();
    }

    @Override
    public void showNoticeList(List<NoticeModel> list) {
        this.noticeList = list;
        if (list != null && list.size() > 0) {
            String str = gson.toJson(noticeList);
            SPUtils.getInstance().put(Constant.HOME_NOTICE_CACHE, str);
            if (TextUtils.equals(str, noticeCloseCacheString)) {
                lin_notice.setVisibility(View.GONE);
                noticeList.clear();
            } else {
                lin_notice.setVisibility(View.GONE);
            }
        }
        setInitHomeNoticeValue();
    }

    @Override
    public void auctionTotal(Integer total) {
        if (total != null && total > 0) {
            tv_auction.setVisibility(View.VISIBLE);
        } else {
            tv_auction.setVisibility(View.GONE);
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

    private void sendValue(final List<MarketNewModel> marketModelList) {
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
                            mPresenter.sortLastPrice(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                        case 2://价格倒序
                            mPresenter.sortLastPrice(marketModelList.get(i).tradeCoinsAutoList, "up");
                            break;
                        case 3://涨跌幅正序
                            mPresenter.sortHour(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                        case 4://涨跌幅倒序
                            mPresenter.sortHour(marketModelList.get(i).tradeCoinsAutoList, "up");
                            break;
                        case 5://交易币首字母正序
                            mPresenter.sortPair(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                        case 6://交易币首字母倒序
                            mPresenter.sortPair(marketModelList.get(i).tradeCoinsAutoList, "up");
                            break;
                        case 7://24小时成交量正序
                            mPresenter.sortVol(marketModelList.get(i).tradeCoinsAutoList, "down");
                            break;
                        case 8://24小时成交量倒序
                            mPresenter.sortVol(marketModelList.get(i).tradeCoinsAutoList, "up");
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
        int unEqualNum = 0;
        if (tabTitle != null && tabTitle.size() > 1 && tabTitle.size() == num) {
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
        }

        if (unEqualNum > 0) {
            return;
        }

        //获取标题数据
        if (marketModelList != null && marketModelList.size() > 0) {
            tabTitle.clear();
            for (int i = 0; i < marketModelList.size(); i++) {
                MarketNewModel marketModel = marketModelList.get(i);
                if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                    tabTitle.add(marketModel.name);
                } else {
                    tabTitle.add(marketModel.name_en);
                }
            }
            tabTitle.add(getResources().getString(R.string.market_selfselection));
        }

        if (tabTitle != null && tabTitle.size() > 1) {
            fragmentList.clear();
            for (int i = 0; i < marketModelList.size(); i++) {
                fragmentList.add(MarketFragment.newInstance());
            }
            fragmentList.add(SelfSelectionFragment.newInstance());
        }

        if (fragmentList != null && fragmentList.size() > 0) {
            indicatorViewPager = new IndicatorViewPager(magicIndicator, viewpager);
            indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager()));
            viewpager.setOffscreenPageLimit(fragmentList.size() - 1);
            viewpager.setCurrentItem(0, false);
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
                            if (isSort) {
                                ((SelfSelectionFragment) fragmentList.get(i)).setSortCoinValue(currentStatus, pairVolStatus, lastPriceStatus, hourStatus);
                            } else {
                                ((SelfSelectionFragment) fragmentList.get(i)).setCoinValue(allTradeCoinList);
                            }
                        }
                    } else {
                        if (!isSort && marketModelList != null && marketModelList.size() > 0) {
                            //如果不加这个判断 就不走下面那一步 不知道为啥
                            if (!TextUtils.isEmpty(currentStatus)) {
                                mPresenter.sortList(currentStatus, pairVolStatus, lastPriceStatus, hourStatus, marketModelList.get(i).tradeCoinsList);
                            }

                            ((MarketFragment) fragmentList.get(i)).setCoinValue(gson.toJson(marketModelList.get(i)));
                        } else if (isSort) {
                            ((MarketFragment) fragmentList.get(i)).setSortCoinValue(currentStatus, pairVolStatus, lastPriceStatus, hourStatus);
                        }
                    }
                }
            }
        }
    }

    private void initTitle() {
        mTopBar.setTitle(getResources().getString(R.string.home));
        mTopBar.setBackgroundDividerEnabled(true);

        ImageView circleImageView = new ImageView(getContext());
        circleImageView.setBackgroundColor(Color.TRANSPARENT);
        circleImageView.setScaleType(CENTER_CROP);
        circleImageView.setImageResource(R.mipmap.icon_mines);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventManage.sendEvent(new BaseEvent<>(EventConstant.MainDrawerLayoutOpenCode, new MainDrawerLayoutOpenEvent(Constant.MAIN_HOME)));
            }
        });
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(UIUtils.dp2px(20), UIUtils.dp2px(20));
        lps.topMargin = UIUtils.dp2px(15);
        lps.rightMargin = UIUtils.dp2px(8);
        mTopBar.addLeftView(circleImageView, R.id.qmui_topbar_item_left_back, lps);

        mTopBar.addRightImageButton(R.drawable.icon_saoma, R.id.qmui_topbar_item_right, 18, 18).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.requestPermissions(getContext(), new OnPermissionListener() {
                    @Override
                    public void onPermissionSucceed() {
                        jumpTo(ZXingActivity.class);
                    }

                    @Override
                    public void onPermissionFailed() {
                    }
                }, Permission.Group.CAMERA);
            }
        });
        /*mTopBar.addRightImageButton(R.drawable.icon_share_white, R.id.qmui_topbar_item_right_two, 20, 20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenShareHelper == null) {
                    screenShareHelper = new ScreenShareHelper(getHostActivity());
                }
                screenShareHelper.invoke(refreshHome);
            }
        });*/
    }

    /**
     * 加载本地缓存
     */
    private void initCache() {
        String homeCacheString = SPUtils.getInstance().getString(Constant.HOME_CACHE);
        homeModel = gson.fromJson(homeCacheString, HomeModel.class);

        String noticeCacheString = SPUtils.getInstance().getString(Constant.HOME_NOTICE_CACHE);
        noticeCloseCacheString = SPUtils.getInstance().getString(Constant.HOME_NOTICE_CLOSE_CACHE);
        if (TextUtils.equals(noticeCacheString, noticeCloseCacheString)) {
            lin_notice.setVisibility(View.GONE);
        } else {
            lin_notice.setVisibility(View.GONE);
            noticeList = gson.fromJson(noticeCacheString, new TypeToken<List<NoticeModel>>() {
            }.getType());
        }

        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = gson.fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());
        if (cacheList != null && cacheList.size() > 0) {
            sendValue(cacheList);
        }

        setInitHomeNoticeValue();
        setBannerValue();
    }

    private void setBannerValue() {
        if (homeModel != null && homeModel.banner != null && homeModel.banner.size() > 0) {
            banner_home.setOnItemClickListener(new XBanner.OnItemClickListener() {
                @Override
                public void onItemClick(XBanner banner, Object model, View view, int position) {
                    CustomViewsInfo customViewsInfo = (CustomViewsInfo) model;
                    if (!TextUtils.isEmpty(customViewsInfo.getXBannerUrl())) {
                        jumpTo(WebviewActivity.createActivity(getContext(), customViewsInfo.getXBannerUrl()));
                    }
                }
            });
            banner_home.loadImage(new XBanner.XBannerAdapter() {
                @Override
                public void loadBanner(XBanner banner, Object model, View view, int position) {
                    ImageView imageView = (ImageView) view;
                    imageView.setScaleType(CENTER_CROP);
                    CustomViewsInfo customViewsInfo = (CustomViewsInfo) model;
                    GlideUtils.disPlay(getContext(), customViewsInfo.getXBannerImage(), imageView);
                }
            });
            bannerImageList.clear();
            for (int i = 0; i < homeModel.banner.size(); i++) {
                bannerImageList.add(new CustomViewsInfo(homeModel.banner.get(i).image, homeModel.banner.get(i).url));
            }
            banner_home.setBannerData(bannerImageList);
        } else {
            banner_home.loadImage(new XBanner.XBannerAdapter() {
                @Override
                public void loadBanner(XBanner banner, Object model, View view, int position) {
                    ImageView imageView = (ImageView) view;
                    imageView.setScaleType(CENTER_CROP);
                    LocalImageInfo localImageInfo = (LocalImageInfo) model;
                    GlideUtils.disPlay(getContext(), localImageInfo.getXBannerUrl(), imageView);
                }
            });
            bannerLocalList.clear();
            banner_home.setBannerData(bannerLocalList);
        }
    }

    /**
     * 网络请求到首页公告的接口数据
     */
    private void setInitHomeNoticeValue() {
        if (noticeList != null && noticeList.size() > 0) {
            lin_notice.setVisibility(View.GONE);
            List<String> infos = new ArrayList<>();
            for (int i = 0; i < noticeList.size(); i++) {
                infos.add("【" + noticeList.get(i).type + "】" + noticeList.get(i).title);
            }
            text_scroll.startWithList(infos);
            text_scroll.setOnItemClickListener(new QMUIScrollTextView.OnItemClickListener() {
                @Override
                public void onItemClick(int position, TextView textView) {
                    jumpTo(WebviewActivity.createActivity(getContext(), noticeList.get(position).htmlUrl));
                }
            });
        } else {
            lin_notice.setVisibility(View.GONE);
        }
    }

    public void setConvertModel() {
        if (fragmentList != null && fragmentList.size() > 0) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.HomeNotifyCode, new HomeNotifyEvent(true)));
        }
    }

    private void pairVolClick() {
        clearAllStatus();
        switch (pairVolStatus) {
            case 0:
                setTextRightDrawable(tvVol, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_down));
                setTextRightDrawable(tvPair, null);
                pairVolStatus = 1;
                break;
            case 1:
                setTextRightDrawable(tvVol, null);
                setTextRightDrawable(tvPair, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_up));
                pairVolStatus = 2;
                break;
            case 2:
                setTextRightDrawable(tvVol, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_down));
                setTextRightDrawable(tvPair, null);
                pairVolStatus = 1;
                break;
        }
        currentStatus = "pairVol";
        setFragmentValue(null, true);
    }

    private void lastPriceClick() {
        clearAllStatus();
        switch (lastPriceStatus) {
            case 0:
                setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_down));
                lastPriceStatus = 1;
                break;
            case 1:
                setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_up));
                lastPriceStatus = 2;
                break;
            case 2:
                setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_down));
                lastPriceStatus = 1;
                break;
        }
        currentStatus = "lastPrice";
        setFragmentValue(null, true);
    }

    private void hourClick() {
        clearAllStatus();
        switch (hourStatus) {
            case 0:
                setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_down));
                hourStatus = 1;
                break;
            case 1:
                setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_up));
                hourStatus = 2;
                break;
            case 2:
                setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_down));
                hourStatus = 1;
                break;
        }
        currentStatus = "24Hour";
        setFragmentValue(null, true);
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
        setTextRightDrawable(tvVol, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_default));
        setTextRightDrawable(tvLastPrice, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_default));
        setTextRightDrawable(tvHour, ContextCompat.getDrawable(getContext(), R.drawable.icon_paixu_default));
    }

    private boolean isKeyc() {
        boolean boo = false;
        switch (spUtil.getIsAuthSenior()) {
            case -1:
            case 0:
            case 1:
                showToastError(getString(R.string.please_get_the_level_of_KYC));
                break;
            case 2:
            case 3:
                if (TextUtils.equals(spUtil.getGaStatus(), "0") || TextUtils.equals(spUtil.getGaStatus(), "2")) {
                    jumpTo(GoogleHintActivity.class);
                } else if (!spUtil.getPhoneStatus()) {
                    jumpTo(BindModeActivity.createActivity(getContext(), 3));
                } else if (!spUtil.getValidatePass()) {
                    showToastError(getResources().getString(R.string.exchange_pwd));
                } else {
                    boo = true;
                }
                break;
        }
        return boo;
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

    public List<MarketNewModel.TradeCoinsBean> getTotalTradeList() {
        return allTradeCoinList;
    }
}
