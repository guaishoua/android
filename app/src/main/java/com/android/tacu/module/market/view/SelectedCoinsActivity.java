package com.android.tacu.module.market.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.market.adapter.SelectedCoinsAdapter;
import com.android.tacu.module.market.contract.SelectedCoinsContract;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.market.presenter.SelectedCoinsPresenter;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SelectedCoinsActivity extends BaseActivity<SelectedCoinsPresenter> implements SelectedCoinsContract.IView {

    @BindView(R.id.base_scrollIndicatorView)
    ScrollIndicatorView baseIndicatorView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.lin_now)
    LinearLayout linNow;
    @BindView(R.id.tv_select_coins)
    TextView tvSelectCoins;

    private List<MarketNewModel> attachment = new ArrayList<>();
    private SelectedCoinsAdapter coinsAdapter;
    private Gson gson = new Gson();
    private String selfModelString;//最初的自选
    private String saveSelfModelString;//改变后的自选数据
    private SelfModel selfModel;

    private boolean isFlag = false;
    private boolean isUpload = false;

    /**
     * @param context
     * @param isFlag             false: 代表选择币种  true: 添加自选
     * @param isUpload           是否直接在这个页面上传自选数据
     * @param selfModelString    从上个页面传递过来的自选数据
     * @param currencyNameEn     当前选择
     * @param baseCurrencyNameEn 当前选择
     * @return
     */
    public static Intent createActivity(Context context, boolean isFlag, boolean isUpload, String selfModelString, String currencyNameEn, String baseCurrencyNameEn) {
        Intent intent = new Intent(context, SelectedCoinsActivity.class);
        intent.putExtra("isFlag", isFlag);
        intent.putExtra("isUpload", isUpload);
        intent.putExtra("selfModelString", selfModelString);
        intent.putExtra("currencyNameEn", currencyNameEn);
        intent.putExtra("baseCurrencyNameEn", baseCurrencyNameEn);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_selected_coins);
    }

    @Override
    protected void initView() {
        isFlag = getIntent().getBooleanExtra("isFlag", false);
        isUpload = getIntent().getBooleanExtra("isUpload", false);
        selfModelString = getIntent().getStringExtra("selfModelString");

        if (isFlag) {
            mTopBar.setTitle(getResources().getString(R.string.add_selfselection));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.market));
        }
        mTopBar.removeAllLeftViews();
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpload) {
                    showLeaveDialog();
                } else {
                    backValue();
                }
            }
        });

        if (isFlag && spUtil.getLogin()) {
            selfModel = gson.fromJson(selfModelString, SelfModel.class);
            if (selfModel == null) {
                selfModel = new SelfModel();
                selfModelString = gson.toJson(selfModel);
            }
        }
        if (isFlag) {
            linNow.setVisibility(View.GONE);
        }

        tvSelectCoins.setText(getIntent().getStringExtra("currencyNameEn") + "/" + getIntent().getStringExtra("baseCurrencyNameEn"));

        baseIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        baseIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        baseIndicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.text_default), 4));
        baseIndicatorView.setSplitAuto(true);
        baseIndicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                setCoinValueList(select);
            }
        });

        //添加分割线
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        coinsAdapter = new SelectedCoinsAdapter(SelectedCoinsActivity.this, isFlag);
        recyclerView.setAdapter(coinsAdapter);

        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = gson.fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());
        if (cacheList != null && cacheList.size() > 0) {
            setBaseCoinsList(cacheList);
        }
    }

    @Override
    protected SelectedCoinsPresenter createPresenter(SelectedCoinsPresenter mPresenter) {
        return new SelectedCoinsPresenter();
    }

    @Override
    public void onBackPressed() {
        if (isUpload) {
            showLeaveDialog();
        } else {
            backValue();
        }
    }

    @Override
    public void uploadSelfSuccess() {
        if (!TextUtils.isEmpty(saveSelfModelString)) {
            SPUtils.getInstance().put(Constant.SELFCOIN_LIST, saveSelfModelString);
        }
        showToastSuccess(getResources().getString(R.string.business_edit_collect));
        finish();
    }

    @Override
    public void uploadSelfError() {
        showToastError(getResources().getString(R.string.business_edit_collect_error));
    }

    public void setBaseCoinsList(List<MarketNewModel> attachment) {
        this.attachment = attachment;
        if (attachment != null && attachment.size() > 0) {
            baseIndicatorView.setAdapter(new BaseIndicatorAdapter(attachment));
            setCoinValueList(0);
        }
    }

    private void backValue() {
        if (isFlag && coinsAdapter.getSelfModel() != null) {
            Intent intent = new Intent();
            intent.putExtra("selfModel", gson.toJson(coinsAdapter.getSelfModel()));
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private void showLeaveDialog() {
        if (coinsAdapter.getSelfModel() != null) {
            saveSelfModelString = gson.toJson(coinsAdapter.getSelfModel());
        }
        if (!TextUtils.equals(selfModelString, saveSelfModelString)) {
            new DroidDialog.Builder(this)
                    .title(getResources().getString(R.string.self_selection_save))
                    .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                        @Override
                        public void onPositive(Dialog droidDialog) {
                            if (spUtil.getLogin() && !TextUtils.isEmpty(saveSelfModelString)) {
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
    }

    private void setCoinValueList(int position) {
        if (coinsAdapter.getSelfModel() == null) {
            coinsAdapter.setData(selfModel);
        }
        coinsAdapter.setNewData(attachment.get(position).tradeCoinsList);
    }

    private class BaseIndicatorAdapter extends Indicator.IndicatorAdapter {

        private List<MarketNewModel> list;

        public BaseIndicatorAdapter(List<MarketNewModel> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                textView.setText(list.get(position).name);
            } else {
                textView.setText(list.get(position).name_en);
            }
            int padding = UIUtils.dp2px(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }
    }
}
