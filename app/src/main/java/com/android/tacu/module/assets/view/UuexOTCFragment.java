package com.android.tacu.module.assets.view;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.UuexOTCContract;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.TransInfoCoinModal;
import com.android.tacu.module.assets.presenter.UuexOTCPresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class UuexOTCFragment extends BaseFragment<UuexOTCPresenter> implements UuexOTCContract.IView {

    @BindView(R.id.tv_mode_email)
    TextView tv_mode_email;
    @BindView(R.id.tv_mode_phone)
    TextView tv_mode_phone;
    @BindView(R.id.view_email)
    View view_email;
    @BindView(R.id.view_phone)
    View view_phone;

    //转入
    @BindView(R.id.ll_in)
    LinearLayout ll_in;
    @BindView(R.id.tv_coin_amount)
    TextView tv_coin_amount;
    @BindView(R.id.et_amount)
    EditText et_amount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //转出
    @BindView(R.id.ll_out)
    LinearLayout ll_out;
    @BindView(R.id.et_amount_out)
    EditText et_amount_out;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.rb_phone_mode)
    RadioButton rb_phone_mode;
    @BindView(R.id.rb_email_mode)
    RadioButton rb_email_mode;
    @BindView(R.id.btn_email_code)
    Button btn_email_code;
    @BindView(R.id.btn_phone_code)
    Button btn_phone_code;
    @BindView(R.id.et_google_code)
    EditText et_google_code;
    @BindView(R.id.et_mode_code)
    EditText et_mode_code;
    @BindView(R.id.ll_googlecode)
    LinearLayout ll_googlecode;

    private String inNumString;
    private String outNumString;

    //2：手机  1：邮箱
    private int type = -1;
    //1：转入  2：转出
    private int flags = 1;

    private int currencyId;
    private String currencyNameEn = "USDT";

    private TransInfoCoinModal transInfoCoinModal;

    //推荐
    private List<MarketNewModel.TradeCoinsBean> list = new ArrayList<>();
    private RecommendAdapter adapter;

    public static UuexOTCFragment newInstance(int currencyId, String currencyNameEn, TransInfoCoinModal transInfoCoinModal) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putSerializable("transInfoCoinModal", transInfoCoinModal);
        UuexOTCFragment fragment = new UuexOTCFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyNameEn = bundle.getString("currencyNameEn");
            currencyId = bundle.getInt("currencyId");
            transInfoCoinModal = (TransInfoCoinModal) bundle.getSerializable("transInfoCoinModal");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_uuex_otc;
    }

    @Override
    protected void initData() {
        initCache();
        dealCoin();
        setTextView();

        tv_mode_email.setText(getResources().getString(R.string.transfer_in));
        tv_mode_phone.setText(getResources().getString(R.string.transfer_out));

        //选择验证方式
        if (CommonUtils.isBindMode() == 2) {
            type = 1;
            isCheckbox(false);
        } else if (CommonUtils.isBindMode() == 1 || CommonUtils.isBindMode() == 3) {
            type = 2;
            isCheckbox(true);
        }

        if (TextUtils.equals(spUtil.getGaStatus(), "0") || TextUtils.equals(spUtil.getGaStatus(), "2")) {
            ll_googlecode.setVisibility(View.GONE);
        }
    }

    @Override
    protected UuexOTCPresenter createPresenter(UuexOTCPresenter mPresenter) {
        return new UuexOTCPresenter();
    }

    @Override
    protected void onPresenterCreated(UuexOTCPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        upload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerEmail != null) {
            timerEmail.cancel();
            timerEmail = null;
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
        if (!isKeyc()) {
            return;
        }

        flags = 2;
        setTextView();
        if (flags == 1 && !TextUtils.isEmpty(inNumString)) {
            tv_coin_amount.setText(inNumString + currencyNameEn);
        } else if (flags == 2 && !TextUtils.isEmpty(outNumString)) {
            tv_coin_amount.setText(outNumString + currencyNameEn);
        }
    }

    /**
     * 选择手机验证码
     */
    @OnClick(R.id.rb_phone_mode)
    void setEtPhoneCode() {
        if (!spUtil.getPhoneStatus()) {
            isCheckbox(false);
            jumpTo(BindModeActivity.createActivity(getContext(), 3));
            return;
        }

        type = 2;
        isCheckbox(true);
    }

    /**
     * 选择邮箱验证码
     */
    @OnClick(R.id.rb_email_mode)
    void setEtEmailCode() {
        if (!spUtil.getEmailStatus()) {
            isCheckbox(true);
            jumpTo(BindModeActivity.createActivity(getContext(), 4));
            return;
        }

        type = 1;
        isCheckbox(false);
    }

    /**
     * 获取邮箱验证码
     */
    @OnClick(R.id.btn_email_code)
    void sendEmail() {
        if (type == -1) {
            showToastError(getResources().getString(R.string.auth_method));
            return;
        }
        mPresenter.emailTakeCoin(type);
    }

    /**
     * 获取手机号验证码
     */
    @OnClick(R.id.btn_phone_code)
    void sendPhone() {
        if (type == -1) {
            showToastError(getResources().getString(R.string.auth_method));
            return;
        }
        mPresenter.emailTakeCoin(type);
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
        et_pwd.setText("");
        et_mode_code.setText("");
        et_google_code.setText("");
        showToastSuccess(getResources().getString(R.string.success));

        upload();
    }

    @Override
    public void transInSuccess() {
        et_amount.setText("");

        upload();
    }

    @Override
    public void coinNum(String num) {
        if (!TextUtils.isEmpty(num)) {
            inNumString = FormatterUtils.getFormatValue(num);
            if (flags == 1) {
                tv_coin_amount.setText(inNumString + currencyNameEn);
            }
        }
    }

    @Override
    public void customerCoinByOneCoin(AmountModel model) {
        if (model != null) {
            outNumString = BigDecimal.valueOf(model.attachment).toPlainString();
            if (flags == 2) {
                tv_coin_amount.setText(outNumString + currencyNameEn);
            }
        }
    }

    @Override
    public void success() {
        showToastSuccess(getString(R.string.send_successful));
        if (type == 1) {
            timerEmail.start();
        } else if (type == 2) {
            timer.start();
        }
    }

    private void upload() {
        mPresenter.coinNum(currencyId);
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
        String tradePw = et_pwd.getText().toString().trim();
        String code = et_mode_code.getText().toString().trim();
        String gAuth = et_google_code.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            et_amount_out.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(tradePw)) {
            et_pwd.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            et_mode_code.startAnimation(shake);
            return;
        }

        mPresenter.transOut(amount, currencyId, Md5Utils.encryptFdPwd(tradePw, spUtil.getUserUid()), code, gAuth);
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

    private void setTextView() {
        if (flags == 1) {
            setTextColor(tv_mode_email, R.color.text_blue_2);
            setTextColor(tv_mode_phone, R.color.text_color);
        } else {
            setTextColor(tv_mode_email, R.color.text_color);
            setTextColor(tv_mode_phone, R.color.text_blue_2);
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
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.text_blue_2));
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
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.text_blue));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.text_blue));
            } else {
                helper.setText(R.id.tv_absolutely, BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                helper.setTextColor(R.id.tv_absolutely, ContextCompat.getColor(getContext(), R.color.text_red));
                helper.setTextColor(R.id.tv_situation, ContextCompat.getColor(getContext(), R.color.text_red));
            }

            helper.setText(R.id.tv_situation, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
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

    private void isCheckbox(boolean isCheck) {
        rb_email_mode.setChecked(!isCheck);
        rb_phone_mode.setChecked(isCheck);

        btn_email_code.setVisibility(isCheck == false ? View.VISIBLE : View.GONE);
        btn_phone_code.setVisibility(isCheck == true ? View.VISIBLE : View.GONE);
    }

    private CountDownTimer timerEmail = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long arg0) {
            try {
                // 定期定期回调
                btn_email_code.setText((arg0 / 1000) + "s");
                btn_email_code.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            try {
                btn_email_code.setEnabled(true);
                // 结束后回到
                btn_email_code.setText(getString(R.string.resend));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后60秒之后会回调onFinish方法
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long arg0) {
            try {
                // 定期定期回调
                btn_phone_code.setText((arg0 / 1000) + "s");
                btn_phone_code.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            try {
                btn_phone_code.setEnabled(true);
                // 结束后回到
                btn_phone_code.setText(getString(R.string.resend));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void dealCoin() {
        if (transInfoCoinModal != null) {
            boolean isIn = false, isOut = false;
            for (int i = 0; i < transInfoCoinModal.inList.size(); i++) {
                if (transInfoCoinModal.inList.get(i).currencyId == currencyId) {
                    isIn = true;
                    break;
                }
            }
            for (int i = 0; i < transInfoCoinModal.outList.size(); i++) {
                if (transInfoCoinModal.outList.get(i).currencyId == currencyId) {
                    isOut = true;
                    break;
                }
            }
            tv_mode_email.setEnabled(true);
            tv_mode_phone.setEnabled(true);
            if (!isIn) {
                tv_mode_email.setEnabled(false);
                tv_mode_phone.setEnabled(false);
                flags = 1;
            }
            if (!isOut) {
                tv_mode_email.setEnabled(false);
                tv_mode_phone.setEnabled(false);
                flags = 2;
            }
        }
    }
}
