package com.android.tacu.module.payinfo.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;

import butterknife.OnClick;

public class PayInfoTypeActivity extends BaseActivity {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_pay_info_type);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.add));
    }

    @OnClick(R.id.rl_yhk)
    void yhkClick() {
        jumpTo(BindingInfoYhkActivity.class);
    }

    @OnClick(R.id.rl_wx)
    void wxClick() {
        jumpTo(BindingInfoWxActivity.class);
    }

    @OnClick(R.id.rl_zfb)
    void zfbClick() {
        jumpTo(BindingInfoZfbActivity.class);
    }
}
