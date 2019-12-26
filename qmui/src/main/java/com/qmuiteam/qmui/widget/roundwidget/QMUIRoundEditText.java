package com.qmuiteam.qmui.widget.roundwidget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;

import com.qmuiteam.qmui.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;

/**
 * 设置输入模式是 密码不可见的时候 会出现 按钮控制密码是否可见
 * 可以设置边的颜色等等
 * Created by jiazhen on 2018/9/4.
 */
public class QMUIRoundEditText extends AppCompatEditText {

    //密码显示
    private Drawable mShowPwd;
    //密码不显示
    private Drawable mDisShowPwd;
    private boolean isAllowPwdVis = false;
    //密码当前的状态 true:密码可见
    private boolean isPwdVis = false;
    //下划线是否加载动画
    private boolean isUnderLineAnim = false;
    private boolean isAnimRunning = false;
    private int underLineFocusColor;
    private int underLineUnFocusColor;
    private int underLineWidth;
    private Paint mUnderFocusPaint;
    private Paint mUnderUnFocusPaint;

    private ObjectAnimator mAnimator;
    private int mAnimatorProgress = 0;
    private int ANIMATOR_TIME = 200;

    private boolean isEditFocus = false;

    public QMUIRoundEditText(Context context) {
        super(context);
        init(context, null, 0);
    }

    public QMUIRoundEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.QMUIButtonStyle);
    }

    public QMUIRoundEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QMUIRoundEditText, defStyleAttr, 0);
        isUnderLineAnim = array.getBoolean(R.styleable.QMUIRoundEditText_qmui_underline_anim, false);
        underLineFocusColor = array.getColor(R.styleable.QMUIRoundEditText_qmui_underline_focuscolor, Color.WHITE);
        underLineUnFocusColor = array.getColor(R.styleable.QMUIRoundEditText_qmui_underline_unfocuscolor, Color.GRAY);
        underLineWidth = array.getDimensionPixelSize(R.styleable.QMUIRoundEditText_qmui_underline_width, QMUIDisplayHelper.dp2px(context, 1));
        array.recycle();

        if (!isUnderLineAnim) {
            QMUIRoundButtonDrawable bg = QMUIRoundButtonDrawable.fromAttributeSet(context, attrs, 0);
            QMUIViewHelper.setBackgroundKeepingPadding(this, bg);
        } else {
            mUnderFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mUnderFocusPaint.setColor(underLineFocusColor);
            mUnderFocusPaint.setStrokeWidth(underLineWidth);

            mUnderUnFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mUnderUnFocusPaint.setColor(underLineUnFocusColor);
            mUnderUnFocusPaint.setStrokeWidth(underLineWidth);
        }

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isUnderLineAnim) {
            drawUnderLine(canvas);
        }
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

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        this.isEditFocus = focused;

        if (isUnderLineAnim) {
            if (focused) {
                isAnimRunning = true;
                mAnimator = ObjectAnimator.ofInt(this, BORDER_PROGRESS, 0, getWidth() / 2);
                mAnimator.setDuration(ANIMATOR_TIME);
                mAnimator.start();
            } else {
                postInvalidate();
            }
        }
    }

    /**
     * 设置edittext密码是否可见
     */
    protected void setPwdVisible(boolean visible) {
        isPwdVis = visible;
        Drawable right = visible ? mShowPwd : mDisShowPwd;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 画下划线
     *
     * @param canvas
     */
    private void drawUnderLine(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        setBackground(null);
        if (isAnimRunning) {
            canvas.drawLine(width / 2 - mAnimatorProgress, height, width / 2 + mAnimatorProgress, height, mUnderFocusPaint);
            if (mAnimatorProgress == width / 2) {
                isAnimRunning = false;
            }
        } else if (this.isEditFocus) {
            canvas.drawLine(0, height, width, height, mUnderFocusPaint);
        } else {
            canvas.drawLine(0, height, width, height, mUnderUnFocusPaint);
        }
    }

    //自定义属性动画
    private static final Property<QMUIRoundEditText, Integer> BORDER_PROGRESS = new Property<QMUIRoundEditText, Integer>(Integer.class, "underLineProgress") {
        @Override
        public Integer get(QMUIRoundEditText powerfulEditText) {
            return powerfulEditText.getBorderProgress();
        }

        @Override
        public void set(QMUIRoundEditText powerfulEditText, Integer value) {
            powerfulEditText.setBorderProgress(value);
        }
    };

    protected int getBorderProgress() {
        return mAnimatorProgress;
    }

    protected void setBorderProgress(int borderProgress) {
        mAnimatorProgress = borderProgress;
        postInvalidate();
    }

    public void setShowPwdDrawable(int showID, int disID) {
        mShowPwd = ContextCompat.getDrawable(getContext(), showID);
        mDisShowPwd = ContextCompat.getDrawable(getContext(), disID);
        mShowPwd.setBounds(QMUIDisplayHelper.dpToPx(5), 0, QMUIDisplayHelper.dpToPx(27), QMUIDisplayHelper.dpToPx(22));
        mDisShowPwd.setBounds(QMUIDisplayHelper.dpToPx(5), 0, QMUIDisplayHelper.dpToPx(27), QMUIDisplayHelper.dpToPx(22));
        setPwdVisible(isPwdVis);
    }
}
