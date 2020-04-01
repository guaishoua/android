package com.github.tifezh.kchartlib.chart.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.utils.ViewUtil;

public class IndexKlineActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_back;

    //MA
    private RelativeLayout view_ma;
    private LinearLayout view_setting_ma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_kline);
        initView();
    }

    private void initView() {
        img_back = findViewById(R.id.img_back);

        view_ma = findViewById(R.id.view_ma);
        view_setting_ma = findViewById(R.id.view_setting_ma);

        img_back.setOnClickListener(this);
        view_ma.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_back) {
            finish();
        } else if (id == R.id.view_ma) {
            if (view_setting_ma.getVisibility() == View.VISIBLE) {
                animateClose(view_setting_ma);
            } else {
                animateOpen(view_setting_ma, ViewUtil.Dp2Px(this, 310));
            }
        }
    }

    private void animateOpen(View v, int mHiddenViewMeasuredHeight) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(v, 0, mHiddenViewMeasuredHeight);
        animator.start();
    }

    private void animateClose(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
