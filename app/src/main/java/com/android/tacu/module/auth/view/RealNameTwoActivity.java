package com.android.tacu.module.auth.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseModel;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.auth.contract.RealNameContract;
import com.android.tacu.module.auth.model.MultipartImageModel;
import com.android.tacu.module.auth.model.UserInfoModel;
import com.android.tacu.module.auth.presenter.RealNamePresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.IdentityAuthUtils;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiaohong on 2018/8/27.
 * <p>
 * 实名认证第二步
 */

public class RealNameTwoActivity extends BaseActivity<RealNamePresenter> implements RealNameContract.IRealTwoView {
    private static final int POSITIVE_CODE = 1001;

    @BindView(R.id.tv_real_title)
    TextView tv_real_title;
    @BindView(R.id.tv_real_hint)
    TextView tv_real_hint;
    @BindView(R.id.tv_real_content)
    TextView tv_real_contentl;
    @BindView(R.id.iv_picture)
    ImageView iv_picture;
    @BindView(R.id.iv_camera)
    ImageView iv_camera;
    @BindView(R.id.btn_next)
    QMUIAlphaButton btn_next;
    @BindView(R.id.tv_info)
    TextView tv_info;

    private TextView tv_award;
    private TextView tv_success_hint;

    //第几页
    private int current = 1;
    //1:中国  2：其他国家地区
    private String isChina;
    //当前所在地是否中国大陆 0.不是 1.是
    private int currentLocation;

    private BaseModel baseModel;
    private AlertDialog dialog;

    //oss
    private OSS mOss = null;
    private String bucketName;

    private UserInfoModel userInfoModel;
    private MultipartImageModel positiveImage;

    private List<OSSAsyncTask> ossAsynTaskList = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showToastError(getResources().getString(R.string.net_busy));
            hideLoadingView();
        }
    };

    public static Intent crestActivity(Context context, UserInfoModel userInfoModel, int current, String isChina, int currentLocation) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfoModel", userInfoModel);
        Intent intent = new Intent(context, RealNameTwoActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("current", current);
        intent.putExtra("isChina", isChina);
        intent.putExtra("currentLocation", currentLocation);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_realname_two);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.identity_authentication));

        isChina = getIntent().getStringExtra("isChina");
        current = getIntent().getIntExtra("current", 1);
        userInfoModel = (UserInfoModel) getIntent().getSerializableExtra("userInfoModel");
        currentLocation = getIntent().getIntExtra("currentLocation", -1);

        if (userInfoModel != null && userInfoModel.currentLocation != null && userInfoModel.currentLocation == currentLocation) {
            if ((current == 1 && userInfoModel.positiveImagesStatus == 1) || (current == 2 && userInfoModel.oppositeImagesStatus == 1) || (current == 3 && userInfoModel.handImagesStatus == 1)) {
                iv_camera.setVisibility(View.GONE);
                tv_info.setVisibility(View.VISIBLE);
            }
        }

        setCurrentPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ossAsynTaskList != null && ossAsynTaskList.size() > 0) {
            for (OSSAsyncTask ossAsyncTask : ossAsynTaskList) {
                ossAsyncTask.cancel();
            }
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @OnClick(R.id.btn_next)
    void next() {
        if (positiveImage == null && getUrl()) {
            if (current == 3) {
                mPresenter.authnewHand(null, isChina, 4);
                return;
            } else {
                startActivity();
                return;
            }
        }

        //没有选择照片不能跳转下一步
        if (positiveImage == null && !getUrl()) {
            showToastError(getResources().getString(R.string.real_toast_hint));
            return;
        }

        showLoadingView();

        uploadImgs(positiveImage);
    }

    @OnClick(R.id.rl_picture)
    void picture() {
        IdentityAuthUtils.setImage(this, POSITIVE_CODE);
    }

    @Override
    protected RealNamePresenter createPresenter(RealNamePresenter mPresenter) {
        return new RealNamePresenter();
    }

    @Override
    protected void onPresenterCreated(RealNamePresenter presenter) {
        super.onPresenterCreated(presenter);
        //当前所在地是否中国大陆 0.不是 1.是
        mPresenter.getOssSetting();
    }

    /**
     * 拍照选择相册回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case POSITIVE_CODE:
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
                                    positiveImage = new MultipartImageModel(CommonUtils.getOSSUuidName(), file.getPath());
                                    GlideUtils.disPlay(RealNameTwoActivity.this, "file://" + file.getPath(), iv_picture);

                                    iv_camera.setVisibility(View.GONE);
                                    tv_info.setVisibility(View.GONE);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) {
                                }
                            });
                }
                break;
        }
    }


    /**
     * 图片名字
     * 图片地址
     */
    private void uploadImgs(MultipartImageModel model) {
        if (!TextUtils.isEmpty(bucketName) && mOss != null) {
            // 构造上传请求
            PutObjectRequest put = new PutObjectRequest(bucketName, model.getImageName(), model.getImageLocalFileName());
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
            submitUserInfo();
        } else if (flag == 2) {
            mHandler.sendEmptyMessage(0);
        }
    }

    private void submitUserInfo() {
        if (current == 1) {
            mPresenter.authnewPositive(positiveImage.getImageName(), isChina, 2);
        } else if (current == 2) {
            mPresenter.authnewOpposite(positiveImage.getImageName(), isChina, 3);
        } else if (current == 3) {
            mPresenter.authnewHand(positiveImage.getImageName(), isChina, 4);
        }
    }

    private void setText(int title, int hint, int content) {
        tv_real_title.setText(getResources().getString(title));
        tv_real_hint.setText(getResources().getString(hint));
        tv_real_contentl.setText(getResources().getString(content));
    }

    private void setChinaText(int title, int content) {
        tv_real_title.setText(getResources().getString(title));
        tv_real_contentl.setText(getResources().getString(content));
    }

    private void setCurrentPage() {
        if (current == 3) {
            btn_next.setText(getResources().getString(R.string.sure));
        } else {
            btn_next.setText(getResources().getString(R.string.next));
        }
        if (userInfoModel.isChina(isChina)) {
            if (current == 1) {
                tv_real_hint.setVisibility(View.GONE);
                setChinaText(R.string.continent_positive, R.string.continent_hint);
                iv_picture.setBackgroundResource(R.mipmap.img_real_reverse_global_domestic);
            } else if (current == 2) {
                tv_real_hint.setVisibility(View.GONE);
                setChinaText(R.string.continent_reverse_title, R.string.continent_hint);
                iv_picture.setBackgroundResource(R.mipmap.img_real_positive_global_domestic);
            } else if (current == 3) {
                tv_real_hint.setVisibility(View.VISIBLE);
                setText(R.string.continent_hand_title, R.string.continent_hand_hint, R.string.continent_hand_content);
                iv_picture.setBackgroundResource(R.mipmap.img_real_hand_global_domestic);
            }
        } else {
            tv_real_hint.setVisibility(View.VISIBLE);
            if (current == 1) {
                setText(R.string.real_reverse_title, R.string.real_reverse_hint, R.string.real_reverse_content);
                iv_picture.setBackgroundResource(R.mipmap.img_real_reverse_global_foreign);
            } else if (current == 2) {
                setText(R.string.real_positive_title, R.string.real_positive_hint, R.string.real_reverse_content);
                iv_picture.setBackgroundResource(R.mipmap.img_real_positive_global_foreign);
            } else if (current == 3) {
                setText(R.string.real_hand_title, R.string.real_hand_hint, R.string.real_hand_content);
                iv_picture.setBackgroundResource(R.mipmap.img_real_hand_global_foreign);
            }
        }
    }

    @Override
    public void authThredSuccess(BaseModel baseModel) {
        this.baseModel = baseModel;
        hideLoadingView();
        dialogView();
    }

    @Override
    public void authNewSuccess() {
        hideLoadingView();
        startActivity();
    }

    @Override
    public void getOssSetting(AuthOssModel model) {
        if (model != null) {
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(model.AccessKeyId, model.AccessKeySecret, model.SecurityToken);
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            OSSLog.enableLog();
            mOss = new OSSClient(getApplicationContext(), Constant.OSS_ENDPOINT, credentialProvider);
            bucketName = model.bucket;
        }
    }

    /**
     * 当前是否有地址
     *
     * @return
     */
    private boolean getUrl() {
        if (current == 1) {
            if (userInfoModel.positiveImagesStatus == 1) {
                return true;
            }
        } else if (current == 2) {
            if (userInfoModel.oppositeImagesStatus == 1) {
                return true;
            }
        } else if (current == 3) {
            if (userInfoModel.handImagesStatus == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onError() {
        hideLoadingView();
    }

    private void startActivity() {
        jumpTo(RealNameTwoActivity.crestActivity(RealNameTwoActivity.this, userInfoModel, current + 1, isChina, currentLocation));
    }

    private void dialogView() {
        View view = View.inflate(this, R.layout.view_dialog_real_success, null);
        tv_award = view.findViewById(R.id.tv_award);
        tv_success_hint = view.findViewById(R.id.tv_success_hint);
        if (baseModel != null && baseModel.attachment != null) {
            tv_award.setText(String.valueOf(baseModel.attachment));
        }

        dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(AuthActivity.createActivity(RealNameTwoActivity.this, true));
            }
        });
        timer.start();
    }

    private CountDownTimer timer = new CountDownTimer(5000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // 定期定期回调
            String success = getResources().getString(R.string.real_success);
            String success2 = getResources().getString(R.string.real_success2);
            StringBuffer sb = new StringBuffer();
            sb.append(success);
            sb.append(millisUntilFinished / 1000);
            sb.append(success2);
            tv_success_hint.setText(sb.toString());
        }

        @Override
        public void onFinish() {
            jumpTo(AuthActivity.createActivity(RealNameTwoActivity.this, true));
        }
    };
}
