package com.android.tacu.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * EditText不可编辑状态下的长按复制
 */

public class CopyTextView extends AppCompatEditText {

    public CopyTextView(Context context) {
        super(context);
    }

    public CopyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CopyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean getDefaultEditable() {//禁止EditText被编辑
        return false;
    }
}
