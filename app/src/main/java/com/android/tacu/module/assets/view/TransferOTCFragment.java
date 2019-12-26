package com.android.tacu.module.assets.view;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.ApiHost;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.TransferRecordContract;
import com.android.tacu.module.assets.model.TransferInfo;
import com.android.tacu.module.assets.presenter.TransferOTCPresenter;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.module.my.view.GoogleHintActivity;
import com.android.tacu.module.webview.model.EPayParam;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.android.tacu.widget.popupwindow.TransferPopWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/21.
 */

public class TransferOTCFragment extends BaseFragment<TransferOTCPresenter> implements TransferRecordContract.ITransferView, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.tv_mode_email)
    TextView tv_mode_email;
    @BindView(R.id.tv_mode_phone)
    TextView tv_mode_phone;
    @BindView(R.id.view_email)
    View view_email;
    @BindView(R.id.view_phone)
    View view_phone;
    @BindView(R.id.tv_hint)
    TextView tv_hint;
    @BindView(R.id.tv_actual)
    TextView tv_actual;
    @BindView(R.id.tv_transfer_fee)
    TextView tv_transfer_fee;
    @BindView(R.id.tv_epay_register)
    TextView tv_epay_register;
    @BindView(R.id.qmuibt_confrim_transfer)
    QMUIAlphaButton qmuibt_confrim_transfer;

    //转入
    @BindView(R.id.ll_in)
    LinearLayout ll_in;
    @BindView(R.id.tv_coins_amount)
    TextView tv_coin_amount;
    @BindView(R.id.et_amount)
    EditText et_amount;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //转出
    @BindView(R.id.ll_out)
    LinearLayout ll_out;
    @BindView(R.id.tv_add_address)
    ImageView iv_add_address;
    @BindView(R.id.tv_note)
    TextView tv_note;
    @BindView(R.id.tv_select_address)
    TextView tv_select_address;
    @BindView(R.id.tv_fee_out)
    TextView tv_fee_out;
    @BindView(R.id.tv_actual_out)
    TextView tv_actual_out;
    @BindView(R.id.rb_email_mode)
    RadioButton rb_email_mode;
    @BindView(R.id.rb_phone_mode)
    RadioButton rb_phone_mode;
    @BindView(R.id.btn_email_code)
    Button btn_email_code;
    @BindView(R.id.btn_phone_code)
    Button btn_phone_code;
    @BindView(R.id.et_mode_code)
    EditText et_mode_code;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.et_amount_out)
    EditText et_amount_out;
    @BindView(R.id.et_google_code)
    EditText et_google_code;
    @BindView(R.id.rl_address)
    RelativeLayout rl_address;
    @BindView(R.id.ll_available)
    LinearLayout ll_available;

    //2：手机  1：邮箱
    private int type = -1;
    //1：转入  2：转出
    private int flags = 1;
    private int currencyId;
    //显示位数
    private int EXCHANGE_DIGIT = 4;
    private String note;
    private String account;
    private String currencyNameEn = "USDT";
    private TransferInfo transferInfo;
    private TransferPopWindow mSpinerPopWindow;

    //推荐
    private List<MarketNewModel.TradeCoinsBean> list;
    private RecommendAdapter adapter;

    public static TransferOTCFragment newInstance(int currencyId, String currencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        TransferOTCFragment fragment = new TransferOTCFragment();
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

        initCache();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_transfer;
    }

    @Override
    protected void initData() {
        setTextView();
        initCache();

        exchangeEditText();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            upload();
        }
    }

    @Override
    protected void initLazy() {
        super.initLazy();
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

    private void upload() {
        mPresenter.customerCoinByOneCoin(currencyId);
    }

    @Override
    protected TransferOTCPresenter createPresenter(TransferOTCPresenter mPresenter) {
        return new TransferOTCPresenter();
    }

    private void transferIn() {
        if (transferInfo != null && transferInfo.swittchInfo.in == 2) {
            return;
        }
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);

        String amount = et_amount.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            et_amount.startAnimation(shake);
            return;
        }
        String titleString = getResources().getString(R.string.epay_hint);
        new DroidDialog.Builder(getContext())
                .content(titleString)
                .positiveButton(getResources().getString(R.string.navigate), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        droidDialog.dismiss();

                        mPresenter.transIn(currencyId, currencyNameEn, et_amount.getText().toString().trim());
                    }
                })
                .contentGravity(Gravity.CENTER)
                .show();
    }

    private void transferOut() {
        if (transferInfo != null && transferInfo.swittchInfo.out == 2) {
            return;
        }

        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);

        String amount = et_amount_out.getText().toString().trim();
        String tradePw = et_pwd.getText().toString().trim();
        String code = et_mode_code.getText().toString().trim();
        String gAuth = et_google_code.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            showToastError(getResources().getString(R.string.epay_account));
            return;
        }
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
        if (TextUtils.isEmpty(gAuth)) {
            et_google_code.startAnimation(shake);
            return;
        }
        mPresenter.transOut(amount, account, currencyId, currencyNameEn, Md5Utils.encryptFdPwd(tradePw, spUtil.getUserUid()), code, gAuth);
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
     * 转入
     */
    @OnClick(R.id.tv_mode_email)
    void modeEmail() {
        flags = 1;
        setTextView();
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

    //===转出页面监听====
    @OnClick(R.id.tv_all_out)
    void allOut() {
        et_amount_out.setText(BigDecimal.valueOf(transferInfo.userInfo.balance).toPlainString());
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
     * 选择提币地址
     */
    @OnClick(R.id.rl_address)
    void setTvSelectAddress() {
        if (mSpinerPopWindow != null) {
            mSpinerPopWindow.setWidth(rl_address.getWidth());
            mSpinerPopWindow.showAsDropDown(rl_address, 0, 0);
            setBackGroundAlpha(0.5f);
            mSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackGroundAlpha(1f);
                }
            });
        } else {
            upload();
        }
    }

    /**
     * 注册Epay
     */
    @OnClick(R.id.tv_epay_register)
    void epayRegister() {
        jumpTo(WebviewActivity.createActivity(getContext(), ApiHost.EPAY_REGISTER, false, null));
    }

    @Override
    public void transIn(EPayParam ePayParam) {
//        etAmount.setText("");
//        tv_transfer_fee.setText("--");
//        tv_coin_amount.setText("--");

        jumpTo(WebviewActivity.createActivity(getContext(), "", false, ePayParam));
    }

    @Override
    public void transOut() {
        et_amount_out.setText("");
        et_pwd.setText("");
        et_mode_code.setText("");
        et_google_code.setText("");
        tv_fee_out.setText("--");
        tv_coin_amount.setText("--");
        showToastSuccess(getResources().getString(R.string.epay_success));

        upload();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        note = transferInfo.userInfo.accountList.get(position).note;
        account = transferInfo.userInfo.accountList.get(position).account;

        tv_note.setText(note);
        tv_select_address.setText(account);
        iv_add_address.setImageResource(R.drawable.icon_expand_grey);

        mSpinerPopWindow.dismiss();
    }

    private void isCheckbox(boolean isCheck) {
        rb_email_mode.setChecked(!isCheck);
        rb_phone_mode.setChecked(isCheck);

        btn_email_code.setVisibility(isCheck == false ? View.VISIBLE : View.GONE);
        btn_phone_code.setVisibility(isCheck == true ? View.VISIBLE : View.GONE);

        if (isCheck) {
            et_mode_code.setHint(getResources().getString(R.string.phone_code));
        } else {
            et_mode_code.setHint(getResources().getString(R.string.email_hint_code));
        }
    }

    private void setTextView() {
        if (flags == 1) {
            setTextColor(tv_mode_email, R.color.text_blue_2);
            setTextColor(tv_mode_phone, R.color.text_color);
            tv_hint.setText(getResources().getString(R.string.transfer_hint2));
        } else {
            setTextColor(tv_mode_email, R.color.text_color);
            setTextColor(tv_mode_phone, R.color.text_blue_2);
            tv_hint.setText(getResources().getString(R.string.transfer_hint3));
        }
        setButtonBackground();
        setViewColor(view_email);
        setViewColor(view_phone);
        ll_in.setVisibility(flags == 1 ? View.VISIBLE : View.GONE);
        ll_out.setVisibility(flags == 2 ? View.VISIBLE : View.GONE);
        view_email.setVisibility(flags == 1 ? View.VISIBLE : View.GONE);
        view_phone.setVisibility(flags == 2 ? View.VISIBLE : View.GONE);
        ll_available.setVisibility(flags == 2 ? View.VISIBLE : View.GONE);
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

    @Override
    public void success() {
        showToastSuccess(getString(R.string.send_successful));
        if (type == 1) {
            timerEmail.start();
        } else if (type == 2) {
            timer.start();
        }
    }

    @Override
    public void addAccount() {
        showToastSuccess(getResources().getString(R.string.add_success));
        upload();
    }

    @Override
    public void customerCoinByOneCoin(TransferInfo model) {
        if (model == null) {
            return;
        }
        this.transferInfo = model;

        setButtonBackground();
        tv_coin_amount.setText(BigDecimal.valueOf(model.userInfo.balance).toPlainString() + currencyNameEn);
        mSpinerPopWindow = new TransferPopWindow(getContext(), model.userInfo, this, mPresenter);

        if (model.swittchInfo.in == 1) {
            if (model.configInfo.in.feeType == 1) {
                tv_transfer_fee.setText(BigDecimal.valueOf(model.configInfo.in.fee).toPlainString());
            } else if (model.configInfo.in.feeType == 2) {
                tv_transfer_fee.setText(MathHelper.mul(model.configInfo.in.fee) + "%");
            }
            et_amount.setHint(getResources().getString(R.string.transfer_num) + FormatterUtils.getFormatValue(model.configInfo.in.minCount) + currencyNameEn);
        }
        if (model.swittchInfo.out == 1) {
            if (model.configInfo.out.feeType == 1) {
                tv_fee_out.setText(BigDecimal.valueOf(model.configInfo.out.fee).toPlainString());
            } else if (model.configInfo.out.feeType == 2) {
                tv_fee_out.setText(MathHelper.mul(model.configInfo.out.fee) + "%");
            }
            et_amount_out.setHint(getResources().getString(R.string.transfer_num) + FormatterUtils.getFormatValue(model.configInfo.out.minCount) + currencyNameEn);
        }
    }

    private void btBackground(boolean boo, int drawable) {
        qmuibt_confrim_transfer.setChangeAlphaWhenPress(boo);
        qmuibt_confrim_transfer.setBackgroundDrawable(getResources().getDrawable(drawable));
    }

    private void setButtonBackground() {
        if (transferInfo == null || transferInfo.swittchInfo == null) {
            return;
        }
        if ((flags == 1 && transferInfo.swittchInfo.in == 2) || (flags == 2 && transferInfo.swittchInfo.out == 2)) {
            btBackground(false, R.drawable.shape_grey_soild_5);
        } else if ((flags == 1 && transferInfo.swittchInfo.in == 1) || (flags == 2 && transferInfo.swittchInfo.out == 1)) {
            btBackground(true, R.drawable.gradient_blue_btn_5);
        }
    }

    private String amount;
    private double actual;
    private String amountOut;
    private double actualOut;

    private void exchangeEditText() {
        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setDigit(s, et_amount);
            }

            @Override
            public void afterTextChanged(Editable ss) {
                try {
                    //实际到账
                    if (transferInfo != null && transferInfo.configInfo != null) {
                        amount = et_amount.getText().toString().trim();
                        if (TextUtils.isEmpty(amount)) {
                            tv_actual.setText("--");
                        } else if (transferInfo.configInfo.in.feeType == 1) {
                            actual = MathHelper.sub(Double.valueOf(amount), transferInfo.configInfo.in.fee);
                            actual(tv_actual, actual);
                        } else if (transferInfo.configInfo.in.feeType == 2) {
                            actual = MathHelper.sub(Double.valueOf(amount), MathHelper.mul(Double.valueOf(amount), transferInfo.configInfo.in.fee));
                            actual(tv_actual, actual);
                        }
                    }
                } catch (Exception e) {

                }
            }
        });

        et_amount_out.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setDigit(s, et_amount_out);
            }

            @Override
            public void afterTextChanged(Editable ss) {
                try {
                    //实际到账
                    if (transferInfo != null && transferInfo.configInfo != null) {
                        amountOut = et_amount_out.getText().toString().trim();
                        if (TextUtils.isEmpty(amountOut)) {
                            tv_actual_out.setText("--");
                        } else if (transferInfo.configInfo.out.feeType == 1) {
                            actualOut = MathHelper.sub(Double.valueOf(amountOut), transferInfo.configInfo.out.fee);
                            actual(tv_actual_out, actualOut);
                        } else if (transferInfo.configInfo.out.feeType == 2) {
                            actualOut = MathHelper.sub(Double.valueOf(amountOut), MathHelper.mul(Double.parseDouble(amountOut), transferInfo.configInfo.out.fee));
                            actual(tv_actual_out, actualOut);
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void actual(TextView tv_actual, double handFee) {
        if (handFee < 0) {
            tv_actual.setText("0.0");
        } else {
            tv_actual.setText(BigDecimal.valueOf(handFee).toPlainString());
        }
    }

    private void setDigit(CharSequence s, EditText et) {
        try {
            //设置小数位小于4
            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > EXCHANGE_DIGIT) {
                    s = s.toString().subSequence(0, s.toString().indexOf(".") + EXCHANGE_DIGIT + 1);
                    et.setText(s);
                    et.setSelection(s.length());
                }
            }
            if (s.toString().trim().substring(0).equals(".")) {
                s = "0" + s;
                et.setText(s);
                et.setSelection(2);
            }
            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    et.setText(s.subSequence(0, 1));
                    et.setSelection(1);
                    return;
                }
            }
        } catch (Exception e) {

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
}
