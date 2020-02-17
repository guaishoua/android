package com.android.tacu.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

public class MerchantPopWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;

    private QMUIAlphaButton btn_left;
    private QMUIAlphaButton btn_right;
    private TextView tv_text;
    private ImageView img_close;

    public MerchantPopWindow(Context context) {
        super(context);
        this.mContext = context;
    }

    public void create() {
        setWidth((int) (UIUtils.getScreenWidth() * 0.8));
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_transparent)));

        setFocusable(true);
        setOutsideTouchable(true);

        View view = View.inflate(mContext, R.layout.pop_merchant_advantage, null);

        btn_left = view.findViewById(R.id.btn_left);
        btn_right = view.findViewById(R.id.btn_right);
        tv_text = view.findViewById(R.id.tv_text);
        img_close = view.findViewById(R.id.img_close);

        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        img_close.setOnClickListener(this);

        setContentView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                btn_left.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                btn_right.setTextColor(ContextCompat.getColor(mContext, R.color.text_color));
                tv_text.setText(mContext.getResources().getString(R.string.merchant_otc_advantage_tip));
                break;
            case R.id.btn_right:
                btn_left.setTextColor(ContextCompat.getColor(mContext, R.color.text_color));
                btn_right.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
                tv_text.setText(mContext.getResources().getString(R.string.merchant_c2c_advantage_tip));
                break;
            case R.id.img_close:
                dismiss();
                break;
        }
    }
}
