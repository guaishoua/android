package com.android.tacu.module.my.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.my.contract.InvitedinfoContract;
import com.android.tacu.module.my.model.InvitedAllModel;
import com.android.tacu.module.my.presenter.InvitedinfoPresenter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.ZXingUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class InvitedinfoActivity extends BaseActivity<InvitedinfoPresenter> implements InvitedinfoContract.IView {

    @BindView(R.id.tv_url)
    TextView tvUrl;
    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.img_qr)
    ImageView img_qr;
    @BindView(R.id.tv_qr_tip)
    TextView tv_qr_tip;

    private InvitedAllModel invitedAllModel;
    private Bitmap bitmapZxing;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_invitedinfo);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.Inviting_friends));
    }

    @Override
    protected InvitedinfoPresenter createPresenter(InvitedinfoPresenter mPresenter) {
        return new InvitedinfoPresenter();
    }

    @Override
    protected void onPresenterCreated(InvitedinfoPresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.getInvitedInfo(1, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmapZxing != null && !bitmapZxing.isRecycled()) {
            bitmapZxing.recycle();
            bitmapZxing = null;
        }
    }

    /**
     * 邀请人数
     */
    @OnClick(R.id.rl_people)
    void invitePerson() {
        jumpTo(InvitedRecordActivity.class);
    }

    @OnClick(R.id.bt_poster)
    void poster() {
        if (invitedAllModel != null && invitedAllModel.psoter != null) {
            jumpTo(InvitePosterActivity.createActivity(this, invitedAllModel));
        }
    }

    @OnClick(R.id.img_invitation2)
    void copyInvitation() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(tvUrl.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
    }

    @OnClick(R.id.img_invitation)
    void copyInvitation2() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(tvCode.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
    }

    @OnLongClick(R.id.rl_invitation2)
    boolean longInvitation() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(tvUrl.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
        return true;
    }

    @OnLongClick(R.id.rl_invitation)
    boolean longInvitation2() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(tvCode.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
        return true;
    }

    @Override
    public void showInvitedInfo(InvitedAllModel model) {
        if (model != null) {
            invitedAllModel = model;
            String url = Constant.INVITED_FRIEND_URL + model.invitedId;
            tvCode.setText(model.invitedId);
            tvUrl.setText(url);
            tvCount.setText(String.valueOf(model.total));

            img_qr.setVisibility(View.VISIBLE);
            tv_qr_tip.setVisibility(View.VISIBLE);
            bitmapZxing = ZXingUtils.createQRImage(url, UIUtils.dp2px(150), UIUtils.dp2px(150));
            img_qr.setImageBitmap(bitmapZxing);
        }
    }
}
