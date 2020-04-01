package com.android.tacu.widget.tab;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.utils.SPUtils;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.base.Status;
import com.github.tifezh.kchartlib.chart.view.IndexKlineActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TargetPopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private KLineChartView mKChartView;
    private IndexKlineModel klineModel;

    private TextView tv_ma;
    private TextView tv_boll;
    private ImageView img_main_eye;

    private TextView tv_macd;
    private TextView tv_kdj;
    private TextView tv_rsi;
    private TextView tv_wr;
    private ImageView img_second_eye;

    private ConstraintLayout view_index;

    private Gson gson = new Gson();

    private List<TextView> mainViewList = new ArrayList<>();
    private List<TextView> secondViewList = new ArrayList<>();

    public TargetPopup(Context context, KLineChartView kChartView, IndexKlineModel klineModel) {
        super(context);
        this.mContext = context;
        this.mKChartView = kChartView;
        this.klineModel = klineModel;
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

        view_index = view.findViewById(R.id.view_index);

        tv_ma.setOnClickListener(this);
        tv_boll.setOnClickListener(this);
        img_main_eye.setOnClickListener(this);
        tv_macd.setOnClickListener(this);
        tv_kdj.setOnClickListener(this);
        tv_rsi.setOnClickListener(this);
        tv_wr.setOnClickListener(this);
        img_second_eye.setOnClickListener(this);
        view_index.setOnClickListener(this);

        mainViewList.add(tv_ma);
        mainViewList.add(tv_boll);

        secondViewList.add(tv_macd);
        secondViewList.add(tv_kdj);
        secondViewList.add(tv_rsi);
        secondViewList.add(tv_wr);

        if (klineModel.MainView == -1) {
            clearMainColor(false);
        } else {
            clearMainColor(true);
            switch (klineModel.MainView) {
                case 0:
                    tv_ma.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                    break;
                case 1:
                    tv_boll.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                    break;
            }
        }
        if (klineModel.SecondView == -1) {
            clearSecondColor(false);
        } else {
            clearSecondColor(true);
            switch (klineModel.SecondView) {
                case 0:
                    tv_macd.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                    break;
                case 1:
                    tv_kdj.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                    break;
                case 2:
                    tv_rsi.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                    break;
                case 3:
                    tv_wr.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ma:
                clearMainColor(true);
                tv_ma.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                klineModel.MainView = 0;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.changeMainDrawType(Status.MA);
                dismiss();
                break;
            case R.id.tv_boll:
                clearMainColor(true);
                tv_boll.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                klineModel.MainView = 1;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.changeMainDrawType(Status.BOLL);
                dismiss();
                break;
            case R.id.img_main_eye:
                clearMainColor(false);

                klineModel.MainView = -1;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.changeMainDrawType(Status.NONE);
                dismiss();
                break;
            case R.id.tv_macd:
                clearSecondColor(true);
                tv_macd.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                klineModel.SecondView = 0;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setChildDraw(klineModel.SecondView);
                dismiss();
                break;
            case R.id.tv_kdj:
                clearSecondColor(true);
                tv_kdj.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                klineModel.SecondView = 1;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setChildDraw(klineModel.SecondView);
                dismiss();
                break;
            case R.id.tv_rsi:
                clearSecondColor(true);
                tv_rsi.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                klineModel.SecondView = 2;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setChildDraw(klineModel.SecondView);
                dismiss();
                break;
            case R.id.tv_wr:
                clearSecondColor(true);
                tv_wr.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));

                klineModel.SecondView = 3;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.setChildDraw(klineModel.SecondView);
                dismiss();
                break;
            case R.id.img_second_eye:
                clearSecondColor(false);

                klineModel.SecondView = -1;
                saveOpearte();

                mKChartView.hideSelectData();
                mKChartView.hideChildDraw();
                dismiss();
                break;
            case R.id.view_index:
                Intent intent = new Intent(mContext, IndexKlineActivity.class);
                mContext.startActivity(intent);
                dismiss();
                break;
        }
    }

    private void saveOpearte() {
        SPUtils.getInstance().put(Constant.MARKET_DETAIL_TIME, gson.toJson(klineModel));
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
