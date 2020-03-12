package com.android.tacu.utils;

import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.MyApplication;
import com.android.tacu.utils.user.UserInfoUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by jiazhen on 2018/8/21.
 */
public class CommonUtils {

    /**
     * 手机震动
     */
    public static void Vibrate(long[] pattern) {
        Vibrator vib = (Vibrator) MyApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, -1);
    }

    /**
     * 设置关键字高亮
     */
    public static SpannableString setKeyWordColor(String content, String keyword, int resColor) {
        SpannableString strValue = new SpannableString(content);
        Pattern pattern = Pattern.compile(keyword);
        Matcher matcher = pattern.matcher(strValue);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            strValue.setSpan(new ForegroundColorSpan(resColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return strValue;
    }

    /**
     * 选择出生年月
     *
     * @param context
     * @param startTime
     */
    public static void selectTime(final Context context, final TextView startTime, String maxDate) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                startTime.setText(DateFormat.format("yyyy-MM-dd", c).toString());
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        if (!TextUtils.isEmpty(maxDate)) {
            long maxDateLong = DateUtils.string2Millis(maxDate, DateUtils.FORMAT_DATE_YMD);
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMaxDate(maxDateLong);
        }
        dialog.show();
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        boolean clientValid = wechat.isClientValid();
        if (!clientValid) {
            Toast.makeText(context, context.getResources().getString(R.string.noweixin), Toast.LENGTH_LONG).show();
        }
        return clientValid;
    }

    /**
     * 获取图片上传的名字  kyc2上传
     *
     * @return
     */
    public static String getOSSUuidName() {
        String replace = UUID.randomUUID().toString().replace("-", "");
        long currentTimeMillis = System.currentTimeMillis();
        return replace.substring(0, 15) + currentTimeMillis + replace.substring(14, 31) + "Android";
    }

    /**
     * 头像上传
     *
     * @return
     */
    public static String getHeadImageName() {
        String replace = UUID.randomUUID().toString().replace("-", "");
        long currentTimeMillis = System.currentTimeMillis();
        return replace.substring(0, 15) + currentTimeMillis + replace.substring(14, 31) + "Android_headimg";
    }

    /**
     * 微信收款码上传
     *
     * @returnx
     */
    public static String getWxImageName() {
        String replace = UUID.randomUUID().toString().replace("-", "");
        long currentTimeMillis = System.currentTimeMillis();
        return replace.substring(0, 15) + currentTimeMillis + replace.substring(14, 31) + "Android_WeixinSK";
    }

    /**
     * 支付宝收款码上传
     *
     * @returnx
     */
    public static String getZfbImageName() {
        String replace = UUID.randomUUID().toString().replace("-", "");
        long currentTimeMillis = System.currentTimeMillis();
        return replace.substring(0, 15) + currentTimeMillis + replace.substring(14, 31) + "Android_ZfbSK";
    }

    /**
     * 商户视频上传
     *
     * @returnx
     */
    public static String getVideoName() {
        String replace = UUID.randomUUID().toString().replace("-", "");
        long currentTimeMillis = System.currentTimeMillis();
        return replace.substring(0, 15) + currentTimeMillis + replace.substring(14, 31) + "Android_Video.mp4";
    }

    /**
     * 付款凭证
     *
     * @returnx
     */
    public static String getPayImageName() {
        String replace = UUID.randomUUID().toString().replace("-", "");
        long currentTimeMillis = System.currentTimeMillis();
        return replace.substring(0, 15) + currentTimeMillis + replace.substring(14, 31) + "Android_PayImage";
    }

    /**
     * 判断绑定邮箱或者手机号
     * 1：绑定手机号和邮箱
     * 2：单邦定邮箱
     * 3：单邦定手机
     *
     * @return
     */
    public static int isBindMode() {
        UserInfoUtils spUtil = UserInfoUtils.getInstance();
        if (spUtil.getEmailStatus() && spUtil.getPhoneStatus()) {
            return 1;
        } else if (spUtil.getEmailStatus()) {
            return 2;
        } else if (spUtil.getPhoneStatus()) {
            return 3;
        }
        return 0;
    }

    /**
     * 判断浮点数
     *
     * @param value
     * @return
     */
    public static boolean isFloatPoint(String value) {
        Pattern pat = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        Matcher m = pat.matcher(value);
        return m.matches();
    }

    /**
     * 判断整数
     *
     * @param value
     * @return
     */
    public static boolean isInteger(String value) {
        Pattern pat = Pattern.compile("^-?\\d+$");
        Matcher m = pat.matcher(value);
        return m.matches();
    }

    /**
     * 选择结束时间
     */
    public static void endTime(final Context context, final TextView endTime, String minDate) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                endTime.setText(DateFormat.format("yyyy-MM-dd", c).toString());
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        if (!TextUtils.isEmpty(minDate)) {
            long minDateLong = DateUtils.string2Millis(minDate, DateUtils.FORMAT_DATE_YMD);
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMinDate(minDateLong);
        }
        dialog.show();
    }

    /**
     * 判断当前区号
     */
    public static int getContryCode() {
        UserInfoUtils spUtils = UserInfoUtils.getInstance();
        if (TextUtils.equals(spUtils.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtils.getLanguage(), Constant.ZH_CN)) {
            return 86;
        } else {
            return 44;
        }
    }

    public static void handleEditTextEyesIssueInBrightBackground(View view) {
        if (view instanceof QMUIRoundEditText) {
            QMUIRoundEditText editText = (QMUIRoundEditText) view;
            editText.setShowPwdDrawable(
                    R.drawable.qmui_icon_edittext_pwdvis_bright_bg,
                    R.drawable.qmui_icon_edittext_pwdgone_bright_bg);
        }
    }

    /**
     * 处理头像中的a-Heads-image
     *
     * @param url
     * @return
     */
    public static String getHead(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains(Constant.OSS_HEAD_DIR)) {
                url.replace(Constant.OSS_HEAD_DIR, "");
            }
            return Constant.HEAD_IMG_URL + url;
        }
        return "";
    }

    public static String nameXing(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        return "*" + name;
    }

    public static String nameDesensitization(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        String myName = null;
        char[] chars = name.toCharArray();
        if (chars.length == 1) {
            myName = name;
        }
        if (chars.length == 2) {
            myName = name.replaceFirst(name.substring(1), "*");
        }
        if (chars.length > 2) {
            myName = name.replaceAll(name.substring(1, chars.length - 1), "*");
        }
        return myName;
    }

    public static JSONArray userInfoData(String nickname, String mobile, String email, String avatar) {
        JSONArray array = new JSONArray();
        array.add(userInfoDataItem("real_name", nickname, false, -1, null, null)); // 昵称
        array.add(userInfoDataItem("mobile_phone", mobile, false, -1, null, null)); // 手机号
        array.add(userInfoDataItem("email", email, false, -1, null, null)); // 邮箱
        array.add(userInfoDataItem("avatar", avatar, false, -1, null, null)); // 头像
        return array;
    }

    private static JSONObject userInfoDataItem(String key, Object value, boolean hidden, int index, String label, String href) {
        JSONObject item = new JSONObject();
        item.put("key", key);
        item.put("value", value);
        if (hidden) {
            item.put("hidden", true);
        }
        if (index >= 0) {
            item.put("index", index);
        }
        if (!TextUtils.isEmpty(label)) {
            item.put("label", label);
        }
        if (!TextUtils.isEmpty(href)) {
            item.put("href", href);
        }
        return item;
    }
}
