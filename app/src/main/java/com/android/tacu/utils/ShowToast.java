package com.android.tacu.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tacu.R;
import com.android.tacu.base.MyApplication;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import static com.qmuiteam.qmui.widget.dialog.QMUITipDialog.Builder.ICON_TYPE_FAIL;
import static com.qmuiteam.qmui.widget.dialog.QMUITipDialog.Builder.ICON_TYPE_NOTHING;
import static com.qmuiteam.qmui.widget.dialog.QMUITipDialog.Builder.ICON_TYPE_SUCCESS;

/**
 * 自定义Toast 用的是qmui的资源
 * Created by jiazhen on 2018/8/9.
 */
public class ShowToast {

    private static Context context = MyApplication.getInstance();

    public static Toast normal(@NonNull String message) {
        return normal(message, Toast.LENGTH_SHORT);
    }

    public static Toast normal(@NonNull String message, int duration) {
        return custom(message, ICON_TYPE_NOTHING, duration);
    }

    public static Toast success(@NonNull String message) {
        return success(message, Toast.LENGTH_SHORT);
    }

    private static Toast success(@NonNull String message, int duration) {
        return custom(message, ICON_TYPE_SUCCESS, duration);
    }

    public static Toast error(@NonNull String message) {
        return error(message, Toast.LENGTH_SHORT);
    }

    private static Toast error(@NonNull String message, int duration) {
        return custom(message, ICON_TYPE_FAIL, duration);
    }

    private static Toast custom(CharSequence tipWord, int iconType, int duration) {
        Toast currentToast = new Toast(context);
        View toastLayout = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        LinearLayout contentWrap = toastLayout.findViewById(R.id.contentWrap);

        if (iconType == ICON_TYPE_SUCCESS || iconType == ICON_TYPE_FAIL || iconType == ICON_TYPE_NOTHING) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams imageViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(imageViewLP);

            if (iconType == ICON_TYPE_SUCCESS) {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.qmui_icon_notify_done));
            } else if (iconType == ICON_TYPE_FAIL) {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.qmui_icon_notify_error));
            } else if (iconType == ICON_TYPE_NOTHING) {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.qmui_icon_notify_info));
            }
            contentWrap.addView(imageView);
        }

        if (tipWord != null && tipWord.length() > 0) {
            TextView tipView = new TextView(context);
            LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tipViewLP.topMargin = QMUIDisplayHelper.dp2px(context, 12);
            tipView.setLayoutParams(tipViewLP);
            tipView.setEllipsize(TextUtils.TruncateAt.END);
            tipView.setGravity(Gravity.CENTER);
            tipView.setMaxLines(2);
            tipView.setTextColor(ContextCompat.getColor(context, R.color.qmui_config_color_white));
            tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tipView.setText(tipWord);
            contentWrap.addView(tipView);
        }

        currentToast.setView(toastLayout);
        currentToast.setGravity(Gravity.CENTER, 0, 0);
        currentToast.setDuration(duration);
        currentToast.show();
        return currentToast;
    }
}
