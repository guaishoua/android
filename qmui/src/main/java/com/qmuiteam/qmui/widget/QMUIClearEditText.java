package com.qmuiteam.qmui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qmuiteam.qmui.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

/**
 * 设置输入模式是 密码不可见的时候 会出现 按钮控制密码是否可见
 * Created by jiazhen on 2018/9/4.
 */
public class QMUIClearEditText extends AppCompatEditText {

    //密码显示
    private Drawable mShowPwd;
    //密码不显示
    private Drawable mDisShowPwd;
    private boolean isAllowPwdVis = false;
    //密码当前的状态 true:密码可见
    private boolean isPwdVis = false;

    public QMUIClearEditText(Context context) {
        super(context);
        init();
    }

    public QMUIClearEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QMUIClearEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLongClickable(false);

        //密码显示
        if (this.getInputType() == (InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)) {
            isAllowPwdVis = true;
            mShowPwd = ContextCompat.getDrawable(getContext(), R.drawable.qmui_icon_edittext_pwdvis);
            mDisShowPwd = ContextCompat.getDrawable(getContext(), R.drawable.qmui_icon_edittext_pwdgone);
            mShowPwd.setBounds(QMUIDisplayHelper.dpToPx(5), 0, QMUIDisplayHelper.dpToPx(27), QMUIDisplayHelper.dpToPx(22));
            mDisShowPwd.setBounds(QMUIDisplayHelper.dpToPx(5), 0, QMUIDisplayHelper.dpToPx(27), QMUIDisplayHelper.dpToPx(22));
            setPwdVisible(false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isAllowPwdVis) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    if (isPwdVis) {
                        setTransformationMethod(PasswordTransformationMethod.getInstance());
                        setPwdVisible(false);
                    } else {
                        setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        setPwdVisible(true);
                    }
                    setSelection(getText().length());
                    return true;
                }
                //防止点击按钮周围 让edittext获取焦点
                boolean isTouch = event.getX() > getWidth() - getPaddingRight() || ((event.getX() < getWidth() - getTotalPaddingRight()) && (event.getX() > getWidth() - getTotalPaddingRight() - QMUIDisplayHelper.dpToPx(8)));
                if (isTouch) {
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置edittext密码是否可见
     */
    protected void setPwdVisible(boolean visible) {
        isPwdVis = visible;
        Drawable right = visible ? mShowPwd : mDisShowPwd;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }
}
