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

public class WRSetting implements CompoundButton.OnCheckedChangeListener {

    private IndexKlineActivity activity;
    private View wrView;

    private CheckBox cb_wr1;
    private CheckBox cb_wr2;
    private CheckBox cb_wr3;
    private EditText ed_wr1;
    private EditText ed_wr2;
    private EditText ed_wr3;
    private LinearLayout lin_refresh_wr;

    private boolean isFirst = true;
    private WRSettingLister wrSettingLister;

    public WRSetting(IndexKlineActivity activity, View view, WRSettingLister wrSettingLister) {
        this.activity = activity;
        this.wrView = view;
        this.wrSettingLister = wrSettingLister;

        initSetting();
    }

    private void initSetting() {
        cb_wr1 = wrView.findViewById(R.id.cb_wr1);
        cb_wr2 = wrView.findViewById(R.id.cb_wr2);
        cb_wr3 = wrView.findViewById(R.id.cb_wr3);
        ed_wr1 = wrView.findViewById(R.id.ed_wr1);
        ed_wr2 = wrView.findViewById(R.id.ed_wr2);
        ed_wr3 = wrView.findViewById(R.id.ed_wr3);
        lin_refresh_wr = wrView.findViewById(R.id.lin_refresh_wr);

        cb_wr1.setOnCheckedChangeListener(this);
        cb_wr2.setOnCheckedChangeListener(this);
        cb_wr3.setOnCheckedChangeListener(this);
        lin_refresh_wr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        ed_wr1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_wr1.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_wr2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_wr2.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_wr3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_wr3.setText("");
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
        cb_wr1.setChecked(activity.getIndexModel().isWR1Check);
        cb_wr2.setChecked(activity.getIndexModel().isWR2Check);
        cb_wr3.setChecked(activity.getIndexModel().isWR3Check);

        ed_wr1.setText(activity.getIndexModel().WR1Value != null ? String.valueOf(activity.getIndexModel().WR1Value) : "");
        ed_wr2.setText(activity.getIndexModel().WR2Value != null ? String.valueOf(activity.getIndexModel().WR2Value) : "");
        ed_wr3.setText(activity.getIndexModel().WR3Value != null ? String.valueOf(activity.getIndexModel().WR3Value) : "");

        maText();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        onMaCheckChanged(id, isChecked);
    }

    private void onMaCheckChanged(int id, boolean isChecked) {
        if (id == R.id.cb_wr1) {
            if (isChecked) {
                ed_wr1.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart1));
                ed_wr1.setTextColor(ContextCompat.getColor(activity, R.color.chart_color1));
            } else {
                ed_wr1.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_wr1.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_wr2) {
            if (isChecked) {
                ed_wr2.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart2));
                ed_wr2.setTextColor(ContextCompat.getColor(activity, R.color.chart_color2));
            } else {
                ed_wr2.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_wr2.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        } else if (id == R.id.cb_wr3) {
            if (isChecked) {
                ed_wr3.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart3));
                ed_wr3.setTextColor(ContextCompat.getColor(activity, R.color.chart_color3));
            } else {
                ed_wr3.setBackground(ContextCompat.getDrawable(activity, R.drawable.shape_chart_grey));
                ed_wr3.setTextColor(ContextCompat.getColor(activity, R.color.text_grey));
            }
        }

        dealData();
    }

    private void dealData() {
        if (isFirst) {
            return;
        }

        activity.getIndexModel().isWR1Check = cb_wr1.isChecked();
        activity.getIndexModel().isWR2Check = cb_wr2.isChecked();
        activity.getIndexModel().isWR3Check = cb_wr3.isChecked();

        activity.getIndexModel().WR1Value = !TextUtils.isEmpty(ed_wr1.getText().toString()) ? Integer.parseInt(ed_wr1.getText().toString()) : null;
        activity.getIndexModel().WR2Value = !TextUtils.isEmpty(ed_wr2.getText().toString()) ? Integer.parseInt(ed_wr2.getText().toString()) : null;
        activity.getIndexModel().WR3Value = !TextUtils.isEmpty(ed_wr3.getText().toString()) ? Integer.parseInt(ed_wr3.getText().toString()) : null;

        maText();
    }

    private void maText() {
        String value = "";
        if (activity.getIndexModel().isWR1Check && activity.getIndexModel().WR1Value != null) {
            value += "\t\tWR1-" + activity.getIndexModel().WR1Value;
        }
        if (activity.getIndexModel().isWR2Check && activity.getIndexModel().WR2Value != null) {
            value += "\t\tWR2-" + activity.getIndexModel().WR2Value;
        }
        if (activity.getIndexModel().isWR3Check && activity.getIndexModel().WR3Value != null) {
            value += "\t\tWR3-" + activity.getIndexModel().WR3Value;
        }
        if (wrSettingLister != null) {
            wrSettingLister.wrSetting(value);
        }
    }

    private void refreshData() {
        isFirst = true;

        activity.getIndexModel().isWR1Check = true;
        activity.getIndexModel().isWR2Check = false;
        activity.getIndexModel().isWR3Check = false;

        activity.getIndexModel().WR1Value = 2;
        activity.getIndexModel().WR2Value = null;
        activity.getIndexModel().WR3Value = null;

        initData();
        isFirst = false;
    }

    public void clear() {
        activity = null;
    }

    public interface WRSettingLister {
        void wrSetting(String value);
    }
}
