package com.github.tifezh.kchartlib.chart.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.base.ChartConstant;
import com.github.tifezh.kchartlib.chart.utils.SPChartUtils;
import com.google.gson.Gson;

public class IndexKlineActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_back;

    // MA
    private MASetting maSetting;
    private RelativeLayout view_ma;
    private TextView tv_title_ma;
    private LinearLayout view_setting_ma;

    // BOLL
    private BOLLSetting bollSetting;
    private RelativeLayout view_boll;
    private TextView tv_title_boll;
    private LinearLayout view_setting_boll;

    // MACD
    private MACDSetting macdSetting;
    private RelativeLayout view_macd;
    private TextView tv_title_macd;
    private LinearLayout view_setting_macd;

    // KDJ
    private KDJSetting kdjSetting;
    private RelativeLayout view_kdj;
    private TextView tv_title_kdj;
    private LinearLayout view_setting_kdj;

    // RSI
    private RSISetting rsiSetting;
    private RelativeLayout view_rsi;
    private TextView tv_title_rsi;
    private LinearLayout view_setting_rsi;

    // WR
    private WRSetting wrSetting;
    private RelativeLayout view_wr;
    private TextView tv_title_wr;
    private LinearLayout view_setting_wr;

    private int MAHeight = 0, BOLLHeight = 0, MACDHeight = 0, KDJHeight = 0, RSIHeight = 0, WRHeight = 0;

    private Gson gson = new Gson();
    private IndexModel indexModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_kline);
        initData();
        initView();
    }

    private void initData() {
        String temp = SPChartUtils.getInstance(this).getString(ChartConstant.KLINE_INDEX_SETTING);
        indexModel = gson.fromJson(temp, IndexModel.class);
        if (indexModel == null) {
            indexModel = new IndexModel();
        }
    }

    private void initView() {
        img_back = findViewById(R.id.img_back);

        initMA();
        initBOLL();
        initMACD();
        initKDJ();
        initRSI();
        initWR();

        img_back.setOnClickListener(this);
    }

    private void initMA() {
        view_ma = findViewById(R.id.view_ma);
        tv_title_ma = findViewById(R.id.tv_title_ma);
        view_setting_ma = findViewById(R.id.view_setting_ma);

        view_setting_ma.post(new Runnable() {
            @Override
            public void run() {
                MAHeight = view_setting_ma.getHeight();
                view_setting_ma.setVisibility(View.GONE);
            }
        });

        maSetting = new MASetting(this, view_setting_ma, new MASetting.MASettingLister() {
            @Override
            public void maSetting(String value) {
                tv_title_ma.setText(value);
            }
        });

        view_ma.setOnClickListener(this);
    }

    private void initBOLL() {
        view_boll = findViewById(R.id.view_boll);
        tv_title_boll = findViewById(R.id.tv_title_boll);
        view_setting_boll = findViewById(R.id.view_setting_boll);

        view_setting_boll.post(new Runnable() {
            @Override
            public void run() {
                BOLLHeight = view_setting_boll.getHeight();
                view_setting_boll.setVisibility(View.GONE);
            }
        });

        bollSetting = new BOLLSetting(this, view_setting_boll, new BOLLSetting.BOLLSettingLister() {
            @Override
            public void bollSetting(String value) {
                tv_title_boll.setText(value);
            }
        });
        view_boll.setOnClickListener(this);
    }

    private void initMACD() {
        view_macd = findViewById(R.id.view_macd);
        tv_title_macd = findViewById(R.id.tv_title_macd);
        view_setting_macd = findViewById(R.id.view_setting_macd);

        view_setting_macd.post(new Runnable() {
            @Override
            public void run() {
                MACDHeight = view_setting_macd.getHeight();
                view_setting_macd.setVisibility(View.GONE);
            }
        });

        macdSetting = new MACDSetting(this, view_setting_macd, new MACDSetting.MACDSettingLister() {
            @Override
            public void macdSetting(String value) {
                tv_title_macd.setText(value);
            }
        });
        view_macd.setOnClickListener(this);
    }

    private void initKDJ() {
        view_kdj = findViewById(R.id.view_kdj);
        tv_title_kdj = findViewById(R.id.tv_title_kdj);
        view_setting_kdj = findViewById(R.id.view_setting_kdj);

        view_setting_kdj.post(new Runnable() {
            @Override
            public void run() {
                KDJHeight = view_setting_kdj.getHeight();
                view_setting_kdj.setVisibility(View.GONE);
            }
        });

        kdjSetting = new KDJSetting(this, view_setting_kdj, new KDJSetting.KDJSettingLister() {
            @Override
            public void kdjSetting(String value) {
                tv_title_kdj.setText(value);
            }
        });
        view_kdj.setOnClickListener(this);
    }

    private void initRSI() {
        view_rsi = findViewById(R.id.view_rsi);
        tv_title_rsi = findViewById(R.id.tv_title_rsi);
        view_setting_rsi = findViewById(R.id.view_setting_rsi);

        view_setting_rsi.post(new Runnable() {
            @Override
            public void run() {
                RSIHeight = view_setting_rsi.getHeight();
                view_setting_rsi.setVisibility(View.GONE);
            }
        });

        rsiSetting = new RSISetting(this, view_setting_rsi, new RSISetting.RSISettingLister() {
            @Override
            public void rsiSetting(String value) {
                tv_title_rsi.setText(value);
            }
        });
        view_rsi.setOnClickListener(this);
    }

    private void initWR() {
        view_wr = findViewById(R.id.view_wr);
        tv_title_wr = findViewById(R.id.tv_title_wr);
        view_setting_wr = findViewById(R.id.view_setting_wr);

        view_setting_wr.post(new Runnable() {
            @Override
            public void run() {
                WRHeight = view_setting_wr.getHeight();
                view_setting_wr.setVisibility(View.GONE);
            }
        });

        wrSetting = new WRSetting(this, view_setting_wr, new WRSetting.WRSettingLister() {
            @Override
            public void wrSetting(String value) {
                tv_title_wr.setText(value);
            }
        });
        view_wr.setOnClickListener(this);
    }

    public IndexModel getIndexModel() {
        return indexModel;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (maSetting != null) {
            maSetting.clear();
        }
        if (bollSetting != null) {
            bollSetting.clear();
        }
        if (macdSetting != null) {
            macdSetting.clear();
        }
        if (kdjSetting != null) {
            kdjSetting.clear();
        }
        if (rsiSetting != null) {
            rsiSetting.clear();
        }
        if (wrSetting != null) {
            wrSetting.clear();
        }
    }

    @Override
    public void onBackPressed() {
        saveChartSetting();
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_back) {
            saveChartSetting();
            finish();
        }
        onMaClickListener(id);
    }

    private void onMaClickListener(int id) {
        if (id == R.id.view_ma) {
            if (view_setting_ma.getVisibility() == View.VISIBLE) {
                animateClose(view_setting_ma);
            } else {
                animateOpen(view_setting_ma, MAHeight);
            }
        } else if (id == R.id.view_boll) {
            if (view_setting_boll.getVisibility() == View.VISIBLE) {
                animateClose(view_setting_boll);
            } else {
                animateOpen(view_setting_boll, BOLLHeight);
            }
        } else if (id == R.id.view_macd) {
            if (view_setting_macd.getVisibility() == View.VISIBLE) {
                animateClose(view_setting_macd);
            } else {
                animateOpen(view_setting_macd, MACDHeight);
            }
        } else if (id == R.id.view_kdj) {
            if (view_setting_kdj.getVisibility() == View.VISIBLE) {
                animateClose(view_setting_kdj);
            } else {
                animateOpen(view_setting_kdj, KDJHeight);
            }
        } else if (id == R.id.view_rsi) {
            if (view_setting_rsi.getVisibility() == View.VISIBLE) {
                animateClose(view_setting_rsi);
            } else {
                animateOpen(view_setting_rsi, RSIHeight);
            }
        } else if (id == R.id.view_wr) {
            if (view_setting_wr.getVisibility() == View.VISIBLE) {
                animateClose(view_setting_wr);
            } else {
                animateOpen(view_setting_wr, WRHeight);
            }
        }
    }

    private void saveChartSetting() {
        SPChartUtils.getInstance(this).put(ChartConstant.KLINE_INDEX_SETTING, gson.toJson(indexModel));
        setResult(RESULT_OK);
    }

    private void animateOpen(View v, int mHiddenViewMeasuredHeight) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(v, 0, mHiddenViewMeasuredHeight);
        animator.start();
    }

    private void animateClose(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
