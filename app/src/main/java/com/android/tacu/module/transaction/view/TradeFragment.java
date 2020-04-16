package com.android.tacu.module.transaction.view;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.model.TradeVisibleHintEvent;
import com.android.tacu.module.vip.model.VipDetailRankModel;
import com.android.tacu.socket.AppSocket;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.JumpTradeCodeIsBuyEvent;
import com.android.tacu.EventBus.model.MainDrawerLayoutOpenEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.assets.view.AssetsActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.module.my.view.GoogleHintActivity;
import com.android.tacu.module.my.view.SecurityCenterActivity;
import com.android.tacu.module.transaction.contract.TradeContract;
import com.android.tacu.module.transaction.model.RecordModel;
import com.android.tacu.module.transaction.model.UserAccountModel;
import com.android.tacu.module.transaction.presenter.TradePresenter;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.ScreenShareHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.view.DecimalDigitsInputFilter;
import com.android.tacu.widget.dialog.DroidDialog;
import com.android.tacu.widget.popupwindow.CoinPopWindow;
import com.android.tacu.widget.popupwindow.TradePopWindow;
import com.android.tacu.widget.seekbar.SignSeekBar;
import com.google.gson.Gson;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static com.android.tacu.api.Constant.SELFCOIN_LIST;

/**
 * Created by jiazhen on 2018/9/27.
 */
public class TradeFragment extends BaseFragment<TradePresenter> implements View.OnClickListener, TradeContract.IView, ISocketEvent, Observer {

    @BindView(R.id.root_view)
    View rootView;
    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.con_layout)
    ConstraintLayout con_layout;
    @BindView(R.id.trade_header)
    ConstraintLayout tradeHeader;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.magic_indicator)
    FixedIndicatorView magicIndicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    //买卖情况部分
    private ConstraintLayout parentView;
    private QMUIRoundButton btnBuy;
    private QMUIRoundButton btnSell;
    private QMUIRoundButton btnOk;
    private TextView tvFee;
    private TextView tvTradeAmount;
    private TextView tvAvailableNumber;
    private EditText editNumber;
    private SignSeekBar seekBar;
    private QMUIRoundRelativeLayout rlPwd;
    private CheckBox cbPwd;
    private EditText editPwd;
    private EditText editPrice;
    private TextView tvValuationPrice;
    private QMUIRoundLinearLayout linPrice;
    private QMUIAlphaImageButton btnPriceSub;
    private QMUIAlphaImageButton btnPriceAdd;
    private QMUIAlphaImageButton btnNumberSub;
    private QMUIAlphaImageButton btnNumberAdd;
    private QMUIAlphaButton btnSelectPrice;
    private TextView tvDepthDecimal;
    //交易对部分
    private RecyclerView rySell;
    private RecyclerView ryBuy;
    private TextView tvNewsPrice;
    private TextView tvNewsPriceRnb;
    private TextView tvNewsPriceRate;

    private QMUIAlphaImageButton btnCollect;
    //市价和限价
    private QMUIBottomSheet mPriceSheet;
    //交易深度
    private QMUIBottomSheet mDepthSheet;

    //设置交易左右的位置
    public static final String VIEW_POSITION = "VIEW_POSITION";
    public static final String POSITION_LEFT = "POSITION_LEFT";
    public static final String POSITION_RIGHT = "POSITION_RIGHT";
    private String positionView;
    private ConstraintSet mConstraintSet1, mConstraintSet2;

    private int currencyId = Constant.TAC_CURRENCY_ID;
    private int baseCurrencyId = Constant.ACU_CURRENCY_ID;
    private String currencyNameEn = Constant.TAC_CURRENCY_NAME;
    private String baseCurrencyNameEn = Constant.ACU_CURRENCY_NAME;
    private int pointPrice = 4;
    private int pointNum = 4;
    //深度合并的小数位
    private int depthPointPrice = 4;
    //根据深度合并的小数位得到的最小值
    private double minDepthPriceValue = 0.0001;
    //最小长度的数量值
    private double minPointNumValue = 0.0001;
    //交易对显示的数量默认7条
    private final int DEPTHNUMBER = 7;
    //交易对的深度合并选项默认条数
    private final int DEPTHPOPNUMBER = 4;
    //当前币种信息
    private CurrentTradeCoinModel currentTradeCoinModel;
    //买卖委托列表
    private RecordModel recordModel;
    //用户数据
    private UserAccountModel userData;
    private List<RecordModel.BuyBean> buyRecordModelList = new ArrayList<>();
    private List<RecordModel.SellBean> sellRecordModelList = new ArrayList<>();
    //深度列表
    private List<String> dataList = new ArrayList<>();
    private List<String> dataStringList = new ArrayList<>();

    private List<String> tabDownTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private IndicatorViewPager indicatorViewPager;

    private CurrentEntrustFragment currentEntrustFragment;
    private LastDealFragment lastDealFragment;
    private QuotationFragment quotationFragment;

    private Gson gson = new Gson();
    private SellAdapter sellAdapter;
    private BuyAdapter buyAdapter;

    private CoinPopWindow coinPopWindow;
    private TradePopWindow tradePopWindow;

    private ScreenShareHelper screenShareHelper;

    //当前是买还是卖 默认买
    private boolean isBuy = true;
    //用户等级
    private int authLevel = 0;
    //用户的基础币的可用余额 买入时展示
    private double baseCoinBalance = 0;
    //用户的交易币的可用余额 买入时展示
    private double tradeCoinBalance = 0;
    //防止EditText和SeekBar死循环
    private boolean isInput = true;
    private boolean isSeekProgress = true;
    //行情价标识 (type=1 限价  type =2 市价 )
    private int limitPriceType = 1;
    //手续费买
    private SpannableString buyFee;
    //手续费卖
    private SpannableString sellFee;
    //买的默认价格
    private String buyDefaultPrice;
    //卖的默认价格
    private String sellDefaultPrice;
    //是否添加到自选
    private boolean isCollect = false;
    private SelfModel selfModel;
    //editprice的价格是否从深度的列表中取值
    private boolean isEditPriceChange = true;

    private VipDetailRankModel vipModel;
    private double discountFee = 1;

    private String UserAccountString;
    private String UserAccountTemp;

    private Handler mSocketHandler = new Handler();
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            //监听列表 列表变 可用余额变
            if (spUtil != null && spUtil.getLogin()) {
                AppSocket.getInstance().userAccount(currencyId, baseCurrencyId, spUtil.getToken(), spUtil.getUserUid());
            }
            if (timeHandler != null) {
                timeHandler.postDelayed(this, 2000);
            }
        }
    };

    public static TradeFragment newInstance() {
        Bundle bundle = new Bundle();
        TradeFragment fragment = new TradeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (spUtil != null) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.TradeVisibleCode, new TradeVisibleHintEvent(isVisibleToUser)));
            currentEntrustFragment.setTradeVisible(isVisibleToUser);
        }
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        setTradeRefresh();
        setTradeRequest();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_trade;
    }

    @Override
    protected void initData(View view) {
        setSocketEvent(this, this, SocketConstant.LOGINAFTERCHANGETRADECOIN, SocketConstant.USERACCOUNT, SocketConstant.ENTRUST);

        tv_name.setText(currencyNameEn + "/" + baseCurrencyNameEn);

        initHeader();
        initTradeHeader(tradeHeader);
    }

    @Override
    protected TradePresenter createPresenter(TradePresenter mPresenter) {
        return new TradePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        isEditPriceChange = true;
        setPwdVis();
        initCacheSelf();
        setTvFee();
        setAvailableNumber();
        setTradeRequest();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.post(timeRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketHandler != null) {
            mSocketHandler.removeCallbacksAndMessages(null);
            mSocketHandler = null;
        }
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
        if (screenShareHelper != null) {
            screenShareHelper.destory();
        }
    }

    @OnClick(R.id.tv_name)
    void tvNameClick() {
        initCoinPopUp(con_layout);
    }

    @OnClick(R.id.img_more)
    void imgMoreClick() {
        initTradePopUp(con_layout);
    }

    @OnClick(R.id.img_kline)
    void klineClick() {
        jumpTo(MarketDetailsActivity.createActivity(getContext(), currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn));
    }

    @OnClick(R.id.btn_select_price)
    void selectPriceClick() {
        initListPricePopup();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_buy:
                buyOrSellStatus(true);
                break;
            case R.id.btn_sell:
                buyOrSellStatus(false);
                break;
            case R.id.btn_ok:
                try {
                    if (spUtil.getLogin()) {
                        Animation shake = AnimationUtils.loadAnimation(getHostActivity(), R.anim.anim_shake);
                        String price = editPrice.getText().toString().trim();
                        String number = editNumber.getText().toString().trim();
                        //type =2 行情价 type=1 限价
                        if (limitPriceType == 1) {
                            if (TextUtils.isEmpty(price) || Double.parseDouble(price) == 0) {
                                editPrice.startAnimation(shake);
                                return;
                            }
                        }
                        if (TextUtils.isEmpty(number) || Double.parseDouble(number) == 0) {
                            editNumber.startAnimation(shake);
                            return;
                        }
                        String pwd = editPwd.getText().toString().trim();
                        if (spUtil.getPwdVisibility()) {
                            if (TextUtils.isEmpty(pwd)) {
                                editPwd.startAnimation(shake);
                                return;
                            }
                        }

                        if (limitPriceType == 1 && currentTradeCoinModel != null) {
                            if (Double.parseDouble(number) < currentTradeCoinModel.currentTradeCoin.amountLowLimit || Double.parseDouble(number) > currentTradeCoinModel.currentTradeCoin.amountHighLimit) {
                                showToastError(String.format(getResources().getString(R.string.currentCoin_limit_high_low), FormatterUtils.getFormatValue(currentTradeCoinModel.currentTradeCoin.amountLowLimit), FormatterUtils.getFormatValue(currentTradeCoinModel.currentTradeCoin.amountHighLimit)));
                                return;
                            }
                        }
                        mPresenter.order(isBuy ? 1 : 2, currencyId, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase() : "", (isBuy && limitPriceType == 2) ? "0" : number, (!isBuy && limitPriceType == 2) ? "0" : ((isBuy && limitPriceType == 2) ? number : price), limitPriceType, baseCurrencyId);
                    } else {
                        jumpTo(LoginActivity.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cb_pwd:
                cbPwd.toggle();
                cancelPwdDialog();
                break;
            case R.id.btn_price_sub:
                try {
                    minDepthPriceValue = 1 / (Math.pow(10, depthPointPrice));
                    if (!TextUtils.isEmpty(editPrice.getText().toString())) {
                        if (Double.parseDouble(editPrice.getText().toString()) <= 0) {
                            return;
                        }
                        if (!isBuy && depthPointPrice < pointPrice) {
                            editPrice.setText(getFormatDoubleUp(MathHelper.sub(Double.parseDouble(editPrice.getText().toString()), minDepthPriceValue)));
                        } else {
                            editPrice.setText(getFormatDoubleDown(MathHelper.sub(Double.parseDouble(editPrice.getText().toString()), minDepthPriceValue)));
                        }
                    } else {
                        editPrice.setText(String.valueOf(getFormatDoubleDown(minDepthPriceValue)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_price_add:
                try {
                    minDepthPriceValue = 1 / (Math.pow(10, depthPointPrice));
                    if (!TextUtils.isEmpty(editPrice.getText().toString())) {
                        if (!isBuy && depthPointPrice < pointPrice) {
                            editPrice.setText(getFormatDoubleUp(MathHelper.add(Double.parseDouble(editPrice.getText().toString()), minDepthPriceValue)));
                        } else {
                            editPrice.setText(getFormatDoubleDown(MathHelper.add(Double.parseDouble(editPrice.getText().toString()), minDepthPriceValue)));
                        }
                    } else {
                        editPrice.setText(String.valueOf(getFormatDoubleDown(minDepthPriceValue)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_number_sub:
                try {
                    minPointNumValue = 1 / (Math.pow(10, pointNum));
                    if (!TextUtils.isEmpty(editNumber.getText().toString())) {
                        if (Double.parseDouble(editNumber.getText().toString()) <= 0) {
                            return;
                        }
                        if (!TextUtils.isEmpty(editNumber.getText().toString())) {
                            editNumber.setText(BigDecimal.valueOf(MathHelper.sub(Double.parseDouble(editNumber.getText().toString()), minPointNumValue)).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString());
                        } else {
                            editNumber.setText(BigDecimal.valueOf(minPointNumValue).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString());
                        }
                    } else {
                        editNumber.setText(BigDecimal.valueOf(minPointNumValue).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_number_add:
                try {
                    minPointNumValue = 1 / (Math.pow(10, pointNum));
                    if (!TextUtils.isEmpty(editNumber.getText().toString())) {
                        if (!TextUtils.isEmpty(editNumber.getText().toString())) {
                            editNumber.setText(BigDecimal.valueOf(MathHelper.add(Double.parseDouble(editNumber.getText().toString()), minPointNumValue)).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString());
                        } else {
                            editNumber.setText(BigDecimal.valueOf(minPointNumValue).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString());
                        }
                    } else {
                        editNumber.setText(BigDecimal.valueOf(minPointNumValue).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_depth_decimal:
                initListDepthPopup();
                break;
        }
    }

    @Override
    public void uploadSelfSuccess() {
        SPUtils.getInstance().put(SELFCOIN_LIST, gson.toJson(selfModel));
        if (isCollect) {
            showToastSuccess(getResources().getString(R.string.business_collect));
        } else {
            showToastSuccess(getResources().getString(R.string.business_uncollect));
        }
    }

    @Override
    public void uploadSelfError() {
        if (isCollect) {
            showToastError(getResources().getString(R.string.business_collect_error));
        } else {
            showToastError(getResources().getString(R.string.business_uncollect_error));
        }
    }

    @Override
    public void buySuccess() {
        showToastSuccess(getResources().getString(R.string.order_coins_success));
        editPrice.setText("");
        editNumber.setText("");
        editPwd.setText("");
        currentEntrustFragment.notiy();
    }

    @Override
    public void updateFdPwdSuccess() {
        showToastSuccess(getResources().getString(R.string.close_tradepwd_message));
        spUtil.setPwdVisibility(false);
        setPwdVis();
    }

    @Override
    public void updateFdPwdError() {
        cbPwd.setChecked(spUtil.getPwdVisibility());
    }

    @Override
    public void selectVipDetail(List<VipDetailRankModel> list) {
        if (list != null && list.size() > 0) {
            vipModel = list.get(0);
            setTvFee();
        } else {
            vipModel = null;
        }
    }

    @Override
    public void socketConnectEventAgain() {
        AppSocket.getInstance().coinInfo(currencyId, baseCurrencyId);
        AppSocket.getInstance().entrust(currencyId, baseCurrencyId);
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
                                    currentTradeCoinModel = coinInfo.getCoinModel();
                                    coinInfo();
                                }
                                break;
                            //获取用户账号信息
                            case SocketConstant.USERACCOUNT:
                                ObserverModel.UserAccount userAccount = model.getUserAccount();
                                if (userAccount != null) {
                                    userData = userAccount.getAccountModel();
                                    UserAccountString = gson.toJson(userData);
                                    userAccountInfo();
                                }
                                break;
                            //获取买卖委托
                            case SocketConstant.ENTRUST:
                                ObserverModel.Entrust entrust = model.getEntrust();
                                if (entrust != null) {
                                    recordModel = entrust.getRecordModel();
                                    entrustInfo();
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

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.JumpTradeIsBuyCode:
                    JumpTradeCodeIsBuyEvent jumpCoinEvent = (JumpTradeCodeIsBuyEvent) event.getData();
                    if (jumpCoinEvent != null) {
                        setCoinInfo(jumpCoinEvent.getCurrencyId(), jumpCoinEvent.getBaseCurrencyId(), jumpCoinEvent.getCurrencyName(), jumpCoinEvent.getBaseCurrencyNameEn());
                        buyOrSellStatus(jumpCoinEvent.isBuy());
                    }
                    break;
            }
        }
    }

    private void initHeader() {
        mTopBar.setTitle(getResources().getString(R.string.trade));
        mTopBar.setBackgroundDividerEnabled(true);

        ImageView circleImageView = new ImageView(getContext());
        circleImageView.setBackgroundColor(Color.TRANSPARENT);
        circleImageView.setScaleType(CENTER_CROP);
        circleImageView.setImageResource(R.mipmap.icon_mines);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventManage.sendEvent(new BaseEvent<>(EventConstant.MainDrawerLayoutOpenCode, new MainDrawerLayoutOpenEvent(Constant.MAIN_TRADE)));
            }
        });
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(UIUtils.dp2px(20), UIUtils.dp2px(20));
        lps.topMargin = UIUtils.dp2px(15);
        lps.rightMargin = UIUtils.dp2px(8);
        mTopBar.addLeftView(circleImageView, R.id.qmui_topbar_item_left_back, lps);

        btnCollect = mTopBar.addRightImageButton(R.drawable.icon_rating_uncollect, R.id.qmui_topbar_item_right, 24, 24);
        btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spUtil.getLogin()) {
                    if (selfModel == null) {
                        selfModel = new SelfModel();
                    }
                    collectSelf();
                    mPresenter.uploadSelfList(gson.toJson(selfModel));
                } else {
                    jumpTo(LoginActivity.class);
                }
            }
        });
        /*mTopBar.addRightImageButton(R.drawable.icon_share_white, R.id.qmui_topbar_item_right_two, 20, 20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenShareHelper == null) {
                    screenShareHelper = new ScreenShareHelper(getHostActivity);
                }
                screenShareHelper.invoke(rootView);
            }
        });*/

        magicIndicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.content_bg_color));
        magicIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(getContext(), R.color.tab_default), ContextCompat.getColor(getContext(), R.color.text_grey_2)).setSize(14, 14));
        magicIndicator.setScrollBar(new ColorBar(getContext(), ContextCompat.getColor(getContext(), R.color.tab_default), 4));
        magicIndicator.setSplitMethod(FixedIndicatorView.SPLITMETHOD_WRAP);

        tabDownTitle.add(getResources().getString(R.string.current_entrust));
        tabDownTitle.add(getResources().getString(R.string.lastest_deal));
        tabDownTitle.add(getResources().getString(R.string.quotation_view));

        currentEntrustFragment = CurrentEntrustFragment.newInstance(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        lastDealFragment = LastDealFragment.newInstance(currencyId, baseCurrencyId);
        quotationFragment = QuotationFragment.newInstance(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        fragmentList.add(currentEntrustFragment);
        fragmentList.add(lastDealFragment);
        fragmentList.add(quotationFragment);

        indicatorViewPager = new IndicatorViewPager(magicIndicator, viewPager);
        indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(0, false);
    }

    /**
     * 头部控件
     *
     * @param view
     */
    private void initTradeHeader(View view) {
        parentView = (ConstraintLayout) view;
        btnBuy = view.findViewById(R.id.btn_buy);
        btnSell = view.findViewById(R.id.btn_sell);
        btnOk = view.findViewById(R.id.btn_ok);
        tvFee = view.findViewById(R.id.tv_fee);
        tvTradeAmount = view.findViewById(R.id.tv_trade_amount);
        tvAvailableNumber = view.findViewById(R.id.tv_available_number);
        editNumber = view.findViewById(R.id.edit_number);
        seekBar = view.findViewById(R.id.seekbar_sign);
        rlPwd = view.findViewById(R.id.rl_pwd);
        cbPwd = view.findViewById(R.id.cb_pwd);
        editPwd = view.findViewById(R.id.edit_pwd);
        editPrice = view.findViewById(R.id.edit_price);
        tvValuationPrice = view.findViewById(R.id.tv_valuation_price);
        linPrice = view.findViewById(R.id.lin_price);
        btnPriceSub = view.findViewById(R.id.btn_price_sub);
        btnPriceAdd = view.findViewById(R.id.btn_price_add);
        btnNumberSub = view.findViewById(R.id.btn_number_sub);
        btnNumberAdd = view.findViewById(R.id.btn_number_add);
        btnSelectPrice = view.findViewById(R.id.btn_select_price);
        tvDepthDecimal = view.findViewById(R.id.tv_depth_decimal);
        rySell = view.findViewById(R.id.recyclerView_sell);
        ryBuy = view.findViewById(R.id.recyclerView_buy);
        tvNewsPrice = view.findViewById(R.id.tv_news_price);
        tvNewsPriceRnb = view.findViewById(R.id.tv_news_price_rnb);
        tvNewsPriceRate = view.findViewById(R.id.tv_news_price_rate);

        limitEditLength();

        sellAdapter = new SellAdapter();
        buyAdapter = new BuyAdapter();
        rySell.setLayoutManager(new LinearLayoutManager(getContext()));
        ryBuy.setLayoutManager(new LinearLayoutManager(getContext()));
        rySell.setAdapter(sellAdapter);
        ryBuy.setAdapter(buyAdapter);

        btnBuy.setOnClickListener(this);
        btnSell.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        cbPwd.setOnClickListener(this);
        btnPriceSub.setOnClickListener(this);
        btnPriceAdd.setOnClickListener(this);
        btnNumberSub.setOnClickListener(this);
        btnNumberAdd.setOnClickListener(this);
        tvDepthDecimal.setOnClickListener(this);

        editPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!TextUtils.isEmpty(s.toString())) {
                        if (currentTradeCoinModel != null) {
                            tvValuationPrice.setText("≈" + getMcM(baseCurrencyId, Double.valueOf(s.toString().trim())));
                        }
                    } else {
                        tvValuationPrice.setText("");
                    }
                    if (limitPriceType == 1) {
                        setTradeAmounnt();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isInput = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!TextUtils.isEmpty(s.toString())) {
                        float progress = 0;
                        if (isBuy && baseCoinBalance != 0) {
                            //type =2 行情价 type=1 限价
                            if (limitPriceType == 1) {
                                if (!TextUtils.isEmpty(editPrice.getText().toString().trim())) {
                                    progress = (float) (Double.parseDouble(s.toString()) * Double.parseDouble(editPrice.getText().toString()) / baseCoinBalance);
                                }
                            } else if (limitPriceType == 2) {
                                progress = (float) (Double.parseDouble(s.toString()) / baseCoinBalance);
                            }
                        } else if (!isBuy && tradeCoinBalance != 0) {
                            progress = (float) (Double.parseDouble(s.toString()) / tradeCoinBalance);
                        }
                        if (isSeekProgress) {
                            if (progress > 1) {
                                progress = 1;
                            } else if (progress < 0) {
                                progress = 0;
                            }
                            seekBar.setProgress(progress);
                        }
                    } else {
                        if (isSeekProgress) {
                            seekBar.setProgress(0);
                        }
                    }
                    if (limitPriceType == 1) {
                        setTradeAmounnt();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInput = true;
            }
        });
        seekBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressStart() {
                isSeekProgress = false;
            }

            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                try {
                    double balance = 0;
                    if (isBuy) {
                        if (limitPriceType == 1) {
                            if (!TextUtils.isEmpty(editPrice.getText().toString().trim())) {
                                balance = progressFloat * baseCoinBalance / Double.parseDouble(editPrice.getText().toString());
                            }
                        } else if (limitPriceType == 2) {
                            balance = progressFloat * baseCoinBalance;
                        }
                    } else {
                        balance = progressFloat * tradeCoinBalance;
                    }
                    if (isInput) {
                        editNumber.setText(BigDecimal.valueOf(balance).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                isSeekProgress = true;
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

        mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintSet1.clone(parentView);
        mConstraintSet2.clone(getContext(), R.layout.header_trade2);
        positionView = SPUtils.getInstance().getString(VIEW_POSITION);
        setLinPosition(positionView, false);
        buyOrSellStatus(isBuy);
    }

    private void initCacheSelf() {
        isCollect = false;
        btnCollect.setImageResource(R.drawable.icon_rating_uncollect);
        if (spUtil.getLogin()) {
            String tempString = SPUtils.getInstance().getString(SELFCOIN_LIST);
            selfModel = gson.fromJson(tempString, SelfModel.class);
            if (selfModel == null) {
                selfModel = new SelfModel();
            }
            if (selfModel != null && selfModel.checkedList != null && selfModel.checkedList.size() > 0) {
                for (int i = 0; i < selfModel.checkedList.size(); i++) {
                    if (TextUtils.equals(selfModel.checkedList.get(i).symbol, currencyId + "," + baseCurrencyId)) {
                        isCollect = true;
                        break;
                    }
                }
            }
            if (isCollect) {
                btnCollect.setImageResource(R.drawable.icon_rating_collect_white);
            } else {
                btnCollect.setImageResource(R.drawable.icon_rating_uncollect);
            }
        }
    }

    /**
     * 添加和取消自选
     */
    private void collectSelf() {
        SelfModel.SymbolBean bean = new SelfModel.SymbolBean();
        for (int i = 0; i < selfModel.checkedList.size(); i++) {
            if (TextUtils.equals(selfModel.checkedList.get(i).symbol, currencyId + "," + baseCurrencyId)) {
                bean = selfModel.checkedList.get(i);
                break;
            }
        }
        if (isCollect) {
            isCollect = false;
            btnCollect.setImageResource(R.drawable.icon_rating_uncollect);
            selfModel.checkedList.remove(bean);
        } else {
            isCollect = true;
            btnCollect.setImageResource(R.drawable.icon_rating_collect_white);
            SelfModel.SymbolBean symbolBean = new SelfModel.SymbolBean();
            symbolBean.symbol = currencyId + "," + baseCurrencyId;
            selfModel.checkedList.add(0, symbolBean);
        }
    }

    /**
     * 切换当前的币种
     *
     * @param currencyId
     * @param baseCurrencyId
     * @param currencyNameEn
     * @param baseCurrencyNameEn
     */
    public void setCoinInfo(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;

        if (currentEntrustFragment != null) {
            currentEntrustFragment.setCoinInfo(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        }
        if (lastDealFragment != null) {
            lastDealFragment.setValue(currencyId, baseCurrencyId);
        }
        if (quotationFragment != null) {
            quotationFragment.setValue(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        }

        tv_name.setText(currencyNameEn + "/" + baseCurrencyNameEn);
        tvNewsPrice.setText("--");
        tvNewsPriceRnb.setText("--");
        tvNewsPriceRate.setText("--");
        tvDepthDecimal.setText("");
        tvAvailableNumber.setText(getResources().getString(R.string.available_number) + " --" + baseCurrencyNameEn);
        sellAdapter.setNewData(null);
        buyAdapter.setNewData(null);
        onEmit();
        setTradeRefresh();
        marketPriceShow();
        initCacheSelf();
    }

    /**
     * 处理从Socket获取的币种信息
     */
    private void coinInfo() {
        if (currentTradeCoinModel != null && currentTradeCoinModel.currentTradeCoin != null && currentTradeCoinModel.currentTradeCoin.baseCurrencyId == baseCurrencyId && currentTradeCoinModel.currentTradeCoin.currencyId == currencyId) {
            if (lastDealFragment != null) {
                lastDealFragment.setCurrentTradeCoinModel(currentTradeCoinModel);
            }
            if (quotationFragment != null) {
                quotationFragment.setCurrentTradeCoinModel(currentTradeCoinModel);
            }
            pointPrice = currentTradeCoinModel.currentTradeCoin.pointPrice;
            pointNum = currentTradeCoinModel.currentTradeCoin.pointNum;

            limitEditLength();

            setTvFee();
            if (TextUtils.isEmpty(tvDepthDecimal.getText().toString())) {
                depthPointPrice = pointPrice;
                tvDepthDecimal.setText(String.format(getResources().getString(R.string.price_point_number), String.valueOf(depthPointPrice)));

                //深度弹窗列表的数据
                dataList.clear();
                dataStringList.clear();
                if (pointPrice >= DEPTHPOPNUMBER) {
                    for (int i = pointPrice; i > pointPrice - 4; i--) {
                        dataList.add(String.valueOf(i));
                        dataStringList.add(String.format(getResources().getString(R.string.price_point_number), String.valueOf(i)));
                    }
                } else {
                    for (int i = pointPrice; i >= 0; i--) {
                        dataList.add(String.valueOf(i));
                        dataStringList.add(String.format(getResources().getString(R.string.price_point_number), String.valueOf(i)));
                    }
                }
            }
            tvNewsPrice.setText(BigDecimal.valueOf(currentTradeCoinModel.currentTradeCoin.currentAmount).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            tvNewsPriceRnb.setText("≈" + getMcM(baseCurrencyId, currentTradeCoinModel.currentTradeCoin.currentAmount));
            if (currentTradeCoinModel.currentTradeCoin.changeRate >= 0) {
                tvNewsPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.color_riseup));
                tvNewsPriceRate.setText("+" + FormatterUtils.getFormatValue(currentTradeCoinModel.currentTradeCoin.changeRate) + "%");
                tvNewsPriceRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_riseup));
            } else {
                tvNewsPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.color_risedown));
                tvNewsPriceRate.setText(FormatterUtils.getFormatValue(currentTradeCoinModel.currentTradeCoin.changeRate) + "%");
                tvNewsPriceRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_risedown));
            }
        }
    }

    /**
     * 根据不同用户收取不同手续费
     */
    private void setTvFee() {
        if (!spUtil.getLogin()) {
            authLevel = 0;
        }
        if (authLevel == 0) {
            tvFee.setVisibility(View.GONE);
        } else {
            tvFee.setVisibility(View.VISIBLE);
            setFeeString();
            if (isBuy) {
                tvFee.setText(buyFee);
            } else {
                tvFee.setText(sellFee);
            }
        }
    }

    /**
     * 给手续费赋值 但是authLevel和currentTradeCoinModel的字段是从两个socket取得
     * 所以都要处理一下
     */
    private void setFeeString() {
        discountFee = 1;
        if (vipModel != null && vipModel.discountFee != null && spUtil.getVip() > 0) {
            discountFee = vipModel.discountFee;
        }
        if (currentTradeCoinModel != null && currentTradeCoinModel.currentTradeCoin != null) {
            if (isBuy) {
                buyFee = null;
                switch (authLevel) {
                    case 1:
                        buyFee = new SpannableString(getResources().getString(R.string.fee) + " " + (currentTradeCoinModel.currentTradeCoin.kycBuy.feeType == 2 ? MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycBuy.feeKyc1 * 100, discountFee) + "%" : MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycBuy.feeKyc1, discountFee)));
                        break;
                    case 2:
                        buyFee = new SpannableString(getResources().getString(R.string.fee) + " " + (currentTradeCoinModel.currentTradeCoin.kycBuy.feeType == 2 ? MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycBuy.feeKyc2 * 100, discountFee) + "%" : MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycBuy.feeKyc2, discountFee)));
                        break;
                    case 3:
                        buyFee = new SpannableString(getResources().getString(R.string.fee) + " " + (currentTradeCoinModel.currentTradeCoin.kycBuy.feeType == 2 ? MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycBuy.feeKyc3 * 100, discountFee) + "%" : MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycBuy.feeKyc3, discountFee)));
                        break;
                }
            } else {
                sellFee = null;
                switch (authLevel) {
                    case 1:
                        sellFee = new SpannableString(getResources().getString(R.string.fee) + " " + (currentTradeCoinModel.currentTradeCoin.kycSell.feeType == 2 ? MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycSell.feeKyc1 * 100, discountFee) + "%" : MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycSell.feeKyc1, discountFee)));
                        break;
                    case 2:
                        sellFee = new SpannableString(getResources().getString(R.string.fee) + " " + (currentTradeCoinModel.currentTradeCoin.kycSell.feeType == 2 ? MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycSell.feeKyc2 * 100, discountFee) + "%" : MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycSell.feeKyc2, discountFee)));
                        break;
                    case 3:
                        sellFee = new SpannableString(getResources().getString(R.string.fee) + " " + (currentTradeCoinModel.currentTradeCoin.kycSell.feeType == 2 ? MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycSell.feeKyc3 * 100, discountFee) + "%" : MathHelper.mul1(currentTradeCoinModel.currentTradeCoin.kycSell.feeKyc3, discountFee)));
                        break;
                }
            }
        }
    }

    /**
     * 处理从Socket获取的用户信息
     */
    private void userAccountInfo() {
        if (userData != null && userData.baseCurrencyId == baseCurrencyId && userData.tradeCurrencyId == currencyId) {
            baseCoinBalance = userData.baseCoinBalance;
            tradeCoinBalance = userData.tradeCoinBalance;
            authLevel = userData.authLevel;
            setTvFee();
            setAvailableNumber();
        }
    }

    /**
     * 根据买卖展示不同的用户可用余额
     */
    private void setAvailableNumber() {
        if (spUtil.getLogin()) {
            if (isBuy) {
                tvAvailableNumber.setText(getResources().getString(R.string.available_number) + " " + BigDecimal.valueOf(baseCoinBalance).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString() + baseCurrencyNameEn);
            } else {
                tvAvailableNumber.setText(getResources().getString(R.string.available_number) + " " + BigDecimal.valueOf(tradeCoinBalance).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString() + currencyNameEn);
            }
            if (!TextUtils.equals(UserAccountString, UserAccountTemp)) {
                UserAccountTemp = UserAccountString;
                if (currentEntrustFragment != null) {
                    currentEntrustFragment.notiy();
                }
                if (lastDealFragment != null) {
                    lastDealFragment.notiy();
                }
            }
        } else {
            if (isBuy) {
                tvAvailableNumber.setText(getResources().getString(R.string.available_number) + " --" + baseCurrencyNameEn);
            } else {
                tvAvailableNumber.setText(getResources().getString(R.string.available_number) + " --" + currencyNameEn);
            }
        }
    }

    /**
     * 处理从Socket获取的买卖委托
     */
    private void entrustInfo() {
        mSocketHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recordModel != null && recordModel.baseCurrencyId == baseCurrencyId && recordModel.tradeCurrencyId == currencyId) {
                    //Socekt的加载动画
                    hideLoadingView();
                    setDepthPriceList(true);
                    if (quotationFragment != null) {
                        quotationFragment.entrustInfo(recordModel, currencyNameEn, baseCurrencyNameEn);
                    }
                }
            }
        });
    }

    /**
     * 深度和并的小数位处理
     */
    private void setDepthPriceList(boolean isSocket) {
        if (recordModel != null) {
            //买
            buyRecordModelList.clear();
            if (recordModel.buy != null && recordModel.buy.size() > 0) {
                String currentBuy = getFormatDoubleDown(recordModel.buy.get(0).current);
                RecordModel.BuyBean buyBean = null;
                for (int i = 0; i < recordModel.buy.size(); i++) {
                    buyBean = recordModel.buy.get(i);
                    buyBean.buyEntrustNumber = buyBean.number;
                    if (i == 0) {
                        buyRecordModelList.add(buyBean);
                    } else {
                        if (TextUtils.equals(getFormatDoubleDown(buyBean.current), currentBuy)) {
                            buyRecordModelList.get(buyRecordModelList.size() - 1).buyEntrustNumber += buyBean.number;
                        } else {
                            currentBuy = getFormatDoubleDown(buyBean.current);
                            buyRecordModelList.add(buyBean);
                        }
                    }
                }
            }
            //卖
            sellRecordModelList.clear();
            if (recordModel.sell != null && recordModel.sell.size() > 0) {
                String currentSell = "";
                if (depthPointPrice < pointPrice) {
                    currentSell = getFormatDoubleUp(recordModel.sell.get(0).current);
                } else {
                    currentSell = getFormatDoubleDown(recordModel.sell.get(0).current);
                }
                RecordModel.SellBean sellBean = null;
                for (int i = 0; i < recordModel.sell.size(); i++) {
                    sellBean = recordModel.sell.get(i);
                    sellBean.sellEntrustNumber = sellBean.number;
                    if (i == 0) {
                        sellRecordModelList.add(sellBean);
                    } else {
                        if (depthPointPrice < pointPrice ? (TextUtils.equals(getFormatDoubleUp(sellBean.current), currentSell)) : (TextUtils.equals(getFormatDoubleDown(sellBean.current), currentSell))) {
                            sellRecordModelList.get(sellRecordModelList.size() - 1).sellEntrustNumber += sellBean.number;
                        } else {
                            currentSell = depthPointPrice < pointPrice ? getFormatDoubleUp(sellBean.current) : getFormatDoubleDown(sellBean.current);
                            sellRecordModelList.add(sellBean);
                        }
                    }
                }
            }

            //计算sell深度
            if (sellRecordModelList != null && sellRecordModelList.size() > 0) {
                for (int i = 0; i < sellRecordModelList.size(); i++) {
                    sellRecordModelList.get(i).sellEntrustScale = BigDecimal.valueOf(sellRecordModelList.get(i).current).doubleValue() * BigDecimal.valueOf(sellRecordModelList.get(i).number).doubleValue() / recordModel.entrustScale;
                }
            }
            //计算buy深度
            if (buyRecordModelList != null && buyRecordModelList.size() > 0) {
                for (int i = 0; i < buyRecordModelList.size(); i++) {
                    buyRecordModelList.get(i).buyEntrustScale = BigDecimal.valueOf(buyRecordModelList.get(i).current).doubleValue() * BigDecimal.valueOf(buyRecordModelList.get(i).number).doubleValue() / recordModel.entrustScale;
                }
            }

            if (sellRecordModelList.size() >= DEPTHNUMBER) {
                List<RecordModel.SellBean> tempList = sellRecordModelList.subList(0, DEPTHNUMBER);
                Collections.reverse(tempList);
                sellAdapter.setNewData(tempList);
            } else {
                Collections.reverse(sellRecordModelList);
                sellAdapter.setNewData(sellRecordModelList);
            }
            if (buyRecordModelList.size() >= DEPTHNUMBER) {
                buyAdapter.setNewData(buyRecordModelList.subList(0, DEPTHNUMBER));
            } else {
                buyAdapter.setNewData(buyRecordModelList);
            }

            if (sellRecordModelList != null && sellRecordModelList.size() > 0) {
                buyDefaultPrice = depthPointPrice < pointPrice ? getFormatDoubleUp(sellRecordModelList.get(sellAdapter.getData().size() - 1).current) : getFormatDoubleDown(sellRecordModelList.get(sellAdapter.getData().size() - 1).current);
            } else {
                buyDefaultPrice = "";
            }
            if (buyRecordModelList != null && buyRecordModelList.size() > 0) {
                sellDefaultPrice = getFormatDoubleDown(buyRecordModelList.get(0).current);
            } else {
                sellDefaultPrice = "";
            }

            if (!isSocket || isEditPriceChange) {
                if (isEditPriceChange) {
                    isEditPriceChange = false;
                }
                setDefaultPriceValue();
            }
        }
    }

    /**
     * 获得交易列表时
     * 买的状态取卖的列表的最小值作为默认价格
     * 卖的状态取买的列表的最大值作为默认价格
     */
    private void setDefaultPriceValue() {
        if (limitPriceType == 1) {
            if (isBuy) {
                editPrice.setText(buyDefaultPrice);
            } else {
                editPrice.setText(sellDefaultPrice);
            }
        }
    }

    /**
     * 密码是否需要输入
     */
    private void setPwdVis() {
        if (spUtil.getLogin() && spUtil.getValidatePass() && spUtil.getPwdVisibility()) {
            rlPwd.setVisibility(View.VISIBLE);
            cbPwd.setChecked(true);
        } else {
            rlPwd.setVisibility(View.GONE);
            cbPwd.setChecked(false);
        }
    }

    /**
     * 加载Socket
     */
    private void setTradeRefresh() {
        isEditPriceChange = true;
        editPrice.setText("");
        editNumber.setText("");
    }

    private void setTradeRequest() {
        if (spUtil.getLogin() && isVisibleToUser) {
            mPresenter.selectVipDetail();
        }
    }

    /**
     * 取消交易密码
     */
    private void cancelPwdDialog() {
        final View view = View.inflate(getContext(), R.layout.view_pwd_dialog, null);
        CommonUtils.handleEditTextEyesIssueInBrightBackground(view.findViewById(R.id.et_pwd));
        new DroidDialog.Builder(getContext())
                .title(getResources().getString(R.string.enter_trading_password))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        final EditText etPassword = view.findViewById(R.id.et_pwd);
                        final String pwd = etPassword.getText().toString().trim();
                        if (TextUtils.isEmpty(pwd)) {
                            showToastError(getResources().getString(R.string.enter_trading_password));
                            return;
                        }
                        mPresenter.updateFdPwdEnabled(2, Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase());
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                    @Override
                    public void onNegative(Dialog droidDialog) {
                    }
                })
                .cancelable(false, false)
                .show();
    }

    /**
     * 设置交易页面左右的位置
     * isAnim：加载的时候不需要 点击的时候需要动画
     */
    private void setLinPosition(String positionString, boolean isAnim) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isAnim) {
            TransitionManager.beginDelayedTransition(parentView);//动画效果
        }
        switch (positionString) {
            case POSITION_LEFT://viewOne在左边
                mConstraintSet1.applyTo(parentView);
                break;
            case POSITION_RIGHT://viewOne在右边
                mConstraintSet2.applyTo(parentView);
                break;
        }
    }

    /**
     * 设置当前买卖的状态
     */
    private void buyOrSellStatus(boolean isBuy) {
        this.isBuy = isBuy;
        if (isBuy) {//买
            ((QMUIRoundButtonDrawable) btnBuy.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_riseup));
            ((QMUIRoundButtonDrawable) btnBuy.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(getContext(), R.color.color_riseup));
            btnBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.text_white));
            ((QMUIRoundButtonDrawable) btnSell.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_transparent));
            ((QMUIRoundButtonDrawable) btnSell.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(getContext(), R.color.color_grey_2));
            btnSell.setTextColor(ContextCompat.getColor(getContext(), R.color.color_grey_2));
            ((QMUIRoundButtonDrawable) btnOk.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_riseup));
            btnOk.setText(getResources().getString(R.string.buy));
        } else {//卖
            ((QMUIRoundButtonDrawable) btnBuy.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_transparent));
            ((QMUIRoundButtonDrawable) btnBuy.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(getContext(), R.color.color_grey_2));
            btnBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.color_grey_2));
            ((QMUIRoundButtonDrawable) btnSell.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_risedown));
            ((QMUIRoundButtonDrawable) btnSell.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(getContext(), R.color.color_risedown));
            btnSell.setTextColor(ContextCompat.getColor(getContext(), R.color.text_white));
            ((QMUIRoundButtonDrawable) btnOk.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_risedown));
            btnOk.setText(getResources().getString(R.string.sell));
        }
        setTvFee();
        setAvailableNumber();
        setDefaultPriceValue();
        marketPriceShow();
        editNumber.setText("");
    }

    /**
     * 卖出
     */
    private class SellAdapter extends BaseQuickAdapter<RecordModel.SellBean, BaseViewHolder> {

        public SellAdapter() {
            super(R.layout.item_trade_depth);
        }

        @Override
        protected void convert(BaseViewHolder helper, final RecordModel.SellBean item) {
            helper.setText(R.id.tv_current, depthPointPrice < pointPrice ? getFormatDoubleUp(item.current) : getFormatDoubleDown(item.current));
            helper.setTextColor(R.id.tv_current, ContextCompat.getColor(getContext(), R.color.color_risedown));
            helper.setText(R.id.tv_number, FormatterUtils.getBigValueFormatter(pointNum, item.sellEntrustNumber));
            ProgressBar view = helper.getView(R.id.progressBar);
            view.setProgressDrawable(getResources().getDrawable(R.drawable.progress_sell_color_right));
            helper.setProgress(R.id.progressBar, BigDecimal.valueOf(item.sellEntrustScale).intValue());
            helper.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_sell_item));
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (limitPriceType == 1) {
                        editPrice.setText(depthPointPrice < pointPrice ? getFormatDoubleUp(item.current) : getFormatDoubleDown(item.current));
                    }
                }
            });
        }
    }

    /**
     * 买入
     */
    private class BuyAdapter extends BaseQuickAdapter<RecordModel.BuyBean, BaseViewHolder> {

        public BuyAdapter() {
            super(R.layout.item_trade_depth);
        }

        @Override
        protected void convert(BaseViewHolder helper, final RecordModel.BuyBean item) {
            helper.setText(R.id.tv_current, getFormatDoubleDown(item.current));
            helper.setTextColor(R.id.tv_current, ContextCompat.getColor(getContext(), R.color.color_riseup));
            helper.setText(R.id.tv_number, FormatterUtils.getBigValueFormatter(pointNum, item.buyEntrustNumber));
            ProgressBar view = helper.getView(R.id.progressBar);
            view.setProgressDrawable(getResources().getDrawable(R.drawable.progress_buy_color));
            helper.setProgress(R.id.progressBar, BigDecimal.valueOf(item.buyEntrustScale).intValue());
            helper.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_buy_item));
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (limitPriceType == 1) {
                        editPrice.setText(getFormatDoubleDown(item.current));
                    }
                }
            });
        }
    }

    /**
     * 市价和限价的pop
     */
    private void initListPricePopup() {
        if (mPriceSheet == null) {
            mPriceSheet = new QMUIBottomSheet.BottomListSheetBuilder(getContext())
                    .addItem(getResources().getString(R.string.limit_price))
                    .addItem(getResources().getString(R.string.lowest_obtainable_price))
                    .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                            if (position == 0) {//限价
                                limitPriceType = 1;
                                editNumber.setText("");
                                setDefaultPriceValue();
                                tvTradeAmount.setVisibility(View.VISIBLE);
                            } else if (position == 1) {//市价
                                limitPriceType = 2;
                                editPrice.setText("");
                                editNumber.setText("");
                                tvTradeAmount.setVisibility(View.GONE);
                            }
                            marketPriceShow();
                            btnSelectPrice.setText(tag);
                            dialog.dismiss();
                        }
                    })
                    .build();
        }
        mPriceSheet.show();
    }

    /**
     * 限价市价买入卖出显示
     */
    private void marketPriceShow() {
        setPriceViewEnabled(true);
        if (limitPriceType == 1) {
            editPrice.setHint("");
            if (isBuy) {
                editNumber.setHint(getResources().getString(R.string.amount) + currencyNameEn);
            } else {
                editNumber.setHint(getResources().getString(R.string.amount) + currencyNameEn);
            }
        } else if (limitPriceType == 2) {//市价
            setPriceViewEnabled(false);
            editPrice.setHint(getResources().getString(R.string.good_price));
            if (isBuy) {
                editNumber.setHint(getResources().getString(R.string.amount_price) + baseCurrencyNameEn);
            } else {
                editNumber.setHint(getResources().getString(R.string.amount) + currencyNameEn);
            }
        }
    }

    private void setPriceViewEnabled(boolean boo) {
        btnPriceAdd.setEnabled(boo);
        btnPriceSub.setEnabled(boo);
        editPrice.setEnabled(boo);
        if (boo) {
            btnPriceAdd.setImageResource(R.drawable.icon_add_default);
            btnPriceSub.setImageResource(R.drawable.icon_sub_default);
            ((QMUIRoundButtonDrawable) linPrice.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_transparent));
        } else {
            btnPriceAdd.setImageResource(R.drawable.icon_add_grey);
            btnPriceSub.setImageResource(R.drawable.icon_sub_grey);
            ((QMUIRoundButtonDrawable) linPrice.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.content_bg_color_grey));
        }
    }

    /**
     * 深度
     */
    private void initListDepthPopup() {
        mDepthSheet = new QMUIBottomSheet.BottomListSheetBuilder(getContext())
                .addItemList(dataStringList)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        depthPointPrice = Integer.parseInt(dataList.get(position));
                        tvDepthDecimal.setText(String.format(getResources().getString(R.string.price_point_number), String.valueOf(depthPointPrice)));
                        setDepthPriceList(false);
                        dialog.dismiss();
                    }
                })
                .build();
        mDepthSheet.show();
    }

    private void setTradeAmounnt() {
        try {
            String price = editPrice.getText().toString().trim();
            String num = editNumber.getText().toString().trim();
            if (!TextUtils.isEmpty(price) && !TextUtils.isEmpty(num)) {
                tvTradeAmount.setText(getResources().getString(R.string.trade_amount) + " " + FormatterUtils.getFormatRoundDown(pointPrice, MathHelper.mul(Double.parseDouble(price), Double.parseDouble(num))));
            } else {
                tvTradeAmount.setText(getResources().getString(R.string.trade_amount) + " " + "--");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 深度合并 直接删除多余的小数位
     * 例如 3.45 变成 3.4
     *
     * @param value
     * @return
     */
    private String getFormatDoubleDown(double value) {
        return BigDecimal.valueOf(value).setScale(depthPointPrice, BigDecimal.ROUND_DOWN).toPlainString();
    }

    /**
     * 深度合并 多余的小数位进位处理
     * 例如 3.41 变成 3.5
     *
     * @param value
     * @return
     */
    private String getFormatDoubleUp(double value) {
        return BigDecimal.valueOf(value).setScale(depthPointPrice, BigDecimal.ROUND_UP).toPlainString();
    }

    private void limitEditLength() {
        editPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(pointPrice)});
    }

    private void initCoinPopUp(View view) {
        if (coinPopWindow != null && coinPopWindow.isShowing()) {
            coinPopWindow.dismiss();
            return;
        }

        if (coinPopWindow == null) {
            coinPopWindow = new CoinPopWindow(getContext());
            coinPopWindow.create(UIUtils.getScreenWidth(), UIUtils.getScreenHeight() / 2, new CoinPopWindow.TabItemSelect() {
                @Override
                public void coinClick(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
                    setCoinInfo(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
                    coinPopWindow.dismiss();
                }
            });
        }
        coinPopWindow.notifyCoinInfo();
        coinPopWindow.showAsDropDown(view, 0, 0);
    }

    private void initTradePopUp(View view) {
        if (tradePopWindow != null && tradePopWindow.isShowing()) {
            tradePopWindow.dismiss();
            return;
        }

        if (tradePopWindow == null) {
            tradePopWindow = new TradePopWindow(getContext());
            tradePopWindow.create(UIUtils.getScreenWidth(), new TradePopWindow.TabItemSelect() {
                @Override
                public void coinClick(int position) {
                    switch (position) {
                        case 0:
                            if (spUtil.getLogin()) {
                                jumpTo(AssetsActivity.createActivity(getHostActivity(), currencyNameEn, currencyId, 0, true));
                            } else {
                                jumpTo(LoginActivity.class);
                            }
                            break;
                        case 1:
                            if (spUtil.getLogin()) {
                                if (!isKeyc()) {
                                    return;
                                }
                                jumpTo(AssetsActivity.createActivity(getHostActivity(), currencyNameEn, currencyId, 1, true));
                            } else {
                                jumpTo(LoginActivity.class);
                            }
                            break;
                        case 2:
                            if (spUtil.getLogin()) {
                                jumpTo(AssetsActivity.createActivity(getHostActivity(), baseCurrencyNameEn, baseCurrencyId, 0, true));
                            } else {
                                jumpTo(LoginActivity.class);
                            }
                            break;
                        case 3:
                            if (spUtil.getLogin()) {
                                if (!isKeyc()) {
                                    return;
                                }
                                jumpTo(AssetsActivity.createActivity(getHostActivity(), baseCurrencyNameEn, baseCurrencyId, 1, true));
                            } else {
                                jumpTo(LoginActivity.class);
                            }
                            break;
                        case 4:
                            switch (positionView) {
                                case POSITION_RIGHT:
                                    setLinPosition(POSITION_LEFT, true);
                                    positionView = POSITION_LEFT;
                                    break;
                                case POSITION_LEFT:
                                default:
                                    setLinPosition(POSITION_RIGHT, true);
                                    positionView = POSITION_RIGHT;
                                    break;
                            }
                            SPUtils.getInstance().put(VIEW_POSITION, positionView);
                            break;
                        case 5:
                            if (spUtil.getLogin()) {
                                jumpTo(SecurityCenterActivity.class);
                            } else {
                                jumpTo(LoginActivity.class);
                            }
                            break;
                        case 6:
                            if (spUtil.getLogin()) {
                                jumpTo(TradeRecordManageActivity.createActivity(getContext(), currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn, 3));
                            } else {
                                jumpTo(LoginActivity.class);
                            }
                            break;
                    }
                    tradePopWindow.dismiss();
                }
            });
        }
        tradePopWindow.notifyInfo(currencyNameEn, baseCurrencyNameEn);
        tradePopWindow.showAsDropDown(view, 0, 0);
    }

    private class TabAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabDownTitle != null ? tabDownTitle.size() : 0;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabDownTitle.get(position));
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
}
