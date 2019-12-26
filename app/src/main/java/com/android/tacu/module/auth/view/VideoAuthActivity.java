package com.android.tacu.module.auth.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/8/28.
 */

public class VideoAuthActivity extends BaseActivity {

    @BindView(R.id.lin_china)
    LinearLayout lin_china;
    @BindView(R.id.lin_english)
    LinearLayout lin_english;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_video_auth);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.identity_authentication));

        if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
            lin_china.setVisibility(View.VISIBLE);
            lin_english.setVisibility(View.GONE);
        } else {
            lin_china.setVisibility(View.GONE);
            lin_english.setVisibility(View.VISIBLE);
        }
    }
}
