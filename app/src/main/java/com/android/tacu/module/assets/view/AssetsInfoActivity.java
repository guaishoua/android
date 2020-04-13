package com.android.tacu.module.assets.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.AssetsContract;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.presenter.AssetsPresenter;
import com.android.tacu.module.vip.view.RechargeDepositActivity;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AssetsInfoActivity extends BaseActivity<AssetsPresenter> implements AssetsContract.IAssetsInfoView {

    @BindView(R.id.img_icon)
    ImageView img_icon;
    @BindView(R.id.tv_icon_name)
    TextView tv_icon_name;
    @BindView(R.id.tv_icon_name_full)
    TextView tv_icon_name_full;

    @BindView(R.id.tv_coin_title)
    TextView tv_coin_title;
    @BindView(R.id.tv_coin_available_title)
    TextView tv_coin_available_title;
    @BindView(R.id.tv_coin_frozen_title)
    TextView tv_coin_frozen_title;

    @BindView(R.id.tv_coin)
    TextView tv_coin;
    @BindView(R.id.tv_coin_rnb)
    TextView tv_coin_rnb;
    @BindView(R.id.tv_coin_available)
    TextView tv_coin_available;
    @BindView(R.id.tv_coin_frozen)
    TextView tv_coin_frozen;

    @BindView(R.id.tv_otc_title)
    TextView tv_otc_title;
    @BindView(R.id.tv_otc_available_title)
    TextView tv_otc_available_title;
    @BindView(R.id.tv_otc_frozen_title)
    TextView tv_otc_frozen_title;

    @BindView(R.id.tv_otc)
    TextView tv_otc;
    @BindView(R.id.tv_otc_rnb)
    TextView tv_otc_rnb;
    @BindView(R.id.tv_otc_available)
    TextView tv_otc_available;
    @BindView(R.id.tv_otc_frozen)
    TextView tv_otc_frozen;

    @BindView(R.id.view_c2c)
    ConstraintLayout view_c2c;
    @BindView(R.id.view_line_c2c)
    View view_line_c2c;
    @BindView(R.id.tv_c2c_title)
    TextView tv_c2c_title;
    @BindView(R.id.tv_c2c_available_title)
    TextView tv_c2c_available_title;
    @BindView(R.id.tv_c2c_frozen_title)
    TextView tv_c2c_frozen_title;

    @BindView(R.id.tv_c2c)
    TextView tv_c2c;
    @BindView(R.id.tv_c2c_rnb)
    TextView tv_c2c_rnb;
    @BindView(R.id.tv_c2c_available)
    TextView tv_c2c_available;
    @BindView(R.id.tv_c2c_frozen)
    TextView tv_c2c_frozen;

    @BindView(R.id.rl_account)
    QMUIRoundRelativeLayout rl_account;
    @BindView(R.id.tv_account)
    TextView tv_account;

    //0=OTC账户 1=保证金账户 2=c2c账户 3=otc划转c2c
    private int flag = 0;

    private boolean isFirst = true;
    private AssetDetailsModel.CoinListBean infoModel;
    private OtcAmountModel otcAmountModel;
    private OtcAmountModel c2cAmountModel;
    private ListPopWindow listPopup;

    public static Intent createActivity(Context context, AssetDetailsModel.CoinListBean infoModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoModel", infoModel);
        Intent intent = new Intent(context, AssetsInfoActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_asset_info);
    }

    @Override
    protected void initView() {
        infoModel = (AssetDetailsModel.CoinListBean) getIntent().getSerializableExtra("infoModel");

        dealFlag();
        setValue();
    }

    @Override
    protected AssetsPresenter createPresenter(AssetsPresenter mPresenter) {
        return new AssetsPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
        }
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        this.otcAmountModel = model;
        setOtcValue();
    }

    @Override
    public void c2cAmount(OtcAmountModel model) {
        this.c2cAmountModel = model;
        setC2cValue();
    }

    @OnClick(R.id.rl_account)
    void accountClick() {
        showAccountType();
    }

    @OnClick(R.id.btn_transfer)
    void transferClick() {
        if (flag == 0) {
            if (!isKeyc2()) {
                return;
            }
            if (infoModel != null) {
                jumpTo(AssetsActivity.createActivity(this, infoModel.currencyNameEn, infoModel.currencyId, 2, true));
            }
        } else if (flag == 1) {
            jumpTo(RechargeDepositActivity.class);
        } else if (flag == 2) {
            if (!isKeyc2()) {
                return;
            }
            if (infoModel != null) {
                jumpTo(AssetsActivity.createActivity(this, infoModel.currencyNameEn, infoModel.currencyId, 3, true));
            }
        } else if (flag == 3) {
            if (!isKeyc2()) {
                return;
            }
            if (infoModel != null) {
                jumpTo(AssetsActivity.createActivity(this, infoModel.currencyNameEn, infoModel.currencyId, 4, true));
            }
        }
    }

    private void setValue() {
        if (infoModel != null) {
            GlideUtils.disPlay(this, Constant.SMALL_ICON_URL + infoModel.icoUrl, img_icon);
            tv_icon_name.setText(infoModel.currencyNameEn);
            tv_icon_name_full.setText(String.format("(%s)", infoModel.currencyName));

            tv_coin_title.setText(getResources().getString(R.string.coin_account) + "(" + infoModel.currencyNameEn + ")");
            tv_coin_available_title.setText(getResources().getString(R.string.available_num) + "(" + infoModel.currencyNameEn + ")");
            tv_coin_frozen_title.setText(getResources().getString(R.string.frozen_num) + "(" + infoModel.currencyNameEn + ")");

            tv_coin.setText(BigDecimal.valueOf(infoModel.amount).toPlainString());
            String ycn = getMcM(infoModel.currencyId, infoModel.amount);
            tv_coin_rnb.setText((TextUtils.isEmpty(ycn) ? "" : "≈" + ycn));
            tv_coin_available.setText(BigDecimal.valueOf(infoModel.cashAmount).toPlainString());
            tv_coin_frozen.setText(BigDecimal.valueOf(infoModel.freezeAmount).toPlainString());
        }
    }

    private void setOtcValue() {
        if (otcAmountModel != null) {
            if ((!TextUtils.isEmpty(otcAmountModel.amount) && Double.parseDouble(otcAmountModel.amount) != 0) || isMerchant()) {
                view_c2c.setVisibility(View.VISIBLE);
                view_line_c2c.setVisibility(View.VISIBLE);

                tv_otc_title.setText(getResources().getString(R.string.otc_account) + "(" + otcAmountModel.currencyName + ")");
                tv_otc_available_title.setText(getResources().getString(R.string.available_num) + "(" + otcAmountModel.currencyName + ")");
                tv_otc_frozen_title.setText(getResources().getString(R.string.frozen_num) + "(" + otcAmountModel.currencyName + ")");

                tv_otc.setText(otcAmountModel.amount);
                String ycn = null;
                if (!TextUtils.isEmpty(otcAmountModel.amount)) {
                    ycn = getMcM(otcAmountModel.currencyId, Double.parseDouble(otcAmountModel.amount));
                }
                tv_otc_rnb.setText((TextUtils.isEmpty(ycn) ? "" : "≈" + ycn));
                tv_otc_available.setText(otcAmountModel.cashAmount);

                Double value1 = (!TextUtils.isEmpty(otcAmountModel.freezeAmount)) ? Double.valueOf(otcAmountModel.freezeAmount) : 0;
                Double value2 = (!TextUtils.isEmpty(otcAmountModel.bondFreezeAmount)) ? Double.valueOf(otcAmountModel.bondFreezeAmount) : 0;
                tv_otc_frozen.setText(FormatterUtils.getFormatValue(MathHelper.add(value1, value2)));
            } else {
                view_c2c.setVisibility(View.GONE);
                view_line_c2c.setVisibility(View.GONE);
            }
        } else {
            if (isMerchant()) {
                view_c2c.setVisibility(View.VISIBLE);
                view_line_c2c.setVisibility(View.VISIBLE);

                tv_otc.setText("0.0");
                tv_otc_rnb.setText("");
                tv_otc_available.setText("0.0");
                tv_otc_frozen.setText("0.0");
            } else {
                view_c2c.setVisibility(View.GONE);
                view_line_c2c.setVisibility(View.GONE);
            }
        }
    }

    private void setC2cValue() {
        if (c2cAmountModel != null) {
            tv_c2c_title.setText(getResources().getString(R.string.c2c_account) + "(" + c2cAmountModel.currencyName + ")");
            tv_c2c_available_title.setText(getResources().getString(R.string.available_num) + "(" + c2cAmountModel.currencyName + ")");
            tv_c2c_frozen_title.setText(getResources().getString(R.string.frozen_num) + "(" + c2cAmountModel.currencyName + ")");

            tv_c2c.setText(c2cAmountModel.amount);
            String ycn = null;
            if (!TextUtils.isEmpty(c2cAmountModel.amount)) {
                ycn = getMcM(c2cAmountModel.currencyId, Double.parseDouble(c2cAmountModel.amount));
            }
            tv_c2c_rnb.setText((TextUtils.isEmpty(ycn) ? "" : "≈" + ycn));
            tv_c2c_available.setText(c2cAmountModel.cashAmount);

            Double value1 = (!TextUtils.isEmpty(c2cAmountModel.freezeAmount)) ? Double.valueOf(c2cAmountModel.freezeAmount) : 0;
            Double value2 = (!TextUtils.isEmpty(c2cAmountModel.bondFreezeAmount)) ? Double.valueOf(c2cAmountModel.bondFreezeAmount) : 0;
            tv_c2c_frozen.setText(FormatterUtils.getFormatValue(MathHelper.add(value1, value2)));
        } else {
            tv_c2c.setText("0.0");
            tv_c2c_rnb.setText("");
            tv_c2c_available.setText("0.0");
            tv_c2c_frozen.setText("0.0");
        }
    }

    private void upload() {
        if (infoModel != null && infoModel.currencyId != null) {
            mPresenter.otcAmount(1, isFirst, infoModel.currencyId);
            mPresenter.c2cAmount(1, isFirst, infoModel.currencyId);
        }
        if (isFirst) {
            isFirst = false;
        }
    }

    private void dealFlag() {
        if (flag == 0) {
            tv_account.setText(getResources().getString(R.string.coin_otc_account));
        } else if (flag == 1) {
            tv_account.setText(getResources().getString(R.string.margin_account));
        } else if (flag == 2) {
            tv_account.setText(getResources().getString(R.string.coin_c2c_account));
        } else if (flag == 3) {
            tv_account.setText(getResources().getString(R.string.otc_c2c_account));
        }
    }

    private boolean isKeyc2() {
        int flag = spUtil.getIsAuthSenior();
        if (flag <= 1) {
            showToastError(getString(R.string.please_get_the_level_of_KYC));
            return false;
        }
        return true;
    }

    private void showAccountType() {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            final List<String> data = new ArrayList<>();

            data.add(getResources().getString(R.string.coin_otc_account));
            data.add(getResources().getString(R.string.margin_account));
            if ((otcAmountModel != null && !TextUtils.isEmpty(otcAmountModel.amount) && Double.parseDouble(otcAmountModel.amount) != 0) || isMerchant()) {
                data.add(getResources().getString(R.string.coin_c2c_account));
                data.add(getResources().getString(R.string.otc_c2c_account));
            }

            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(this, adapter);
            listPopup.create(UIUtils.dp2px(200), UIUtils.dp2px(40) * data.size(), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (TextUtils.equals(data.get(position), getResources().getString(R.string.coin_otc_account))) {
                        flag = 0;
                    } else if (TextUtils.equals(data.get(position), getResources().getString(R.string.margin_account))) {
                        flag = 1;
                    } else if (TextUtils.equals(data.get(position), getResources().getString(R.string.coin_c2c_account))) {
                        flag = 2;
                    } else if (TextUtils.equals(data.get(position), getResources().getString(R.string.otc_c2c_account))) {
                        flag = 3;
                    }
                    dealFlag();
                    listPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.START);
        }
        listPopup.setAnchorView(rl_account);
        listPopup.show();
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
