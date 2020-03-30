package com.android.tacu.widget.tab;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiazhen on 2018/9/24.
 */
public class TabLayoutView extends LinearLayout implements View.OnClickListener {

    private static long MIN_1 = 60000L;
    private static long MIN_5 = 300000L;
    private static long MIN_15 = 900000L;
    private static long MIN_30 = 1800000L;
    private static long HOUR_1 = 3600000L;
    private static long HOUR_6 = 21600000L;
    private static long DAY_1 = 86400000L;
    private static long WEEK_1 = 604800000L;
    private static long MONTH_1 = 2592000000L;

    private View parentView;
    private RelativeLayout view_mim1;
    private RelativeLayout view_mim15;
    private RelativeLayout view_hour1;
    private RelativeLayout view_day1;
    private RelativeLayout view_more;
    private RelativeLayout view_setting;

    private TextView tab_min1;
    private TextView tab_min15;
    private TextView tab_hour1;
    private TextView tab_day1;
    private TextView tab_more;
    private ImageView img_setting;

    private View indicator_min1;
    private View indicator_min15;
    private View indicator_hour1;
    private View indicator_day1;
    private View indicator_more;

    private long chartTime = DAY_1;
    private List<View> tabList = new ArrayList<>();

    private TabSelectListener mTabSelectListener = null;

    private TabPopup timePopUp;

    public TabLayoutView(Context context) {
        this(context, null);
    }

    public TabLayoutView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initData();
    }

    private void init() {
        parentView = LayoutInflater.from(getContext()).inflate(R.layout.view_tab_layout, null);
        addView(parentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setOrientation(HORIZONTAL);

        view_mim1 = parentView.findViewById(R.id.view_mim1);
        view_mim15 = parentView.findViewById(R.id.view_mim15);
        view_hour1 = parentView.findViewById(R.id.view_hour1);
        view_day1 = parentView.findViewById(R.id.view_day1);
        view_more = parentView.findViewById(R.id.view_more);
        view_setting = parentView.findViewById(R.id.view_setting);

        tab_min1 = parentView.findViewById(R.id.tab_min1);
        tab_min15 = parentView.findViewById(R.id.tab_min15);
        tab_hour1 = parentView.findViewById(R.id.tab_hour1);
        tab_day1 = parentView.findViewById(R.id.tab_day1);
        tab_more = parentView.findViewById(R.id.tab_more);
        img_setting = parentView.findViewById(R.id.img_setting);

        indicator_min1 = parentView.findViewById(R.id.indicator_min1);
        indicator_min15 = parentView.findViewById(R.id.indicator_min15);
        indicator_hour1 = parentView.findViewById(R.id.indicator_hour1);
        indicator_day1 = parentView.findViewById(R.id.indicator_day1);
        indicator_more = parentView.findViewById(R.id.indicator_more);

        view_mim1.setOnClickListener(this);
        view_mim15.setOnClickListener(this);
        view_hour1.setOnClickListener(this);
        view_day1.setOnClickListener(this);
        view_more.setOnClickListener(this);
        view_setting.setOnClickListener(this);

        tabList.add(tab_min1);
        tabList.add(tab_min15);
        tabList.add(tab_hour1);
        tabList.add(tab_day1);
        tabList.add(tab_more);
        tabList.add(img_setting);
        tabList.add(indicator_min1);
        tabList.add(indicator_min15);
        tabList.add(indicator_hour1);
        tabList.add(indicator_day1);
        tabList.add(indicator_more);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_mim1:
                chartTime = MIN_1;
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected(chartTime);
                }

                clearAllColor();
                tab_min1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_min1.setVisibility(View.VISIBLE);
                break;
            case R.id.view_mim15:
                chartTime = MIN_15;
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected(chartTime);
                }

                clearAllColor();
                tab_min15.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_min15.setVisibility(View.VISIBLE);
                break;
            case R.id.view_hour1:
                chartTime = HOUR_1;
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected(chartTime);
                }

                clearAllColor();
                tab_hour1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_hour1.setVisibility(View.VISIBLE);
                break;
            case R.id.view_day1:
                chartTime = DAY_1;
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected(chartTime);
                }

                clearAllColor();
                tab_day1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_day1.setVisibility(View.VISIBLE);
                break;
            case R.id.view_more:
                clearAllColor();
                tab_more.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_more.setVisibility(View.VISIBLE);

                initTimePopUp();
                break;
            case R.id.view_setting:
                clearAllColor();
                img_setting.setImageResource(R.drawable.icon_kline_setting_default);
                break;
        }
    }

    //获取当前选择的时间类型
    public long getChartTime() {
        return chartTime;
    }

    private void initTimePopUp() {
        if (timePopUp != null && timePopUp.isShowing()) {
            timePopUp.dismiss();
            return;
        }
        final List<String> datas = new ArrayList<>();
        datas.add(getContext().getResources().getString(R.string.min_5));
        datas.add(getContext().getResources().getString(R.string.min_30));
        datas.add(getContext().getResources().getString(R.string.hour_6));
        datas.add(getContext().getResources().getString(R.string.week_1));
        datas.add(getContext().getResources().getString(R.string.month_1));
        timePopUp = new TabPopup(getContext());
        timePopUp.create(UIUtils.getScreenWidth() - UIUtils.dp2px(10), UIUtils.dp2px(40), datas, new TabPopup.TabItemSelect() {
            @Override
            public void onTabItemSelectListener(int position) {
                switch (position) {
                    case 0:
                        chartTime = MIN_5;
                        break;
                    case 1:
                        chartTime = MIN_30;
                        break;
                    case 2:
                        chartTime = HOUR_6;
                        break;
                    case 3:
                        chartTime = WEEK_1;
                        break;
                    case 4:
                        chartTime = MONTH_1;
                        break;
                }
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected(chartTime);
                }
                tab_more.setText(datas.get(position));

                clearAllColor();
                tab_more.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_more.setVisibility(VISIBLE);

                timePopUp.dismiss();
            }
        });
        timePopUp.showAsDropDown(parentView, UIUtils.dp2px(5), UIUtils.dp2px(5));
    }

    public void setOnTabSelectListener(TabSelectListener listener) {
        this.mTabSelectListener = listener;
    }

    public interface TabSelectListener {
        void onTabSelected(long time);
    }

    private void clearAllColor() {
        for (int i = 0; i < tabList.size(); i++) {
            if (tabList.get(i) instanceof TextView) {
                ((TextView) tabList.get(i)).setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            } else if (tabList.get(i) instanceof ImageView) {
                ((ImageView) tabList.get(i)).setImageResource(R.drawable.icon_kline_setting);
            } else {
                tabList.get(i).setVisibility(GONE);
            }
        }
    }

    public void clear() {
        if (timePopUp != null && timePopUp.isShowing()) {
            timePopUp.dismiss();
            return;
        }
    }
}
