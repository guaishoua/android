package com.android.tacu.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

/**
 * Created by droidbyme on 28-03-2017.
 */

public class DroidDialog {

    private Dialog dialog = null;
    private QMUIAlphaButton btnPositive;
    private QMUIAlphaButton btnNegative;

    public DroidDialog(final Builder builder) {

        dialog = new Dialog(builder.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(builder.context).inflate(R.layout.view_layout_dialog, null);
        dialog.setContentView(view);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = UIUtils.getScreenWidth() * 9 / 10;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setBackgroundDrawableResource(R.color.color_transparent);
        dialog.getWindow().setAttributes(lp);

        /**
         *findviewById
         **/
        RelativeLayout rlTitle = view.findViewById(R.id.rl_title);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtContent = view.findViewById(R.id.txtContent);

        btnPositive = view.findViewById(R.id.btnPositive);
        btnNegative = view.findViewById(R.id.btnNegative);
        QMUIAlphaButton btnNeutral = view.findViewById(R.id.btnNeutral);

        NestedScrollView scrollview = view.findViewById(R.id.scrollview);
        LinearLayout lin_view = view.findViewById(R.id.lin_view);

        /**
         *apply customization to dialog
         **/
        btnPositive.setTextColor(builder.buttonTextColor);
        btnNegative.setTextColor(builder.buttonTextColor);
        btnNeutral.setTextColor(builder.buttonTextColor);

        if (TextUtils.isEmpty(builder.title)) {
            rlTitle.setVisibility(View.GONE);
        } else {
            rlTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(builder.title);
        }

        if (builder.titleGravity != -1) {
            txtTitle.setGravity(builder.titleGravity);
        } else {
            txtTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }

        if (TextUtils.isEmpty(builder.content)) {
            txtContent.setVisibility(View.GONE);
        } else {
            txtContent.setVisibility(View.VISIBLE);
            txtContent.setText(builder.content);
        }

        if (builder.contentGravity != -1) {
            txtContent.setGravity(builder.contentGravity);
        } else {
            txtContent.setGravity(Gravity.LEFT);
        }

        if (builder.lin_view_layout != null) {
            txtContent.setVisibility(View.GONE);
            lin_view.addView(builder.lin_view_layout);
        }

        if (TextUtils.isEmpty(builder.content) && builder.lin_view_layout == null) {
            scrollview.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(builder.positiveText)) {
            btnPositive.setVisibility(View.GONE);
        } else {
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setText(builder.positiveText);
            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder.onPositiveListener != null) {
                        builder.onPositiveListener.onPositive(dialog);
                    }
                    if (builder.isPositiveClickDismiss) {
                        dismiss();
                    }
                }
            });
        }

        if (TextUtils.isEmpty(builder.negativeText)) {
            btnNegative.setVisibility(View.GONE);
        } else {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setText(builder.negativeText);
            btnNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder.onNegativeListener != null) {
                        builder.onNegativeListener.onNegative(dialog);
                    }
                    dismiss();
                }
            });
        }

        if (TextUtils.isEmpty(builder.neutralText)) {
            btnNeutral.setVisibility(View.GONE);
        } else {
            btnNeutral.setVisibility(View.VISIBLE);
            btnNeutral.setText(builder.neutralText);
            btnNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (builder.onNeutralListener != null) {
                        builder.onNeutralListener.onNeutral(dialog);
                    }
                    dismiss();
                }
            });
        }

        if (!TextUtils.isEmpty(builder.typeface)) {
            Typeface ttf = Typeface.createFromAsset(builder.context.getAssets(), "fonts/" + builder.typeface);
            txtTitle.setTypeface(ttf);
            txtContent.setTypeface(ttf);
            btnPositive.setTypeface(ttf, Typeface.BOLD);
            btnNegative.setTypeface(ttf, Typeface.BOLD);
            btnNeutral.setTypeface(ttf, Typeface.BOLD);
        }

        dialog.setCancelable(builder.isCancelable);
        dialog.setCanceledOnTouchOutside(builder.isCancelableTouchOutside);
        dialog.show();
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    public void setBtnPositive(int visibility) {
        if (dialog != null && btnPositive != null) {
            btnPositive.setVisibility(visibility);
        }
    }

    public void setBtnNegative(int visibility) {
        if (dialog != null && btnNegative != null) {
            btnNegative.setVisibility(visibility);
        }
    }

    public static class Builder {

        // default values
        private Context context;
        private String title = "";
        private String content = "";
        private View lin_view_layout;

        private int titleGravity = -1;
        private int contentGravity = -1;

        private boolean isCancelable = true;
        private boolean isCancelableTouchOutside = true;

        private String positiveText = "";
        private boolean isPositiveClickDismiss = true;//true=当前的按钮点击之后 直接自动关闭 false=当前的按钮需要写代码控制按钮消失
        private onPositiveListener onPositiveListener = new onPositiveListener() {
            @Override
            public void onPositive(Dialog droidDialog) {
            }
        };

        private String negativeText = "";
        private onNegativeListener onNegativeListener = new onNegativeListener() {
            @Override
            public void onNegative(Dialog droidDialog) {
            }
        };

        private String neutralText = "";
        private onNeutralListener onNeutralListener = new onNeutralListener() {
            @Override
            public void onNeutral(Dialog droidDialog) {
            }
        };

        private String typeface = "";
        private int buttonTextColor = 0;

        public Builder(Context context) {
            this.context = context;
            buttonTextColor = ContextCompat.getColor(context, R.color.color_white);
        }

        public DroidDialog show() {
            return new DroidDialog(this);
        }

        /**
         * dialog title
         **/
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder titleGravity(int titleGravity) {
            this.titleGravity = titleGravity;
            return this;
        }

        /**
         * dialog content message
         **/
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder contentGravity(int contentGravity) {
            this.contentGravity = contentGravity;
            return this;
        }

        /**
         * 自定义的view填充lin_view
         */
        public Builder viewCustomLayout(View view) {
            this.lin_view_layout = view;
            return this;
        }

        /**
         * dialog cancelable flag
         **/
        public Builder cancelable(boolean isCancelable, boolean isCancelableTouchOutside) {
            this.isCancelable = isCancelable;
            this.isCancelableTouchOutside = isCancelableTouchOutside;
            return this;
        }

        /**
         * dialog positive button and click event handler
         */
        public Builder positiveButton(String positiveText, onPositiveListener onPositiveListener) {
            this.positiveText = positiveText;
            this.onPositiveListener = onPositiveListener;
            return this;
        }

        public Builder positiveButton(String positiveText, boolean isPositiveClickDismiss, onPositiveListener onPositiveListener) {
            this.positiveText = positiveText;
            this.isPositiveClickDismiss = isPositiveClickDismiss;
            this.onPositiveListener = onPositiveListener;
            return this;
        }

        /**
         * dialog negative button and click event handler
         **/
        public Builder negativeButton(String negativeText, onNegativeListener onNegativeListener) {
            this.negativeText = negativeText;
            this.onNegativeListener = onNegativeListener;
            return this;
        }

        public Builder negativeButton(onNegativeListener onNegativeListener) {
            this.onNegativeListener = onNegativeListener;
            return this;
        }

        /**
         * dialog neutral button and click event handler
         **/
        public Builder neutralButton(String neutralText, onNeutralListener onNeutralListener) {
            this.neutralText = neutralText;
            this.onNeutralListener = onNeutralListener;
            return this;
        }

        /**
         * dialog custom typeface, applied to title, content message and button
         * -- put your .ttf file in assets/fonts directory
         * -- pass font file name with extension in String type
         **/
        public Builder typeface(String typeface) {
            this.typeface = typeface;
            return this;
        }

        /**
         * dialog color
         * -- titleBgColor : dialog topbar background color
         * -- iconTintColor : dialog topbar icon tint color
         * -- buttonTextColor : dialog positive, negative and neutral button text color
         **/
        public Builder color(int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }
    }

    public interface onPositiveListener {
        void onPositive(Dialog droidDialog);
    }

    public interface onNegativeListener {
        void onNegative(Dialog droidDialog);
    }

    public interface onNeutralListener {
        void onNeutral(Dialog droidDialog);
    }
}
