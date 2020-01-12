package com.android.tacu.module.my.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.my.model.InvitedAllModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.ScreenShootUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.ZXingUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static com.android.tacu.api.ApiHost.SOCKET_IP;

/**
 * Created by xiaohong on 2018/10/17.
 */

public class InvitePosterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_wechat)
    ImageView iv_wechat;
    @BindView(R.id.iv_wechatmoment)
    ImageView iv_wechatmoment;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int width;
    private int hidth;
    private int zxingHidth;
    private int current = -1;
    private String invitedId;
    private String imageUrl;
    private Bitmap bitmapZxing;
    private Platform.ShareParams sp;
    private RelativeLayout rl_poster;
    private PosterAdapter posterAdapter;
    private List<InvitedAllModel.PosterModel> psoterList = new ArrayList();
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/share.jpg";

    public static Intent createActivity(Context context, InvitedAllModel invitedAllModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("invitePoster", (Serializable) invitedAllModel.psoter);
        Intent intent = new Intent(context, InvitePosterActivity.class);
        intent.putExtra("invitedId", invitedAllModel.invitedId);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_invite_poster);
    }

    @Override
    protected void initView() {
        invitedId = getIntent().getStringExtra("invitedId");
        psoterList = (List<InvitedAllModel.PosterModel>) getIntent().getSerializableExtra("invitePoster");

        width = UIUtils.getScreenWidth() * 14 / 25;
        hidth = width * 16 / 9;
        zxingHidth = hidth * 258 / 1920 - UIUtils.dp2px(20);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);

        posterAdapter = new PosterAdapter();
        posterAdapter.setNewData(psoterList);
        recyclerView.setAdapter(posterAdapter);
        if (psoterList != null && psoterList.size() > 2) {
            recyclerView.smoothScrollToPosition(1);
        }

        iv_wechat.setOnClickListener(this);
        iv_wechatmoment.setOnClickListener(this);

        String url;
        if (TextUtils.isEmpty(invitedId)) {
            url = Constant.ANDROID_APP_DOWNLOAD;//Android下载链接
        } else {
            url = Constant.INVITED_FRIEND_URL + invitedId;
        }

        bitmapZxing = ZXingUtils.createQRImage(url, UIUtils.dp2px(zxingHidth), UIUtils.dp2px(zxingHidth));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmapZxing != null && !bitmapZxing.isRecycled()) {
            bitmapZxing.recycle();
            bitmapZxing = null;
        }
    }

    @OnClick(R.id.tv_cancel)
    void cancel() {
        finish();
    }

    public void setConten(String type) {
        sp = new Platform.ShareParams();
        sp.setTitle("TACU");
        sp.setImagePath(path);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform platform = ShareSDK.getPlatform(type);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        // 执行图文分享
        platform.share(sp);
    }

    @Override
    public void onClick(View v) {
        if (!CommonUtils.isWeixinAvilible(this)) {
            return;
        }
        if (current == -1) {
            showToast(getResources().getString(R.string.invite_hint));
            return;
        }

        shareView();

        switch (v.getId()) {
            case R.id.iv_wechat:
                setConten(Wechat.NAME);
                break;
            case R.id.iv_wechatmoment:
                setConten(WechatMoments.NAME);
                break;
        }
    }

    private void shareView() {
        View view = View.inflate(this, R.layout.item_poster, null);
        ImageView iv_poster = view.findViewById(R.id.iv_poster);
        ImageView iv_zxing = view.findViewById(R.id.iv_zxing);
        RelativeLayout rl = view.findViewById(R.id.rl_poster_share);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl.getLayoutParams();
        params.height = hidth;
        params.width = width;
        rl.setLayoutParams(params);

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) iv_poster.getLayoutParams();
        params2.height = hidth;
        params2.width = width;
        iv_poster.setLayoutParams(params2);

        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) iv_zxing.getLayoutParams();
        params3.height = zxingHidth;
        params3.width = zxingHidth;
        iv_zxing.setLayoutParams(params3);

        if (bitmapZxing != null) {
            iv_zxing.setImageBitmap(bitmapZxing);
        }

        GlideUtils.disPlay(InvitePosterActivity.this, imageUrl, iv_poster);
        ScreenShootUtils.convertViewToBitmap(view, path);
    }

    private class PosterAdapter extends BaseQuickAdapter<InvitedAllModel.PosterModel, BaseViewHolder> {

        public PosterAdapter() {
            super(R.layout.item_poster);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final InvitedAllModel.PosterModel item) {
            rl_poster = helper.getView(R.id.rl_poster_share);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_poster.getLayoutParams();
            params.height = hidth;
            params.width = width;
            rl_poster.setLayoutParams(params);

            ImageView imageView = helper.getView(R.id.iv_poster);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params2.height = hidth;
            params2.width = width;
            imageView.setLayoutParams(params2);

            ImageView iv_zxing = helper.getView(R.id.iv_zxing);
            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) iv_zxing.getLayoutParams();
            params3.height = zxingHidth;
            params3.width = zxingHidth;
            iv_zxing.setLayoutParams(params3);

            GlideUtils.disPlay(InvitePosterActivity.this, item.imageUrl, (ImageView) helper.getView(R.id.iv_poster));

            if (current == helper.getLayoutPosition()) {
                helper.setGone(R.id.view_bg, true);
                helper.setGone(R.id.iv_zxing, true);
            } else {
                helper.setGone(R.id.view_bg, false);
                helper.setGone(R.id.iv_zxing, false);
            }

            if (bitmapZxing != null) {
                helper.setImageBitmap(R.id.iv_zxing, bitmapZxing);
            }

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageUrl = item.imageUrl;
                    current = helper.getLayoutPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
