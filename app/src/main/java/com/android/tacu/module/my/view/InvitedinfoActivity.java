package com.android.tacu.module.my.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.my.contract.InvitedinfoContract;
import com.android.tacu.module.my.model.InvitedAllModel;
import com.android.tacu.module.my.presenter.InvitedinfoPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.ZXingUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class InvitedinfoActivity extends BaseActivity<InvitedinfoPresenter> implements InvitedinfoContract.IView {

    @BindView(R.id.tv_invited_people)
    TextView tv_invited_people;
    @BindView(R.id.tv_accumulated_commission)
    TextView tv_accumulated_commission;
    @BindView(R.id.tv_forzen_commission)
    TextView tv_forzen_commission;

    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.tv_url)
    TextView tvUrl;
    @BindView(R.id.img_qr)
    ImageView img_qr;

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
        mPresenter.getInvitedInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmapZxing != null && !bitmapZxing.isRecycled()) {
            bitmapZxing.recycle();
            bitmapZxing = null;
        }
        System.gc();
    }

    @OnClick(R.id.tv_invited_record)
    void invitedRecordClick() {

    }

    @OnClick({R.id.tv_code, R.id.img_code})
    void codeClick() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(tvCode.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
    }

    @OnClick({R.id.tv_url, R.id.img_url})
    void urlClick() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(tvUrl.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
    }

    @OnClick(R.id.bt_poster)
    void posterClick() {

    }

    @Override
    public void showInvitedInfo(InvitedAllModel model) {
        if (model != null) {
            invitedAllModel = model;

            String value = "";
            value = "<big>" + model.total + "</big>" + getResources().getString(R.string.people);
            tv_invited_people.setText(Html.fromHtml(value));

            value = "<big>" + FormatterUtils.getFormatRoundDown(2, model.all) + "</big>" + Constant.ACU_CURRENCY_NAME;
            tv_accumulated_commission.setText(value);

            value = "<big>" + FormatterUtils.getFormatRoundDown(2, model.pre) + "</big>" + Constant.ACU_CURRENCY_NAME;
            tv_forzen_commission.setText(value);

            tvCode.setText(model.invitedId);

            String url = Constant.INVITED_FRIEND_URL + model.invitedId;
            tvUrl.setText(url);

            bitmapZxing = ZXingUtils.createQRImage(url, UIUtils.dp2px(150), UIUtils.dp2px(150));
            img_qr.setImageBitmap(bitmapZxing);
        }
    }
}
