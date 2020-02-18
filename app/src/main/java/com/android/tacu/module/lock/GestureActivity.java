package com.android.tacu.module.lock;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseModel;
import com.android.tacu.db.model.LockNewModel;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.login.contract.LoginContract;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.login.presenter.LoginPresenter;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.gesture.GestureLockViewGroup;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

public class GestureActivity extends BaseActivity<LoginPresenter> implements LoginContract.IView, ISwitchView {

    @BindView(R.id.tv_prompt_lock)
    TextView mTextView;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.gesture_lock_view_group_lock)
    GestureLockViewGroup mGesture;

    private Gson gson = new Gson();
    private SwitchPresenter switchPresenter;
    private int interIndex;
    private int interAll;
    private boolean isGoMain = false;
    private boolean isClearTop = false;

    /**
     * @param context
     * @param isGoMain   为true的情况下  登录成功直接跳转MainActivity
     * @param isClearTop A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @return
     */
    public static Intent createActivity(Context context, boolean isGoMain, boolean isClearTop) {
        Intent intent = new Intent(context, GestureActivity.class);
        intent.putExtra("isGoMain", isGoMain);
        intent.putExtra("isClearTop", isClearTop);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_gesture);
    }

    @Override
    protected void initView() {
        isGoMain = getIntent().getBooleanExtra("isGoMain", false);
        isClearTop = getIntent().getBooleanExtra("isClearTop", false);

        mTextView.setText(getResources().getString(R.string.draw_your_pattern_lock));
        tv_phone.setText(spUtil.getAccount());

        if (!TextUtils.isEmpty(LockUtils.getGesture())) {
            mGesture.setAnswer(LockUtils.getGesture());
        }
        mGesture.setOnGestureLockViewListener(mListener);
    }

    @Override
    protected LoginPresenter createPresenter(LoginPresenter mPresenter) {
        return new LoginPresenter();
    }

    @Override
    protected void onPresenterCreated(LoginPresenter presenter) {
        super.onPresenterCreated(presenter);
        switchPresenter = new SwitchPresenter(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (switchPresenter != null) {
            switchPresenter.destroy();
            switchPresenter = null;
        }
    }

    @OnClick(R.id.tv_account_login)
    void accountLoginClick() {
        jumpTo(LoginActivity.createActivity(GestureActivity.this, isGoMain, isClearTop, true));
        finish();
    }

    @Override
    public void switchSuccess(String token) {
        LockNewModel lockNewModel = LockUtils.getLockNewModel();
        if (lockNewModel != null) {
            mPresenter.login(lockNewModel.getAccountString(), lockNewModel.getAccountPwd(), token);
        }
    }

    @Override
    public void showContent(BaseModel<LoginModel> model) {
        if (model != null) {
            // 5555谷歌认证，暂时关闭
            if (model.status != 200 && model.status != 5555) {
                showToastError(model.message);
            }
            if (model.status == 200) {
                UserManageUtils.login(model);
                mustNeedInfo();
            }
        }
    }

    @Override
    public void ownCenterSuccess(OwnCenterModel model) {
        if (model != null) {
            UserManageUtils.setPersonInfo(model);

            getMustNeed();
        }
    }

    @Override
    public void ownCenterError() {
        getMustNeed();
    }

    @Override
    public void getSelfSelectionValueSuccess(SelfModel selfModel) {
        if (selfModel == null) {
            selfModel = new SelfModel();
        }
        SPUtils.getInstance().put(Constant.SELFCOIN_LIST, gson.toJson(selfModel));
        getMustNeed();
    }

    @Override
    public void getSelfSelectionValueError() {
        getMustNeed();
    }

    /**
     * 登录成功后 需要掉一些接口获取数据
     */
    private void mustNeedInfo() {
        interIndex = 0;
        interAll = 2;
        mPresenter.ownCenter();
        mPresenter.getSelfList();
    }

    private void getMustNeed() {
        interIndex++;
        if (interIndex >= interAll) {
            showToastSuccess(getResources().getString(R.string.login_success));
            if (isGoMain) {
                jumpTo(MainActivity.class);
            }
            finish();
        }
    }

    /**
     * 处理手势图案的输入结果
     *
     * @param matched
     */
    private void gestureEvent(boolean matched) {
        if (matched) {
            switchPresenter.switchView();
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
        jumpTo(LoginActivity.createActivity(GestureActivity.this, isGoMain, isClearTop, true));
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
