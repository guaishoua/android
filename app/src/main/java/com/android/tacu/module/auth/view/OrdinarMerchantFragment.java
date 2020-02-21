package com.android.tacu.module.auth.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;
import com.android.tacu.module.vip.view.BuyVipActivity;
import com.android.tacu.utils.LogUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.view.GlideLoader;
import com.lcw.library.imagepicker.ImagePicker;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class OrdinarMerchantFragment extends BaseFragment<AuthMerchantPresenter> implements AuthMerchantContract.IOrdinarView {

    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;
    @BindView(R.id.btn_submit)
    QMUIRoundButton btn_submit;

    private static final int REQUEST_SELECT_IMAGES_CODE = 1001;
    private ArrayList<String> mImagePaths;

    public static OrdinarMerchantFragment newInstance() {
        Bundle bundle = new Bundle();
        OrdinarMerchantFragment fragment = new OrdinarMerchantFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_ordinar_merchant;
    }

    @Override
    protected void initData(View view) {
        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.auth_xieyi1) + "<font color=" + ContextCompat.getColor(getContext(), R.color.text_default) + ">" + getResources().getString(R.string.auth_xieyi2) + "</font>"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
        }
    }

    @OnClick(R.id.tv_membership_right)
    void buyVip() {
        jumpTo(BuyVipActivity.class);
    }

    @OnClick(R.id.btn_upload_video)
    void uploadClick() {
        PermissionUtils.requestPermissions(getContext(), new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                ImagePicker.getInstance()
                        .setTitle("")//设置标题
                        .showCamera(false)//设置是否显示拍照按钮
                        .showImage(false)//设置是否展示图片
                        .showVideo(true)//设置是否展示视频
                        .setSingleType(true)//设置图片视频不能同时选择
                        .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                        .setImagePaths(mImagePaths)//保存上一次选择图片的状态，如果不需要可以忽略
                        .setImageLoader(new GlideLoader())//设置自定义图片加载器
                        .start(getHostActivity(), REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
    }

    @OnClick(R.id.btn_submit)
    void submitClick() {
        LogUtils.i("jiazhen", "111");
    }
}
