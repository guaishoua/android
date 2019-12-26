package com.android.tacu.module.assets.view;

import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.LockProfitContract;
import com.android.tacu.module.assets.model.CashChexAndRateModel;
import com.android.tacu.module.assets.model.LockChexAmountModel;
import com.android.tacu.module.assets.presenter.LockProfitPresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import java.math.BigDecimal;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class LockProfitActivity extends BaseActivity<LockProfitPresenter> implements LockProfitContract.IView {

    @BindView(R.id.tv_lock_profit)
    TextView tv_lock_profit;
    @BindView(R.id.tv_yourchex_num)
    TextView tv_yourchex_num;
    @BindView(R.id.tv_have_profit)
    TextView tv_have_profit;
    @BindView(R.id.tv_prediction_profit_chex)
    TextView tv_prediction_profit_chex;
    @BindView(R.id.tv_use_chex)
    TextView tv_use_chex;
    @BindView(R.id.rdGroup)
    RadioGroup rdGroup;
    @BindView(R.id.rb_12)
    RadioButton rb_12;
    @BindView(R.id.rb_6)
    RadioButton rb_6;
    @BindView(R.id.edit_input_lock_num)
    EditText edit_input_lock_num;
    @BindView(R.id.edit_pwd)
    QMUIRoundEditText edit_pwd;
    @BindView(R.id.btn_apply)
    QMUIRoundButton btn_apply;
    @BindView(R.id.tv_prediction_profit)
    TextView tv_prediction_profit;
    @BindView(R.id.tv_year_rate)
    TextView tv_year_rate;

    private double rate_12 = 0.12;
    private double rate_6 = 0.06;
    private double currentRate = 0.12;
    private int dateVal = 2;
    private Date startDate;
    private Date endDate;
    private double profit = 0;

    private LockChexAmountModel lockChexAmountModel;
    private CashChexAndRateModel cashChexAndRateModel;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_lock_profit);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.wenxibao));
        mTopBar.addRightTextButton(getResources().getString(R.string.lock_record), R.id.qmui_topbar_item_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(LockRecordActivity.class);
            }
        });

        rdGroup.setOnCheckedChangeListener(listen);
        edit_input_lock_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnApplyIsVis();
                calProfit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnApplyIsVis();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //默认12个月的年利率
        tv_year_rate.setText(String.valueOf(currentRate * 100) + "%");
        calProfit();
    }

    @Override
    protected LockProfitPresenter createPresenter(LockProfitPresenter mPresenter) {
        return new LockProfitPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getLockChexAmount(lockChexAmountModel != null ? false : true);
        mPresenter.getCashChexAndRate(cashChexAndRateModel != null ? false : true);
    }

    @OnClick(R.id.img_describe)
    void describeClick() {
        new DroidDialog.Builder(this)
                .content(getResources().getString(R.string.lock_describe))
                .positiveButton(getResources().getString(R.string.node_know), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        droidDialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.btn_apply)
    void btnApply() {
        if (Integer.parseInt(edit_input_lock_num.getText().toString()) < 10) {
            showToastError(getResources().getString(R.string.apply_locknum_error));
            return;
        }
        if (!spUtil.getValidatePass()) {
            showToastError(getResources().getString(R.string.exchange_pwd));
            return;
        }
        showDialogOk();
    }

    @Override
    public void setLockChexAmount(LockChexAmountModel model) {
        this.lockChexAmountModel = model;
        if (model != null) {
            tv_lock_profit.setText(model.allProfit + " CHEX");
            tv_yourchex_num.setText(getResources().getString(R.string.your_chex_lock_num) + model.lockNum);
            tv_have_profit.setText(model.historyProfit);
            tv_prediction_profit_chex.setText(model.profit);

            rate_6 = Double.parseDouble(model.rate1) / 100;
            rate_12 = Double.parseDouble(model.rate2) / 100;
        }
    }

    @Override
    public void setCashChexAndRate(CashChexAndRateModel model) {
        this.cashChexAndRateModel = model;
        if (model != null) {
            tv_use_chex.setText(model.cashAmount);
        }
    }

    @Override
    public void lockChexSuccess() {
        jumpTo(LockRecordActivity.class);
    }

    private RadioGroup.OnCheckedChangeListener listen = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rb_12:
                    currentRate = rate_12;
                    tv_year_rate.setText(String.valueOf(currentRate * 100) + "%");
                    dateVal = 2;
                    calProfit();
                    break;
                case R.id.rb_6:
                    currentRate = rate_6;
                    tv_year_rate.setText(String.valueOf(currentRate * 100) + "%");
                    dateVal = 1;
                    calProfit();
                    break;
            }
        }
    };

    private void btnApplyIsVis() {
        try {
            if (!TextUtils.isEmpty(edit_input_lock_num.getText().toString().trim()) && !TextUtils.isEmpty(edit_pwd.getText().toString().trim())) {
                btn_apply.setEnabled(true);
                ((QMUIRoundButtonDrawable) btn_apply.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_golden));
            } else {
                btn_apply.setEnabled(false);
                ((QMUIRoundButtonDrawable) btn_apply.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_grey_2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calProfit() {
        try {
            if (TextUtils.isEmpty(edit_input_lock_num.getText().toString().trim())) {
                return;
            }
            startDate = new Date();
            if (dateVal == 1) {//6個月
                endDate = DateUtils.profit6(startDate);
                int day = DateUtils.differentDays(startDate, endDate);
                profit = BigDecimal.valueOf(Integer.parseInt(edit_input_lock_num.getText().toString()) * 10000 * currentRate / 365).setScale(8, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(day)).doubleValue();
            } else if (dateVal == 2) {//12個月
                endDate = DateUtils.profit12(startDate);
                profit = Integer.parseInt(edit_input_lock_num.getText().toString()) * 10000 * currentRate;
            }
            tv_prediction_profit.setText(FormatterUtils.getFormatRoundDown(8, profit));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialogOk() {
        final View view = View.inflate(this, R.layout.view_apply_lock, null);
        TextView tv_apply_locknum = view.findViewById(R.id.tv_apply_locknum);
        TextView tv_lockcycle = view.findViewById(R.id.tv_lockcycle);
        TextView tv_rate = view.findViewById(R.id.tv_rate);
        TextView tv_starttime = view.findViewById(R.id.tv_starttime);
        TextView tv_endtime = view.findViewById(R.id.tv_endtime);
        TextView tv_profit = view.findViewById(R.id.tv_profit);

        tv_apply_locknum.setText(edit_input_lock_num.getText().toString().trim() + getResources().getString(R.string.wan) + "CHEX");
        if (dateVal == 1) {
            tv_lockcycle.setText(getResources().getString(R.string.month_6));
        } else if (dateVal == 2) {
            tv_lockcycle.setText(getResources().getString(R.string.month_12));
        }
        tv_rate.setText(String.valueOf(currentRate * 100) + "%");
        tv_starttime.setText(DateUtils.date2String(startDate, DateUtils.FORMAT_DATE_YMD));
        tv_endtime.setText(DateUtils.date2String(endDate, DateUtils.FORMAT_DATE_YMD));
        tv_profit.setText(FormatterUtils.getFormatRoundDown(8, profit) + "CHEX");

        new DroidDialog.Builder(this)
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String num = edit_input_lock_num.getText().toString().trim();
                        String fdPwd = Md5Utils.encryptFdPwd(edit_pwd.getText().toString().trim(), spUtil.getUserUid());
                        mPresenter.lockChex(num, dateVal, fdPwd);
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                    @Override
                    public void onNegative(Dialog droidDialog) {
                        droidDialog.dismiss();
                    }
                })
                .cancelable(false, false)
                .show();
    }
}
