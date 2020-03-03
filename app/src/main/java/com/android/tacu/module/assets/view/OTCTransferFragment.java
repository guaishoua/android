package com.android.tacu.module.assets.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.OTCTransferContract;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.presenter.OTCTransferPresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OTCTransferFragment extends BaseFragment<OTCTransferPresenter> implements OTCTransferContract.IView {

    @BindView(R.id.tv_mode_email)
    TextView tv_mode_email;
    @BindView(R.id.tv_mode_phone)
    TextView tv_mode_phone;
    @BindView(R.id.view_email)
    View view_email;
    @BindView(R.id.view_phone)
    View view_phone;
    @BindView(R.id.tv_coin_amount)
    TextView tv_coin_amount;
    //转入
    @BindView(R.id.ll_in)
    LinearLayout ll_in;
    @BindView(R.id.et_amount)
    EditText et_amount;
    //转出
    @BindView(R.id.ll_out)
    LinearLayout ll_out;
    @BindView(R.id.et_amount_out)
    EditText et_amount_out;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String inNumString;
    private String outNumString;

    //1：转入  2：转出
    private int flags = 1;
    private boolean isFlag;

    private int currencyId;
    private String currencyNameEn = "USDT";

    //推荐
    private List<MarketNewModel.TradeCoinsBean> list = new ArrayList<>();
    private RecommendAdapter adapter;

    public static OTCTransferFragment newInstance(int currencyId, String currencyNameEn, boolean isFlag) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putBoolean("isFlag", isFlag);
        OTCTransferFragment fragment = new OTCTransferFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upload();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyNameEn = bundle.getString("currencyNameEn");
            currencyId = bundle.getInt("currencyId");
            isFlag = bundle.getBoolean("isFlag");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_transfer;
    }

    @Override
    protected void initData(View view) {
        initCache();
        setTextView();

        tv_mode_email.setText(getResources().getString(R.string.transfer_in));
        tv_mode_phone.setText(getResources().getString(R.string.transfer_out));
    }

    @Override
    protected OTCTransferPresenter createPresenter(OTCTransferPresenter mPresenter) {
        return new OTCTransferPresenter();
    }

    @Override
    protected void onPresenterCreated(OTCTransferPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        if (isVisibleToUser && isFlag) {
            upload();
        }
    }

    /**
     * 转入
     */
    @OnClick(R.id.tv_mode_email)
    void modeEmail() {
        flags = 1;
        setTextView();
        if (flags == 1 && !TextUtils.isEmpty(inNumString)) {
            tv_coin_amount.setText(inNumString + currencyNameEn);
        } else if (flags == 2 && !TextUtils.isEmpty(outNumString)) {
            tv_coin_amount.setText(outNumString + currencyNameEn);
        }
    }

    /**
     * 转出
     */
    @OnClick(R.id.tv_mode_phone)
    void modePhone() {
        flags = 2;
        setTextView();
        if (flags == 1 && !TextUtils.isEmpty(inNumString)) {
            tv_coin_amount.setText(inNumString + currencyNameEn);
        } else if (flags == 2 && !TextUtils.isEmpty(outNumString)) {
            tv_coin_amount.setText(outNumString + currencyNameEn);
        }
    }

    /**
     * 确定
     */
    @OnClick(R.id.qmuibt_confrim_transfer)
    void transferCoin() {
        if (flags == 1) {
            transferIn();
        } else {
            transferOut();
        }
    }

    @Override
    public void transOutSuccess() {
        et_amount_out.setText("");
        showToastSuccess(getResources().getString(R.string.success));

        upload();
    }

    @Override
    public void transInSuccess() {
        et_amount.setText("");

        upload();
    }

    @Override
    public void customerCoinByOneCoin(AmountModel model) {
        if (model != null) {
            inNumString = FormatterUtils.getFormatValue(model.attachment);
            if (flags == 1) {
                tv_coin_amount.setText(inNumString + currencyNameEn);
            }
        } else {
            inNumString = FormatterUtils.getFormatValue(0);
        }
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        if (model != null) {
            outNumString = FormatterUtils.getFormatValue(model.cashAmount);
            if (flags == 2) {
                tv_coin_amount.setText(outNumString + currencyNameEn);
            }
        } else {
            outNumString = FormatterUtils.getFormatValue(0);
        }
    }

    private void upload() {
        mPresenter.otcAmount(currencyId);
        mPresenter.customerCoinByOneCoin(currencyId);
    }

    private void transferIn() {
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);

        String amount = et_amount.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            et_amount.startAnimation(shake);
            return;
        }
        mPresenter.transIn(amount, currencyId);
    }

    private void transferOut() {
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);

        String amount = et_amount_out.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            et_amount_out.startAnimation(shake);
            return;
        }
        mPresenter.transOut(amount, currencyId);
    }

    private void setTextView() {
        if (flags == 1) {
            setTextColor(tv_mode_email, R.color.text_default);
            setTextColor(tv_mode_phone, R.color.text_color);
        } else {
            setTextColor(tv_mode_email, R.color.text_color);
            setTextColor(tv_mode_phone, R.color.text_default);
        }
        setViewColor(view_email);
        setViewColor(view_phone);
        ll_in.setVisibility(flags == 1 ? View.VISIBLE : View.GONE);
        ll_out.setVisibility(flags == 2 ? View.VISIBLE : View.GONE);
        view_email.setVisibility(flags == 1 ? View.VISIBLE : View.GONE);
        view_phone.setVisibility(flags == 2 ? View.VISIBLE : View.GONE);
    }

    private void setTextColor(TextView tv, int color) {
        tv.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    private void setViewColor(View view) {
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.text_default));
    }

    /**
     * 加载缓存
     */
    private void initCache() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = new Gson().fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());

        list = new ArrayList<>();
        MarketNewModel.TradeCoinsBean tradeBean;
        if (cacheList != null && cacheList.size() > 0) {
            for (int i = 0; i < cacheList.size(); i++) {
                for (int j = 0; j < cacheList.get(i).tradeCoinsList.size(); j++) {
                    if (cacheList.get(i).tradeCoinsList.get(j).currencyId == currencyId) {
                        tradeBean = cacheList.get(i).tradeCoinsList.get(j);
                        list.add(tradeBean);
                        break;
                    }
                }
            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new RecommendAdapter();
        recyclerView.setAdapter(adapter);

        if (list != null && list.size() > 0) {
            adapter.setNewData(list);
        }
    }

    private class RecommendAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {

        public RecommendAdapter() {
            super(R.layout.item_recharge_recommend);
        }

        @Override
        protected void convert(BaseViewHolder helper, final MarketNewModel.TradeCoinsBean item) {
            helper.setText(R.id.tv_currency, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
            if (item.changeRate >= 0) {
                helper.setText(R.id.tv_absolutely, "+" + BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.color_riseup));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.color_riseup));
            } else {
                helper.setText(R.id.tv_absolutely, BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.color_risedown));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.color_risedown));
            }

            helper.setText(R.id.tv_situation, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            helper.setText(R.id.tv_money, "≈" + getMcM(item.baseCurrencyId, item.currentAmount));

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(MarketDetailsActivity.createActivity(getContext(), item.currencyId, item.baseCurrencyId, item.currencyNameEn, item.baseCurrencyNameEn));
                }
            });
        }
    }
}
