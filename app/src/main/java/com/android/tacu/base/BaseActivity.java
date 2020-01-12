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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.R;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.socket.AppSocket;
import com.android.tacu.socket.MainSocketManager;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.NetworkUtils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.StatusBarUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.LoadingAnim;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.Observer;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jiazhen on 2018/8/8.
 */
public abstract class BaseActivity<P extends BaseMvpPresenter> extends AppCompatActivity implements IBaseMvpView {

    /**
     * 重要 勿动 不能修改
     */
    static final String FRAGMENTS_TAG = "android:support:fragments";

    protected P mPresenter;
    private Unbinder unBinder;
    private LoadingAnim loadingView;
    protected QMUITopBar mTopBar;
    protected UserInfoUtils spUtil;
    protected ActivityStack activityManage;
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

    protected void setBackGroundAlpha(float backGroundAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = backGroundAlpha;
        getWindow().setAttributes(lp);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /**
         * 通过判断paramBundle是否为空来确定activity是否被回收过
         * 如果为true表示被回收过 清除activity里存储的fragment 重新创建
         * 注意setUserVisibleHint 它的执行顺序在onCreate前面
         */
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable(FRAGMENTS_TAG, null);
        }
        super.onCreate(savedInstanceState);
        //沉浸栏
        StatusBarUtils.setColorNoTranslucent(this, ContextCompat.getColor(this, R.color.statusbar_color));

        //禁止app横竖屏切换
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        activityManage = ActivityStack.getInstance();
        activityManage.addActivity(this);
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

    protected boolean onTopBarBackPressed() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisibleActivity = true;
        onEmit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisibleActivity = false;
        disconnectEmit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingView();
        activityManage.finishActivity(this);
        if (unBinder != null) {
            unBinder.unbind();
        }
        if (loadingView != null) {
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
     * 判断网络是否连接
     */
    public boolean isAvailable() {
        if (!NetworkUtils.isConnected(this)) {
            return false;
        } else {
            return true;
        }
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
