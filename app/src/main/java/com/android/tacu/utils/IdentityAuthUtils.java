package com.android.tacu.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.android.tacu.R;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.utils.permission.PermissionUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;

/**
 * Created by xiaohong on 2018/8/27.
 */

public class IdentityAuthUtils {

    /**
     * 选择性别
     */
    public static void setGender(final Context context, final TextView itemGender) {
        new QMUIBottomSheet.BottomListSheetBuilder(context)
                .addItem(context.getResources().getString(R.string.woman))
                .addItem(context.getResources().getString(R.string.man))
                .addItem(context.getResources().getString(R.string.cancel))
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        switch (position) {
                            case 0:
                                itemGender.setText(context.getResources().getString(R.string.woman));
                                itemGender.setTag(1);
                                dialog.dismiss();
                                break;
                            case 1:
                                itemGender.setText(context.getResources().getString(R.string.man));
                                itemGender.setTag(2);
                                dialog.dismiss();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .build()
                .show();
    }


    /**
     * 选择出生年月
     * 默认起止时间 = 当前时间-start，结束时间 = 当前时间+end
     *
     * @param context
     * @param itemBirthday
     */
    public static void setBirthday(final Context context, final TextView itemBirthday, int start, int end) {
        //时间选择器(随时间改变开始时间与结束时间需要改变)
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        int startTime;
        int endTime;
        if (start == 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            selectedDate.add(Calendar.YEAR, 100);
            startTime = Integer.parseInt(sdf.format(selectedDate.getTime()).substring(0, 4));
            selectedDate.add(Calendar.YEAR, 100 + 100);
            endTime = Integer.parseInt(sdf.format(selectedDate.getTime()).substring(0, 4));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            selectedDate.add(Calendar.YEAR, -start);
            startTime = Integer.parseInt(sdf.format(selectedDate.getTime()).substring(0, 4));
            selectedDate.add(Calendar.YEAR, end + start);
            endTime = Integer.parseInt(sdf.format(selectedDate.getTime()).substring(0, 4));
        }

        startDate.set(startTime, 0, 1);
        endDate.set(endTime, 11, 31);

        TimePickerView pvTime = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                itemBirthday.setText(DateUtils.date2String(date, DateUtils.FORMAT_DATE_YMD));
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setCancelText(context.getResources().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(context.getResources().getString(R.string.sure))//确认按钮文字
                .setContentTextSize(16)//滚轮文字大小
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setSubmitColor(ContextCompat.getColor(context, R.color.text_black))//确定按钮文字颜色
                .setCancelColor(ContextCompat.getColor(context, R.color.text_black))//取消按钮文字颜色
                .setTitleBgColor(ContextCompat.getColor(context, R.color.color_white))//标题背景颜色 Night mode
                .setBgColor(ContextCompat.getColor(context, R.color.color_white))//滚轮背景颜色 Night mode
                .setDate(Calendar.getInstance())// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel(context.getResources().getString(R.string.picker_year), context.getResources().getString(R.string.picker_month), context.getResources().getString(R.string.picker_dete), "", "", "")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    /**
     * 关闭软键盘
     *
     * @param ct
     */
    public static void closeKeyBoard(Activity ct) {
        try {
            InputMethodManager im = (InputMethodManager) ct.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(ct.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍身份证正面照
     *
     * @param activity
     * @param code
     */
    public static void setImage(final Activity activity, final int code) {
        PermissionUtils.requestPermissions(activity, new OnPermissionListener() {

            @Override
            public void onPermissionSucceed() {
                boolean mTakePhotoEnabled = true;
                // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "tacu");
                activity.startActivityForResult(BGAPhotoPickerActivity.newIntent(activity, mTakePhotoEnabled ? takePhotoDir : null, 1, null, mTakePhotoEnabled), code);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
    }


}
