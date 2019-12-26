package com.android.tacu.module.lock.view;

import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.widget.gesture.GestureLockViewGroup;

import butterknife.BindView;

/**
 * 设置手势密码
 */
public class GestureOnActivity extends BaseActivity {

    @BindView(R.id.tv_prompt_lock_on)
    TextView mTextView;
    @BindView(R.id.gesture_lock_view_group_lock_on)
    GestureLockViewGroup mGesture;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_gesture_on);
        disablePatternLock(false);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.set_pattern_lock));

        mTextView.setText(getResources().getString(R.string.draw_your_pattern_lock));
        mGesture.isFirstSet(true);
        mGesture.setUnMatchExceedBoundary(10000);
        mGesture.setOnGestureLockViewListener(mListener);
    }

    private void gestureEvent(boolean matched) {
        if (matched) {
            LockUtils.addGesture(mGesture.getChooseStr());
            finish();
        } else {
            mTextView.setText(getResources().getString(R.string.pwd_do_not));
        }
    }

    private void firstSetPattern(boolean patternOk) {
        if (patternOk) {
            mTextView.setText(getResources().getString(R.string.please_draw_the_pattern_again_to_confirm));
        } else {
            mTextView.setText(getResources().getString(R.string.require_more_than_points));
        }
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
                }

                @Override
                public void onFirstSetPattern(boolean patternOk) {
                    firstSetPattern(patternOk);
                }
            };
}
