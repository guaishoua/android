package com.android.tacu.module.otc.view;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;

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

    private int type = 0;// 0=待处理 1= 已完成

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage_order);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.shoper_order));

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
}
