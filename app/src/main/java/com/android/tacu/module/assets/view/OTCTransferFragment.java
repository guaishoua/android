package com.android.tacu.module.assets.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.OTCTransferContract;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.presenter.OTCTransferPresenter;
import com.android.tacu.utils.FormatterUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class OTCTransferFragment extends BaseFragment<OTCTransferPresenter> implements OTCTransferContract.IView {

    @BindView(R.id.tv_asset_one)
    TextView tv_asset_one;
    @BindView(R.id.tv_asset_two)
    TextView tv_asset_two;
    @BindView(R.id.tv_coin_account_available_title)
    TextView tv_coin_account_available_title;
    @BindView(R.id.tv_coin_account_available)
    TextView tv_coin_account_available;
    @BindView(R.id.edit_num)
    EditText edit_num;

    private int currencyId;
    private String currencyNameEn;
    private boolean isFlag;
    private int type = 1;//1=币币账户->OTC账户 2=OTC账户->币币账户

    private Double amountValue;
    private OtcAmountModel otcAmountModel;

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
        exchangeFlag();
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
    }

    @Override
    protected OTCTransferPresenter createPresenter(OTCTransferPresenter mPresenter) {
        return new OTCTransferPresenter();
    }

    @Override
    protected void onPresenterCreated(OTCTransferPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        if (isVisibleToUser && isFlag) {
            exchangeFlag();
        }
    }

    @OnClick(R.id.img_exchange)
    void imgExchangeClick() {
        if (type == 1) {
            type = 2;
        } else if (type == 2) {
            type = 1;
        }
        exchangeFlag();
    }

    @OnClick(R.id.btn_all)
    void allClick() {
        if (type == 1 && amountValue != null) {
            edit_num.setText(FormatterUtils.getFormatValue(amountValue));
        } else if (type == 2 && otcAmountModel != null) {
            edit_num.setText(FormatterUtils.getFormatValue(otcAmountModel.cashAmount));
        }
    }

    @OnClick(R.id.btn_ok)
    void okClick() {
        String amount = edit_num.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            showToastError(getResources().getString(R.string.please_input_transfer_num));
            return;
        }
        //1=币币账户->OTC账户 2=OTC账户->币币账户
        if (type == 1) {
            mPresenter.transIn(amount, currencyId);
        } else if (type == 2) {
            mPresenter.transOut(amount, currencyId);
        }
    }

    @Override
    public void transOutSuccess() {
        edit_num.setText("");
        showToastSuccess(getResources().getString(R.string.transfer_success));
        mPresenter.otcAmount(currencyId);
    }

    @Override
    public void transInSuccess() {
        edit_num.setText("");
        showToastSuccess(getResources().getString(R.string.transfer_success));
        mPresenter.customerCoinByOneCoin(currencyId);
    }

    @Override
    public void customerCoinByOneCoin(Double value) {
        this.amountValue = value;
        if (value != null) {
            tv_coin_account_available.setText(FormatterUtils.getFormatValue(value));
        }
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        this.otcAmountModel = model;
        if (model != null) {
            tv_coin_account_available.setText(FormatterUtils.getFormatValue(model.cashAmount));
        }
    }

    private void exchangeFlag() {
        if (type == 1) {
            tv_asset_one.setText(getResources().getString(R.string.coin_account));
            tv_asset_two.setText(getResources().getString(R.string.otc_account));
            tv_coin_account_available_title.setText(getResources().getString(R.string.coin_account_available) + "(" + currencyNameEn + ")" + "：");
            mPresenter.customerCoinByOneCoin(currencyId);
        } else if (type == 2) {
            tv_asset_one.setText(getResources().getString(R.string.otc_account));
            tv_asset_two.setText(getResources().getString(R.string.coin_account));
            tv_coin_account_available_title.setText(getResources().getString(R.string.otc_account_available) + "(" + currencyNameEn + ")" + "：");
            mPresenter.otcAmount(currencyId);
        }
    }
}
