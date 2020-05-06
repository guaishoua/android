package com.android.tacu.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity的懒加载
 * 将一些显示的加载放到 window.decorView.post{} （启动的时机在 onCreate、onStart、onResume之后，整个页面渲染完成之后）
 * 以减少启动页面的时候的启动时间
 */
public class LazyLoadBaseActivity extends AppCompatActivity {

    private boolean mIsFirstVisible = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                onActivityFirstVisible();
                onActivityResume();
                mIsFirstVisible = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsFirstVisible) {
            onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mIsFirstVisible) {
            onActivityPause();
        }
    }

    public void onActivityResume() {
    }

    public void onActivityFirstVisible() {
    }

    public void onActivityPause() {
    }
}
