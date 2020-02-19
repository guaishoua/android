package com.android.tacu.module.assets.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.PayInfoEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.base.MyApplication;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.contract.BindingPayInfoContract;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.assets.presenter.BindingPayInfoPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.view.GlideLoader;
import com.lcw.library.imagepicker.ImagePicker;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class BindingInfoZfbFragment extends BaseFragment<BindingPayInfoPresenter> implements BindingPayInfoContract.IZfbView {

    @BindView(R.id.lin_edit)
    LinearLayout lin_edit;
    @BindView(R.id.tv_account_owner)
    TextView tv_account_owner;
    @BindView(R.id.edit_zfb_name)
    EditText edit_zfb_name;
    @BindView(R.id.lin_trade_pwd)
    LinearLayout lin_trade_pwd;
    @BindView(R.id.edit_trade_password)
    EditText edit_trade_password;
    @BindView(R.id.img_zfb_shoukuan)
    QMUIRadiusImageView img_zfb_shoukuan;
    @BindView(R.id.rl_upload)
    QMUIRoundRelativeLayout rl_upload;

    @BindView(R.id.lin_list)
    LinearLayout lin_list;
    @BindView(R.id.tv_account_owner1)
    TextView tv_account_owner1;
    @BindView(R.id.tv_zfb_name)
    TextView tv_zfb_name;
    @BindView(R.id.img_zfb_shoukuan1)
    QMUIRadiusImageView img_zfb_shoukuan1;

    private static final int REQUEST_SELECT_IMAGES_CODE = 1001;
    private ArrayList<String> mImagePaths;
    private PayInfoModel payInfoModel;

    //oss
    private String imageName;
    private OSS mOss = null;
    private String bucketName;
    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    hideLoadingView();
                    GlideUtils.disPlay(getContext(), Constant.UPLOAD_IMG_URL + imageName, img_zfb_shoukuan);
                    rl_upload.setVisibility(View.GONE);
                    break;
                case 1:
                    showToastError(getResources().getString(R.string.net_busy));
                    hideLoadingView();
                    rl_upload.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public static BindingInfoZfbFragment newInstance() {
        Bundle bundle = new Bundle();
        BindingInfoZfbFragment fragment = new BindingInfoZfbFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_zfb;
    }

    @Override
    protected void initData(View view) {
        if (spUtil.getPwdVisibility()) {
            lin_trade_pwd.setVisibility(View.VISIBLE);
        } else {
            lin_trade_pwd.setVisibility(View.GONE);
        }
        tv_account_owner.setText(spUtil.getKYCName());
        tv_account_owner1.setText(spUtil.getKYCName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            for (int i = 0; i < mImagePaths.size(); i++) {
                String imageUri = mImagePaths.get(i);
                File fileOrgin = new File(imageUri);
                new Compressor(getContext())
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
                                showLoadingView();
                                mPresenter.getOssSetting(2, file.getPath());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                            }
                        });
            }
        }
    }

    @OnClick({R.id.img_zfb_shoukuan, R.id.rl_upload})
    void wxImageClick() {
        PermissionUtils.requestPermissions(getContext(), new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                ImagePicker.getInstance()
                        .setTitle("")//设置标题
                        .showCamera(true)//设置是否显示拍照按钮
                        .showImage(true)//设置是否展示图片
                        .showVideo(false)//设置是否展示视频
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

    @OnClick(R.id.btn_bindinng)
    void bindingClick() {
        String weChatNo = edit_zfb_name.getText().toString().trim();
        String pwdString = edit_trade_password.getText().toString().trim();
        if (TextUtils.isEmpty(weChatNo)) {
            showToastError(getResources().getString(R.string.please_input_wx_account));
            return;
        }
        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
            showToastError(getResources().getString(R.string.please_input_trade_password));
            return;
        }
        mPresenter.insertBank(2, null, null, null, null, weChatNo, imageName, null, null, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        getHostActivity().finish();
    }

    @OnClick(R.id.btn_delete)
    void deleteClick() {
        mPresenter.deleteBank(2, payInfoModel.id);
    }

    @Override
    public void insertBankSuccess() {
        sendRefresh();
    }

    @Override
    public void deleteBankSuccess() {
        sendRefresh();
    }

    @Override
    public void getOssSetting(AuthOssModel model, String fileLocalNameAddress) {
        if (model != null) {
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(model.AccessKeyId, model.AccessKeySecret, model.SecurityToken);
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            OSSLog.enableLog();
            mOss = new OSSClient(MyApplication.getInstance(), Constant.OSS_ENDPOINT, credentialProvider);
            bucketName = model.bucket;

            uploadImgs(fileLocalNameAddress);
        }
    }

    private void sendRefresh() {
        EventManage.sendEvent(new BaseEvent<>(EventConstant.PayInfoCode, new PayInfoEvent(true)));
    }

    public void setValue(PayInfoModel model) {
        this.payInfoModel = model;
        if (model != null) {
            lin_edit.setVisibility(View.GONE);
            lin_list.setVisibility(View.VISIBLE);

            tv_zfb_name.setText(model.weChatNo);
            GlideUtils.disPlay(getContext(), Constant.UPLOAD_IMG_URL + model.weChatImg, img_zfb_shoukuan1);
        } else {
            lin_edit.setVisibility(View.VISIBLE);
            lin_list.setVisibility(View.GONE);
        }
    }

    private void uploadImgs(String fileLocalNameAddress) {
        if (!TextUtils.isEmpty(bucketName) && mOss != null) {
            // 构造上传请求
            imageName = CommonUtils.getZfbImageName();
            PutObjectRequest put = new PutObjectRequest(bucketName, imageName, fileLocalNameAddress);
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                }
            });
            OSSAsyncTask task = mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    dealValue(1);
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    dealValue(2);
                }
            });
            ossAsynTaskList.add(task);
            //可以取消任务
            //task.cancel();
            //可以等待直到任务完成
            //task.waitUntilFinished();
        }
    }

    private void dealValue(int flag) {
        if (flag == 1) {
            mHandler.sendEmptyMessage(0);
        } else {
            mHandler.sendEmptyMessage(1);
        }
    }
}
