package com.android.tacu.module.my.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.my.contract.EditPersonalDataContact;
import com.android.tacu.module.my.presenter.EditPersonalDataPresenter;
import com.android.tacu.utils.permission.PermissionUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EditPersonalDataActivity extends BaseActivity<EditPersonalDataPresenter> implements EditPersonalDataContact.IView {

    @BindView(R.id.img_head_sculpture)
    QMUIRadiusImageView img_head_sculpture;

    private final int TAKE_PIC = 1001;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_edit_person_data);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.edit_person_data));
    }

    @Override
    protected EditPersonalDataPresenter createPresenter(EditPersonalDataPresenter mPresenter) {
        return new EditPersonalDataPresenter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == TAKE_PIC) {
            ArrayList<String> imageList = BGAPhotoPickerActivity.getSelectedImages(data);
            for (int i = 0; i < imageList.size(); i++) {
                String imageUri = imageList.get(i);
                File fileOrgin = new File(imageUri);
                new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFileAsFlowable(fileOrgin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<File>() {
                            @Override
                            public void accept(File file) {
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                            }
                        });
            }
        }
    }

    @OnClick({R.id.img_head_sculpture, R.id.img_head_sculpture_tag})
    void headClick() {
        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                boolean mTakePhotoEnabled = true;
                // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "tacu");
                startActivityForResult(BGAPhotoPickerActivity.newIntent(EditPersonalDataActivity.this, mTakePhotoEnabled ? takePhotoDir : null, 1, null, mTakePhotoEnabled), TAKE_PIC);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
    }
}
