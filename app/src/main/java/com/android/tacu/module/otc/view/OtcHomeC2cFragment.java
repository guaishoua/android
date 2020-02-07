package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcHomeC2cContract;
import com.android.tacu.module.otc.presenter.OtcHomeC2cPresenter;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcHomeC2cFragment extends BaseFragment<OtcHomeC2cPresenter> implements OtcHomeC2cContract.IView {

    @BindView(R.id.tv_left)
    TextView tv_left;
    @BindView(R.id.img_left)
    ImageView img_left;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.img_right)
    ImageView img_right;
    @BindView(R.id.edit_amount)
    EditText edit_amount;
    @BindView(R.id.tv_amount_right)
    TextView tv_amount_right;
    @BindView(R.id.tv_last_price)
    TextView tv_last_price;

    private int coinTypeDirection = 0;// 0=币种设置的按钮在右边 1=在左边
    private QMUIBottomSheet mBottomSheet;

    public static OtcHomeC2cFragment newInstance() {
        Bundle bundle = new Bundle();
        OtcHomeC2cFragment fragment = new OtcHomeC2cFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_home_c2c;
    }

    @Override
    protected void initData() {
        edit_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence value, int start, int before, int count) {
                if (!TextUtils.isEmpty(value)) {
                    tv_amount_right.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
                    tv_amount_right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                } else {
                    tv_amount_right.setTextColor(ContextCompat.getColor(getContext(), R.color.text_grey));
                    tv_amount_right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected OtcHomeC2cPresenter createPresenter(OtcHomeC2cPresenter mPresenter) {
        return new OtcHomeC2cPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBottomSheet != null) {
            mBottomSheet.dismiss();
        }
    }

    @OnClick(R.id.con_left)
    void leftClick() {
        if (coinTypeDirection == 1) {
            showCoinType(tv_left);
        }
    }

    @OnClick(R.id.con_right)
    void rightClick() {
        if (coinTypeDirection == 0) {
            showCoinType(tv_right);
        }
    }

    @OnClick(R.id.img_switch)
    void switchClick() {
        coinTypeDirection = 1 - coinTypeDirection;
        setCoinType();
    }

    @OnClick(R.id.btn_buy)
    void buyClick() {

    }

    private void setCoinType() {
        if (coinTypeDirection == 0) {
            img_left.setVisibility(View.GONE);
            img_right.setVisibility(View.VISIBLE);
        } else if (coinTypeDirection == 1) {
            img_left.setVisibility(View.VISIBLE);
            img_right.setVisibility(View.GONE);
        }
        String value = tv_right.getText().toString();
        tv_right.setText(tv_left.getText().toString());
        tv_left.setText(value);
    }

    private void showCoinType(final TextView tv) {
        if (mBottomSheet == null) {
            mBottomSheet = new QMUIBottomSheet.BottomListSheetBuilder(getContext())
                    .addItem("ACU")
                    .addItem("BTC")
                    .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                            if (position == 0) {
                                tv.setText("ACU");
                            } else if (position == 1) {
                                tv.setText("BTC");
                            }
                            dialog.dismiss();
                        }
                    })
                    .build();
        }
        mBottomSheet.show();
    }
}
