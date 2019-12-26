package com.android.tacu.module.assets.view;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.utils.CommonUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

/**
 * Created by xiaohong on 2018/8/22.
 */

public class SelectDateActivity extends BaseActivity{
    @BindView(R.id.tv_start_time)
    TextView tv_start_time;
    @BindView(R.id.tv_end_time)
    TextView tv_end_time;
    @BindView(R.id.bt_commit)
    QMUIRoundButton bt_commit;

    public final static int SELECT_TIME = 1000;
    private String recordStartTime;
    private String recordEndTime;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_select_date);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.date_select));

        calendar = Calendar.getInstance();
        calendar.setTime(new Date(calendar.getTime().getTime() - 24 * 60 * 60 * 1000));
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        tv_start_time.setText(simpleDateFormat.format(calendar.getTime()));
        tv_end_time.setText(simpleDateFormat.format(new Date()));
        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.selectTime(SelectDateActivity.this, tv_start_time, tv_end_time.getText().toString().trim());
            }
        });
        tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.endTime(SelectDateActivity.this, tv_end_time, tv_start_time.getText().toString().trim());
            }
        });
        tv_start_time.setText(String.valueOf(simpleDateFormat.format(calendar.getTime())));

        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tv_end_time.getText().toString().trim())) {
                    tv_end_time.setText(String.valueOf(simpleDateFormat.format(new Date())));
                }
                recordStartTime = tv_start_time.getText().toString().trim();
                recordEndTime = tv_end_time.getText().toString().trim();

                Intent intent = new Intent();
                intent.putExtra("startTime", recordStartTime);
                intent.putExtra("endTime", recordEndTime);
                setResult(SELECT_TIME, intent);
                finish();
            }
        });
    }
}
