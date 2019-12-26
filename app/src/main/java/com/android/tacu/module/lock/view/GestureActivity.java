package com.android.tacu.module.lock.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.my.view.SecurityCenterActivity;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.widget.gesture.GestureLockViewGroup;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;

public class GestureActivity extends BaseActivity {

    @BindView(R.id.tv_prompt_lock)
    TextView mTextView;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPassword;
    @BindView(R.id.gesture_lock_view_group_lock)
    GestureLockViewGroup mGesture;

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_gesture);
        //禁止唤起手势页和指纹页
        disablePatternLock(false);
    }

    @Override
    protected void initView() {
        mTextView.setText(getResources().getString(R.string.draw_your_pattern_lock));
        tv_phone.setText(spUtil.getPhone());

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(SecurityCenterActivity.class);
            }
        });
        if (!TextUtils.isEmpty(LockUtils.getLockSetting().getGesture())) {
            mGesture.setAnswer(LockUtils.getLockSetting().getGesture());
        }
        mGesture.setOnGestureLockViewListener(mListener);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 2000) {
            showToast(getResources().getString(R.string.exit));
            firstTime = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(getApplicationContext());
            activityManage.appExit();
        }
    }

    /**
     * 处理手势图案的输入结果
     *
     * @param matched
     */
    private void gestureEvent(boolean matched) {
        if (matched) {
            setResult(Constant.PWD_RESULT);
            finish();
        } else {
            if (mGesture.getTryTimes() >= 1) {
                mTextView.setText(getResources().getString(R.string.input_error) + mGesture.getTryTimes() + getResources().getString(R.string.second));
            }
        }
    }

    /**
     * 处理输错次数超限的情况
     */
    private void unmatchedExceedBoundary() {
        //正常情况这里需要做处理（如退出或重登）
        showToastError(getResources().getString(R.string.too_many_input_errors));
        tokenInvalid();
        finish();
    }

    // 手势操作的回调监听
    private GestureLockViewGroup.OnGestureLockViewListener mListener = new GestureLockViewGroup.OnGestureLockViewListener() {

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
