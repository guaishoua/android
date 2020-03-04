package com.android.tacu.module.splash.view;

import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.splash.contract.TradeMatchContract;
import com.android.tacu.module.splash.presenter.TradeMatchPresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.PackageUtils;
import com.android.tacu.utils.downloadfile.AppUpdateUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import butterknife.BindView;

public class TradeMatchActivity extends BaseActivity<TradeMatchPresenter> implements TradeMatchContract.IView {

    @BindView(R.id.tv_hour)
    TextView tv_hour;
    @BindView(R.id.tv_minute)
    TextView tv_minute;
    @BindView(R.id.tv_second)
    TextView tv_second;

    //当前页面可以展示时间 2020-03-04 00:00:00
    public static final long SHOW_START = 1583251200000L;
    //交易赛开始时间，这时当前页面不展示 2020-03-04 20:00:00
    public static final long MATCH_START = 1583323200000L;
    //k线的请求 1s一次
    private static final int KREFRESH_TIME = 1000;

    private long currentTime;
    private long value;
    private String[] timeArr;
    private static long currentTimeS;
    private boolean isFlag = false;

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            dealGo();
            //每隔1秒循环执行run方法
            if (kHandler != null) {
                kHandler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };

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
        if (kHandler != null && kRunnable != null) {
            kHandler.post(kRunnable);
        }
        mPresenter.upload(PackageUtils.getVersion(), PackageUtils.getMetaValue(this, PackageUtils.META_NAME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
            kHandler = null;
            kRunnable = null;
        }
    }

    @Override
    public void upload(UploadModel model) {
        if (model != null) {
            if (PackageUtils.splitVersionNum(model.nowVersion, PackageUtils.getVersion())) {
                AppUpdateUtils.showSimpleUpdate(this, model);
            }
        }
    }

    private void dealGo() {
        currentTime = DateUtils.getCurrentBeiJingTime();
        if (currentTime >= SHOW_START && currentTime < MATCH_START) {
            isFlag = false;
        } else {
            isFlag = true;
        }
        if (isFlag) {
            if (kHandler != null && kRunnable != null) {
                kHandler.removeCallbacks(kRunnable);
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            jumpTo(MainActivity.class);
            finish();
        } else {
            value = MATCH_START - currentTime;
            try {
                timeArr = DateUtils.getCountDownTime2(value);
                tv_hour.setText(timeArr[0]);
                tv_minute.setText(timeArr[1]);
                tv_second.setText(timeArr[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 2020年3月4日20:00 关闭蒙层
     *
     * @return true=跳转首页 false跳转TradeMatchActivity页面
     */
    public static boolean isGoMain() {
        currentTimeS = DateUtils.getCurrentBeiJingTime();
        if (currentTimeS >= SHOW_START && currentTimeS < MATCH_START) {
            return false;
        }
        return true;
    }
}
