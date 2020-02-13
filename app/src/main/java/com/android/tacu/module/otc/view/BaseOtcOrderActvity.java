package com.android.tacu.module.otc.view;

import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseMvpPresenter;

import butterknife.BindView;

public abstract class BaseOtcOrderActvity<P extends BaseMvpPresenter> extends BaseActivity<P> {

    //卖
    @BindView(R.id.img_seller)
    ImageView img_seller;
    @BindView(R.id.tv_seller_nickname)
    TextView tv_seller_nickname;
    @BindView(R.id.img_seller_vip)
    ImageView img_seller_vip;
    @BindView(R.id.img_seller_phone_auth)
    ImageView img_seller_phone_auth;
    @BindView(R.id.img_seller_truenname_auth)
    ImageView img_seller_truenname_auth;
    @BindView(R.id.img_seller_email_auth)
    ImageView img_seller_email_auth;
    @BindView(R.id.img_seller_video_auth)
    ImageView img_seller_video_auth;
    @BindView(R.id.tv_seller_phone_auth)
    TextView tv_seller_phone_auth;
    @BindView(R.id.tv_seller_truenname_auth)
    TextView tv_seller_truenname_auth;
    @BindView(R.id.tv_seller_email_auth)
    TextView tv_seller_email_auth;
    @BindView(R.id.tv_seller_video_auth)
    TextView tv_seller_video_auth;
    @BindView(R.id.tv_seller_pay)
    TextView tv_seller_pay;

    //买
    @BindView(R.id.img_buyer)
    ImageView img_buyer;
    @BindView(R.id.tv_buyer_nickname)
    TextView tv_buyer_nickname;
    @BindView(R.id.img_buyer_vip)
    ImageView img_buyer_vip;
    @BindView(R.id.img_buyer_phone_auth)
    ImageView img_buyer_phone_auth;
    @BindView(R.id.img_buyer_truenname_auth)
    ImageView img_buyer_truenname_auth;
    @BindView(R.id.img_buyer_email_auth)
    ImageView img_buyer_email_auth;
    @BindView(R.id.img_buyer_video_auth)
    ImageView img_buyer_video_auth;
    @BindView(R.id.tv_buyer_phone_auth)
    TextView tv_buyer_phone_auth;
    @BindView(R.id.tv_buyer_truenname_auth)
    TextView tv_buyer_truenname_auth;
    @BindView(R.id.tv_buyer_email_auth)
    TextView tv_buyer_email_auth;
    @BindView(R.id.tv_buyer_video_auth)
    TextView tv_buyer_video_auth;
    @BindView(R.id.tv_buyer_pay)
    TextView tv_buyer_pay;
}
