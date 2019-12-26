package com.android.tacu.widget.tab;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;

/**
 * 选择栏
 * Created by jiazhen on 2018/9/24.
 */
public class TabView extends RelativeLayout {

    private TextView mTextView;
    private View mIndicator;
    private int mSelectColor;
    private int mUnSelectColor;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView(LayoutInflater.from(getContext()).inflate(R.layout.item_tab, null));
        mTextView = findViewById(R.id.tab_text);
        mIndicator = findViewById(R.id.indicator);

        setTextColor(ContextCompat.getColor(context, R.color.text_default), ContextCompat.getColor(context, R.color.text_white));
        setIndicatorColor(ContextCompat.getColor(context, R.color.text_default));

        setSelected(false);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        mTextView.setTextColor(selected ? mSelectColor : mUnSelectColor);
        mIndicator.setVisibility(selected ? VISIBLE : INVISIBLE);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setTextColor(int selectColor, int unSelectColor) {
        this.mSelectColor = selectColor;
        this.mUnSelectColor = unSelectColor;
        mTextView.setTextColor(unSelectColor);
    }

    public void setIndicatorColor(int color) {
        mIndicator.setBackgroundColor(color);
    }
}
