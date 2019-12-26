package com.android.tacu.module.my.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.main.model.GoogleAuthModel;
import com.android.tacu.module.my.contract.GoogleAuthContact;
import com.android.tacu.module.my.presenter.GoogleAuthPresenter;
import com.android.tacu.utils.ImgUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.yanzhenjie.permission.Permission;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by xiaohong on 2018/9/7.
 * 谷歌绑定第一步与第二步
 */

public class GoogleHintActivity extends BaseActivity<GoogleAuthPresenter> implements GoogleAuthContact.IGAView {
    @BindView(R.id.tv_ga_address)
    TextView tv_ga_address;
    @BindView(R.id.iv_ga_code)
    ImageView iv_ga_code;
    @BindView(R.id.btn_next)
    QMUIAlphaButton btn_next;
    @BindView(R.id.ll_google_one)
    LinearLayout ll_google_one;
    @BindView(R.id.ll_google_two)
    LinearLayout ll_google_two;

    private String secretKey;
    private Bitmap bitmap;
    //true：第一个页面  false：第二个页面
    private boolean isFlag = true;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_google_hint);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.bind_ga));
        ll_google_one.setVisibility(View.VISIBLE);
        ll_google_two.setVisibility(View.GONE);

        mTopBar.removeAllLeftViews();
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    @Override
    protected GoogleAuthPresenter createPresenter(GoogleAuthPresenter mPresenter) {
        return new GoogleAuthPresenter();
    }

    @Override
    protected void onPresenterCreated(GoogleAuthPresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.getSecretKey();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
        }
        return true;
    }

    private void goBack() {
        if (isFlag) {
            finish();
        } else {
            isFlag = true;
            ll_google_one.setVisibility(View.VISIBLE);
            ll_google_two.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_next)
    void netx() {
        if (isFlag) {
            isFlag = false;
            ll_google_one.setVisibility(View.GONE);
            ll_google_two.setVisibility(View.VISIBLE);
        } else {
            jumpTo(GoogleAuthActivity.createActivty(GoogleHintActivity.this, true));
        }
    }

    @OnLongClick(R.id.tv_ga_address)
    boolean gaPwdLongClick() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(secretKey); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
        return true;
    }

    @OnClick(R.id.iv_ga_code)
    void copyCode() {
        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                ImgUtils.saveImageToGallery(GoogleHintActivity.this, bitmap);
                showToastSuccess(getResources().getString(R.string.save_image));
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE);
    }

    @OnClick(R.id.ll_google_one)
    void upload() {
        Intent intent = new Intent();
        Uri content_url = Uri.parse("http://sj.qq.com/myapp/detail.htm?apkName=com.google.android.apps.authenticator2");
        intent.setAction("android.intent.action.VIEW");
        intent.setData(content_url);
        startActivity(intent);
    }

    @Override
    public void getSecretKey(GoogleAuthModel model) {
        String imCode = model.qrcode;
        secretKey = model.secretKey;
        tv_ga_address.setText(secretKey);
        byte b[] = Base64.decode(imCode.getBytes(), Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        iv_ga_code.setImageBitmap(bitmap);
    }

    @OnClick(R.id.ll_ga_copy)
    void onCopyClicked() {
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(secretKey); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        showToastSuccess(getResources().getString(R.string.copy_success));
    }
}
