package com.android.tacu.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.main.view.TradeDataBridge;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.socket.AppSocket;
import com.android.tacu.socket.MainSocketManager;
import com.android.tacu.utils.ConvertMoneyUtils;
import com.android.tacu.utils.NetworkUtils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.LoadingAnim;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Observer;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 注意点：
 * 1.使用getChildFragmentManager的Fragment 会在Fragment重置的时候保存之前Fragment的数据
 * <p>
 * Created by jiazhen on 2018/8/10.
 */
public abstract class BaseFragment<P extends BaseMvpPresenter> extends Fragment implements IBaseMvpView {

    private FragmentActivity mFragAct;
    private Unbinder unBinder;
    private LoadingAnim loadingView;
    protected P mPresenter;
    protected UserInfoUtils spUtil;
    //Socket
    private ISocketEvent baseSocketEvent;
    private Observer baseObserver;
    protected AppSocket baseAppSocket;
    protected MainSocketManager baseSocketManager;
    //当前fragment的可见
    protected boolean isVisibleToUser = false;
    protected boolean isVisibleHidden = true;
    //当前fragment所依赖的Activity的可见
    protected boolean isVisibleActivity = true;

    protected abstract int getContentViewLayoutID();

    protected abstract void initData(View view);

    /**
     * 1.Socket在isVisibleToUser=false解除数据绑定 isVisibleToUser=true绑定数据并且emit
     * 2.Socket在hidden=false解除数据绑定 hidden=true绑定数据并且emit
     * fragment如果是第一个需要setSocketEvent之后调用socketConnectEventAgain
     * 因为setUserVisibleHint在setSocketEvent方法之前
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
        WindowManager.LayoutParams lp = getHostActivity().getWindow().getAttributes();
        lp.alpha = backGroundAlpha;
        getHostActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        //通过spUtil != null来让第一次fragment加载时不会走initLazy这个方法
        if (spUtil != null) {
            if (isVisibleToUser) {
                initLazy();
                onEmit();
            } else {
                disconnectEmit();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.isVisibleHidden = hidden;
        if (spUtil != null) {
            if (hidden) {
                disconnectEmit();
            } else {
                initLazy();
                onEmit();
            }
        }
    }

    @Override
    public void onAttach(Context paramContext) {
        super.onAttach(paramContext);
        onAttachToContext(paramContext);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mFragAct != null) {
            mFragAct = null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtil = UserInfoUtils.getInstance();
        mPresenter = createPresenter(mPresenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup paramViewGroup, Bundle paramBundle) {
        if (getContentViewLayoutID() != 0) {
            return inflater.inflate(getContentViewLayoutID(), null);
        } else {
            return super.onCreateView(inflater, paramViewGroup, paramBundle);
        }
    }

    @Override
    public void onViewCreated(View paramView, @Nullable Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
        unBinder = ButterKnife.bind(this, paramView);
        initData(paramView);

        if (mPresenter != null) {
            mPresenter.attachView(this);
            onPresenterCreated(mPresenter);
        }
        EventManage.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisibleActivity = true;
        if (isVisibleToUser || !isVisibleHidden) {
            onEmit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibleActivity = false;
        if (isVisibleToUser || !isVisibleHidden) {
            disconnectEmit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoadingView();
        if (unBinder != null) {
            unBinder.unbind();
        }
        if (loadingView != null) {
            loadingView.disAllLoading();
            loadingView = null;
        }
        if (mPresenter != null && mPresenter.isViewAttached()) {
            mPresenter.detachView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
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
     * 使用此方法代替getActivity，防止getActivity空指针
     *
     * @return
     */
    public FragmentActivity getHostActivity() {
        FragmentActivity localFragmentActivity = getActivity();
        if (localFragmentActivity == null)
            localFragmentActivity = mFragAct;
        return localFragmentActivity;
    }

    /**
     * Fragment的懒加载模式
     * 当前的Fragment和当前的Activity同时可见时为true
     * 常常用于Fragment的网络请求 不可见的Fragment没必要网络请求
     */
    protected void initLazy() {
    }

    protected P createPresenter(P mPresenter) {
        return mPresenter;
    }

    protected void onPresenterCreated(P mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public void showLoadingView() {
        if (loadingView == null) {
            loadingView = new LoadingAnim(getHostActivity());
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
     * fragment重启
     *
     * @param context
     */
    protected void onAttachToContext(Context context) {
        mFragAct = ((FragmentActivity) context);
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
        Intent intent = new Intent(getContext(), clazz);
        startActivity(intent);
    }

    /**
     * 没参数的直接跳转并带返回值
     */
    public void jumpTo(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(getContext(), clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 有参数并带返回值
     *
     * @param intent
     * @param requestCode
     */
    public void jumpTo(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    /**
     * 需要传递参数的
     */
    public void jumpTo(Intent intent) {
        startActivity(intent);
    }

    /**
     * 判断网络是否连接
     */
    public boolean isAvailable() {
        if (!NetworkUtils.isConnected(getContext())) {
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

    protected TradeDataBridge getDataBridge() {
        if (getHostActivity() instanceof TradeDataBridge) {
            return (TradeDataBridge) getHostActivity();
        } else {
            return new TradeDataBridge() {
                @Override
                public List<MarketNewModel.TradeCoinsBean> getTradeList() {
                    return null;
                }
            };
        }
    }
}
