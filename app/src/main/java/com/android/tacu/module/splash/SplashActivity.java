package com.android.tacu.module.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.android.tacu.R;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.LanguageUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

import static com.android.tacu.utils.ActivityStack.STATUS_NORMAL;

public class SplashActivity extends AppCompatActivity {

    private QMUIRoundButton btn_go;
    private Handler timerHandler = new Handler();

    /**
     * @param context
     * @param isClearTop A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @return
     */
    public static Intent createActivity(Context context, boolean isClearTop) {
        Intent intent = new Intent(context, SplashActivity.class);
        if (isClearTop) {
            ActivityStack.getInstance().setAppStatus(STATUS_NORMAL);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setView();
        initView();
    }

    private void setView() {
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActivityStack.getInstance().setAppStatus(STATUS_NORMAL);
        ActivityStack.getInstance().addActivity(this);

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
        btn_go = findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerHandler != null) {
                    timerHandler.removeCallbacksAndMessages(null);
                }
                goMainActivity();
            }
        });

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
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goMainActivity();
            }
        }, 3000);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        ActivityStack.getInstance().finishActivity(this);
        if (timerHandler != null) {
            timerHandler.removeCallbacksAndMessages(null);
            timerHandler = null;
        }
        super.onDestroy();
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

        /*Intent intent = new Intent(this, TradeMatchActivity.class);
        startActivity(intent);*/
    }
}
