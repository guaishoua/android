package com.github.tifezh.kchartlib.chart.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.tifezh.kchartlib.R;

public class KDJSetting {

    private IndexKlineActivity activity;
    private View kdjView;

    private EditText ed_kdj_n;
    private EditText ed_kdj_m1;
    private EditText ed_kdj_m2;
    private LinearLayout lin_refresh_kdj;

    private boolean isFirst = true;
    private KDJSettingLister kdjSettingLister;

    public KDJSetting(IndexKlineActivity activity, View view, KDJSettingLister kdjSettingLister) {
        this.activity = activity;
        this.kdjView = view;
        this.kdjSettingLister = kdjSettingLister;

        initSetting();
    }

    private void initSetting() {
        ed_kdj_n = kdjView.findViewById(R.id.ed_kdj_n);
        ed_kdj_m1 = kdjView.findViewById(R.id.ed_kdj_m1);
        ed_kdj_m2 = kdjView.findViewById(R.id.ed_kdj_m2);
        lin_refresh_kdj = kdjView.findViewById(R.id.lin_refresh_kdj);

        lin_refresh_kdj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        ed_kdj_n.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_kdj_n.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_kdj_m1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_kdj_m1.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_kdj_m2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_kdj_m2.setText("");
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
        ed_kdj_n.setText(activity.getIndexModel().KDJNValue != null ? String.valueOf(activity.getIndexModel().KDJNValue) : "");
        ed_kdj_m1.setText(activity.getIndexModel().KDJM1Value != null ? String.valueOf(activity.getIndexModel().KDJM1Value) : "");
        ed_kdj_m2.setText(activity.getIndexModel().KDJM2Value != null ? String.valueOf(activity.getIndexModel().KDJM2Value) : "");

        maText();
    }

    private void dealData() {
        if (isFirst) {
            return;
        }

        activity.getIndexModel().KDJNValue = !TextUtils.isEmpty(ed_kdj_n.getText().toString()) ? Integer.parseInt(ed_kdj_n.getText().toString()) : null;
        activity.getIndexModel().KDJM1Value = !TextUtils.isEmpty(ed_kdj_m1.getText().toString()) ? Integer.parseInt(ed_kdj_m1.getText().toString()) : null;
        activity.getIndexModel().KDJM2Value = !TextUtils.isEmpty(ed_kdj_m2.getText().toString()) ? Integer.parseInt(ed_kdj_m2.getText().toString()) : null;

        maText();
    }

    private void maText() {
        String value = "";
        if (activity.getIndexModel().KDJNValue != null) {
            value += "\t\tN" + activity.getIndexModel().KDJNValue;
        }
        if (activity.getIndexModel().KDJM1Value != null) {
            value += "\t\tM1-" + activity.getIndexModel().KDJM1Value;
        }
        if (activity.getIndexModel().KDJM2Value != null) {
            value += "\t\tM2-" + activity.getIndexModel().KDJM2Value;
        }
        if (kdjSettingLister != null) {
            kdjSettingLister.kdjSetting(value);
        }
    }

    private void refreshData() {
        isFirst = true;

        activity.getIndexModel().KDJNValue = 14;
        activity.getIndexModel().KDJM1Value = 1;
        activity.getIndexModel().KDJM2Value = 3;

        initData();
        isFirst = false;
    }

    public void clear() {
        activity = null;
    }

    public interface KDJSettingLister {
        void kdjSetting(String value);
    }
}
