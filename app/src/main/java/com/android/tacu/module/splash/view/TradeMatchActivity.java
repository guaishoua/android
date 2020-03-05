package com.android.tacu.module.splash.view;

import android.view.WindowManager;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.splash.contract.TradeMatchContract;
import com.android.tacu.module.splash.presenter.TradeMatchPresenter;
import com.android.tacu.utils.DateUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import butterknife.OnClick;

public class TradeMatchActivity extends BaseActivity<TradeMatchPresenter> implements TradeMatchContract.IView {

    //当前页面可以展示时间 2020-03-12 20:00:00
    public static final long SHOW_START = 1584014400000L;

    private static long currentTimeS;

    @Override
    protected void setView() {
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_trade_match);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected TradeMatchPresenter createPresenter(TradeMatchPresenter mPresenter) {
        return new TradeMatchPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.btn_end_close)
    void endCloseClick() {
        goMainActivity();
    }

    private void goMainActivity() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        jumpTo(MainActivity.class);
        finish();
    }

    /**
     * 2020年3月4日20:00 关闭蒙层
     *
     * @return true=跳转首页 false跳转TradeMatchActivity页面
     */
    public static boolean isGoMain() {
        currentTimeS = DateUtils.getCurrentBeiJingTime();
        if (currentTimeS >= SHOW_START) {
            return false;
        }
        return true;
    }
}
