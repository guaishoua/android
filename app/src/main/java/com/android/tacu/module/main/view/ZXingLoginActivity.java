package com.android.tacu.module.main.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.main.contract.ZXingLoginContract;
import com.android.tacu.module.main.presenter.ZXingLoginPresenter;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jiazhen on 2018/12/5.
 */
public class ZXingLoginActivity extends BaseActivity<ZXingLoginPresenter> implements ZXingLoginContract.IView {

    @BindView(R.id.btnLogin)
    QMUIRoundButton btnLogin;
    @BindView(R.id.btnCancle)
    QMUIAlphaButton btnCancle;

    private String UUID;

    public static Intent creatActivity(Context context, String UUID) {
        Intent intent = new Intent(context, ZXingLoginActivity.class);
        intent.putExtra("UUID", UUID);
        return intent;
    }

    @Override
    protected ZXingLoginPresenter createPresenter(ZXingLoginPresenter mPresenter) {
        return new ZXingLoginPresenter();
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_zxing_login);
    }

    @Override
    protected void initView() {
        mTopBar.removeAllLeftViews();
        mTopBar.addLeftTextButton(getResources().getString(R.string.closed), R.id.qmui_topbar_item_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UUID = getIntent().getStringExtra("UUID");
    }

    @OnClick(R.id.btnLogin)
    void btnLoginClick() {
        if (!TextUtils.isEmpty(UUID)) {
            mPresenter.sendZxing(UUID);
        }
    }

    @OnClick(R.id.btnCancle)
    void btnCancleClick() {
        finish();
    }

    @Override
    public void sendZxingSuccess() {
        finish();
    }
}
