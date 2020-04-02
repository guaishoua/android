package com.github.tifezh.kchartlib.chart.view;

import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.tifezh.kchartlib.R;

public class RSISetting implements CompoundButton.OnCheckedChangeListener {

    private IndexKlineActivity activity;
    private View rsiView;

    private CheckBox cb_rsi1;
    private CheckBox cb_rsi2;
    private CheckBox cb_rsi3;
    private EditText ed_rsi1;
    private EditText ed_rsi2;
    private EditText ed_rsi3;
    private ImageView img_refresh_rsi;

    private boolean isFirst = true;
    private RSISettingLister rsiSettingLister;

    public RSISetting(IndexKlineActivity activity, View view, RSISettingLister rsiSettingLister) {
        this.activity = activity;
        this.rsiView = view;
        this.rsiSettingLister = rsiSettingLister;

        initSetting();
    }

    private void initSetting() {
        cb_rsi1 = rsiView.findViewById(R.id.cb_rsi1);
        cb_rsi2 = rsiView.findViewById(R.id.cb_rsi2);
        cb_rsi3 = rsiView.findViewById(R.id.cb_rsi3);
        ed_rsi1 = rsiView.findViewById(R.id.ed_rsi1);
        ed_rsi2 = rsiView.findViewById(R.id.ed_rsi2);
        ed_rsi3 = rsiView.findViewById(R.id.ed_rsi3);
        img_refresh_rsi = rsiView.findViewById(R.id.img_refresh_rsi);

        cb_rsi1.setOnCheckedChangeListener(this);
        cb_rsi2.setOnCheckedChangeListener(this);
        cb_rsi3.setOnCheckedChangeListener(this);
        img_refresh_rsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        ed_rsi1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_rsi1.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_rsi2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_rsi2.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_rsi3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_rsi3.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        initData();
        isFirst = false;
    }

    private void initData() {
        cb_rsi1.setChecked(activity.getIndexModel().isRSI1Check);
        cb_rsi2.setChecked(activity.getIndexModel().isRSI2Check);
        cb_rsi3.setChecked(activity.getIndexModel().isRSI3Check);

        ed_rsi1.setText(activity.getIndexModel().RSI1Value != null ? String.valueOf(activity.getIndexModel().RSI1Value) : "");
        ed_rsi2.setText(activity.getIndexModel().RSI2Value != null ? String.valueOf(activity.getIndexModel().RSI2Value) : "");
        ed_rsi3.setText(activity.getIndexModel().RSI3Value != null ? String.valueOf(activity.getIndexModel().RSI3Value) : "");

        maText();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        onMaCheckChanged(id, isChecked);
    }

    private void onMaCheckChanged(int id, boolean isChecked) {
        if (id == R.id.cb_rsi1) {
            if (isChecked) {
                ed_rsi1.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart1));
                ed_rsi1.setTextColor(ContextCompat.getColor(activity, R.color.chart_color1));
            } else {
                ed_rsi1.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_rsi1.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_rsi2) {
            if (isChecked) {
                ed_rsi2.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart2));
                ed_rsi2.setTextColor(ContextCompat.getColor(activity, R.color.chart_color2));
            } else {
                ed_rsi2.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_rsi2.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_rsi3) {
            if (isChecked) {
                ed_rsi3.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart3));
                ed_rsi3.setTextColor(ContextCompat.getColor(activity, R.color.chart_color3));
            } else {
                ed_rsi3.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_rsi3.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        }

        dealData();
    }

    private void dealData() {
        if (isFirst) {
            return;
        }

        activity.getIndexModel().isRSI1Check = cb_rsi1.isChecked();
        activity.getIndexModel().isRSI2Check = cb_rsi2.isChecked();
        activity.getIndexModel().isRSI3Check = cb_rsi3.isChecked();

        activity.getIndexModel().RSI1Value = !TextUtils.isEmpty(ed_rsi1.getText().toString()) ? Integer.parseInt(ed_rsi1.getText().toString()) : null;
        activity.getIndexModel().RSI2Value = !TextUtils.isEmpty(ed_rsi2.getText().toString()) ? Integer.parseInt(ed_rsi2.getText().toString()) : null;
        activity.getIndexModel().RSI3Value = !TextUtils.isEmpty(ed_rsi3.getText().toString()) ? Integer.parseInt(ed_rsi3.getText().toString()) : null;

        maText();
    }

    private void maText() {
        String value = "";
        if (activity.getIndexModel().isRSI1Check && activity.getIndexModel().RSI1Value != null) {
            value += "\t\tRSI1-" + activity.getIndexModel().RSI1Value;
        }
        if (activity.getIndexModel().isRSI2Check && activity.getIndexModel().RSI2Value != null) {
            value += "\t\tRSI2-" + activity.getIndexModel().RSI2Value;
        }
        if (activity.getIndexModel().isRSI3Check && activity.getIndexModel().RSI3Value != null) {
            value += "\t\tRSI3-" + activity.getIndexModel().RSI3Value;
        }
        if (rsiSettingLister != null) {
            rsiSettingLister.rsiSetting(value);
        }
    }

    private void refreshData() {
        isFirst = true;

        activity.getIndexModel().isRSI1Check = true;
        activity.getIndexModel().isRSI2Check = false;
        activity.getIndexModel().isRSI3Check = false;

        activity.getIndexModel().RSI1Value = 2;
        activity.getIndexModel().RSI2Value = null;
        activity.getIndexModel().RSI3Value = null;

        initData();
        isFirst = false;
    }

    public void clear() {
        activity = null;
    }

    public interface RSISettingLister {
        void rsiSetting(String value);
    }
}
