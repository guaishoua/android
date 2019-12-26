package com.qmuiteam.qmui.widget.roundwidget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.qmuiteam.qmui.R;
import com.qmuiteam.qmui.util.QMUIViewHelper;

/**
 * Created by jiazhen on 2018/9/4.
 */
public class QMUIRoundTextView extends AppCompatTextView {

    public QMUIRoundTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public QMUIRoundTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.QMUIButtonStyle);
    }

    public QMUIRoundTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        QMUIRoundButtonDrawable bg = QMUIRoundButtonDrawable.fromAttributeSet(context, attrs, 0);
        QMUIViewHelper.setBackgroundKeepingPadding(this, bg);
    }
}
