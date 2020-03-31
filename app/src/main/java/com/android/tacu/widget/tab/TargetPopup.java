package com.android.tacu.widget.tab;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.base.Status;

import java.util.ArrayList;
import java.util.List;

public class TargetPopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private KLineChartView mKChartView;

    private TextView tv_ma;
    private TextView tv_boll;
    private ImageView img_main_eye;

    private TextView tv_macd;
    private TextView tv_kdj;
    private TextView tv_rsi;
    private TextView tv_wr;
    private ImageView img_second_eye;

    //null=隐藏 0=ma 1=boll
    private Integer mainFlag = null;
    //null=隐藏 0=macd 1=kdj 2=rsi 3=wr
    private Integer secondFlag = null;

    private List<TextView> mainViewList = new ArrayList<>();
    private List<TextView> secondViewList = new ArrayList<>();

    public TargetPopup(Context context, KLineChartView kChartView) {
        super(context);
        this.mContext = context;
        this.mKChartView = kChartView;
    }

    public void create(int width, int maxHeight) {
        setWidth(width);
        setHeight(maxHeight);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.content_bg_color)));

        View view = View.inflate(mContext, R.layout.pop_target, null);
        init(view);
        setContentView(view);
    }

    private void init(View view) {
        tv_ma = view.findViewById(R.id.tv_ma);
        tv_boll = view.findViewById(R.id.tv_boll);
        img_main_eye = view.findViewById(R.id.img_main_eye);

        tv_macd = view.findViewById(R.id.tv_macd);
        tv_kdj = view.findViewById(R.id.tv_kdj);
        tv_rsi = view.findViewById(R.id.tv_rsi);
        tv_wr = view.findViewById(R.id.tv_wr);
        img_second_eye = view.findViewById(R.id.img_second_eye);

        tv_ma.setOnClickListener(this);
        tv_boll.setOnClickListener(this);
        img_main_eye.setOnClickListener(this);

        tv_macd.setOnClickListener(this);
        tv_kdj.setOnClickListener(this);
        tv_rsi.setOnClickListener(this);
        tv_wr.setOnClickListener(this);
        img_second_eye.setOnClickListener(this);

        mainViewList.add(tv_ma);
        mainViewList.add(tv_boll);

        secondViewList.add(tv_macd);
        secondViewList.add(tv_kdj);
        secondViewList.add(tv_rsi);
        secondViewList.add(tv_wr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ma:
                clearMainColor(true);
                tv_ma.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                mainFlag = 0;
                mKChartView.hideSelectData();
                mKChartView.changeMainDrawType(Status.MA);
                break;
            case R.id.tv_boll:
                clearMainColor(true);
                tv_boll.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                mainFlag = 1;
                mKChartView.hideSelectData();
                mKChartView.changeMainDrawType(Status.BOLL);
                break;
            case R.id.img_main_eye:
                clearMainColor(false);

                mainFlag = null;
                mKChartView.hideSelectData();
                mKChartView.changeMainDrawType(Status.NONE);
                break;
            case R.id.tv_macd:
                clearSecondColor(true);
                tv_macd.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                secondFlag = 0;
                mKChartView.hideSelectData();
                mKChartView.setChildDraw(secondFlag);
                break;
            case R.id.tv_kdj:
                clearSecondColor(true);
                tv_kdj.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                secondFlag = 1;
                mKChartView.hideSelectData();
                mKChartView.setChildDraw(secondFlag);
                break;
            case R.id.tv_rsi:
                clearSecondColor(true);
                tv_rsi.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                secondFlag = 2;
                mKChartView.hideSelectData();
                mKChartView.setChildDraw(secondFlag);
                break;
            case R.id.tv_wr:
                clearSecondColor(true);
                tv_wr.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                secondFlag = 3;
                mKChartView.hideSelectData();
                mKChartView.setChildDraw(secondFlag);
                break;
            case R.id.img_second_eye:
                clearSecondColor(false);

                secondFlag = null;
                mKChartView.hideSelectData();
                mKChartView.hideChildDraw();
                break;
        }
    }

    private void clearMainColor(boolean isEye) {
        for (int i = 0; i < mainViewList.size(); i++) {
            mainViewList.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.text_color));
        }
        if (isEye) {
            img_main_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdvis);
        } else {
            img_main_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdgone);
        }
    }

    private void clearSecondColor(boolean isEye) {
        for (int i = 0; i < secondViewList.size(); i++) {
            secondViewList.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.text_color));
        }
        if (isEye) {
            img_second_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdvis);
        } else {
            img_second_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdgone);
        }
    }

    public void clear() {
        mKChartView = null;
    }
}
