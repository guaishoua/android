package com.android.tacu.module.assets.view;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.ExchangeContract;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.ExchangeModel;
import com.android.tacu.module.assets.model.ExchangeShowModel;
import com.android.tacu.module.assets.presenter.ExchangePresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.utils.LogUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class ExchangeFragment extends BaseFragment<ExchangePresenter> implements ExchangeContract.IExchangeView {

    @BindView(R.id.tv_exchange_num)
    TextView tv_exchange_num;
    @BindView(R.id.tv_exchange_price)
    TextView tv_exchange_price;
    @BindView(R.id.tv_exchange)
    TextView tv_exchange;
    @BindView(R.id.bt_exchange_step)
    QMUIAlphaButton bt_exchange_step;
    @BindView(R.id.cb_agree)
    CheckBox cb_agree;
    @BindView(R.id.et_exchange_pwd)
    EditText et_exchange_pwd;
    @BindView(R.id.et_exchange_num)
    EditText et_exchange_num;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int EXCHANGE_DIGIT = 8;//显示位数
    private int currencyId;
    private String currencyNameEn;

    private AmountModel amountModel;
    private ExchangeModel exchangeModel;
    private List<MarketNewModel.TradeCoinsBean> list;
    private RecommendAdapter adapter;

    public static ExchangeFragment newInstance(int currencyId, String currencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        ExchangeFragment fragment = new ExchangeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyNameEn = bundle.getString("currencyNameEn");
            currencyId = bundle.getInt("currencyId");
        }
        super.onCreate(savedInstanceState);
    }

    public void updateUI(int currencyId, String currencyNameEn) {
        this.currencyId = currencyId;
        this.currencyNameEn = currencyNameEn;

        if (isVisibleToUser) {
            upLoad();
        }

        if (isUSDT()) {
            et_exchange_num.setHint(getResources().getString(R.string.exchange_usdt_hint));
            tv_exchange.setText("≈0.00000000CODE");
        } else {
            et_exchange_num.setHint(getResources().getString(R.string.exchange_code_hint));
            tv_exchange.setText("≈0.00000000USDT");
        }

        initCache();
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upLoad();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_exchange;
    }

    @Override
    protected void initData() {
        if (isUSDT()) {
            et_exchange_num.setHint(getResources().getString(R.string.exchange_usdt_hint));
            tv_exchange.setText("≈0.00000000CODE");
        } else {
            et_exchange_num.setHint(getResources().getString(R.string.exchange_code_hint));
            tv_exchange.setText("≈0.00000000USDT");
        }

        initCache();

        bt_exchange_step.setChangeAlphaWhenPress(false);
        bt_exchange_step.setBackgroundResource(R.drawable.shape_grey_soild_5);

        Drawable drawable = getResources().getDrawable(R.drawable.checkbox_square);
        drawable.setBounds(0, 0, 34, 34);
        cb_agree.setCompoundDrawables(drawable, null, null, null);

        cb_agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                colorBoutton();
            }
        });

        et_exchange_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    //设置小数位小于8
                    if (s.toString().contains(".")) {
                        if (s.length() - 1 - s.toString().indexOf(".") > EXCHANGE_DIGIT) {
                            s = s.toString().subSequence(0, s.toString().indexOf(".") + EXCHANGE_DIGIT + 1);
                            et_exchange_num.setText(s);
                            et_exchange_num.setSelection(s.length());
                        }
                    }
                    if (s.toString().trim().substring(0).equals(".")) {
                        s = "0" + s;
                        et_exchange_num.setText(s);
                        et_exchange_num.setSelection(2);
                    }
                    if (s.toString().startsWith("0")
                            && s.toString().trim().length() > 1) {
                        if (!s.toString().substring(1, 2).equals(".")) {
                            et_exchange_num.setText(s.subSequence(0, 1));
                            et_exchange_num.setSelection(1);
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    colorBoutton();

                    if (!TextUtils.isEmpty(et_exchange_num.getText().toString().trim())) {
                        exchangeNum();
                    }
                } catch (Exception e) {
                    LogUtils.e("==e" + e.getMessage());
                } finally {
                    if (TextUtils.isEmpty(et_exchange_num.getText().toString().trim())) {
                        if (isUSDT()) {
                            tv_exchange.setText("≈0.00000000CODE");
                        } else {
                            tv_exchange.setText("≈0.00000000USDT");
                        }
                    }
                }
            }
        });
        et_exchange_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                colorBoutton();
            }
        });
    }

    @Override
    protected ExchangePresenter createPresenter(ExchangePresenter mPresenter) {
        return new ExchangePresenter();
    }

    private void upLoad() {
        mPresenter.getUSDT(currencyId);
        mPresenter.customerCoinByOneCoin(currencyId);
    }

    /**
     * 全部监听
     */
    @OnClick(R.id.tv_exchange_all)
    void exchangeAll() {
        if (amountModel != null) {
            et_exchange_num.setText(BigDecimal.valueOf(amountModel.attachment).toPlainString());
        }
    }

    /**
     * 兑换
     */
    @OnClick(R.id.bt_exchange_step)
    void exchange() {
        String fdPwd = et_exchange_pwd.getText().toString().trim();
        String num = et_exchange_num.getText().toString().trim();

        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_shake);
        if (TextUtils.isEmpty(fdPwd)) {
            et_exchange_pwd.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(num)) {
            et_exchange_num.startAnimation(shake);
            return;
        }
        if (!cb_agree.isChecked()) {
            return;
        }

        mPresenter.confirmMessage(Md5Utils.encryptFdPwd(fdPwd, spUtil.getUserUid()), String.valueOf(currencyId), num);
    }

    /**
     * 加载缓存
     */
    private void initCache() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new RecommendAdapter();
        recyclerView.setAdapter(adapter);

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

        if (list != null && list.size() > 0) {
            adapter.setNewData(list);
        }
    }

    @Override
    public void getUSTPrice(ExchangeModel model) {
        if (model != null) {
            this.exchangeModel = model;
            setPrice();
        }
    }

    @Override
    public void confirmMessage(ExchangeShowModel model) {
        if (model != null) {
            exchangeUSDT(model);
        }
    }

    @Override
    public void exchangeCoinUSDTToCode() {
        showToastSuccess(getResources().getString(R.string.exchange_success));

        et_exchange_num.setText("");
        et_exchange_pwd.setText("");

        if (isUSDT()) {
            tv_exchange_num.setText(getResources().getString(R.string.exchange_num_usdt) + "--");
        } else {
            tv_exchange_num.setText(getResources().getString(R.string.exchange_num_code) + "--");
        }

        mPresenter.customerCoinByOneCoin(currencyId);
    }

    @Override
    public void customerCoinByOneCoin(AmountModel model) {
        if (model != null) {
            this.amountModel = model;
            setPrice();
        }
    }

    private void setPrice() {
        if (amountModel != null && exchangeModel != null) {
            if (isUSDT()) {
                tv_exchange_num.setText(getResources().getString(R.string.exchange_num_usdt) + BigDecimal.valueOf(amountModel.attachment).toPlainString());
                tv_exchange_price.setText("1USDT=" + BigDecimal.valueOf(exchangeModel.sellPrice).setScale(4).toPlainString() + "CODE");
            } else {
                tv_exchange_num.setText(getResources().getString(R.string.exchange_num_code) + BigDecimal.valueOf(amountModel.attachment).toPlainString());
                tv_exchange_price.setText("1USDT=" + BigDecimal.valueOf(exchangeModel.buyPrice).setScale(4).toPlainString() + "CODE");
            }
        }
    }

    /**
     * 设置兑换按钮背景色
     */
    public void colorBoutton() {
        if (cb_agree.isChecked() && !TextUtils.isEmpty(et_exchange_num.getText().toString().trim()) && !TextUtils.isEmpty(et_exchange_pwd.getText().toString().trim())) {
            bt_exchange_step.setChangeAlphaWhenPress(true);
            bt_exchange_step.setBackgroundResource(R.drawable.gradient_blue_btn_5);
        } else {
            bt_exchange_step.setChangeAlphaWhenPress(false);
            bt_exchange_step.setBackgroundResource(R.drawable.shape_grey_soild_5);
        }
    }

    /**
     * 约等于价格
     */
    private void exchangeNum() {
        double etNum = Double.parseDouble(et_exchange_num.getText().toString().trim());
        if (isUSDT()) {
            tv_exchange.setText("≈" + BigDecimal.valueOf(etNum).multiply(BigDecimal.valueOf(exchangeModel.sellPrice)).setScale(EXCHANGE_DIGIT, BigDecimal.ROUND_DOWN).toPlainString() + "CODE");
        } else {
            tv_exchange.setText("≈" + BigDecimal.valueOf(etNum).divide(BigDecimal.valueOf(exchangeModel.buyPrice), EXCHANGE_DIGIT, BigDecimal.ROUND_DOWN).toPlainString() + "USDT");
        }
    }

    /**
     * 判读是USDT还是CODE兑换
     *
     * @return
     */
    private boolean isUSDT() {
        if (TextUtils.equals(currencyNameEn, "USDT")) {
            return true;
        } else if (TextUtils.equals(currencyNameEn, "CODE")) {
            return false;
        }
        return false;
    }

    private void exchangeUSDT(ExchangeShowModel model) {
        View view = View.inflate(getActivity(), R.layout.view_exchange_dialog, null);
        TextView tv_exchange_code = view.findViewById(R.id.tv_exchange_code);
        TextView tv_exchange_real = view.findViewById(R.id.tv_exchange_real);
        TextView tv_obtain_code = view.findViewById(R.id.tv_obtain_code);

        if (isUSDT()) {
            tv_exchange_code.setText(getResources().getString(R.string.exchange_num_usdt2) + BigDecimal.valueOf(model.exchangeAmount));
            tv_exchange_real.setText(getResources().getString(R.string.exchange_real_price) + "1USDT=" + model.scale + "CODE");
            tv_obtain_code.setText(getResources().getString(R.string.exchange_obtain_code) + BigDecimal.valueOf(model.GetAmount));
        } else {
            tv_exchange_code.setText(getResources().getString(R.string.exchange_num_code2) + BigDecimal.valueOf(model.exchangeAmount));
            tv_exchange_real.setText(getResources().getString(R.string.exchange_real_price) + "1USDT=" + model.scale + "CODE");
            tv_obtain_code.setText(getResources().getString(R.string.exchange_obtain_usdt) + BigDecimal.valueOf(model.GetAmount));
        }

        new DroidDialog.Builder(getActivity())
                .title(getResources().getString(R.string.exchange_info))
                .viewCustomLayout(view)
                .titleGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.exchange_affrim), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String fdPwd = et_exchange_pwd.getText().toString().trim();
                        String num = et_exchange_num.getText().toString().trim();
                        mPresenter.exchangeCoinUSDTToCode(Md5Utils.encryptFdPwd(fdPwd, spUtil.getUserUid()), String.valueOf(currencyId), num);
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
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
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.text_blue));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.text_blue));
            } else {
                helper.setText(R.id.tv_absolutely, BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.text_red));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.text_red));
            }

            helper.setText(R.id.tv_situation, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            //后期有数据记得测试
            if (item.baseCurrencyId == 22) {
                helper.setText(R.id.tv_money, "");
            } else {
                helper.setText(R.id.tv_money, "≈" + getMcM(item.baseCurrencyId, item.currentAmount));
            }
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(MarketDetailsActivity.createActivity(getContext(), item.currencyId, item.baseCurrencyId, item.currencyNameEn, item.baseCurrencyNameEn));
                }
            });
        }
    }
}
