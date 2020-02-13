package com.android.tacu.module.otc.view;

import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseMvpPresenter;

import butterknife.BindView;

public abstract class BaseOtcHalfOrderActvity<P extends BaseMvpPresenter> extends BaseActivity<P> {

    @BindView(R.id.img_people)
    ImageView img_people;
    @BindView(R.id.tv_people_nickname)
    TextView tv_people_nickname;
    @BindView(R.id.img_people_vip)
    ImageView img_people_vip;
    @BindView(R.id.img_people_phone_auth)
    ImageView img_people_phone_auth;
    @BindView(R.id.img_people_truenname_auth)
    ImageView img_people_truenname_auth;
    @BindView(R.id.img_people_email_auth)
    ImageView img_people_email_auth;
    @BindView(R.id.img_people_video_auth)
    ImageView img_people_video_auth;
    @BindView(R.id.tv_people_phone_auth)
    TextView tv_people_phone_auth;
    @BindView(R.id.tv_people_truenname_auth)
    TextView tv_people_truenname_auth;
    @BindView(R.id.tv_people_email_auth)
    TextView tv_people_email_auth;
    @BindView(R.id.tv_people_video_auth)
    TextView tv_people_video_auth;
    @BindView(R.id.tv_people_pay)
    TextView tv_people_pay;

    @BindView(R.id.tv_bond)
    TextView tv_bond;
    @BindView(R.id.tv_all_deal_amount)
    TextView tv_all_deal_amount;
    @BindView(R.id.tv_buy_amount)
    TextView tv_buy_amount;
    @BindView(R.id.tv_sell_amount)
    TextView tv_sell_amount;
    @BindView(R.id.tv_deal_rate)
    TextView tv_deal_rate;

    @BindView(R.id.tv_dispute_amount)
    TextView tv_dispute_amount;
    @BindView(R.id.tv_be_arbitrated_amount)
    TextView tv_be_arbitrated_amount;
    @BindView(R.id.tv_arbitration_amount)
    TextView tv_arbitration_amount;
    @BindView(R.id.tv_appeal_amount)
    TextView tv_appeal_amount;
    @BindView(R.id.tv_win_lawsuit_amount)
    TextView tv_win_lawsuit_amount;

    @BindView(R.id.tv_average_payment_time)
    TextView tv_average_payment_time;
}
