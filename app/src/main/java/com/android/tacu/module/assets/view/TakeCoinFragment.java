package com.android.tacu.module.assets.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.CoinsContract;
import com.android.tacu.module.assets.model.SelectTakeCoinAddressModel;
import com.android.tacu.module.assets.presenter.CoinsPresenter;
import com.android.tacu.module.main.view.ZXingCommonActivity;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.utils.ClipboardUtil;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.ZXingUtils;
import com.android.tacu.widget.popupwindow.SpinerPopWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/22.
 * <p>
 * 提币
 */

public class TakeCoinFragment extends BaseFragment<CoinsPresenter> implements CoinsContract.ITakeCoinView, BaseQuickAdapter.OnItemClickListener {

    private static final int REQUEST_CODE_QRCODE = 321;

    @BindView(R.id.tv_select_address)
    EditText etSelectAddress;
    @BindView(R.id.tv_label_address)
    TextView tvLabelAddress;
    @BindView(R.id.tv_note)
    EditText etNote;
    @BindView(R.id.btn_email_code)
    Button btnEmailCode;
    @BindView(R.id.btn_phone_code)
    Button btnPhoneCode;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_actual)
    TextView tvActual;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.et_google_code)
    EditText etGoogleCode;
    @BindView(R.id.et_email_code)
    EditText etEmailCode;
    @BindView(R.id.et_pwd)
    QMUIRoundEditText etPwd;
    @BindView(R.id.cb_email_mode)
    RadioButton cbEmailMode;
    @BindView(R.id.cb_phone_mode)
    RadioButton cbPhoneMode;
    @BindView(R.id.ll_add_address)
    View llAddAddress;
    @BindView(R.id.tv_add_address)
    TextView tvAddAddress;
    @BindView(R.id.ll_payment)
    LinearLayout llPayment;
    @BindView(R.id.et_paymentid)
    EditText etPaymentId;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.ll_step_one)
    LinearLayout ll_step_one;
    @BindView(R.id.ll_step_two)
    LinearLayout ll_step_two;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_at_least_mount)
    TextView tvAtLeastMount;

    private int type = 1;

    private String tempAddressString;
    private String tempNoteString;
    private int currencyId;
    private String currencyNameEn;
    private boolean isFlag = false;

    private Animation shake;
    private RecommendAdapter adapter;
    private SpinerPopWindow mSpinerPopWindow;
    private SelectTakeCoinAddressModel attachment;
    private List<MarketNewModel.TradeCoinsBean> list;
    private List<SelectTakeCoinAddressModel.RespBean.AddressListBean> addressModel;

    public static TakeCoinFragment newInstance(int currencyId, String currencyNameEn, boolean isFlag) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putBoolean("isFlag", isFlag);
        TakeCoinFragment fragment = new TakeCoinFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upLoad();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
            isFlag = bundle.getBoolean("isFlag");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_take_coin;
    }

    /**
     * spUtil.getGaStatus():0未开启 1是开启 2是关闭
     */
    @Override
    protected void initData(View view) {
        isShow(true);
        initListener();
        initCache();

        //选择验证方式
        if (CommonUtils.isBindMode() == 1 || CommonUtils.isBindMode() == 3) {
            type = 2;
            isCheckbox(true);
        } else if (CommonUtils.isBindMode() == 2) {
            type = 1;
            isCheckbox(false);
        }
        CommonUtils.handleEditTextEyesIssueInBrightBackground(etPwd);
        etNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                detectAddressByNote();
            }
        });

    }

    private void detectAddressByNote() {
        if (this.addressModel != null) {
            String note = etNote.getText().toString();
            String address = null;
            for (SelectTakeCoinAddressModel.RespBean.AddressListBean model : this.addressModel) {
                if (TextUtils.equals(model.note, note)) {
                    address = model.address;
                    break;
                }
            }
            if (address != null) {
                etSelectAddress.setText(address);
            }
        }

    }

    @Override
    protected CoinsPresenter createPresenter(CoinsPresenter mPresenter) {
        return new CoinsPresenter();
    }

    @Override
    protected void onPresenterCreated(CoinsPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        if (isVisibleToUser && isFlag) {
            upLoad();
        }
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

    public void updateUI(int currencyId, String currencyNameEn) {
        this.currencyId = currencyId;
        this.currencyNameEn = currencyNameEn;

        if (isVisibleToUser) {
            upLoad();
        }

        initCache();
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

    private void upLoad() {
        mPresenter.selectTakeCoin(currencyId);
    }

    /**
     * =======页面一监听======
     */

    @OnClick(R.id.btn_scan_qr_code)
    void onQrCodeClicked() {
        ZXingUtils.start(this, REQUEST_CODE_QRCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_QRCODE:

                if (data == null || TakeCoinFragment.this.isDetached()) {
                    return;
                }

                if (resultCode == Activity.RESULT_OK) {
                    String qrCode = data.getStringExtra(ZXingCommonActivity.RESULT_CODE_ENTITY);
                    etSelectAddress.setText(qrCode);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    String errorMsg = data.getStringExtra(ZXingCommonActivity.RESULT_CODE_ERROR);
                    if (errorMsg != null)
                        showToastError(errorMsg);
                }


                break;
            default:
                break;
        }

    }

    @OnClick(R.id.btn_address_paste)
    void onAddressPasteClicked() {
        etSelectAddress.setText(ClipboardUtil.getPasteContent(getContext()));
    }

    /**
     * 选择提币地址
     */
    @OnClick(R.id.ll_add_address)
    void invokeSelectedAddress() {
        if (mSpinerPopWindow != null) {
            mSpinerPopWindow.setWidth(etNote.getWidth());
            mSpinerPopWindow.showAsDropDown(tvLabelAddress, 0, 0);
            tvAddAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_btn_arrow_down, 0);
            mSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    tvAddAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_btn_arrow_right, 0);
                }
            });
        } else {
            upLoad();
        }
    }

    /**
     * 全部
     */
    @OnClick(R.id.tv_amount_all)
    void allAmount() {
        if (attachment != null) {
            etAmount.setText(BigDecimal.valueOf(attachment.resp.cashAmount).toPlainString());
        }
    }

    /**
     * 下一步
     */
    @OnClick(R.id.bt_step)
    void submit() {
        shake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);
        tempAddressString = etSelectAddress.getText().toString();
        String amount = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(tempAddressString)) {
            showToastError(getResources().getString(R.string.please_select_address));
            return;
        }

        if (TextUtils.isEmpty(amount)) {
            etAmount.startAnimation(shake);
            return;
        }

        isShow(false);
    }

    /**
     * =======页面二监听======
     */
    @OnClick(R.id.cb_email_mode)
    void setEtEmailCode() {
        if (!spUtil.getEmailStatus()) {
            isCheckbox(true);
            jumpTo(BindModeActivity.createActivity(getContext(), 4));
            return;
        }

        type = 1;
        isCheckbox(false);
    }

    @OnClick(R.id.cb_phone_mode)
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

    @OnClick(R.id.bt_above)
    void above() {
        isShow(true);
    }


    /**
     * 提幣
     */
    @OnClick(R.id.bt_two_submit)
    void submitTwo() {
        String pwd = etPwd.getText().toString().trim();
        String emailCode = etEmailCode.getText().toString().trim();

        String gAuth = etGoogleCode.getText().toString().trim();
        String paymentId = etPaymentId.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(tempAddressString)) {
            showToastError(getResources().getString(R.string.please_select_address));
            return;
        }

        if (TextUtils.isEmpty(amount)) {
            etAmount.startAnimation(shake);
            return;
        }

        if (TextUtils.isEmpty(emailCode)) {
            etEmailCode.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(gAuth)) {
            etGoogleCode.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            etPwd.startAnimation(shake);
            return;
        }
        mPresenter.takeCoin("4", tempAddressString, amount, currencyId, Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase(), tempNoteString, emailCode, gAuth, "0".equals(attachment.resp.msgCode) ? paymentId : "".equals(paymentId) ? "" : "none");
    }

    private void initListener() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initActual(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initActual(CharSequence s) {
        try {
            if (attachment != null) {
                String value = "--";

                if (TextUtils.isEmpty(s.toString())) {
                    tvActual.setText("--");
                } else if (attachment.detail.feeType == 1) {
                    value = BigDecimal.valueOf(MathHelper.sub(Double.valueOf(s.toString()), attachment.resp.fee)).toPlainString();
                } else if (attachment.detail.feeType == 2) {
                    value = BigDecimal.valueOf(MathHelper.sub(Double.valueOf(s.toString()), MathHelper.mul(Double.valueOf(s.toString()), attachment.resp.fee))).toPlainString();
                }
                value += " " + currencyNameEn;
                tvActual.setText(value);
            }
        } catch (Exception e) {
        }
    }

    private void isCheckbox(boolean isCheck) {
        cbEmailMode.setChecked(!isCheck);
        cbPhoneMode.setChecked(isCheck);

        btnEmailCode.setVisibility(isCheck == false ? View.VISIBLE : View.GONE);
        btnPhoneCode.setVisibility(isCheck == true ? View.VISIBLE : View.GONE);

        if (isCheck) {
            etEmailCode.setHint(getResources().getString(R.string.phone_code));
        } else {
            etEmailCode.setHint(getResources().getString(R.string.email_hint_code));
        }
    }

    public boolean hackBackPress() {
        if (ll_step_two.getVisibility() == View.VISIBLE) {
            above();
            return true;
        }
        return false;
    }

    private void isShow(boolean boo) {
        ll_step_one.setVisibility(boo == true ? View.VISIBLE : View.GONE);
        ll_step_two.setVisibility(boo == false ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        EventManage.sendEvent(new BaseEvent<>(EventConstant.TakeCode, boo));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        tempAddressString = addressModel.get(position).address;
        tempNoteString = addressModel.get(position).note;
        etSelectAddress.setText(addressModel.get(position).address);
        etNote.setText(addressModel.get(position).note);
        mSpinerPopWindow.dismiss();
    }

    @Override
    public void delCoinAddress() {
        mPresenter.selectTakeCoin(currencyId);
        mSpinerPopWindow.dismiss();
    }

    @Override
    public void showCoinListAddress(SelectTakeCoinAddressModel attachment) {
        this.attachment = attachment;
        this.addressModel = attachment.resp.addressList;

        if (TextUtils.equals(attachment.resp.msgCode, "0")) {
            llPayment.setVisibility(View.VISIBLE);
        } else {
            llPayment.setVisibility(View.GONE);
        }
        etPaymentId.setHint(attachment.resp.msgName);
        String sAgeFormat = getResources().getString(R.string.paymentid);
        String sFinal = String.format(sAgeFormat, attachment.resp.msgName);
        tvId.setText(sFinal);

        mSpinerPopWindow = new SpinerPopWindow(getContext(), attachment, this, mPresenter);

        tvAmount.setText(BigDecimal.valueOf(attachment.resp.cashAmount).toPlainString() + " " + currencyNameEn);

        if (attachment.detail.feeType == 1) {
            tvFee.setText(BigDecimal.valueOf(attachment.resp.fee).toPlainString());
        } else if (attachment.detail.feeType == 2) {
            tvFee.setText(MathHelper.mul(attachment.resp.fee) + "%");
        }
        String amount = FormatterUtils.getFormatValue(attachment.detail.amountLowLimit);
        etAmount.setHint(getResources().getString(R.string.min_extract) + amount);
        tvAtLeastMount.setText(amount);
        if (!TextUtils.isEmpty(etAmount.getText().toString().trim())) {
            initActual(etAmount.getText().toString().trim());
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
    public void takeCoinSuccess() {
        isShow(true);
        showToastSuccess(getResources().getString(R.string.success));

        upLoad();

        etPwd.setText("");
        etAmount.setText("");
        etPaymentId.setText("");
        etEmailCode.setText("");
        etGoogleCode.setText("");
        tvAmount.setText("");
        etSelectAddress.setText("");
//        tvAddAddress.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_black));
    }

    @Override
    public void addressSuccess() {
        showToastSuccess(getResources().getString(R.string.add_success));
        upLoad();
    }

    /**
     * 设置页面透明度，1表示不透明
     *
     * @param backGroundAlpha
     */


    private CountDownTimer timerEmail = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long arg0) {
            try {
                // 定期定期回调
                btnEmailCode.setText((arg0 / 1000) + "s");
                btnEmailCode.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            try {
                btnEmailCode.setEnabled(true);
                // 结束后回到
                btnEmailCode.setText(getString(R.string.resend));
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
                btnPhoneCode.setText((arg0 / 1000) + "s");
                btnPhoneCode.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            try {
                btnPhoneCode.setEnabled(true);
                // 结束后回到
                btnPhoneCode.setText(getString(R.string.resend));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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

            if (item.currentAmount != 0) {
                helper.setText(R.id.tv_situation, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
                helper.setText(R.id.tv_money, "≈" + getMcM(item.baseCurrencyId, item.currentAmount));
            } else {
                helper.setText(R.id.tv_situation, "--");
                helper.setText(R.id.tv_money, "--");
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
