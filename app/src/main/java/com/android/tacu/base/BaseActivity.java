package com.android.tacu.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.R;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.splash.SplashActivity;
import com.android.tacu.socket.AppSocket;
import com.android.tacu.socket.MainSocketManager;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.StatusBarUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.LoadingAnim;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.Observer;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.android.tacu.utils.ActivityStack.STATUS_KILLED;

/**
 * Created by jiazhen on 2018/8/8.
 */
public abstract class BaseActivity<P extends BaseMvpPresenter> extends LazyLoadBaseActivity implements IBaseMvpView {

    protected P mPresenter;
    private Unbinder unBinder;
    private LoadingAnim loadingView;
    protected QMUITopBar mTopBar;
    protected UserInfoUtils spUtil;
    //Socket
    private ISocketEvent baseSocketEvent;
    private Observer baseObserver;
    protected AppSocket baseAppSocket;
    private MainSocketManager baseSocketManager;
    //判断当前Activity是否可见
    protected boolean isVisibleActivity = true;

    protected abstract void setView();

    protected abstract void initView();

    /**
     * Socket在onPause解除数据绑定 onResume绑定数据并且emit
     *
     * @param observer
     * @param strings
     */
    public void setSocketEvent(ISocketEvent mSocketEvent, Observer observer, String... strings) {
        this.baseSocketEvent = mSocketEvent;
        this.baseObserver = observer;
        baseAppSocket = new AppSocket();
        baseSocketManager = new MainSocketManager(baseAppSocket.getSocket());
        baseSocketManager.initEmitterEvent(strings);
        baseSocketManager.addObserver(observer);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            protectApp();
        }
        super.onCreate(savedInstanceState);
        //沉浸栏
        StatusBarUtils.setColorNoTranslucent(this, ContextCompat.getColor(this, R.color.statusbar_color));

        settingActivityScreenDir();

        if (ActivityStack.getInstance().getAppStatus() == STATUS_KILLED) {
            protectApp();
            return;
        }
        ActivityStack.getInstance().addActivity(this);

        spUtil = UserInfoUtils.getInstance();

        setView();
        unBinder = ButterKnife.bind(this);
        mTopBar = findViewById(R.id.title);
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onTopBarBackPressed())
                    finish();
            }
        });
        mTopBar.setBackgroundDividerEnabled(true);
        initView();
        initLazy();

        mPresenter = createPresenter(mPresenter);
        if (mPresenter != null) {
            mPresenter.attachView(this);
            onPresenterCreated(mPresenter);
        }
        EventManage.register(this);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        onEmit();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        disconnectEmit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisibleActivity = true;
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisibleActivity = false;
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingView();
        ActivityStack.getInstance().finishActivity(this);
        if (unBinder != null) {
            unBinder.unbind();
        }
        if (loadingView != null) {
            loadingView.disAllLoading();
            loadingView = null;
        }
        if (mPresenter != null && mPresenter.isViewAttached()) {
            mPresenter.destroy();
            mPresenter = null;
        }
        if (baseSocketManager != null && baseObserver != null) {
            baseSocketManager.deleteObserver(baseObserver);
            baseSocketManager.disconnectEmitterListener();
            baseSocketManager = null;
        }
        if (baseAppSocket != null) {
            baseAppSocket.clearSocket();
            baseAppSocket = null;
        }
        EventManage.unregister(this);
    }

    /**
     * 防止系统改变字体的大小
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1 || (MyApplication.getUserLocale() != null && !(TextUtils.equals(res.getConfiguration().locale.getLanguage(), MyApplication.getUserLocale().getLanguage()) && TextUtils.equals(res.getConfiguration().locale.getCountry(), MyApplication.getUserLocale().getCountry())))) {
            Configuration config = new Configuration();
            config.setToDefaults();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return res;
    }

    protected boolean onTopBarBackPressed() {
        return false;
    }

    /**
     * Activity的懒加载模式 优化Activity启动
     * <p>
     * getWindow().getDecorView().post(new runnable)
     * getWindow().getDecorView().postDelayed(new Runnable , delayMillis)
     * <p>
     * 不同手机的配置的不同启动时间也变得不同 所以不能用handle的延迟时间
     * 等待界面都加载完成之后再执行 比如一些会让界面卡顿的方法
     */
    protected void initLazy() {
    }

    protected P createPresenter(P mPresenter) {
        return mPresenter;
    }

    protected void onPresenterCreated(P presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoadingView() {
        if (loadingView == null) {
            loadingView = new LoadingAnim(this);
        }
        loadingView.showLoading();
    }

    @Override
    public void hideLoadingView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadingView != null) {
                    loadingView.disLoading();
                }
            }
        }, 1000);
    }

    @Override
    public void hideRefreshView() {
    }

    @Override
    public void tokenInvalid() {
        UserManageUtils.logout();
        //友盟退出
        MobclickAgent.onProfileSignOff();
        jumpTo(LoginActivity.class);
    }

    @Override
    public void showToast(String paramString) {
        if (!TextUtils.isEmpty(paramString)) {
            ShowToast.normal(paramString);
        }
    }

    @Override
    public void showToastSuccess(String paramString) {
        if (!TextUtils.isEmpty(paramString)) {
            ShowToast.success(paramString);
        }
    }

    @Override
    public void showToastError(String paramString) {
        if (!TextUtils.isEmpty(paramString)) {
            ShowToast.error(paramString);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageStickyEvent(BaseEvent event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     */
    protected void receiveEvent(BaseEvent event) {
    }

    /**
     * 接受到分发的粘性事件
     */
    protected void receiveStickyEvent(BaseEvent event) {
    }

    /**
     * 没参数的直接跳转
     */
    public void jumpTo(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * 没参数的直接跳转并带返回值
     */
    public void jumpTo(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 需要传递参数的
     */
    public void jumpTo(Intent intent) {
        startActivity(intent);
    }

    /**
     * 需要传递参数并带返回参数的跳转
     * requestCode
     *
     * @param intent
     * @param requestCode
     */
    public void jumpTo(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    /**
     * 防止activity被系统回收所导致的异常
     */
    protected void protectApp() {
        jumpTo(SplashActivity.createActivity(this, true));
        finish();
    }

    /**
     * 限制屏幕方向
     */
    protected void settingActivityScreenDir() {
        //当前限制是竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 根据当前基础币数量来计算人民币数量
     *
     * @param baseCurrentId 基础币Id
     * @param number        基础币数量
     * @return
     */
    protected String getMcM(int baseCurrentId, double number) {
        return ConvertMoneyUtils.getMcM(baseCurrentId, number);
    }

    protected BigDecimal getMcMValue(int baseCurrentId, double number) {
        return ConvertMoneyUtils.getMcMValue(baseCurrentId, number);
    }

    protected void setBackGroundAlpha(float backGroundAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = backGroundAlpha;
        getWindow().setAttributes(lp);
    }

    protected void onEmit() {
        if (baseSocketManager != null && baseSocketEvent != null) {
            baseSocketManager.onEmitterListener();
            baseSocketEvent.socketConnectEventAgain();
        }
    }

    protected void disconnectEmit() {
        if (baseSocketManager != null) {
            baseSocketManager.disconnectEmitterListener();
        }
    }
}
