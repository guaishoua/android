package com.github.tifezh.kchartlib.chart.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.tifezh.kchartlib.R;

public class MACDSetting {

    private IndexKlineActivity activity;
    private View macdView;

    private EditText ed_macd_s;
    private EditText ed_macd_l;
    private EditText ed_macd_m;
    private ImageView img_refresh_macd;

    private boolean isFirst = true;
    private MACDSettingLister macdSettingLister;

    public MACDSetting(IndexKlineActivity activity, View view, MACDSettingLister macdSettingLister) {
        this.activity = activity;
        this.macdView = view;
        this.macdSettingLister = macdSettingLister;

        initSetting();
    }

    private void initSetting() {
        ed_macd_s = macdView.findViewById(R.id.ed_macd_s);
        ed_macd_l = macdView.findViewById(R.id.ed_macd_l);
        ed_macd_m = macdView.findViewById(R.id.ed_macd_m);
        img_refresh_macd = macdView.findViewById(R.id.img_refresh_macd);

        img_refresh_macd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        ed_macd_s.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_macd_s.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_macd_l.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_macd_l.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_macd_m.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_macd_m.setText("");
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
        ed_macd_s.setText(activity.getIndexModel().MACDSValue != null ? String.valueOf(activity.getIndexModel().MACDSValue) : "");
        ed_macd_l.setText(activity.getIndexModel().MACDLValue != null ? String.valueOf(activity.getIndexModel().MACDLValue) : "");
        ed_macd_m.setText(activity.getIndexModel().MACDMValue != null ? String.valueOf(activity.getIndexModel().MACDMValue) : "");

        maText();
    }

    private void dealData() {
        if (isFirst) {
            return;
        }

        activity.getIndexModel().MACDSValue = !TextUtils.isEmpty(ed_macd_s.getText().toString()) ? Integer.parseInt(ed_macd_s.getText().toString()) : null;
        activity.getIndexModel().MACDLValue = !TextUtils.isEmpty(ed_macd_l.getText().toString()) ? Integer.parseInt(ed_macd_l.getText().toString()) : null;
        activity.getIndexModel().MACDMValue = !TextUtils.isEmpty(ed_macd_m.getText().toString()) ? Integer.parseInt(ed_macd_m.getText().toString()) : null;

        maText();
    }

    private void maText() {
        String value = "";
        if (activity.getIndexModel().MACDSValue != null) {
            value += "\t\tS" + activity.getIndexModel().MACDSValue;
        }
        if (activity.getIndexModel().MACDLValue != null) {
            value += "\t\tL" + activity.getIndexModel().MACDLValue;
        }
        if (activity.getIndexModel().MACDMValue != null) {
            value += "\t\tM" + activity.getIndexModel().MACDMValue;
        }
        if (macdSettingLister != null) {
            macdSettingLister.macdSetting(value);
        }
    }

    private void refreshData() {
        isFirst = true;

        activity.getIndexModel().MACDSValue = 12;
        activity.getIndexModel().MACDLValue = 26;
        activity.getIndexModel().MACDMValue = 9;

        initData();
        isFirst = false;
    }

    public void clear() {
        activity = null;
    }

    public interface MACDSettingLister {
        void macdSetting(String value);
    }
}
