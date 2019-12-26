package com.android.tacu.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2019/4/17.
 */
public class ExpandView extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    // 是否已经展开
    private boolean flag = false;

    private final int MINHEIGHT = UIUtils.dp2px(25);

    private ConstraintLayout viewTitle;
    private TextView tvTitle;
    private RecyclerView recyclerView;
    private ExpandAdapter expandAdapter;

    private String title;
    private List<String> dataList = new ArrayList<>();

    public ExpandView(Context context) {
        this(context, null);
    }

    public ExpandView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ExpandView);
        title = arr.getString(R.styleable.ExpandView_titleText);
        arr.recycle();

        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_expand, this);
        viewTitle = findViewById(R.id.view_title);
        tvTitle = findViewById(R.id.tv_expand_title);
        recyclerView = findViewById(R.id.recyclerView);

        tvTitle.setText(title);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expandAdapter = new ExpandAdapter();
        recyclerView.setAdapter(expandAdapter);

        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height = MINHEIGHT;
        recyclerView.setLayoutParams(layoutParams);

        viewTitle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_title:
                expand();
                break;
        }
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setData(List<String> list) {
        this.dataList = list;
        if (list != null && list.size() > 0) {
            expandAdapter.setNewData(list);
        }
    }

    private void expand() {
        int startHeight;
        int targetHeight;
        if (flag) {
            startHeight = getMaxMeasureHeight();
            targetHeight = getMinMeasureHeight();
            if (startHeight < targetHeight) {
                startHeight = targetHeight;
            }
        } else {
            startHeight = getMinMeasureHeight();
            targetHeight = getMaxMeasureHeight();
            if (targetHeight < startHeight) {
                targetHeight = startHeight;
            }
        }
        flag = !flag;
        final ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(startHeight, targetHeight);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.height = (int) animation.getAnimatedValue();
                recyclerView.setLayoutParams(layoutParams);
            }
        });
        animator.start();
    }

    private class ExpandAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public ExpandAdapter() {
            super(R.layout.item_expand_view);
        }

        @Override
        protected void convert(BaseViewHolder helper, final String item) {
            helper.setText(R.id.tv_content, item);
        }
    }

    /**
     * 获取最大高度
     *
     * @return int 最大测量高度
     */
    public int getMaxMeasureHeight() {
        return dataList != null && dataList.size() > 0 ? MINHEIGHT * dataList.size() + UIUtils.dp2px(5) : 0;
    }

    /**
     * 获取最小高度
     *
     * @return int 最小测量高度
     */
    public int getMinMeasureHeight() {
        return dataList != null && dataList.size() > 0 ? MINHEIGHT : 0;
    }
}
