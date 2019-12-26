package com.android.tacu.module.market.view;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.db.DaoManager;
import com.android.tacu.db.model.SearchHistorysModel;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.market.contract.SelectedCoinsContract;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.market.presenter.SelectedCoinsPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 搜索
 */
public class SearchHistoryActivity extends BaseActivity<SelectedCoinsPresenter> implements SelectedCoinsContract.IView {

    @BindView(R.id.edit_search)
    EditText editText;
    @BindView(R.id.btn_cancel)
    QMUIAlphaButton btn_cancel;
    @BindView(R.id.tv_clear)
    TextView tv_clear;
    @BindView(R.id.ry_history)
    RecyclerView ryHistory;
    @BindView(R.id.ry_search)
    RecyclerView rySearch;

    //所有币种
    private List<MarketNewModel.TradeCoinsBean> allTradeCoinList = new ArrayList<>();
    //搜索出来的币种
    private List<MarketNewModel.TradeCoinsBean> searchCoinList = new ArrayList<>();

    private String keyword;
    private String selfModelString;//最初的自选
    private String saveSelfModelString;//改变后的自选数据
    private SelfModel selfModel;

    private SelfModel.SymbolBean historySymbolBean;
    private SelfModel.SymbolBean searchSymbolBean;

    private HistoryAdapter historyAdapter;
    private SearchAdapter searchAdapter;
    private Gson gson = new Gson();
    private Intent startIntent;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_search_history);
    }

    @Override
    protected void initView() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword = s.toString().toUpperCase();
                searchCoinListValue();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        historyAdapter = new HistoryAdapter();
        ryHistory.setLayoutManager(new LinearLayoutManager(this));
        ryHistory.setAdapter(historyAdapter);

        searchAdapter = new SearchAdapter();
        rySearch.setLayoutManager(new LinearLayoutManager(this));
        rySearch.setAdapter(searchAdapter);

        initCache();
    }

    @Override
    protected SelectedCoinsPresenter createPresenter(SelectedCoinsPresenter mPresenter) {
        return new SelectedCoinsPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSelfCache();
        hasDataAll();
        if (searchAdapter.getData() != null && searchAdapter.getData().size() > 0) {
            searchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            backActivity(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        backActivity(false);
    }

    @OnClick(R.id.tv_clear)
    void clearClick() {
        deleteAll();
        hasDataAll();
    }

    @Override
    public void uploadSelfSuccess() {
        if (!TextUtils.isEmpty(saveSelfModelString)) {
            SPUtils.getInstance().put(Constant.SELFCOIN_LIST, saveSelfModelString);
        }
        showToastSuccess(getResources().getString(R.string.business_edit_collect));
        startIntentActivity();
    }

    @Override
    public void uploadSelfError() {
        showToastError(getResources().getString(R.string.business_edit_collect_error));
    }

    private void initCache() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = gson.fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());
        if (cacheList != null && cacheList.size() > 0) {
            allTradeCoinList.clear();
            for (int i = 0; i < cacheList.size(); i++) {
                if (cacheList.get(i).tradeCoinsList != null && cacheList.get(i).tradeCoinsList.size() > 0) {
                    for (int j = 0; j < cacheList.get(i).tradeCoinsList.size(); j++) {
                        allTradeCoinList.add(cacheList.get(i).tradeCoinsList.get(j));
                    }
                }
            }
        }
    }

    /**
     * 如果跳转到详情页 并且收藏了 这里需要处理
     */
    private void initSelfCache() {
        if (spUtil.getLogin() && selfModel == null) {
            selfModelString = SPUtils.getInstance().getString(Constant.SELFCOIN_LIST);
            selfModel = gson.fromJson(selfModelString, SelfModel.class);
            if (selfModel == null) {
                selfModel = new SelfModel();
                selfModelString = gson.toJson(selfModel);
            }
        }
    }

    /**
     * 筛选搜索币种列表
     */
    private void searchCoinListValue() {
        if (allTradeCoinList != null && allTradeCoinList.size() > 0) {
            searchCoinList.clear();
            if (!TextUtils.isEmpty(keyword)) {
                for (int i = 0; i < allTradeCoinList.size(); i++) {
                    if ((allTradeCoinList.get(i).currencyNameEn + "/" + allTradeCoinList.get(i).baseCurrencyNameEn).contains(keyword.toUpperCase()) || allTradeCoinList.get(i).currencyName.toUpperCase().contains(keyword.toUpperCase())) {
                        searchCoinList.add(allTradeCoinList.get(i));
                    }
                }
            }
            searchAdapter.setNewData(searchCoinList);
        }
    }

    //搜索成功跳转并保存
    private void saveHistory(MarketNewModel.TradeCoinsBean model) {
        if (model != null) {
            boolean hasData = hasData(model.currencyNameEn, model.baseCurrencyNameEn);
            if (!hasData) {
                insertData(model.currencyId, model.baseCurrencyId, model.currencyNameEn, model.baseCurrencyNameEn);
                hasDataAll();
            }
        }
    }

    /**
     * 退出当前页面
     * 有键盘就先关闭键盘
     * 有搜索页面就关闭搜索页面
     * 最后退出
     */
    private void backActivity(boolean isBackKey) {
        if (isBackKey) {
            boolean flag = QMUIKeyboardHelper.hideKeyboard(editText);
            if (flag) {
                return;
            }
        }
        if (searchAdapter.getData() != null && searchAdapter.getData().size() > 0) {
            editText.setText("");
            searchAdapter.setNewData(null);
            return;
        }
        showLeaveDialog();
    }

    /**
     * 退出时弹窗询问是否保存自选数据
     */
    private void showLeaveDialog() {
        if (spUtil.getLogin()) {
            if (selfModel != null) {
                saveSelfModelString = gson.toJson(selfModel);
            }
            if (!TextUtils.equals(selfModelString, saveSelfModelString)) {
                new DroidDialog.Builder(this)
                        .title(getResources().getString(R.string.self_selection_save))
                        .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                            @Override
                            public void onPositive(Dialog droidDialog) {
                                if (!TextUtils.isEmpty(saveSelfModelString)) {
                                    mPresenter.uploadSelfList(saveSelfModelString);
                                } else {
                                    finish();
                                }
                            }
                        })
                        .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                            @Override
                            public void onNegative(Dialog droidDialog) {
                                finish();
                            }
                        })
                        .cancelable(false, false)
                        .show();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * 跳转币种详情页时弹窗询问是否保存自选数据
     */
    private void showStartDialog() {
        if (spUtil.getLogin()) {
            if (selfModel != null) {
                saveSelfModelString = gson.toJson(selfModel);
            }
            if (!TextUtils.equals(selfModelString, saveSelfModelString)) {
                new DroidDialog.Builder(this)
                        .title(getResources().getString(R.string.self_selection_save))
                        .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                            @Override
                            public void onPositive(Dialog droidDialog) {
                                if (!TextUtils.isEmpty(saveSelfModelString)) {
                                    mPresenter.uploadSelfList(saveSelfModelString);
                                } else {
                                    startIntentActivity();
                                }
                            }
                        })
                        .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                            @Override
                            public void onNegative(Dialog droidDialog) {
                                startIntentActivity();
                            }
                        })
                        .cancelable(false, false)
                        .show();
            } else {
                startIntentActivity();
            }
        } else {
            startIntentActivity();
        }
    }

    private void startIntentActivity() {
        if (startIntent != null) {
            jumpTo(startIntent);
            startIntent = null;
            selfModel = null;
        } else {
            finish();
        }
    }

    /**
     * 检索该字段是否重复
     */
    private boolean hasData(String currencyNameEn, String baseCurrencyNameEn) {
        return DaoManager.getSearchHistoryUtils().getSearchHistoryModel(spUtil.getUserUid(), currencyNameEn, baseCurrencyNameEn);
    }

    /**
     * 添加
     */
    private void insertData(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        DaoManager.getSearchHistoryUtils().insertSearchHistoryModel(spUtil.getUserUid(), currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
    }

    /**
     * 查询全部
     */
    private void hasDataAll() {
        List<SearchHistorysModel> tempList = DaoManager.getSearchHistoryUtils().getSearchHistoryModelList(spUtil.getUserUid());
        if (tempList != null && tempList.size() > 0) {
            tv_clear.setVisibility(VISIBLE);
        } else {
            tempList = new ArrayList<>();
            tv_clear.setVisibility(GONE);
        }
        historyAdapter.setNewData(tempList);
    }

    /**
     * 删除全部
     */
    private void deleteAll() {
        DaoManager.getSearchHistoryUtils().deleteMultSearchHistoryModel(spUtil.getUserUid());
    }

    /**
     * 历史纪录
     */
    private class HistoryAdapter extends BaseQuickAdapter<SearchHistorysModel, BaseViewHolder> {

        public HistoryAdapter() {
            super(R.layout.item_history);
        }

        @Override
        protected void convert(BaseViewHolder helper, final SearchHistorysModel item) {
            helper.setText(R.id.tv_name, item.getCurrencyNameEn() + "/" + item.getBaseCurrencyNameEn());

            starShow(true, (ImageView) helper.getView(R.id.img_select), item.getCurrencyId() + "," + item.getBaseCurrencyId());

            helper.setOnClickListener(R.id.tv_name, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIntent = MarketDetailsActivity.createActivity(SearchHistoryActivity.this, item.getCurrencyId(), item.getBaseCurrencyId(), item.getCurrencyNameEn(), item.getBaseCurrencyNameEn());
                    showStartDialog();
                }
            });

            final SelfModel.SymbolBean tempSelectCoin = historySymbolBean;
            helper.setOnClickListener(R.id.img_select, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    starSelectClick(true, tempSelectCoin, item.getCurrencyId() + "," + item.getBaseCurrencyId());
                }
            });
        }
    }

    /**
     * 搜索列表
     */
    private class SearchAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {

        public SearchAdapter() {
            super(R.layout.item_history);
        }

        @Override
        protected void convert(BaseViewHolder helper, final MarketNewModel.TradeCoinsBean item) {
            helper.setText(R.id.tv_name, CommonUtils.setKeyWordColor(item.currencyNameEn + "/" + item.baseCurrencyNameEn, keyword, ContextCompat.getColor(SearchHistoryActivity.this, R.color.color_blue_2)));

            starShow(false, (ImageView) helper.getView(R.id.img_select), item.currencyId + "," + item.baseCurrencyId);

            helper.setOnClickListener(R.id.tv_name, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveHistory(item);
                    startIntent = MarketDetailsActivity.createActivity(SearchHistoryActivity.this, item.currencyId, item.baseCurrencyId, item.currencyNameEn, item.baseCurrencyNameEn);
                    showStartDialog();
                }
            });

            final SelfModel.SymbolBean tempSelectCoin = searchSymbolBean;
            helper.setOnClickListener(R.id.img_select, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    starSelectClick(false, tempSelectCoin, item.currencyId + "," + item.baseCurrencyId);
                }
            });
        }
    }

    /**
     * 星星的显示
     *
     * @param isHistory true：代表HistoryAdapter调用这个方法
     */
    private void starShow(boolean isHistory, ImageView imageView, String currAndBase) {
        if (isHistory) {
            historySymbolBean = null;
        } else {
            searchSymbolBean = null;
        }
        imageView.setImageResource(R.drawable.icon_rating_uncollect);
        if (spUtil.getLogin()) {
            if (selfModel != null && selfModel.checkedList != null && selfModel.checkedList.size() > 0) {
                imageView.setImageResource(R.drawable.icon_rating_uncollect);
                for (int i = 0; i < selfModel.checkedList.size(); i++) {
                    if (TextUtils.equals(selfModel.checkedList.get(i).symbol, currAndBase)) {
                        if (isHistory) {
                            historySymbolBean = selfModel.checkedList.get(i);
                        } else {
                            searchSymbolBean = selfModel.checkedList.get(i);
                        }
                        imageView.setImageResource(R.drawable.icon_rating_collect);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 星星的点击事件
     *
     * @param isHistory      true：代表HistoryAdapter调用这个方法 只刷新historyAdapter  不需要刷新searchAdapter
     * @param tempSelectCoin
     */
    private void starSelectClick(boolean isHistory, SelfModel.SymbolBean tempSelectCoin, String currAndBase) {
        if (spUtil.getLogin()) {
            if (tempSelectCoin != null) {
                selfModel.checkedList.remove(tempSelectCoin);
            } else {
                SelfModel.SymbolBean bean = new SelfModel.SymbolBean();
                bean.symbol = currAndBase;
                selfModel.checkedList.add(0, bean);
            }
            if (!isHistory) {
                searchAdapter.notifyDataSetChanged();
            }
            historyAdapter.notifyDataSetChanged();
        } else {
            jumpTo(LoginActivity.class);
        }
    }
}
