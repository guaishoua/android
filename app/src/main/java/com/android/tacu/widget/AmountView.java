package com.android.tacu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.tacu.R;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;

/**
 * Created by jiazhen on 2019/4/18.
 */
public class AmountView extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private int currentValue = 0;
    private int defaultValue = 1;//起始值
    private int maxValue = 10;//最大值
    private int minValue = 0;//最小值
    private int changeValue = 1;//每次的加减值
    private String hintValue = "";//hint值

    private QMUIAlphaImageButton btnSub;
    private QMUIAlphaImageButton btnAdd;
    private EditText editValue;

    public AmountView(Context context) {
        this(context, null);
    }

    public AmountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.AmountView);
        defaultValue = arr.getInt(R.styleable.AmountView_default_value, 0);
        maxValue = arr.getInt(R.styleable.AmountView_max_value, 10);
        minValue = arr.getInt(R.styleable.AmountView_min_value, 0);
        changeValue = arr.getInt(R.styleable.AmountView_change_value, 1);
        hintValue = arr.getString(R.styleable.AmountView_hint_value);
        arr.recycle();

        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_amount, this);
        btnSub = findViewById(R.id.btn_sub);
        btnAdd = findViewById(R.id.btn_add);
        editValue = findViewById(R.id.edit_value);

        if (!TextUtils.isEmpty(hintValue)) {
            editValue.setHint(hintValue);
        }
        editValue.setText(String.valueOf(defaultValue));

        currentValue = defaultValue;

        btnSub.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sub:
                currentValue -= changeValue;
                if (currentValue < minValue) {
                    currentValue = minValue;
                }
                editValue.setText(String.valueOf(currentValue));
                break;
            case R.id.btn_add:
                currentValue += changeValue;
                if (currentValue > maxValue) {
                    currentValue = maxValue;
                }
                editValue.setText(String.valueOf(currentValue));
                break;
        }
    }

    public void setCurrentTextValue(int currentValue) {
        this.currentValue = currentValue;
        editValue.setText(String.valueOf(currentValue));
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void addTextChange(TextWatcher watcher) {
        if (watcher != null) {
            editValue.addTextChangedListener(watcher);
        }
    }
}
