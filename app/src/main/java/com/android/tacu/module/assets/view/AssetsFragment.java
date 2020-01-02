package com.android.tacu.module.assets.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.android.tacu.module.assets.model.TransInfoCoinModal;
import com.android.tacu.module.assets.presenter.AssetsPresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.module.my.view.GoogleHintActivity;
import com.android.tacu.utils.FormatterUtils;
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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private TextView tv_btc_total;
    private TextView tv_ycn_total;
    private CheckBox cb_search;
    private EditText et_search;

    private String btc_total_string;
    private String ycn_total_string;

    private String searchStr;
    //true：已有资产  false：全部资产
    private boolean isChecked = false;
    //总资产比特币小数点后几位
    private int btcAmount = 8;
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
    private TransInfoCoinModal transInfoCoinModal;

    private ScreenShareHelper screenShareHelper;

    public static AssetsFragment newInstance() {
        Bundle bundle = new Bundle();
        AssetsFragment fragment = new AssetsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        if (spUtil.getLogin()) {
            myAssets();
            upLoad(assetDetailsModel != null ? false : true);
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_assets;
    }

    @Override
    protected void initData() {
        mTopBar.setTitle(getResources().getString(R.string.assets));
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
                            screenShareHelper = new ScreenShareHelper(getActivity());
                        }
                        screenShareHelper.invoke(refreshlayout);
                    }
                });*/

        emptyView = View.inflate(getActivity(), R.layout.view_empty, null);
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

        initReyclerView();
        initAssetHeader();
        editViewSearch();
    }

    @Override
    protected AssetsPresenter createPresenter(AssetsPresenter mPresenter) {
        return new AssetsPresenter();
    }

    @Override
    protected void onPresenterCreated(AssetsPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        mPresenter.transInfoCoin();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            upLoad(assetDetailsModel != null ? false : true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //跳转其他页面关闭EditText焦点
        et_search.setFocusable(false);
        if (inputMethod.isActive()) {
            inputMethod.hideSoftInputFromWindow(et_search.getWindowToken(), 0);//隐藏输入法
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refreshlayout != null && refreshlayout.isRefreshing()) {
            refreshlayout.finishRefresh();
        }
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
        tv_btc_total.setText(defaultEyeStatus ? btc_total_string : "*****");
        tv_ycn_total.setText(defaultEyeStatus ? ycn_total_string : "*****");
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

        int indexOf = assetDetailsModel.allMoney.indexOf(".");
        if (indexOf > 0) {
            btcAmount = assetDetailsModel.allMoney.length() - (indexOf + 1);
        }

        if (assetDetailsModel != null) {
            if (refreshlayout != null && refreshlayout.isRefreshing()) {
                refreshlayout.finishRefresh();
            }
            btc_total_string = FormatterUtils.getFormatRoundUp(btcAmount, assetDetailsModel.allMoney) + Constant.ASSETS_COIN;
            tv_btc_total.setText(defaultEyeStatus ? btc_total_string : "*****");
            myAssets();
            dealAssetList();
            if (assetDetailsModel.otcCoinList != null && assetDetailsModel.otcCoinList.size() > 0) {
                SPUtils.getInstance().put(OTC_SELECT_COIN_CACHE, gson.toJson(assetDetailsModel.otcCoinList));
            }
        }
    }

    @Override
    public void showContentError() {
        if (refreshlayout != null && refreshlayout.isRefreshing()) {
            refreshlayout.finishRefresh();
        }
    }

    @Override
    public void transInfoCoin(TransInfoCoinModal attachment) {
        transInfoCoinModal = attachment;
        dealAssetList();
    }

    private void initReyclerView() {
        adapter = new AssetAdapter();
        adapter.setHeaderFooterEmpty(true, false);
        //添加分割线
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void initAssetHeader() {
        assetHeaderView = View.inflate(getActivity(), R.layout.header_asset_details, null);
        tv_btc_total = assetHeaderView.findViewById(R.id.tv_btc_total);
        tv_ycn_total = assetHeaderView.findViewById(R.id.tv_ycn_total);
        cb_search = assetHeaderView.findViewById(R.id.cb_search);
        et_search = assetHeaderView.findViewById(R.id.et_search);

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

        //得到drawable对象，即所要插入的图片
        Drawable d = getResources().getDrawable(R.drawable.icon_question);
        d.setBounds(2, UIUtils.dp2px(-4), UIUtils.dp2px(15), UIUtils.dp2px(10));
        ImageSpan imgSpan = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        int strLength = getResources().getString(R.string.node_profit).length() + 1;
        SpannableString spanString = new SpannableString(getResources().getString(R.string.node_profit) + " ");
        spanString.setSpan(imgSpan, strLength - 1, strLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void dealAssetList() {
        if (assetDetailsModel != null) {
            if (isChecked) {
                currentList = assetDetailsModel.selfCoinList;
            } else {
                currentList = assetDetailsModel.coinList;
            }
        }
        if (currentList != null && currentList.size() > 0 && transInfoCoinModal != null) {
            if (transInfoCoinModal.inList != null && transInfoCoinModal.inList.size() > 0) {
                for (int i = 0; i < currentList.size(); i++) {
                    for (int j = 0; j < transInfoCoinModal.inList.size(); j++) {
                        if (currentList.get(i).currencyId == transInfoCoinModal.inList.get(j).currencyId) {
                            currentList.get(i).isUuexOtc = true;
                        }
                    }
                }
            }

            if (transInfoCoinModal.outList != null && transInfoCoinModal.outList.size() > 0) {
                for (int i = 0; i < currentList.size(); i++) {
                    for (int j = 0; j < transInfoCoinModal.outList.size(); j++) {
                        if (currentList.get(i).currencyId == transInfoCoinModal.outList.get(j).currencyId) {
                            currentList.get(i).isUuexOtc = true;
                        }
                    }
                }
            }
        }
        search();
    }

    private void upLoad(boolean isShowLoadingView) {
        mPresenter.getAssetDetails(isShowLoadingView);
    }

    //单一类型adapter
    class AssetAdapter extends BaseQuickAdapter<AssetDetailsModel.CoinListBean, BaseViewHolder> {
        public AssetAdapter() {
            super(R.layout.item_asset_details_global);
        }

        @Override
        protected void convert(BaseViewHolder holder, final AssetDetailsModel.CoinListBean data) {
            GlideUtils.disPlay(getContext(), Constant.API_QINIU_URL + data.icoUrl, (ImageView) holder.getView(R.id.iv_asset_item_icon));
            holder.setText(R.id.iv_asset_item_coin_simple, data.currencyNameEn);
            holder.setText(R.id.iv_asset_item_coin_full, String.format("(%s)", data.currencyName));
            holder.setText(R.id.tv_asset_item_own_count, defaultEyeStatus ? BigDecimal.valueOf(data.amount).toPlainString() : "*****");
            holder.setText(R.id.tv_asset_item_own_available, defaultEyeStatus ? BigDecimal.valueOf(data.cashAmount).toPlainString() : "*****");
            holder.setText(R.id.tv_asset_item_freeze_count, defaultEyeStatus ? BigDecimal.valueOf(data.freezeAmount).toPlainString() : "*****");
            String ycn = getMcM(data.currencyId, data.amount);
            holder.setText(R.id.tv_assets_item_cny_value, defaultEyeStatus ? (TextUtils.isEmpty(ycn) ? "" : "≈" + ycn) : "*****");

            holder.setGone(R.id.tv_uuex_transfer, data.isUuexOtc);

            holder.itemView.findViewById(R.id.tv_asset_item_recharge).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(AssetsActivity.createActivity(getActivity(), data.currencyNameEn, data.currencyId, 0, true));
                }
            });

            holder.itemView.findViewById(R.id.tv_asset_item_withdraw).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isKeyc()) {
                        return;
                    }
                    jumpTo(AssetsActivity.createActivity(getActivity(), data.currencyNameEn, data.currencyId, 1, true));
                }
            });

            holder.setOnClickListener(R.id.tv_uuex_transfer, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isKeyc2()) {
                        return;
                    }
                    if (transInfoCoinModal != null) {
                        boolean isFlag = false;
                        for (int i = 0; i < transInfoCoinModal.outList.size(); i++) {
                            if (transInfoCoinModal.outList.get(i).currencyId == data.currencyId) {
                                isFlag = true;
                                break;
                            }
                        }
                        if (isFlag && !isKeycUUEX()) {
                            return;
                        }
                    } else {
                        return;
                    }

                    jumpTo(
                            AssetsActivity.createActivity(
                                    getActivity(), data.currencyNameEn,
                                    data.currencyId, 2,
                                    true, transInfoCoinModal)
                    );
                }
            });

            holder.itemView.findViewById(R.id.tv_asset_item_history).setOnClickListener(new View.OnClickListener() {
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
                    jumpTo(MoneyFlowActivity.createActivity(getActivity(), event));


                }
            });

            holder.itemView.findViewById(R.id.tv_asset_item_trade).setOnClickListener(new View.OnClickListener() {

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
                            new CoinPickerView(getActivity(), data.currencyNameEn, selected, new CoinPickerView.Listener() {
                                @Override
                                public void onItemSelected(MarketNewModel.TradeCoinsBean target) {
                                    sendEvent(target);
                                }

                                @Override
                                public void onDismiss() {
                                    setBackGroundAlpha(1f);
                                }
                            })
                                    .showOnAnchor(getActivity().findViewById(R.id.view_base_line),
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
        myAssets();
    }

    private void myAssets() {
        if (assetDetailsModel != null) {
            ycn_total_string = "≈" + getMcM(1, Double.parseDouble(assetDetailsModel.allMoney));
            tv_ycn_total.setText(defaultEyeStatus ? ycn_total_string : "*****");
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

    private boolean isKeycUUEX() {
        boolean boo = false;
        switch (spUtil.getIsAuthSenior()) {
            case -1:
            case 0:
            case 1:
                showToastError(getString(R.string.please_get_the_level_of_KYC));
                break;
            case 2:
            case 3:
                if (!spUtil.getPhoneStatus()) {
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

    private boolean isKeyc2() {
        int flag = spUtil.getIsAuthSenior();
        if (flag <= 1) {
            showToastError(getString(R.string.please_get_the_level_of_KYC));
            return false;
        }
        return true;
    }
}
