package com.android.tacu.module.otc.view;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcHomeC2cContract;
import com.android.tacu.module.otc.presenter.OtcHomeC2cPresenter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;

import java.util.ArrayList;
import java.util.List;

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
    private ListPopWindow listPopup;

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
        if (listPopup != null) {
            listPopup.dismiss();
        }
    }

    @OnClick(R.id.con_left)
    void leftClick() {
        if (coinTypeDirection == 1) {
            showCoinType(tv_left, img_left);
        }
    }

    @OnClick(R.id.con_right)
    void rightClick() {
        if (coinTypeDirection == 0) {
            showCoinType(tv_right, img_right);
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

    private void showCoinType(final TextView tv, ImageView img) {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            final List<String> data = new ArrayList<>();
            data.add("ACU");
            data.add("BTC");
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(getContext(), adapter);
            listPopup.create(UIUtils.dp2px(100), UIUtils.dp2px(81), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv.setText(data.get(position));
                    listPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.END);
        }
        listPopup.setAnchorView(img);
        listPopup.show();
    }
}
