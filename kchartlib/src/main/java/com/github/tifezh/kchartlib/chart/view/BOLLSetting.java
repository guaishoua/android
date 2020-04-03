package com.github.tifezh.kchartlib.chart.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.tifezh.kchartlib.R;

public class BOLLSetting {

    private IndexKlineActivity activity;
    private View bollView;

    private EditText ed_boll_n;
    private EditText ed_boll_p;
    private LinearLayout lin_refresh_boll;

    private boolean isFirst = true;
    private BOLLSettingLister bollSettingLister;

    public BOLLSetting(IndexKlineActivity activity, View view, BOLLSettingLister bollSettingLister) {
        this.activity = activity;
        this.bollView = view;
        this.bollSettingLister = bollSettingLister;

        initSetting();
    }

    private void initSetting() {
        ed_boll_n = bollView.findViewById(R.id.ed_boll_n);
        ed_boll_p = bollView.findViewById(R.id.ed_boll_p);
        lin_refresh_boll = bollView.findViewById(R.id.lin_refresh_boll);

        lin_refresh_boll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        ed_boll_n.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_boll_n.setText("");
                } else {
                    dealData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ed_boll_p.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) && Integer.parseInt(s.toString()) == 0) {
                    ed_boll_p.setText("");
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
        ed_boll_n.setText(activity.getIndexModel().BOLLNValue != null ? String.valueOf(activity.getIndexModel().BOLLNValue) : "");
        ed_boll_p.setText(activity.getIndexModel().BOLLPValue != null ? String.valueOf(activity.getIndexModel().BOLLPValue) : "");

        maText();
    }

    private void dealData() {
        if (isFirst) {
            return;
        }

        activity.getIndexModel().BOLLNValue = !TextUtils.isEmpty(ed_boll_n.getText().toString()) ? Integer.parseInt(ed_boll_n.getText().toString()) : null;
        activity.getIndexModel().BOLLPValue = !TextUtils.isEmpty(ed_boll_p.getText().toString()) ? Integer.parseInt(ed_boll_p.getText().toString()) : null;

        maText();
    }

    private void maText() {
        String value = "";
        if (activity.getIndexModel().BOLLNValue != null) {
            value += "\t\tN" + activity.getIndexModel().BOLLNValue;
        }
        if (activity.getIndexModel().BOLLPValue != null) {
            value += "\t\tP" + activity.getIndexModel().BOLLPValue;
        }
        if (bollSettingLister != null) {
            bollSettingLister.bollSetting(value);
        }
    }

    private void refreshData() {
        isFirst = true;

        activity.getIndexModel().BOLLNValue = 20;
        activity.getIndexModel().BOLLPValue = 2;

        initData();
        isFirst = false;
    }

    public void clear() {
        activity = null;
    }

    public interface BOLLSettingLister {
        void bollSetting(String value);
    }
}
