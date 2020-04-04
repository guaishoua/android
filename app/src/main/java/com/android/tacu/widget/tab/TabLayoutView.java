package com.android.tacu.widget.tab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.market.view.MarketDetailsActivity;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.UIUtils;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.base.Status;
import com.github.tifezh.kchartlib.chart.view.IndexKlineActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiazhen on 2018/9/24.
 */
public class TabLayoutView extends LinearLayout implements View.OnClickListener {

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

    private KLineChartView mKChartView;
    private IndexKlineModel klineModel;

    private List<View> tabList = new ArrayList<>();
    private List<String> timeTabList = new ArrayList<>();

    private TabPopup timePopUp;
    private TargetPopup targetPopup;

    private Gson gson = new Gson();
    private TabSelectListener mTabSelectListener = null;

    // 0来源自Activity 1来源自Fragment
    private int flag = 0;
    private Activity activity;
    private Fragment fragment;

    public TabLayoutView(Context context) {
        this(context, null);
    }

    public TabLayoutView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
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

        timeTabList.add(getContext().getResources().getString(R.string.min_time));
        timeTabList.add(getContext().getResources().getString(R.string.min_5));
        timeTabList.add(getContext().getResources().getString(R.string.min_30));
        timeTabList.add(getContext().getResources().getString(R.string.hour_6));
        timeTabList.add(getContext().getResources().getString(R.string.week_1));
        timeTabList.add(getContext().getResources().getString(R.string.month_1));
    }

    private void initData() {
        String temp = SPUtils.getInstance().getString(Constant.MARKET_DETAIL_TIME);
        if (!TextUtils.isEmpty(temp)) {
            klineModel = gson.fromJson(temp, IndexKlineModel.class);
        } else {
            klineModel = new IndexKlineModel();
        }

        //时间部分的处理
        if (klineModel.isLine) {
            tab_more.setText(timeTabList.get(0));
            tab_more.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
            indicator_more.setVisibility(View.VISIBLE);
        } else {
            if (klineModel.ChartTime == KLineChartView.MIN_1) {
                tab_min1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_min1.setVisibility(View.VISIBLE);
            } else if (klineModel.ChartTime == KLineChartView.MIN_15) {
                tab_min15.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_min15.setVisibility(View.VISIBLE);
            } else if (klineModel.ChartTime == KLineChartView.HOUR_1) {
                tab_hour1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_hour1.setVisibility(View.VISIBLE);
            } else if (klineModel.ChartTime == KLineChartView.DAY_1) {
                tab_day1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_day1.setVisibility(View.VISIBLE);
            } else {
                if (klineModel.ChartTime == KLineChartView.MIN_5) {
                    tab_more.setText(timeTabList.get(1));
                } else if (klineModel.ChartTime == KLineChartView.MIN_30) {
                    tab_more.setText(timeTabList.get(2));
                } else if (klineModel.ChartTime == KLineChartView.HOUR_6) {
                    tab_more.setText(timeTabList.get(3));
                } else if (klineModel.ChartTime == KLineChartView.WEEK_1) {
                    tab_more.setText(timeTabList.get(4));
                } else if (klineModel.ChartTime == KLineChartView.MONTH_1) {
                    tab_more.setText(timeTabList.get(5));
                }
                tab_more.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_more.setVisibility(View.VISIBLE);
            }
        }

        //指标的处理
        mKChartView.hideSelectData();
        switch (klineModel.MainView) {
            case -1:
                mKChartView.changeMainDrawType(Status.NONE);
                break;
            case 0:
                mKChartView.changeMainDrawType(Status.MA);
                break;
            case 1:
                mKChartView.changeMainDrawType(Status.BOLL);
                break;
        }
        if (klineModel.SecondView == -1) {
            mKChartView.hideChildDraw();
        } else {
            mKChartView.setChildDraw(klineModel.SecondView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_mim1:
                clearAllColor();
                tab_min1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_min1.setVisibility(View.VISIBLE);
                tab_more.setText(getResources().getString(R.string.more));

                klineModel.ChartTime = KLineChartView.MIN_1;
                klineModel.isLine = false;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setMainDrawLine(false);
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected();
                }
                break;
            case R.id.view_mim15:
                clearAllColor();
                tab_min15.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_min15.setVisibility(View.VISIBLE);
                tab_more.setText(getResources().getString(R.string.more));

                klineModel.ChartTime = KLineChartView.MIN_15;
                klineModel.isLine = false;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setMainDrawLine(false);
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected();
                }
                break;
            case R.id.view_hour1:
                clearAllColor();
                tab_hour1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_hour1.setVisibility(View.VISIBLE);
                tab_more.setText(getResources().getString(R.string.more));

                klineModel.ChartTime = KLineChartView.HOUR_1;
                klineModel.isLine = false;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setMainDrawLine(false);
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected();
                }
                break;
            case R.id.view_day1:
                clearAllColor();
                tab_day1.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_day1.setVisibility(View.VISIBLE);
                tab_more.setText(getResources().getString(R.string.more));

                klineModel.ChartTime = KLineChartView.DAY_1;
                klineModel.isLine = false;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setMainDrawLine(false);
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected();
                }
                break;
            case R.id.view_more:
                clearAllColor();
                tab_more.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                if (TextUtils.equals(tab_more.getText().toString(), getResources().getString(R.string.more))) {
                    indicator_more.setVisibility(View.GONE);
                } else {
                    indicator_more.setVisibility(View.VISIBLE);
                }

                initTimePopUp();
                break;
            case R.id.view_setting:
                img_setting.setImageResource(R.drawable.icon_kline_setting_default);

                initTargetPopUp();
                break;
        }
    }

    //获取当前选择的时间类型
    public long getChartTime() {
        return klineModel.ChartTime;
    }

    public boolean getIsLine() {
        return klineModel.isLine;
    }

    public void setOnKChartView(KLineChartView kChartView) {
        this.mKChartView = kChartView;
        initData();
    }

    public void setOnTabSelectListener(TabSelectListener listener) {
        this.mTabSelectListener = listener;
    }

    public void setFlagActivity(int flag, Activity activity) {
        this.flag = flag;
        this.activity = activity;
    }

    public void setFlagFragment(int flag, Fragment fragment) {
        this.flag = flag;
        this.fragment = fragment;
    }

    private void initTimePopUp() {
        if (timePopUp != null && timePopUp.isShowing()) {
            timePopUp.dismiss();
            return;
        }
        timePopUp = new TabPopup(getContext());
        timePopUp.create(UIUtils.getScreenWidth() - UIUtils.dp2px(10), UIUtils.dp2px(40), timeTabList, new TabPopup.TabItemSelect() {
            @Override
            public void onTabItemSelectListener(int position) {
                switch (position) {
                    case 0:
                        klineModel.ChartTime = KLineChartView.MIN_1;
                        klineModel.isLine = true;
                        saveOpearte();
                        break;
                    case 1:
                        klineModel.ChartTime = KLineChartView.MIN_5;
                        klineModel.isLine = false;
                        saveOpearte();

                        mKChartView.hideSelectData();
                        mKChartView.setMainDrawLine(false);
                        break;
                    case 2:
                        klineModel.ChartTime = KLineChartView.MIN_30;
                        klineModel.isLine = false;
                        saveOpearte();

                        mKChartView.hideSelectData();
                        mKChartView.setMainDrawLine(false);
                        break;
                    case 3:
                        klineModel.ChartTime = KLineChartView.HOUR_6;
                        klineModel.isLine = false;
                        saveOpearte();

                        mKChartView.hideSelectData();
                        mKChartView.setMainDrawLine(false);
                        break;
                    case 4:
                        klineModel.ChartTime = KLineChartView.WEEK_1;
                        klineModel.isLine = false;
                        saveOpearte();

                        mKChartView.hideSelectData();
                        mKChartView.setMainDrawLine(false);
                        break;
                    case 5:
                        klineModel.ChartTime = KLineChartView.MONTH_1;
                        klineModel.isLine = false;
                        saveOpearte();

                        mKChartView.hideSelectData();
                        mKChartView.setMainDrawLine(false);
                        break;
                }
                if (mTabSelectListener != null) {
                    mTabSelectListener.onTabSelected();
                }
                tab_more.setText(timeTabList.get(position));

                clearAllColor();
                tab_more.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                indicator_more.setVisibility(VISIBLE);

                timePopUp.dismiss();
            }
        });
        timePopUp.showAsDropDown(parentView, UIUtils.dp2px(5), UIUtils.dp2px(5));
    }

    private void initTargetPopUp() {
        if (targetPopup != null && targetPopup.isShowing()) {
            targetPopup.dismiss();
            return;
        }
        targetPopup = new TargetPopup(getContext(), mKChartView, klineModel, new TargetPopup.TargetListener() {
            @Override
            public void onTarget() {
                if (flag == 0) {
                    Intent intent = new Intent(activity, IndexKlineActivity.class);
                    activity.startActivityForResult(intent, MarketDetailsActivity.REQUESTCODE);
                } else if (flag == 1) {
                    Intent intent = new Intent(fragment.getContext(), IndexKlineActivity.class);
                    fragment.startActivityForResult(intent, MarketDetailsActivity.REQUESTCODE);
                }
            }
        });
        targetPopup.create(UIUtils.getScreenWidth() - UIUtils.dp2px(10), UIUtils.dp2px(120));
        targetPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                img_setting.setImageResource(R.drawable.icon_kline_setting);
            }
        });
        targetPopup.showAsDropDown(parentView, UIUtils.dp2px(5), UIUtils.dp2px(5));
    }

    private void saveOpearte() {
        SPUtils.getInstance().put(Constant.MARKET_DETAIL_TIME, gson.toJson(klineModel));
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
        if (targetPopup != null && targetPopup.isShowing()) {
            targetPopup.dismiss();
            targetPopup.clear();
            return;
        }
    }

    public interface TabSelectListener {
        void onTabSelected();
    }
}
