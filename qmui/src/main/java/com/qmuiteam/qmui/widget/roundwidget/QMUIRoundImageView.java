package com.qmuiteam.qmui.widget.roundwidget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.qmuiteam.qmui.R;
import com.qmuiteam.qmui.util.QMUIViewHelper;

/**
 * 见 {@link QMUIRoundButton} 与 {@link QMUIRoundButtonDrawable}
 */
public class QMUIRoundImageView extends AppCompatImageView {

    public QMUIRoundImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public QMUIRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.QMUIButtonStyle);
    }

    public QMUIRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        QMUIRoundButtonDrawable bg = QMUIRoundButtonDrawable.fromAttributeSet(context, attrs, defStyleAttr);
        QMUIViewHelper.setBackgroundKeepingPadding(this, bg);
    }
}
