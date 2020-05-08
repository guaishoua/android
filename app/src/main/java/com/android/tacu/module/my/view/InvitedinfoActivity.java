package com.android.tacu.module.my.view;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.my.contract.InvitedinfoContract;
import com.android.tacu.module.my.model.InvitedAllModel;
import com.android.tacu.module.my.presenter.InvitedinfoPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.ImgUtils;
import com.android.tacu.utils.ScreenShootUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.ZXingUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.yanzhenjie.permission.Permission;

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
    private Bitmap picBitmap;

    private View view;
    private Dialog dialog;
    private Thread thread;

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
        if (picBitmap != null && !picBitmap.isRecycled()) {
            picBitmap.recycle();
            picBitmap = null;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        System.gc();
    }

    @OnClick(R.id.tv_invited_record)
    void invitedRecordClick() {
        jumpTo(InvitedRecordActivity.class);
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
        if (invitedAllModel != null && invitedAllModel.psoter != null && invitedAllModel.psoter.size() > 0) {
            view = View.inflate(this, R.layout.view_invited_share, null);
            ImageView img_url = view.findViewById(R.id.img_url);
            final ImageView img_qr = view.findViewById(R.id.img_qr);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveImg();
                }
            });

            GlideUtils.disPlay(this, invitedAllModel.psoter.get(0).imageUrl, img_url);
            img_qr.post(new Runnable() {
                @Override
                public void run() {
                    bitmapZxing = ZXingUtils.createQRImage(Constant.INVITED_FRIEND_URL + invitedAllModel.invitedId, img_qr.getWidth(), img_qr.getHeight());
                    if (bitmapZxing != null) {
                        img_qr.setImageBitmap(bitmapZxing);
                    }
                }
            });

            PermissionUtils.requestPermissions(this, new OnPermissionListener() {
                @Override
                public void onPermissionSucceed() {
                    showDialog();
                }

                @Override
                public void onPermissionFailed() {
                }
            }, Permission.Group.STORAGE);
        }
    }

    @Override
    public void showInvitedInfo(InvitedAllModel model) {
        if (model != null) {
            invitedAllModel = model;

            String value = "";
            value = "<big><big>" + model.total + "</big></big>" + getResources().getString(R.string.people);
            tv_invited_people.setText(Html.fromHtml(value));

            value = "<big><big>" + FormatterUtils.getFormatRoundDown(2, model.all) + "</big></big>" + Constant.ACU_CURRENCY_NAME;
            tv_accumulated_commission.setText(Html.fromHtml(value));

            value = "<big><big>" + FormatterUtils.getFormatRoundDown(2, model.pre) + "</big></big>" + Constant.ACU_CURRENCY_NAME;
            tv_forzen_commission.setText(Html.fromHtml(value));

            tvCode.setText(model.invitedId);

            String url = Constant.INVITED_FRIEND_URL + model.invitedId;
            tvUrl.setText(url);

            bitmapZxing = ZXingUtils.createQRImage(url, UIUtils.dp2px(150), UIUtils.dp2px(150));
            img_qr.setImageBitmap(bitmapZxing);
        }
    }

    private void showDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = UIUtils.getScreenWidth() * 4 / 5;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setBackgroundDrawableResource(R.color.color_transparent);
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void saveImg() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        picBitmap = ScreenShootUtils.convertViewToBitmap(view);
                        if (picBitmap != null) {
                            ImgUtils.saveImageToGallery(InvitedinfoActivity.this, picBitmap, 100);
                            showToastSuccess(getResources().getString(R.string.save_success));
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
        thread.start();
    }
}
