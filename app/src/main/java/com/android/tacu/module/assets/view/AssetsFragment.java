package com.android.tacu.module.assets.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.vip.view.RechargeDepositActivity;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
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
import com.android.tacu.module.assets.contract.AssetsContract;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.MoneyFlowEvent;
import com.android.tacu.module.assets.presenter.AssetsPresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.module.my.view.GoogleHintActivity;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.ScreenShareHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.view.popup.CoinPickerView;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.google.gson.Gson;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static com.android.tacu.api.Constant.OTC_SELECT_COIN_CACHE;

/**
 * Created by jiazhen on 2018/8/18.
 */
public class AssetsFragment extends BaseFragment<AssetsPresenter> implements AssetsContract.IAssetsView, View.OnClickListener {

    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //头布局
    private View view_bond;
    private View view_line_bond;
    private TextView tv_margin_account;
    private TextView tv_all_amount_title;
    private TextView tv_all_amount;
    private TextView tv_otc_account_title;
    private TextView tv_otc_account;
    private TextView tv_coin_account_title;
    private TextView tv_coin_account;
    private TextView tv_c2c_account_title;
    private TextView tv_c2c_account;

    private QMUIRoundButton btn_take;
    private QMUIRoundButton btn_recharge;

    private CheckBox cb_search;
    private EditText et_search;

    //保证金账号
    private String bond_string = "0";
    //otc账号
    private String otc_string = "0";
    //币币账号
    private String btc_total_string = "0";
    //c2c账号
    private String c2c_string = "0";
    //总资产
    private String all_total_string = "0";

    private String searchStr;
    //true：已有资产  false：全部资产
    private boolean isChecked = false;
    private View emptyView;
    private View assetHeaderView;
    private AssetAdapter adapter;
    private Gson gson = new Gson();
    private InputMethodManager inputMethod;
    private boolean defaultEyeStatus = true;
    private AssetDetailsModel assetDetailsModel;
    private List<AssetDetailsModel.CoinListBean> currentList = new ArrayList<>();
    private List<AssetDetailsModel.CoinListBean> currentSearchList = new ArrayList<>();
    private QMUIAlphaImageButton currentPrivacyView;
    private List<MarketNewModel> marketNewModelList = new ArrayList<>();

    private ScreenShareHelper screenShareHelper;

    public static AssetsFragment newInstance() {
        Bundle bundle = new Bundle();
        AssetsFragment fragment = new AssetsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_assets;
    }

    @Override
    protected void initData(View view) {
        mTopBar.setTitle(getResources().getString(R.string.assets));
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

        defaultEyeStatus = spUtil.getAssetShowStatus();
        currentPrivacyView = mTopBar.addRightImageButton(
                defaultEyeStatus ? R.mipmap.icon_watch : R.mipmap.icon_watch_disable,
                R.id.qmui_topbar_item_right, 22, 22);

        currentPrivacyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultEyeStatus = !defaultEyeStatus;
                spUtil.setAssetShowStatus(defaultEyeStatus);
                invalidateDataAllDataSet();
            }
        });

        /*mTopBar.addRightImageButton(R.drawable.icon_share_white, R.id.qmui_topbar_item_right_two, 20, 20)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (screenShareHelper == null) {
                            screenShareHelper = new ScreenShareHelper(getHostActivity());
                        }
                        screenShareHelper.invoke(refreshlayout);
                    }
                });*/

        emptyView = View.inflate(getHostActivity(), R.layout.view_empty, null);
        inputMethod = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        CustomTextHeaderView header = new CustomTextHeaderView(getContext());
        header.setPrimaryColors(ContextCompat.getColor(getContext(), R.color.content_bg_color), ContextCompat.getColor(getContext(), R.color.text_color));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upLoad(false);
            }
        });
    }

    @Override
    protected AssetsPresenter createPresenter(AssetsPresenter mPresenter) {
        return new AssetsPresenter();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

        initReyclerView();
        initAssetHeader();
        editViewSearch();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (spUtil.getLogin()) {
            dealBondValue();
            upLoad(assetDetailsModel != null ? false : true);
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        //跳转其他页面关闭EditText焦点
        et_search.setFocusable(false);
        if (inputMethod.isActive()) {
            inputMethod.hideSoftInputFromWindow(et_search.getWindowToken(), 0);//隐藏输入法
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screenShareHelper != null) {
            screenShareHelper.destory();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                search();
                break;
            case R.id.et_search:
                et_search.setFocusable(true);//设置输入框可聚集
                et_search.setFocusableInTouchMode(true);//设置触摸聚焦
                et_search.requestFocus();//请求焦点
                et_search.findFocus();//获取焦点
                inputMethod.showSoftInput(et_search, InputMethodManager.SHOW_FORCED);//显示输入法
                break;
            case R.id.btn_take:
            case R.id.btn_recharge:
                jumpTo(RechargeDepositActivity.class);
                break;
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshlayout != null && refreshlayout.isRefreshing()) {
            refreshlayout.finishRefresh();
        }
    }

    private void invalidateDataAllDataSet() {
        tv_margin_account.setText(defaultEyeStatus ? bond_string + Constant.ACU_CURRENCY_NAME : "*****");
        tv_all_amount.setText(defaultEyeStatus ? all_total_string + Constant.ACU_CURRENCY_NAME : "*****");
        tv_otc_account.setText(defaultEyeStatus ? otc_string + Constant.ACU_CURRENCY_NAME : "*****");
        tv_coin_account.setText(defaultEyeStatus ? btc_total_string + Constant.ACU_CURRENCY_NAME : "*****");
        tv_c2c_account.setText(defaultEyeStatus ? c2c_string + Constant.ACU_CURRENCY_NAME : "*****");
        adapter.notifyDataSetChanged();
        currentPrivacyView.setImageResource(defaultEyeStatus ? R.mipmap.icon_watch : R.mipmap.icon_watch_disable);
    }

    private void editViewSearch() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });
    }


    /**
     * 模糊搜索
     */
    private void search() {
        currentSearchList.clear();
        searchStr = et_search.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(searchStr)) {
            setAdapter(currentList);
            return;
        }
        if (currentList != null && currentList.size() > 0) {
            for (int i = 0; i < currentList.size(); i++) {
                if (currentList.get(i).currencyNameEn.toLowerCase().contains(searchStr) || currentList.get(i).currencyName.toLowerCase().contains(searchStr)) {
                    currentSearchList.add(currentList.get(i));
                }
            }
        }
        setAdapter(currentSearchList);
    }

    private void setAdapter(List<AssetDetailsModel.CoinListBean> list) {
        if (list != null && list.size() > 0) {
            adapter.setNewData(list);
        } else {
            adapter.setNewData(null);
            adapter.setEmptyView(emptyView);
        }
    }

    @Override
    public void showContent(AssetDetailsModel model) {
        this.assetDetailsModel = model;

        if (assetDetailsModel != null) {
            myAssets();
            dealAssetList();
            if (assetDetailsModel.otcCoinList != null && assetDetailsModel.otcCoinList.size() > 0) {
                SPUtils.getInstance().put(OTC_SELECT_COIN_CACHE, gson.toJson(assetDetailsModel.otcCoinList));
            }
        }
    }

    @Override
    public void BondAccount(OtcAmountModel model) {
        if (model != null) {
            bond_string = model.amount;
        } else {
            bond_string = "0";
        }
        bond_string = FormatterUtils.getFormatRoundUp(2, bond_string);
        tv_margin_account.setText(defaultEyeStatus ? bond_string + Constant.ACU_CURRENCY_NAME : "*****");
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        if (model != null) {
            otc_string = model.amount;
        } else {
            otc_string = "0";
        }
        otc_string = FormatterUtils.getFormatRoundUp(2, otc_string);
        tv_otc_account.setText(defaultEyeStatus ? otc_string + Constant.ACU_CURRENCY_NAME : "*****");
        dealValue();
    }

    @Override
    public void c2cAmount(OtcAmountModel model) {
        if (model != null) {
            c2c_string = model.amount;
        } else {
            c2c_string = "0";
        }
        if ((!TextUtils.isEmpty(c2c_string) && Double.parseDouble(c2c_string) != 0) || isMerchant()) {
            tv_c2c_account_title.setVisibility(View.VISIBLE);
            tv_c2c_account.setVisibility(View.VISIBLE);

            c2c_string = FormatterUtils.getFormatRoundUp(2, c2c_string);
            tv_c2c_account.setText(defaultEyeStatus ? c2c_string + Constant.ACU_CURRENCY_NAME : "*****");
            dealValue();
        } else {
            tv_c2c_account_title.setVisibility(View.GONE);
            tv_c2c_account.setVisibility(View.GONE);
        }
    }

    private void initReyclerView() {
        adapter = new AssetAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider_dp10));
        adapter.setHeaderFooterEmpty(true, false);
        //添加分割线
        recyclerView.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
    }

    private void initAssetHeader() {
        assetHeaderView = View.inflate(getHostActivity(), R.layout.header_asset_details, null);

        view_bond = assetHeaderView.findViewById(R.id.view_bond);
        view_line_bond = assetHeaderView.findViewById(R.id.view_line_bond);

        tv_margin_account = assetHeaderView.findViewById(R.id.tv_margin_account);
        tv_all_amount_title = assetHeaderView.findViewById(R.id.tv_all_amount_title);
        tv_all_amount = assetHeaderView.findViewById(R.id.tv_all_amount);
        tv_otc_account_title = assetHeaderView.findViewById(R.id.tv_otc_account_title);
        tv_otc_account = assetHeaderView.findViewById(R.id.tv_otc_account);
        tv_coin_account_title = assetHeaderView.findViewById(R.id.tv_coin_account_title);
        tv_coin_account = assetHeaderView.findViewById(R.id.tv_coin_account);
        tv_c2c_account_title = assetHeaderView.findViewById(R.id.tv_c2c_account_title);
        tv_c2c_account = assetHeaderView.findViewById(R.id.tv_c2c_account);

        tv_all_amount_title.setText(getResources().getString(R.string.all_amount) + "(" + Constant.ACU_CURRENCY_NAME + ")");
        tv_otc_account_title.setText(getResources().getString(R.string.otc_account) + "(" + Constant.ACU_CURRENCY_NAME + ")");
        tv_coin_account_title.setText(getResources().getString(R.string.coin_account) + "(" + Constant.ACU_CURRENCY_NAME + ")");
        tv_c2c_account_title.setText(getResources().getString(R.string.c2c_account) + "(" + Constant.ACU_CURRENCY_NAME + ")");

        btn_take = assetHeaderView.findViewById(R.id.btn_take);
        btn_recharge = assetHeaderView.findViewById(R.id.btn_recharge);
        cb_search = assetHeaderView.findViewById(R.id.cb_search);
        et_search = assetHeaderView.findViewById(R.id.et_search);

        btn_take.setOnClickListener(this);
        btn_recharge.setOnClickListener(this);
        et_search.setOnClickListener(this);
        adapter.addHeaderView(assetHeaderView);

        cb_search.setChecked(isChecked);
        cb_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AssetsFragment.this.isChecked = isChecked;
                dealAssetList();
            }
        });
    }

    private void dealAssetList() {
        if (assetDetailsModel != null) {
            if (isChecked) {
                currentList = assetDetailsModel.selfCoinList;
            } else {
                currentList = assetDetailsModel.coinList;
            }
        }
        search();
    }

    private void upLoad(boolean isShowLoadingView) {
        mPresenter.getAssetDetails(isShowLoadingView, 1);
        mPresenter.BondAccount(isShowLoadingView, Constant.ACU_CURRENCY_ID);
        mPresenter.otcAmount(0, isShowLoadingView, Constant.ACU_CURRENCY_ID);
        mPresenter.c2cAmount(0, isShowLoadingView, Constant.ACU_CURRENCY_ID);
    }

    private void dealValue() {
        if (TextUtils.isEmpty(btc_total_string)) {
            btc_total_string = "0";
        }
        if (TextUtils.isEmpty(otc_string)) {
            otc_string = "0";
        }
        if (TextUtils.isEmpty(c2c_string)) {
            c2c_string = "0";
        }
        all_total_string = FormatterUtils.getFormatRoundHalfUp(2, MathHelper.add(Double.parseDouble(btc_total_string), MathHelper.add(Double.parseDouble(otc_string), Double.parseDouble(c2c_string))));

        tv_all_amount.setText(defaultEyeStatus ? all_total_string + Constant.ACU_CURRENCY_NAME : "*****");
    }

    private void dealBondValue() {
        if (spUtil.getApplyMerchantStatus() == 2 || spUtil.getApplyAuthMerchantStatus() == 2) {
            view_bond.setVisibility(View.VISIBLE);
            view_line_bond.setVisibility(View.VISIBLE);
        } else {
            view_bond.setVisibility(View.GONE);
            view_line_bond.setVisibility(View.GONE);
        }
    }

    //单一类型adapter
    class AssetAdapter extends BaseQuickAdapter<AssetDetailsModel.CoinListBean, BaseViewHolder> {
        private List<String> otcList;

        public AssetAdapter() {
            super(R.layout.item_asset_details);
            otcList = Arrays.asList(Constant.OTCList);
        }

        @Override
        protected void convert(BaseViewHolder holder, final AssetDetailsModel.CoinListBean data) {
            GlideUtils.disPlay(getContext(), Constant.SMALL_ICON_URL + data.icoUrl, (ImageView) holder.getView(R.id.img_icon));
            holder.setText(R.id.tv_icon_name, data.currencyNameEn);
            holder.setText(R.id.tv_icon_name_full, String.format("(%s)", data.currencyName));
            if (otcList.contains(data.currencyNameEn)) {
                holder.setGone(R.id.btn_info, true);
            } else {
                holder.setGone(R.id.btn_info, false);
            }

            holder.setText(R.id.tv_total_holdings_title, getResources().getString(R.string.total_holdings) + "(" + data.currencyNameEn + ")");
            holder.setText(R.id.tv_available_title, getResources().getString(R.string.available_num) + "(" + data.currencyNameEn + ")");
            holder.setText(R.id.tv_frozen_title, getResources().getString(R.string.frozen_num) + "(" + data.currencyNameEn + ")");

            holder.setText(R.id.tv_total_holdings, defaultEyeStatus ? BigDecimal.valueOf(data.amount).toPlainString() : "*****");
            String ycn = getMcM(data.currencyId, data.amount);
            holder.setText(R.id.tv_total_holdings_rnb, defaultEyeStatus ? (TextUtils.isEmpty(ycn) ? "" : "≈" + ycn) : "*****");
            holder.setText(R.id.tv_available, defaultEyeStatus ? BigDecimal.valueOf(data.cashAmount).toPlainString() : "*****");
            holder.setText(R.id.tv_frozen, defaultEyeStatus ? BigDecimal.valueOf(data.freezeAmount).toPlainString() : "*****");

            holder.setOnClickListener(R.id.btn_info, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(AssetsInfoActivity.class);
                }
            });
            holder.setOnClickListener(R.id.btn_withdraw, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(AssetsActivity.createActivity(getHostActivity(), data.currencyNameEn, data.currencyId, 0, true));
                }
            });
            holder.setOnClickListener(R.id.btn_platform_transfer, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isKeyc()) {
                        return;
                    }
                    jumpTo(AssetsActivity.createActivity(getHostActivity(), data.currencyNameEn, data.currencyId, 1, true));
                }
            });
            holder.setOnClickListener(R.id.btn_history, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MoneyFlowEvent event = new MoneyFlowEvent(
                            getString(R.string.all),
                            "0",
                            data.currencyId,
                            data.currencyNameEn,
                            "",
                            ""
                    );

                    event.setType("0");
                    jumpTo(MoneyFlowActivity.createActivity(getHostActivity(), event));
                }
            });
            holder.setOnClickListener(R.id.btn_go_trade, new View.OnClickListener() {

                void sendEvent(MarketNewModel.TradeCoinsBean bean) {
                    EventManage.sendEvent(new BaseEvent<>(EventConstant.JumpTradeIsBuyCode, new JumpTradeCodeIsBuyEvent(bean.currencyId, bean.baseCurrencyId, bean.currencyNameEn, bean.baseCurrencyNameEn, true)));
                }

                @Override
                public void onClick(View v) {
                    List<MarketNewModel.TradeCoinsBean> list = getDataBridge().getTradeList();
                    if (list != null) {
                        List<MarketNewModel.TradeCoinsBean> selected = new ArrayList<>();
                        for (MarketNewModel.TradeCoinsBean bean : list) {
                            if (bean.currencyId == data.currencyId) {
                                selected.add(bean);
                                continue;
                            }
                            if (bean.baseCurrencyId == data.currencyId) {
                                selected.add(bean);
                            }
                        }

                        if (selected.isEmpty()) {
                            showToastError(getResources().getString(R.string.msg_coin_picker_not_found, data.currencyNameEn));
                        } else if (selected.size() == 1) {
                            sendEvent(selected.get(0));
                        } else {
                            setBackGroundAlpha(0.5f);
                            new CoinPickerView(getHostActivity(), data.currencyNameEn, selected, new CoinPickerView.Listener() {
                                @Override
                                public void onItemSelected(MarketNewModel.TradeCoinsBean target) {
                                    sendEvent(target);
                                }

                                @Override
                                public void onDismiss() {
                                    setBackGroundAlpha(1f);
                                }
                            })
                                    .showOnAnchor(getHostActivity().findViewById(R.id.view_base_line),
                                            RelativePopupWindow.VerticalPosition.ABOVE,
                                            RelativePopupWindow.HorizontalPosition.CENTER,
                                            true);
                        }
                    } else {
                        showToastError(getResources().getString(R.string.msg_coin_picker_not_found, data.currencyNameEn));
                    }
                }
            });
        }
    }

    public void setConvertModel() {
        //myAssets();
    }

    private void myAssets() {
        if (assetDetailsModel != null) {
            btc_total_string = FormatterUtils.getFormatRoundUp(2, assetDetailsModel.allMoney);
            tv_coin_account.setText(defaultEyeStatus ? btc_total_string + Constant.ACU_CURRENCY_NAME : "*****");

            dealValue();
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

    /**
     * 判断当前用户是否是普通商户
     *
     * @return
     */
    private boolean isMerchant() {
        return spUtil.getApplyMerchantStatus() == 2 && spUtil.getApplyAuthMerchantStatus() != 2;
    }
}
