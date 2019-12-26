package com.android.tacu.module.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.lock.view.FingerprintActivity;
import com.android.tacu.module.lock.view.GestureActivity;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.LanguageUtils;
import com.android.tacu.utils.lock.LockUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    //为true表示开启了手势或者指纹验证
    private boolean isGestureAndFingerLock = false;

    private Handler timerHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView();
        initView();
    }

    private void setView() {
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //问题描述：安装成功之后会有两个提示 （完成和打开）如果点击打开就会有问题
        //但是如果MainActivity没有设置singleTask就没事
        //完美解决：APP下载安装后，点击“直接打开”，启动应用后，按下HOME键，再次点击桌面上的应用，会重启一个新的应用问题
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
    }

    private void initView() {
        //语言
        Locale userLocale = LanguageUtils.getUserLocale(this);
        LanguageUtils.updateLocale(userLocale);

       new Thread(new Runnable() {
            @Override
            public void run() {
                ConvertMoneyUtils.setSpConvertBean();
                ConvertMoneyUtils.setSpBaseCoinScale();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LockUtils.getLockSetting().isFinger() || !TextUtils.isEmpty(LockUtils.getLockSetting().getGesture())) {
            //减得当前APP在后台滞留的时间 durTime
            long durTime = System.currentTimeMillis() - LockUtils.getLockTime();
            if (durTime > LockUtils.LOCK_TIME) {
                if (LockUtils.getLockSetting().isFinger()) {
                    isGestureAndFingerLock = true;
                    Intent intent = new Intent(this, FingerprintActivity.class);
                    startActivityForResult(intent, 0);
                } else if (!TextUtils.isEmpty(LockUtils.getLockSetting().getGesture())) {
                    isGestureAndFingerLock = true;
                    Intent intent = new Intent(this, GestureActivity.class);
                    startActivityForResult(intent, 0);
                }
            }
        }
        if (!isGestureAndFingerLock) {
            timerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goMainActivity();
                }
            }, 3000);
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LockUtils.setLockTime(System.currentTimeMillis());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        if (timerHandler != null) {
            timerHandler.removeCallbacksAndMessages(null);
            timerHandler = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Constant.PWD_RESULT) {
            goMainActivity();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 跳转MainActivity
     */
    private void goMainActivity() {
        //退出全屏 防止切换非全屏卡顿
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
