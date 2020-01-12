package com.android.tacu.module.lock;

import android.text.TextUtils;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.widget.gesture.GestureLockViewGroup;

import butterknife.BindView;

/**
 * 取消手势密码
 */
public class GestureOffActivity extends BaseActivity {

    @BindView(R.id.tv_prompt_lock_off)
    TextView mTextView;
    @BindView(R.id.gesture_lock_view_group_lock_off)
    GestureLockViewGroup mGesture;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_gesture_off);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.cancel_the_pattern_lock));

        mTextView.setText(getResources().getString(R.string.draw_your_pattern_lock));
        if (!TextUtils.isEmpty(LockUtils.getGesture())) {
            mGesture.setAnswer(LockUtils.getGesture());
        }
        mGesture.setOnGestureLockViewListener(mListener);
    }

    private void gestureEvent(boolean matched) {
        if (matched) {
            LockUtils.addGesture("");
            finish();
        } else {
            if (mGesture.getTryTimes() >= 1) {
                mTextView.setText(getResources().getString(R.string.input_error) + mGesture.getTryTimes() + getResources().getString(R.string.second));
            }
        }
    }

    private void unmatchedExceedBoundary() {
        // 正常情况这里需要做处理（如退出或重登）
        showToastError(getResources().getString(R.string.too_many_input_errors));
        tokenInvalid();
        finish();
    }

    // 回调监听
    private GestureLockViewGroup.OnGestureLockViewListener mListener = new
            GestureLockViewGroup.OnGestureLockViewListener() {
                @Override
                public void onGestureEvent(boolean matched) {
                    gestureEvent(matched);
                }

                @Override
                public void onUnmatchedExceedBoundary() {
                    unmatchedExceedBoundary();
                }

                @Override
                public void onFirstSetPattern(boolean patternOk) {
                }
            };
}
