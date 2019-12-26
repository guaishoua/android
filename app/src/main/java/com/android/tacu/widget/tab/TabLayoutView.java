package com.android.tacu.widget.tab;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;

/**
 * Created by jiazhen on 2018/9/24.
 */
public class TabLayoutView extends RelativeLayout implements View.OnClickListener {

    private TabSelectListener mTabSelectListener = null;
    private EnLargeSelectListener mEnLargeSelectListener = null;
    private TabView previousTabView = null;
    private int mSelectedIndex = 0;

    private int mSelectColor;
    private int mUnSelectColor;

    public TabLayoutView(Context context) {
        this(context, null);
    }

    public TabLayoutView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View view) {
        if (mSelectedIndex >= 0 && mSelectedIndex < getChildCount()) {
            getChildAt(mSelectedIndex).setSelected(false);
        }
        view.setSelected(true);
        mSelectedIndex = indexOfChild(view);
        mTabSelectListener.onTabSelected(mSelectedIndex);
    }

    public void addTab(String text) {
        TabView tabView = new TabView(getContext());
        tabView.setId(Integer.MAX_VALUE - 1000 - getChildCount());
        tabView.setOnClickListener(this);
        tabView.setText(text);
        tabView.setTextColor(mSelectColor, mUnSelectColor);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (previousTabView == null) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            lp.addRule(RelativeLayout.RIGHT_OF, previousTabView.getId());
        }
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        previousTabView = tabView;
        addView(tabView, lp);
    }

    public void addImageView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.icon_enlarge_white);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnLargeSelectListener != null) {
                    mEnLargeSelectListener.onLargeSelected();
                }
            }
        });
        LayoutParams lp = new LayoutParams(UIUtils.dp2px(20), UIUtils.dp2px(20));
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(imageView, lp);
    }

    public void setTabText(int index, String text) {
        ((TabView) getChildAt(index)).setText(text);
    }

    public void setTabSelected(int index) {
        ((TabView) getChildAt(index)).setSelected(true);
        mSelectedIndex = index;
    }

    public void setTextColor(int selectColor, int unSelectColor) {
        this.mSelectColor = selectColor;
        this.mUnSelectColor = unSelectColor;
    }

    /**
     * 设置选项卡监听
     *
     * @param listener TabSelectListener
     */
    public void setOnTabSelectListener(TabSelectListener listener) {
        this.mTabSelectListener = listener;
    }

    public void setOnLargeSelectListener(EnLargeSelectListener listener) {
        this.mEnLargeSelectListener = listener;
    }

    public interface TabSelectListener {
        /**
         * 选择tab的位置序号
         *
         * @param position
         */
        void onTabSelected(int position);
    }

    public interface EnLargeSelectListener {
        /**
         * 选择tab的位置序号
         */
        void onLargeSelected();
    }
}
