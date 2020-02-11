package com.android.tacu.module;

import android.content.Context;
import android.content.Intent;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.widget.ZoomImageView;

import butterknife.BindView;

public class ZoomImageViewActivity extends BaseActivity {

    @BindView(R.id.zoom_imageview)
    ZoomImageView imageView;

    private String url;

    public static Intent createActivity(Context context, String url) {
        Intent intent = new Intent(context, ZoomImageViewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_zoom_imageview);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.picture));

        url = getIntent().getStringExtra("url");
        GlideUtils.disPlay(this, url, imageView);
    }
}
