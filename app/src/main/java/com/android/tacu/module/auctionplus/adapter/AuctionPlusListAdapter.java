package com.android.tacu.module.auctionplus.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.auctionplus.modal.AuctionPlusModel;
import com.android.tacu.module.auctionplus.view.AuctionPlusDetailActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.utils.AnimUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Auction的列表公用的适配器
 * Created by jiazhen on 2019/4/17.
 */
public class AuctionPlusListAdapter extends BaseQuickAdapter<AuctionPlusModel, BaseViewHolder> {

    public final String TransitionsImageview = "TransitionsImageview";
    private final int KREFRESH_TIME = 10;//10毫秒

    private final String PAYLOAD = "PAYLOAD";
    private final String PAYLOAD_OTHER = "PAYLOAD_OTHER";
    private String payloadString;

    private UserInfoUtils spUtil;
    private Date date;
    private long time;
    private long millisInFuture;

    private String img;
    private String name;
    private String introduce;
    private String paper;

    private boolean isPost = false;
    private SparseArray<TimeModel> timeArray = new SparseArray<>();
    private SparseArray<TimeBtnModel> btnArray = new SparseArray<>();
    private TimeModel tml;
    private TimeModel timeModel;
    private TimeBtnModel tbl;
    private TimeBtnModel timeBtnModel;

    private PayClick click;

    private boolean isHandle = false;
    private Handler tHandler = new Handler();
    private Runnable tRunnable = new Runnable() {
        @Override
        public void run() {
            handleCountTime();
            if (tHandler != null) {
                tHandler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };

    public AuctionPlusListAdapter() {
        super(R.layout.item_auctionplus);
        spUtil = UserInfoUtils.getInstance();
    }

    public void setPayClickListener(PayClick click) {
        this.click = click;
    }

    public void setDataA(List<AuctionPlusModel> list) {
        if (list != null && list.size() > 0) {
            setNewData(list);
            isPost = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).auctionStatus == 1) {
                    isPost = true;
                    break;
                }
            }
            if (isPost) {
                startHandle();
            } else {
                removeHandle();
                cancelTime();
            }
        } else {
            setNewData(null);
            removeHandle();
            cancelTime();
        }
    }

    /**
     * @param index
     * @param data
     * @param type  1=PAYLOAD 2=PAYLOAD_OTHER
     */
    public void setDataL(int index, AuctionPlusModel data, int type) {
        if (mData != null && mData.size() > 0) {
            mData.set(index, data);
            if (type == 1) {
                notifyItemChanged(index + getHeaderLayoutCount(), PAYLOAD);
            } else if (type == 2) {
                notifyItemChanged(index + getHeaderLayoutCount(), PAYLOAD_OTHER);
            }
        }
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void cancelTime() {
        if (timeArray != null && timeArray.size() > 0) {
            timeArray.clear();
        }
        if (btnArray != null && btnArray.size() > 0) {
            btnArray.clear();
        }
    }

    public void startHandle() {
        if (tHandler != null && tRunnable != null && !isHandle) {
            tHandler.post(tRunnable);
            isHandle = true;
        }
    }

    public void removeHandle() {
        if (tHandler != null && tRunnable != null && isHandle) {
            tHandler.removeCallbacks(tRunnable);
            isHandle = false;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        convertD(holder, getItem(position - getHeaderLayoutCount()), payloads);
    }

    @Override
    protected void convert(BaseViewHolder holder, final AuctionPlusModel item) {
    }

    private void convertD(final BaseViewHolder holder, final AuctionPlusModel item, List<Object> payloads) {
        if (item == null) {
            return;
        }

        switch (item.auctionStatus) {
            case 1://进行中
                holder.setGone(R.id.view_hint, false);

                holder.setText(R.id.tv_left_name1, mContext.getResources().getString(R.string.auctionplus_residual_times));
                holder.setText(R.id.tv_left_name2, mContext.getResources().getString(R.string.auctionplus_num));
                holder.setText(R.id.tv_left_name3, mContext.getResources().getString(R.string.auctionplus_dealprice));

                holder.setText(R.id.tv_right_name1, item.surplusNumber);
                holder.setText(R.id.tv_right_name2, FormatterUtils.getFormatValue(item.eachNum) + item.currencyName);
                holder.setText(R.id.tv_right_name3, FormatterUtils.getFormatValue(item.eachPrice) + item.payCurrencyName);

                holder.setGone(R.id.tv_right_name3, true);
                holder.setGone(R.id.tv_right_top_name3, false);
                holder.setGone(R.id.tv_right_bottom_name3, false);

                ((TextView) holder.getView(R.id.tv_right_name1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                holder.setTextColor(R.id.tv_right_name1, ContextCompat.getColor(mContext, R.color.text_auction_currentprice));
                ((QMUIRoundButtonDrawable) holder.getView(R.id.rl_status).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_status_ing));
                holder.setText(R.id.tv_status, mContext.getResources().getString(R.string.auction_list_going));

                if (btnArray == null || btnArray.size() == 0 || btnArray.get(holder.getLayoutPosition()) == null || btnArray.get(holder.getLayoutPosition()).beforeTimeMillis == 0) {
                    setBtnEable((QMUIRoundButton) holder.getView(R.id.pay), R.string.auction_now_goprice, R.color.color_pay_now, true);
                }

                startCountTime(holder, item);
                break;
            case 2://未开始
                holder.setGone(R.id.view_hint, false);

                holder.setText(R.id.tv_left_name1, mContext.getResources().getString(R.string.auctionplus_all_time));
                holder.setText(R.id.tv_left_name2, mContext.getResources().getString(R.string.auctionplus_num));
                holder.setText(R.id.tv_left_name3, mContext.getResources().getString(R.string.auction_list_starttime));

                holder.setText(R.id.tv_right_name1, item.totalNumber);
                holder.setText(R.id.tv_right_name2, FormatterUtils.getFormatValue(item.eachNum) + item.currencyName);
                date = DateUtils.string2Date(item.startTime, DateUtils.DEFAULT_PATTERN);
                holder.setText(R.id.tv_right_top_name3, DateUtils.date2String(date, DateUtils.FORMAT_DATE_YMD));
                holder.setText(R.id.tv_right_bottom_name3, DateUtils.date2String(date, DateUtils.FORMAT_DATE_HMS));

                holder.setGone(R.id.tv_right_name3, false);
                holder.setGone(R.id.tv_right_top_name3, true);
                holder.setGone(R.id.tv_right_bottom_name3, true);

                ((TextView) holder.getView(R.id.tv_right_name1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                holder.setTextColor(R.id.tv_right_name1, ContextCompat.getColor(mContext, R.color.text_color));
                ((QMUIRoundButtonDrawable) holder.getView(R.id.rl_status).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_status_soonstart));
                holder.setText(R.id.tv_status, mContext.getResources().getString(R.string.auctionplus_list_start));

                finishCountTime(holder);

                setBtnEable((QMUIRoundButton) holder.getView(R.id.pay), R.string.auction_now_goprice, R.color.color_pay_gotime, false);
                break;
            case 3://已结束
                holder.setGone(R.id.view_hint, true);

                holder.setText(R.id.tv_left_name1, mContext.getResources().getString(R.string.auctionplus_all_time));
                holder.setText(R.id.tv_left_name2, mContext.getResources().getString(R.string.auctionplus_num));
                holder.setText(R.id.tv_left_name3, mContext.getResources().getString(R.string.auctionplus_dealprice));

                holder.setText(R.id.tv_right_name1, item.totalNumber);
                holder.setText(R.id.tv_right_name2, FormatterUtils.getFormatValue(item.eachNum) + item.currencyName);
                holder.setText(R.id.tv_right_name3, FormatterUtils.getFormatValue(item.eachPrice) + item.payCurrencyName);

                holder.setGone(R.id.tv_right_name3, true);
                holder.setGone(R.id.tv_right_top_name3, false);
                holder.setGone(R.id.tv_right_bottom_name3, false);

                ((TextView) holder.getView(R.id.tv_right_name1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                holder.setTextColor(R.id.tv_right_name1, ContextCompat.getColor(mContext, R.color.text_color));
                ((QMUIRoundButtonDrawable) holder.getView(R.id.rl_status).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_status_finish));
                holder.setText(R.id.tv_status, mContext.getResources().getString(R.string.auction_list_finish));

                finishCountTime(holder);

                setBtnEable((QMUIRoundButton) holder.getView(R.id.pay), R.string.auction_now_goprice, R.color.color_pay_gotime, false);
                if (spUtil.getLogin() && item.payPlusStatus != 0) {
                    switch (item.payPlusStatus) {
                        case 1://待支付
                            setBtnEable((QMUIRoundButton) holder.getView(R.id.pay), R.string.auction_list_paynow, R.color.color_pay_now, true);
                            break;
                        case 2://已支付
                            setBtnEable((QMUIRoundButton) holder.getView(R.id.pay), R.string.auction_list_payafter, R.color.color_pay_after, false);
                            break;
                        case 3://支付过期
                            setBtnEable((QMUIRoundButton) holder.getView(R.id.pay), R.string.auction_list_paygotime, R.color.color_pay_gotime, false);
                            break;
                    }
                }
                break;
        }

        if (payloads.isEmpty()) {
            if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW)) {
                img = item.img;
                name = item.auctionName;
                introduce = item.currencyIntroduce;
                paper = item.paper;
            }  else {
                img = item.imgEn;
                name = item.auctionNameEn;
                introduce = item.currencyIntroduceEn;
                paper = item.paperEn;
            }

            GlideUtils.disPlay(mContext, img, (ImageView) holder.getView(R.id.img_coin));
            holder.setText(R.id.tv_name, name);
            holder.setText(R.id.tv_introduce, introduce);
            if (TextUtils.isEmpty(paper)) {
                holder.setGone(R.id.tv_whitepaper, false);
                holder.setGone(R.id.img_whitepaper, false);
            } else {
                holder.setGone(R.id.tv_whitepaper, true);
                holder.setGone(R.id.img_whitepaper, true);
            }

            holder.addOnClickListener(R.id.img_whitepaper);
            holder.addOnClickListener(R.id.tv_whitepaper);
            holder.setOnClickListener(R.id.pay, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (spUtil.getLogin()) {
                        if (click != null) {
                            click.payClickListener(v, holder.getLayoutPosition());
                        }
                        timeBtnModel = new TimeBtnModel(System.currentTimeMillis(), (QMUIRoundButton) holder.getView(R.id.pay));
                        btnArray.put(holder.getLayoutPosition(), timeBtnModel);
                    } else {
                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AuctionPlusDetailActivity.createActivity(mContext, item.id, item);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, holder.getView(R.id.layout_img), TransitionsImageview).toBundle();
                        ActivityCompat.startActivity(mContext, intent, bundle);
                    } else {
                        mContext.startActivity(intent);
                    }
                }
            });
        } else {
            if (item.auctionStatus == 1) {
                payloadString = (String) payloads.get(0);
                switch (payloadString) {
                    case PAYLOAD:
                        AnimUtils.startTextViewAnim(holder.getView(R.id.tv_right_name1));
                        break;
                }
            }
        }
    }

    private void startCountTime(BaseViewHolder holder, AuctionPlusModel item) {
        if (spUtil.getLogin() && !TextUtils.isEmpty(item.uid) && spUtil.getUserUid() == Integer.parseInt(item.uid)) {
            ((QMUIRoundButtonDrawable) holder.getView(R.id.cardView).getBackground()).setStrokeData(UIUtils.dp2px(1F), ContextCompat.getColorStateList(mContext, R.color.color_default));
        } else {
            ((QMUIRoundButtonDrawable) holder.getView(R.id.cardView).getBackground()).setStrokeData(UIUtils.dp2px(1F), ContextCompat.getColorStateList(mContext, R.color.content_bg_color_grey));
        }

        holder.setGone(R.id.rl_time, true);
        if (item.timestamp != 0 && time != 0) {
            millisInFuture = item.timestamp - time;
            if (millisInFuture > 0) {
                holder.setText(R.id.chronometer, String.format(mContext.getResources().getString(R.string.auction_estimate), DateUtils.getCountDownTime(millisInFuture)));

                timeModel = new TimeModel(millisInFuture, millisInFuture, (TextView) holder.getView(R.id.chronometer));
                timeArray.put(holder.getLayoutPosition(), timeModel);
            } else {
                holder.setText(R.id.chronometer, String.format(mContext.getResources().getString(R.string.auction_estimate), "00:00:00"));
            }
        }
    }

    private void finishCountTime(BaseViewHolder holder) {
        ((QMUIRoundButtonDrawable) holder.getView(R.id.cardView).getBackground()).setStrokeData(UIUtils.dp2px(1F), ContextCompat.getColorStateList(mContext, R.color.content_bg_color_grey));
        holder.setGone(R.id.rl_time, false);
        holder.setText(R.id.chronometer, "");
        timeArray.remove(holder.getLayoutPosition());
    }

    private void handleCountTime() {
        if (timeArray != null && timeArray.size() > 0) {
            for (int i = 0; i < timeArray.size(); i++) {
                tml = timeArray.get(timeArray.keyAt(i));
                if (tml != null) {
                    if (tml.millisInFuture - tml.millisUntilFinished < 1000) {
                        tml.tv.setText(String.format(mContext.getResources().getString(R.string.auction_estimate), DateUtils.getCountDownTime(tml.millisUntilFinished)));
                        tml.millisUntilFinished -= KREFRESH_TIME;
                    }
                }
            }
        }
        if (btnArray != null && btnArray.size() > 0) {
            for (int i = 0; i < btnArray.size(); i++) {
                tbl = btnArray.get(btnArray.keyAt(i));
                if (tbl != null && tbl.beforeTimeMillis != 0) {
                    if ((System.currentTimeMillis() - tbl.beforeTimeMillis) <= 2000) {
                        setBtnEable(tbl.btn, R.string.auction_now_goprice, R.color.color_pay_gotime, false);
                    } else {
                        btnArray.remove(btnArray.keyAt(i));
                    }
                }
            }
        }
    }

    private void setBtnEable(QMUIRoundButton btn, int text, int colors, boolean enable) {
        btn.setText(mContext.getResources().getString(text));
        ((QMUIRoundButtonDrawable) btn.getBackground()).setBgData(ContextCompat.getColorStateList(mContext, colors));
        btn.setEnabled(enable);
    }

    /**
     * millisInFuture 倒计时总时间
     * millisUntilFinished 倒计时剩余时间
     */
    class TimeModel implements Serializable {
        public long millisInFuture;
        public long millisUntilFinished;
        public TextView tv;

        public TimeModel(long millisInFuture, long millisUntilFinished, TextView tv) {
            this.millisInFuture = millisInFuture;
            this.millisUntilFinished = millisUntilFinished;
            this.tv = tv;
        }
    }

    /**
     * beforeTimeMillis 点击立即出价时候的时间戳
     */
    class TimeBtnModel implements Serializable {
        public long beforeTimeMillis;
        public QMUIRoundButton btn;

        public TimeBtnModel(long beforeTimeMillis, QMUIRoundButton btn) {
            this.beforeTimeMillis = beforeTimeMillis;
            this.btn = btn;
        }
    }

    public interface PayClick {
        void payClickListener(View view, int position);
    }
}