package com.android.tacu.module.otc.dialog;

import android.content.Context;
import android.view.View;

import com.android.tacu.R;

public class OtcDialogUtils {

    public static boolean isDialogShow(Context mContext) {
        View view = View.inflate(mContext, R.layout.view_otc_judge, null);
        return true;
    }
}
