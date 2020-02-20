package com.android.tacu.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;

public class OtcPopWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private LinearLayout linView;
    private ImageView img_close;

    public OtcPopWindow(Context context) {
        super(context);
        this.mContext = context;
    }

    public void create(View customView) {
        setWidth((int) (UIUtils.getScreenWidth() * 0.8));
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.color_transparent)));

        setFocusable(true);
        setOutsideTouchable(true);

        View view = View.inflate(mContext, R.layout.pop_otc, null);
        linView = view.findViewById(R.id.lin_view);
        img_close = view.findViewById(R.id.img_close);
        img_close.setOnClickListener(this);

        linView.addView(customView);

        setContentView(view);
    }

    public void setBg(float value) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = value;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                dismiss();
                break;
        }
    }
}
