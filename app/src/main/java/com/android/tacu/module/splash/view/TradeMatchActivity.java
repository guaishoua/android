package com.android.tacu.module.splash.view;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.splash.SplashActivity;
import com.android.tacu.module.splash.contract.TradeMatchContract;
import com.android.tacu.module.splash.model.TradeWinListModel;
import com.android.tacu.module.splash.model.TradeWinModel;
import com.android.tacu.module.splash.presenter.TradeMatchPresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.PackageUtils;
import com.android.tacu.utils.downloadfile.AppUpdateUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class TradeMatchActivity extends BaseActivity<TradeMatchPresenter> implements TradeMatchContract.IView {

    @BindView(R.id.view_end)
    NestedScrollView view_end;
    @BindView(R.id.view_result)
    NestedScrollView view_result;
    @BindView(R.id.recycler1)
    RecyclerView ryWin;
    @BindView(R.id.recycler2)
    RecyclerView ryExpe;

    //当前页面可以展示时间 2020-03-12 20:00:00
    public static final long SHOW_START = 1584014400000L;
    //当前页面可以展示时间 2020-03-13 10:00:00
    public static final long SHOW_START_RESULT = 1584064800000L;
    private static final int KREFRESH_TIME = 1000;

    private static long currentTimeS;
    private static long currentTime;
    private boolean isFlag = true;

    private int ownUid;
    private WinAdapter winAdapter;
    private ExpeAdapter expeAdapter;
    private LinearLayoutManager winManager;
    private LinearLayoutManager expeManager;

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            dealVoid();
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
        winAdapter = new WinAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider_win));
        winManager = new LinearLayoutManager(this);
        ryWin.setLayoutManager(winManager);
        ryWin.addItemDecoration(dividerItemDecoration);
        ryWin.setAdapter(winAdapter);

        expeAdapter = new ExpeAdapter();
        expeManager = new LinearLayoutManager(this);
        ryWin.setLayoutManager(expeManager);
        ryExpe.setLayoutManager(new LinearLayoutManager(this));
        ryExpe.setAdapter(expeAdapter);

        ownUid = spUtil.getUserUid();
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
    public void onBackPressed() {
        activityManage.finishActivity(SplashActivity.class);
        finish();
    }

    @OnClick(R.id.btn_end_close)
    void endCloseClick() {
        goMainActivity();
    }

    @OnClick(R.id.btn_result_end_close)
    void reslutEndCloseClick() {
        goMainActivity();
    }

    @Override
    public void upload(UploadModel model) {
        if (model != null) {
            if (PackageUtils.splitVersionNum(model.nowVersion, PackageUtils.getVersion())) {
                AppUpdateUtils.showSimpleUpdate(this, model);
            }
        }
    }

    @Override
    public void tradeWinList(TradeWinListModel model) {
        if (model != null && ((model.win != null && model.win.size() > 0) || (model.expe != null && model.expe.size() > 0))) {
            view_end.setVisibility(View.GONE);
            view_result.setVisibility(View.VISIBLE);
            if (model.win != null && model.win.size() > 0) {
                winAdapter.setNewData(model.win);
                for (int i = 0; i < model.win.size(); i++) {
                    if (!TextUtils.isEmpty(model.win.get(i).uid) && TextUtils.equals(model.win.get(i).uid, String.valueOf(ownUid))) {
                        final int finalValue = i;
                        ryWin.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                winManager.scrollToPositionWithOffset(finalValue, 0);
                            }
                        }, 1000);
                        break;
                    }
                }
            }
            if (model.expe != null && model.expe.size() > 0) {
                expeAdapter.setNewData(model.expe);
                for (int i = 0; i < model.win.size(); i++) {
                    if (!TextUtils.isEmpty(model.win.get(i).uid) && TextUtils.equals(model.win.get(i).uid, String.valueOf(ownUid))) {
                        final int finalValue = i;
                        ryExpe.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                expeManager.scrollToPositionWithOffset(finalValue, 0);
                            }
                        }, 1000);
                        break;
                    }
                }
            }
        } else {
            view_end.setVisibility(View.VISIBLE);
            view_result.setVisibility(View.GONE);
        }
    }

    private void dealVoid() {
        if (isGoReslut() && isFlag) {
            mPresenter.tradeWinList();
            isFlag = false;
        }
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

    /**
     * 为true则请求奖励结果的接口
     *
     * @return
     */
    public boolean isGoReslut() {
        currentTime = DateUtils.getCurrentBeiJingTime();
        if (currentTime >= SHOW_START_RESULT) {
            return true;
        }
        return false;
    }

    public class WinAdapter extends BaseQuickAdapter<TradeWinModel, BaseViewHolder> {

        public WinAdapter() {
            super(R.layout.item_match_win);
        }

        @Override
        protected void convert(BaseViewHolder holder, TradeWinModel item) {
            holder.setText(R.id.tv_rank, String.format(getResources().getString(R.string.rank_num), item.id));
            holder.setText(R.id.tv_uid, item.name + "(" + item.uid + ")");
            holder.setText(R.id.tv_tradeAmount, item.tradeAmount + Constant.ACU_CURRENCY_NAME);
            holder.setText(R.id.tv_all, item.amount + Constant.ACU_CURRENCY_NAME);
            holder.setText(R.id.tv_reward, item.reward + Constant.ACU_CURRENCY_NAME);
        }
    }

    public class ExpeAdapter extends BaseQuickAdapter<TradeWinModel, BaseViewHolder> {

        public ExpeAdapter() {
            super(R.layout.item_match_expe);
        }

        @Override
        protected void convert(BaseViewHolder holder, TradeWinModel item) {
            holder.setText(R.id.tv_uid, item.name + "(" + item.uid + ")");
            holder.setText(R.id.tv_reward, item.reward + Constant.ACU_CURRENCY_NAME);
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_transparent));
            ((TextView) holder.getView(R.id.tv_uid)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            ((TextView) holder.getView(R.id.tv_reward)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            if (!TextUtils.isEmpty(item.uid) && TextUtils.equals(item.uid, String.valueOf(ownUid))) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_match));
                ((TextView) holder.getView(R.id.tv_uid)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                ((TextView) holder.getView(R.id.tv_reward)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            }
        }
    }
}
