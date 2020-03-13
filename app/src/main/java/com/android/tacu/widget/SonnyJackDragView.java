package com.android.tacu.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * 可拖动的悬浮按钮
 * Created by linqs on 2017/12/21.
 */

public class SonnyJackDragView implements View.OnTouchListener {
    private Builder mBuilder;

    private int mStatusBarHeight, mScreenWidth, mScreenHeight;

    //手指按下位置
    private int mStartX, mStartY, mLastX, mLastY;
    private boolean mTouchResult = false;

    private SonnyJackDragView(SonnyJackDragView.Builder builder) {
        mBuilder = builder;
        initDragView();
    }

    public View getDragView() {
        return mBuilder.view;
    }

    public Activity getActivity() {
        return mBuilder.activity;
    }

    private void initDragView() {
        if (null == getActivity()) {
            throw new NullPointerException("the activity is null");
        }
        if (null == mBuilder.view) {
            throw new NullPointerException("the dragView is null");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mBuilder.activity.isDestroyed()) {
            return;
        }

        //屏幕宽高
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight = displayMetrics.heightPixels;
        }

        //状态栏高度
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusBarHeight = frame.top;
        if (mStatusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                mStatusBarHeight = getActivity().getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        FrameLayout rootLayout = (FrameLayout) getActivity().getWindow().getDecorView();
        int left = mScreenWidth - mBuilder.defaultRight - mBuilder.size;
        FrameLayout.LayoutParams layoutParams = createLayoutParams(left, mScreenHeight - mBuilder.defaultBottom, 0, 0);
        rootLayout.addView(getDragView(), layoutParams);
        getDragView().setOnTouchListener(this);
    }

    private static SonnyJackDragView createDragView(SonnyJackDragView.Builder builder) {
        if (null == builder) {
            throw new NullPointerException("the param builder is null when execute method createDragView");
        }
        if (null == builder.activity) {
            throw new NullPointerException("the activity is null");
        }
        if (null == builder.view) {
            throw new NullPointerException("the view is null");
        }
        SonnyJackDragView sonnyJackDragView = new SonnyJackDragView(builder);
        return sonnyJackDragView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchResult = false;
                mStartX = mLastX = (int) event.getRawX();
                mStartY = mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int left, top, right, bottom;
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                left = v.getLeft() + dx;
                if (left < mScreenWidth - mBuilder.defaultRight - v.getWidth()) {
                    left = mScreenWidth - mBuilder.defaultRight - v.getWidth();
                }
                right = left + v.getWidth();
                if (right > mScreenWidth - mBuilder.defaultRight) {
                    right = mScreenWidth - mBuilder.defaultRight;
                    left = right - v.getWidth();
                }
                top = v.getTop() + dy;
                if (top < mStatusBarHeight) {
                    top = mStatusBarHeight;
                }
                bottom = top + v.getHeight();
                if (bottom > mScreenHeight) {
                    bottom = mScreenHeight;
                    top = bottom - v.getHeight();
                }

                v.setLayoutParams(createLayoutParams(left, top, right, bottom));
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getRawX();
                float endY = event.getRawY();
                if (Math.abs(endX - mStartX) > 15 || Math.abs(endY - mStartY) > 15) {
                    //防止点击的时候稍微有点移动点击事件被拦截了
                    mTouchResult = true;
                }
                break;
        }
        return mTouchResult;
    }

    private FrameLayout.LayoutParams createLayoutParams(int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mBuilder.size, mBuilder.size);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static class Builder {
        private Activity activity;
        private int size = FrameLayout.LayoutParams.WRAP_CONTENT;
        private int defaultRight = 0;
        private int defaultBottom = 0;
        private View view;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setDefaultRight(int right) {
            this.defaultRight = right;
            return this;
        }

        public Builder setDefaultBottom(int bottom) {
            this.defaultBottom = bottom;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public SonnyJackDragView build() {
            return createDragView(this);
        }
    }
}