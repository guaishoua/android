package com.android.tacu.module.payinfo.view;

import android.content.Context;
import android.content.Intent;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;

import butterknife.OnClick;

public class PayInfoTypeActivity extends BaseActivity {

    private boolean isYHK = false;
    private boolean isWX = false;
    private boolean isZFB = false;

    public static Intent createActivity(Context context, boolean isYHK, boolean isWX, boolean isZFB) {
        Intent intent = new Intent(context, PayInfoTypeActivity.class);
        intent.putExtra("isYHK", isYHK);
        intent.putExtra("isWX", isWX);
        intent.putExtra("isZFB", isZFB);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_pay_info_type);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.add));

        isYHK = getIntent().getBooleanExtra("isYHK", false);
        isWX = getIntent().getBooleanExtra("isWX", false);
        isZFB = getIntent().getBooleanExtra("isZFB", false);
    }

    @OnClick(R.id.rl_yhk)
    void yhkClick() {
        if (spUtil.getApplyAuthMerchantStatus() != 2 && isYHK) {
            showToastError(getResources().getString(R.string.pay_info_one));
            return;
        }
        jumpTo(BindingInfoYhkActivity.class);
    }

    @OnClick(R.id.rl_wx)
    void wxClick() {
        if (spUtil.getApplyAuthMerchantStatus() != 2 && isWX) {
            showToastError(getResources().getString(R.string.pay_info_one));
            return;
        }
        jumpTo(BindingInfoWxActivity.class);
    }

    @OnClick(R.id.rl_zfb)
    void zfbClick() {
        if (spUtil.getApplyAuthMerchantStatus() != 2 && isZFB) {
            showToastError(getResources().getString(R.string.pay_info_one));
            return;
        }
        jumpTo(BindingInfoZfbActivity.class);
    }
}
