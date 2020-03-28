package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageOrderActivity extends BaseActivity {

    @BindView(R.id.tv_wait)
    TextView tv_wait;
    @BindView(R.id.lin_wait_point)
    QMUIRoundLinearLayout lin_wait_point;
    @BindView(R.id.view_wait)
    View view_wait;

    @BindView(R.id.tv_finish)
    TextView tv_finish;
    @BindView(R.id.lin_finish_point)
    QMUIRoundLinearLayout lin_finish_point;
    @BindView(R.id.view_finish)
    View view_finish;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private List<Fragment> fragmentList = new ArrayList<>();
    private OtcManageWaitFragment waitFragment;
    private OtcManageFinishFragment finishFragment;

    private String orderId;
    private int type = 0;// 0=待处理 1= 已完成

    public static Intent createActivity(Context context, String orderId) {
        Intent intent = new Intent(context, OtcManageOrderActivity.class);
        intent.putExtra("orderId", orderId);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage_order);
    }

    @Override
    protected void initView() {
        orderId = getIntent().getStringExtra("orderId");

        mTopBar.setTitle(getResources().getString(R.string.shoper_order));

        waitFragment = OtcManageWaitFragment.newInstance(orderId);
        finishFragment = OtcManageFinishFragment.newInstance(orderId);

        fragmentList.add(waitFragment);
        fragmentList.add(finishFragment);
        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewpager.setOffscreenPageLimit(fragmentList.size() - 1);

        waitFragment.setOnWaitLister(new onWaitLister() {
            @Override
            public void onWaitL(List<OtcTradeAllModel> list) {
                if (list != null && list.size() > 0) {
                    int num = 0;
                    OtcTradeAllModel item;
                    for (int i = 0; i < list.size(); i++) {
                        item = list.get(i);
                        if (item.tradeModel.buyuid == spUtil.getUserUid() && item.tradeModel.buyreadstatus != null && item.tradeModel.buyreadstatus == 0) {
                            num += 1;
                        } else if (item.tradeModel.selluid == spUtil.getUserUid() && item.tradeModel.sellreadstatus != null && item.tradeModel.sellreadstatus == 0) {
                            num += 1;
                        }
                    }
                    if (num > 0) {
                        lin_wait_point.setVisibility(View.VISIBLE);
                    } else {
                        lin_wait_point.setVisibility(View.GONE);
                    }
                } else {
                    lin_wait_point.setVisibility(View.GONE);
                }
            }
        });
        finishFragment.setOnFinishLister(new onFinishLister() {
            @Override
            public void onFinishL(List<OtcTradeAllModel> list) {
                if (list != null && list.size() > 0) {
                    int num = 0;
                    OtcTradeAllModel item;
                    for (int i = 0; i < list.size(); i++) {
                        item = list.get(i);
                        if (item.tradeModel.buyuid == spUtil.getUserUid() && item.tradeModel.buyreadstatus != null && item.tradeModel.buyreadstatus == 0) {
                            num += 1;
                        } else if (item.tradeModel.selluid == spUtil.getUserUid() && item.tradeModel.sellreadstatus != null && item.tradeModel.sellreadstatus == 0) {
                            num += 1;
                        }
                    }
                    if (num > 0) {
                        lin_finish_point.setVisibility(View.VISIBLE);
                    } else {
                        lin_finish_point.setVisibility(View.GONE);
                    }
                } else {
                    lin_finish_point.setVisibility(View.GONE);
                }
            }
        });

        setShowBtn();
    }

    @OnClick(R.id.rl_wait)
    void waitClick() {
        type = 0;
        setShowBtn();
    }

    @OnClick(R.id.rl_finish)
    void finishClick() {
        type = 1;
        setShowBtn();
    }

    private void setShowBtn() {
        tv_wait.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        tv_finish.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        view_wait.setVisibility(View.GONE);
        view_finish.setVisibility(View.GONE);

        if (type == 0) {
            tv_wait.setTextColor(ContextCompat.getColor(this, R.color.text_default));
            view_wait.setVisibility(View.VISIBLE);
            viewpager.setCurrentItem(0);
        } else if (type == 1) {
            tv_finish.setTextColor(ContextCompat.getColor(this, R.color.text_default));
            view_finish.setVisibility(View.VISIBLE);
            viewpager.setCurrentItem(1);
        }
    }

    public interface onWaitLister {
        void onWaitL(List<OtcTradeAllModel> list);
    }

    public interface onFinishLister {
        void onFinishL(List<OtcTradeAllModel> list);
    }
}
