package com.android.tacu.module.vip.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabIndicatorAdapter;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.dialog.OtcPwdDialogUtils;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.vip.contract.RechargeDepositContract;
import com.android.tacu.module.vip.model.SelectBondModel;
import com.android.tacu.module.vip.presenter.RechargeDepositPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundTextView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RechargeDepositActivity extends BaseActivity<RechargeDepositPresenter> implements RechargeDepositContract.IView {

    @BindView(R.id.tv_account_balance)
    TextView tv_account_balance;
    @BindView(R.id.tv_margin_balance)
    TextView tv_margin_balance;
    @BindView(R.id.tv_available_balance)
    TextView tv_available_balance;
    @BindView(R.id.tv_frozen_balance)
    TextView tv_frozen_balance;

    @BindView(R.id.tv_total_account_balance)
    TextView tv_total_account_balance;
    @BindView(R.id.coin_indicatorView)
    ScrollIndicatorView coin_indicatorView;
    @BindView(R.id.tv_total_account)
    TextView tv_total_account;
    @BindView(R.id.type_indicatorView)
    ScrollIndicatorView type_indicatorView;
    @BindView(R.id.tv_tag)
    TextView tv_tag;

    @BindView(R.id.rl_recharge)
    RelativeLayout rl_recharge;
    @BindView(R.id.tv_recharge)
    QMUIRoundTextView tv_recharge;

    @BindView(R.id.lin_take)
    QMUIRoundLinearLayout lin_take;
    @BindView(R.id.edit_take)
    EditText edit_take;
    @BindView(R.id.tv_coinname)
    TextView tv_coinname;
    @BindView(R.id.tv_deposit_take_amount)
    TextView tv_deposit_take_amount;

    @BindView(R.id.btn_ok)
    QMUIRoundButton btn_ok;

    private List<String> tabList = new ArrayList<>();
    private List<String> tabList2 = new ArrayList<>();
    private TabIndicatorAdapter tabIndicatorAdapter;

    //0=币币账户 1=OTC账户 2=C2C账户
    private int accountType = 0;
    //0=保证金充值 1=保证金提取
    private int depositType = 0;

    private double ccValue = 0;
    private double otcValue = 0;
    private double c2cValue = 0;

    private ListPopWindow listPopup;
    private List<SelectBondModel> selectBondModelList = new ArrayList<>();

    private OtcAmountModel bondAmountModel;

    private boolean isFirst = true;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_recharge_deposit);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.recharge_deposit));
        mTopBar.addRightImageButton(R.drawable.icon_deposit_record, R.id.qmui_topbar_item_right, 20, 20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Uri content_url = Uri.parse(Constant.SHOP_APPLY_SYSTEM);
                intent.setAction("android.intent.action.VIEW");
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        tabList.add(getResources().getString(R.string.coin_account));
        tabList.add(getResources().getString(R.string.otc_account));
        //tabList.add(getResources().getString(R.string.c2c_account));
        coin_indicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        coin_indicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        coin_indicatorView.setScrollBar(new TextWidthColorBar(this, coin_indicatorView, ContextCompat.getColor(this, R.color.text_default), 4));
        coin_indicatorView.setSplitAuto(false);
        tabIndicatorAdapter = new TabIndicatorAdapter(this, tabList);
        coin_indicatorView.setAdapter(tabIndicatorAdapter);
        coin_indicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                accountType = select;
                showAccountType();
            }
        });

        tabList2.add(getResources().getString(R.string.deposit_recharge));
        tabList2.add(getResources().getString(R.string.deposit_take));
        type_indicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        type_indicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        type_indicatorView.setScrollBar(new TextWidthColorBar(this, type_indicatorView, ContextCompat.getColor(this, R.color.text_default), 4));
        type_indicatorView.setSplitAuto(false);
        type_indicatorView.setAdapter(new TabIndicatorAdapter(this, tabList2));
        type_indicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                depositType = select;
                showDepositType();
            }
        });

        tv_coinname.setText(Constant.ACU_CURRENCY_NAME);

        showDepositType();
    }

    @Override
    protected RechargeDepositPresenter createPresenter(RechargeDepositPresenter mPresenter) {
        return new RechargeDepositPresenter();
    }

    @Override
    protected void onPresenterCreated(RechargeDepositPresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.selectBond();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload(isFirst);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
        }
    }

    @OnClick(R.id.btn_record)
    void btnRecordClick() {
        jumpTo(DepositRecordActivity.class);
    }

    @OnClick(R.id.rl_recharge)
    void rechargeClick() {
        showBondList();
    }

    @OnClick(R.id.btn_all)
    void btnAllClick() {
        if (bondAmountModel != null) {
            if (!TextUtils.isEmpty(bondAmountModel.cashAmount)) {
                edit_take.setText(FormatterUtils.getFormatValue(bondAmountModel.cashAmount));
            } else {
                edit_take.setText("0");
            }
        } else {
            edit_take.setText("0");
        }
    }

    @OnClick(R.id.btn_ok)
    void btnOkClick() {
        if (!OtcTradeDialogUtils.isDialogShow(this)) {
            String inputString = null;
            if (depositType == 0) {
                inputString = tv_recharge.getText().toString().replace(Constant.ACU_CURRENCY_NAME, "");
            } else if (depositType == 1) {
                inputString = edit_take.getText().toString().trim();
            }
            if (TextUtils.isEmpty(inputString)) {
                if (depositType == 0) {
                    showToastError(getResources().getString(R.string.please_choose_deposit_amount));
                } else if (depositType == 1) {
                    showToastError(getResources().getString(R.string.please_input_deposit_amount));
                }
                return;
            }
            if (spUtil.getPwdVisibility()) {
                showDialog();
            } else {
                dealSubmit(null);
            }
        }
    }

    @Override
    public void customerCoinByOneCoin(Double value) {
        if (value == null) {
            value = 0d;
        }
        ccValue = value;
        dealAmount();
    }

    @Override
    public void BondAccount(OtcAmountModel model) {
        this.bondAmountModel = model;
        if (model != null) {
            if (!TextUtils.isEmpty(model.amount)) {
                tv_account_balance.setText(FormatterUtils.getFormatValue(model.amount) + Constant.ACU_CURRENCY_NAME);
                tv_margin_balance.setText(FormatterUtils.getFormatValue(model.amount) + Constant.ACU_CURRENCY_NAME);
            } else {
                tv_account_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
                tv_margin_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            }
            if (!TextUtils.isEmpty(model.cashAmount)) {
                tv_available_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + Constant.ACU_CURRENCY_NAME);
                tv_deposit_take_amount.setText(getResources().getString(R.string.deposit_take_amount) + FormatterUtils.getFormatValue(model.cashAmount) + Constant.ACU_CURRENCY_NAME);
            } else {
                tv_available_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
                tv_deposit_take_amount.setText(getResources().getString(R.string.deposit_take_amount) + "0" + Constant.ACU_CURRENCY_NAME);
            }

            Double value1 = (!TextUtils.isEmpty(model.freezeAmount)) ? Double.valueOf(model.freezeAmount) : 0;
            Double value2 = (!TextUtils.isEmpty(model.bondFreezeAmount)) ? Double.valueOf(model.bondFreezeAmount) : 0;
            tv_frozen_balance.setText(FormatterUtils.getFormatValue(MathHelper.add(value1, value2)) + Constant.ACU_CURRENCY_NAME);
        } else {
            tv_account_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            tv_margin_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            tv_available_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            tv_frozen_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            tv_deposit_take_amount.setText(getResources().getString(R.string.deposit_take_amount) + "0" + Constant.ACU_CURRENCY_NAME);
        }
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        if (model != null && !TextUtils.isEmpty(model.cashAmount)) {
            try {
                otcValue = Double.parseDouble(model.cashAmount);
                dealAmount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void c2cAmount(OtcAmountModel model) {
        if (model != null) {
            if (!TextUtils.isEmpty(model.cashAmount)) {
                try {
                    c2cValue = Double.parseDouble(model.cashAmount);
                    dealAmount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if ((model != null && !TextUtils.isEmpty(model.amount) && Double.parseDouble(model.amount) != 0) || isMerchant()) {
            if (!tabList.contains(getResources().getString(R.string.c2c_account))) {
                tabList.add(getResources().getString(R.string.c2c_account));
                tabIndicatorAdapter.setTabTitle(tabList);
            }
        } else {
            if (tabList.contains(getResources().getString(R.string.c2c_account))) {
                tabList.remove(getResources().getString(R.string.c2c_account));
                tabIndicatorAdapter.setTabTitle(tabList);
            }
        }
    }

    @Override
    public void selectBond(List<SelectBondModel> list) {
        this.selectBondModelList = list;
        if (list != null && list.size() > 0) {
            tv_recharge.setText(FormatterUtils.getFormatValue(list.get(0).num) + Constant.ACU_CURRENCY_NAME);
        }
    }

    @Override
    public void CcToBondSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        upload(true);
    }

    @Override
    public void BondToCcSuccess() {
        edit_take.setText("");
        showToastSuccess(getResources().getString(R.string.success));
        upload(true);
    }

    @Override
    public void otcToBondSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        upload(true);
    }

    @Override
    public void BondToOtcSuccess() {
        edit_take.setText("");
        showToastSuccess(getResources().getString(R.string.success));
        upload(true);
    }

    @Override
    public void c2cToBondSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        upload(true);
    }

    @Override
    public void BondToC2cSuccess() {
        edit_take.setText("");
        showToastSuccess(getResources().getString(R.string.success));
        upload(true);
    }

    private void upload(boolean isShowView) {
        if (isFirst) {
            isFirst = false;
        }
        mPresenter.customerCoinByOneCoin(isShowView, Constant.ACU_CURRENCY_ID);
        mPresenter.otcAmount(isShowView, Constant.ACU_CURRENCY_ID);
        mPresenter.c2cAmount(isShowView, Constant.ACU_CURRENCY_ID);
        mPresenter.BondAccount(isShowView, Constant.ACU_CURRENCY_ID);
    }

    private void showAccountType() {
        if (accountType == 0) {
            tv_total_account.setText(FormatterUtils.getFormatValue(ccValue) + Constant.ACU_CURRENCY_NAME);
        } else if (accountType == 1) {
            tv_total_account.setText(FormatterUtils.getFormatValue(otcValue) + Constant.ACU_CURRENCY_NAME);
        } else if (accountType == 2) {
            tv_total_account.setText(FormatterUtils.getFormatValue(c2cValue) + Constant.ACU_CURRENCY_NAME);
        }
        dealValue();
    }

    private void showDepositType() {
        if (depositType == 0) {
            rl_recharge.setVisibility(View.VISIBLE);
            lin_take.setVisibility(View.GONE);
            tv_deposit_take_amount.setVisibility(View.GONE);
            btn_ok.setText(getResources().getString(R.string.confirm_recharge));
        } else if (depositType == 1) {
            rl_recharge.setVisibility(View.GONE);
            lin_take.setVisibility(View.VISIBLE);
            tv_deposit_take_amount.setVisibility(View.VISIBLE);
            btn_ok.setText(getResources().getString(R.string.submit_apply));
        }
        dealValue();
    }

    private void dealValue() {
        if (accountType == 0 && depositType == 0) {
            tv_tag.setText(getResources().getString(R.string.recharge_deposit_tag1));
        } else if (accountType == 0 && depositType == 1) {
            tv_tag.setText(getResources().getString(R.string.recharge_deposit_tag3));
        } else if (accountType == 1 && depositType == 0) {
            tv_tag.setText(getResources().getString(R.string.recharge_deposit_tag2));
        } else if (accountType == 1 && depositType == 1) {
            tv_tag.setText(getResources().getString(R.string.recharge_deposit_tag4));
        } else if (accountType == 2 && depositType == 0) {
            tv_tag.setText(getResources().getString(R.string.recharge_deposit_tag5));
        } else if (accountType == 2 && depositType == 1) {
            tv_tag.setText(getResources().getString(R.string.recharge_deposit_tag6));
        }
    }

    private void dealAmount() {
        tv_total_account_balance.setText(FormatterUtils.getFormatValue(MathHelper.add(ccValue, MathHelper.add(otcValue, c2cValue))) + Constant.ACU_CURRENCY_NAME);
        showAccountType();
    }

    private void dealSubmit(String fdPassword) {
        if (accountType == 0 && depositType == 0) {
            mPresenter.CcToBond(tv_recharge.getText().toString().replace(Constant.ACU_CURRENCY_NAME, ""), Constant.ACU_CURRENCY_ID, fdPassword);
        } else if (accountType == 0 && depositType == 1) {
            mPresenter.BondToCc(edit_take.getText().toString().trim(), Constant.ACU_CURRENCY_ID, fdPassword);
        } else if (accountType == 1 && depositType == 0) {
            mPresenter.otcToBond(tv_recharge.getText().toString().replace(Constant.ACU_CURRENCY_NAME, ""), Constant.ACU_CURRENCY_ID, fdPassword);
        } else if (accountType == 1 && depositType == 1) {
            mPresenter.bondToOtc(edit_take.getText().toString().trim(), Constant.ACU_CURRENCY_ID, fdPassword);
        } else if (accountType == 2 && depositType == 0) {
            mPresenter.c2cToBond(tv_recharge.getText().toString().replace(Constant.ACU_CURRENCY_NAME, ""), Constant.ACU_CURRENCY_ID, fdPassword);
        } else if (accountType == 2 && depositType == 1) {
            mPresenter.bondToC2c(edit_take.getText().toString().trim(), Constant.ACU_CURRENCY_ID, fdPassword);
        }
    }

    private void showBondList() {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (selectBondModelList == null || selectBondModelList.size() <= 0) {
            return;
        }
        final List<String> data = new ArrayList<>();
        for (int i = 0; i < selectBondModelList.size(); i++) {
            data.add(FormatterUtils.getFormatValue(selectBondModelList.get(i).num) + Constant.ACU_CURRENCY_NAME);
        }

        int height = 0;
        int itemHeight = UIUtils.dp2px(40);
        if (data.size() == 1) {
            height = itemHeight;
        } else {
            for (int i = 0; i < data.size(); i++) {
                height += itemHeight;
            }
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        listPopup = new ListPopWindow(this, adapter);
        listPopup.create(UIUtils.getScreenWidth() - UIUtils.dp2px(30), height, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_recharge.setText(data.get(position));
                listPopup.dismiss();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.END);
        }
        listPopup.setAnchorView(tv_recharge);
        listPopup.show();
    }


    private void showDialog() {
        OtcPwdDialogUtils.showPwdDiaglog(this, getResources().getString(R.string.trade_password), new OtcPwdDialogUtils.OnPassListener() {
            @Override
            public void onPass(String pwd) {
                dealSubmit(Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase());
            }
        });
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
