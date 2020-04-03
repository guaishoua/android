package com.github.tifezh.kchartlib.chart.view;

import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.tifezh.kchartlib.R;

public class MASetting implements CompoundButton.OnCheckedChangeListener {

    private IndexKlineActivity activity;
    private View maView;

    private CheckBox cb_ma1;
    private CheckBox cb_ma2;
    private CheckBox cb_ma3;
    private CheckBox cb_ma4;
    private CheckBox cb_ma5;
    private CheckBox cb_ma6;
    private EditText ed_ma1;
    private EditText ed_ma2;
    private EditText ed_ma3;
    private EditText ed_ma4;
    private EditText ed_ma5;
    private EditText ed_ma6;
    private LinearLayout lin_refresh_ma;

    private boolean isFirst = true;
    private MASettingLister maSettingLister;

    public MASetting(IndexKlineActivity activity, View view, MASettingLister maSettingLister) {
        this.activity = activity;
        this.maView = view;
        this.maSettingLister = maSettingLister;

        initSetting();
    }

    private void initSetting() {
        cb_ma1 = maView.findViewById(R.id.cb_ma1);
        cb_ma2 = maView.findViewById(R.id.cb_ma2);
        cb_ma3 = maView.findViewById(R.id.cb_ma3);
        cb_ma4 = maView.findViewById(R.id.cb_ma4);
        cb_ma5 = maView.findViewById(R.id.cb_ma5);
        cb_ma6 = maView.findViewById(R.id.cb_ma6);
        ed_ma1 = maView.findViewById(R.id.ed_ma1);
        ed_ma2 = maView.findViewById(R.id.ed_ma2);
        ed_ma3 = maView.findViewById(R.id.ed_ma3);
        ed_ma4 = maView.findViewById(R.id.ed_ma4);
        ed_ma5 = maView.findViewById(R.id.ed_ma5);
        ed_ma6 = maView.findViewById(R.id.ed_ma6);
        lin_refresh_ma = maView.findViewById(R.id.lin_refresh_ma);

        cb_ma1.setOnCheckedChangeListener(this);
        cb_ma2.setOnCheckedChangeListener(this);
        cb_ma3.setOnCheckedChangeListener(this);
        cb_ma4.setOnCheckedChangeListener(this);
        cb_ma5.setOnCheckedChangeListener(this);
        cb_ma6.setOnCheckedChangeListener(this);
        lin_refresh_ma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        ed_ma1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_ma1.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_ma2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_ma2.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_ma3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_ma3.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_ma4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_ma4.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_ma5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_ma5.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_ma6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_ma6.setText("");
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
        cb_ma1.setChecked(activity.getIndexModel().isMa1Check);
        cb_ma2.setChecked(activity.getIndexModel().isMa2Check);
        cb_ma3.setChecked(activity.getIndexModel().isMa3Check);
        cb_ma4.setChecked(activity.getIndexModel().isMa4Check);
        cb_ma5.setChecked(activity.getIndexModel().isMa5Check);
        cb_ma6.setChecked(activity.getIndexModel().isMa6Check);

        ed_ma1.setText(activity.getIndexModel().Ma1Value != null ? String.valueOf(activity.getIndexModel().Ma1Value) : "");
        ed_ma2.setText(activity.getIndexModel().Ma2Value != null ? String.valueOf(activity.getIndexModel().Ma2Value) : "");
        ed_ma3.setText(activity.getIndexModel().Ma3Value != null ? String.valueOf(activity.getIndexModel().Ma3Value) : "");
        ed_ma4.setText(activity.getIndexModel().Ma4Value != null ? String.valueOf(activity.getIndexModel().Ma4Value) : "");
        ed_ma5.setText(activity.getIndexModel().Ma5Value != null ? String.valueOf(activity.getIndexModel().Ma5Value) : "");
        ed_ma6.setText(activity.getIndexModel().Ma6Value != null ? String.valueOf(activity.getIndexModel().Ma6Value) : "");

        maText();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        onMaCheckChanged(id, isChecked);
    }

    private void onMaCheckChanged(int id, boolean isChecked) {
        if (id == R.id.cb_ma1) {
            if (isChecked) {
                ed_ma1.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart1));
                ed_ma1.setTextColor(ContextCompat.getColor(activity, R.color.chart_color1));
            } else {
                ed_ma1.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_ma1.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_ma2) {
            if (isChecked) {
                ed_ma2.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart2));
                ed_ma2.setTextColor(ContextCompat.getColor(activity, R.color.chart_color2));
            } else {
                ed_ma2.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_ma2.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_ma3) {
            if (isChecked) {
                ed_ma3.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart3));
                ed_ma3.setTextColor(ContextCompat.getColor(activity, R.color.chart_color3));
            } else {
                ed_ma3.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_ma3.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_ma4) {
            if (isChecked) {
                ed_ma4.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart4));
                ed_ma4.setTextColor(ContextCompat.getColor(activity, R.color.chart_color4));
            } else {
                ed_ma4.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_ma4.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_ma5) {
            if (isChecked) {
                ed_ma5.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart5));
                ed_ma5.setTextColor(ContextCompat.getColor(activity, R.color.chart_color5));
            } else {
                ed_ma5.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_ma5.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_ma6) {
            if (isChecked) {
                ed_ma6.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart6));
                ed_ma6.setTextColor(ContextCompat.getColor(activity, R.color.chart_color6));
            } else {
                ed_ma6.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_ma6.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        }

        dealData();
    }

    private void dealData() {
        if (isFirst) {
            return;
        }

        activity.getIndexModel().isMa1Check = cb_ma1.isChecked();
        activity.getIndexModel().isMa2Check = cb_ma2.isChecked();
        activity.getIndexModel().isMa3Check = cb_ma3.isChecked();
        activity.getIndexModel().isMa4Check = cb_ma4.isChecked();
        activity.getIndexModel().isMa5Check = cb_ma5.isChecked();
        activity.getIndexModel().isMa6Check = cb_ma6.isChecked();

        activity.getIndexModel().Ma1Value = !TextUtils.isEmpty(ed_ma1.getText().toString()) ? Integer.parseInt(ed_ma1.getText().toString()) : null;
        activity.getIndexModel().Ma2Value = !TextUtils.isEmpty(ed_ma2.getText().toString()) ? Integer.parseInt(ed_ma2.getText().toString()) : null;
        activity.getIndexModel().Ma3Value = !TextUtils.isEmpty(ed_ma3.getText().toString()) ? Integer.parseInt(ed_ma3.getText().toString()) : null;
        activity.getIndexModel().Ma4Value = !TextUtils.isEmpty(ed_ma4.getText().toString()) ? Integer.parseInt(ed_ma4.getText().toString()) : null;
        activity.getIndexModel().Ma5Value = !TextUtils.isEmpty(ed_ma5.getText().toString()) ? Integer.parseInt(ed_ma5.getText().toString()) : null;
        activity.getIndexModel().Ma6Value = !TextUtils.isEmpty(ed_ma6.getText().toString()) ? Integer.parseInt(ed_ma6.getText().toString()) : null;

        maText();
    }

    private void maText() {
        String value = "";
        if (activity.getIndexModel().isMa1Check && activity.getIndexModel().Ma1Value != null) {
            value += "\t\tMA" + activity.getIndexModel().Ma1Value;
        }
        if (activity.getIndexModel().isMa2Check && activity.getIndexModel().Ma2Value != null) {
            value += "\t\tMA" + activity.getIndexModel().Ma2Value;
        }
        if (activity.getIndexModel().isMa3Check && activity.getIndexModel().Ma3Value != null) {
            value += "\t\tMA" + activity.getIndexModel().Ma3Value;
        }
        if (activity.getIndexModel().isMa4Check && activity.getIndexModel().Ma4Value != null) {
            value += "\t\tMA" + activity.getIndexModel().Ma4Value;
        }
        if (activity.getIndexModel().isMa5Check && activity.getIndexModel().Ma5Value != null) {
            value += "\t\tMA" + activity.getIndexModel().Ma5Value;
        }
        if (activity.getIndexModel().isMa6Check && activity.getIndexModel().Ma6Value != null) {
            value += "\t\tMA" + activity.getIndexModel().Ma6Value;
        }
        if (maSettingLister != null) {
            maSettingLister.maSetting(value);
        }
    }

    private void refreshData() {
        isFirst = true;

        activity.getIndexModel().isMa1Check = true;
        activity.getIndexModel().isMa2Check = true;
        activity.getIndexModel().isMa3Check = true;
        activity.getIndexModel().isMa4Check = false;
        activity.getIndexModel().isMa5Check = false;
        activity.getIndexModel().isMa6Check = false;

        activity.getIndexModel().Ma1Value = 5;
        activity.getIndexModel().Ma2Value = 10;
        activity.getIndexModel().Ma3Value = 30;
        activity.getIndexModel().Ma4Value = null;
        activity.getIndexModel().Ma5Value = null;
        activity.getIndexModel().Ma6Value = null;

        initData();
        isFirst = false;
    }

    public void clear() {
        activity = null;
    }

    public interface MASettingLister {
        void maSetting(String value);
    }
}
