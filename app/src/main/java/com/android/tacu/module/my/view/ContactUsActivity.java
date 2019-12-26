package com.android.tacu.module.my.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;

public class ContactUsActivity extends BaseActivity {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_contact_us);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.contact_us));
    }
}
