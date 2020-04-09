package com.github.tifezh.kchartlib.chart.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * Created by tifezh on 2016/4/27.
 */
public class DateUtil {

    public static SimpleDateFormat longTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public static final String FORMAT_DATE_HM = "HH:mm";
    public static final String FORMAT_DATE_MDHM = "MM-dd HH:mm";
    public static final String FORMAT_DATE_YMD = "yyyy-MM-dd";

    public static String millis2String(long millis, String pattern) {
        SimpleDateFormat format = getSimpleDateFormat(pattern);
        return format.format(new Date(millis));
    }

    /**
     * 将 其他格式的 日期 转成新的格式的日期
     * 例如：2018-09-26 10:59:08 转成 2018-09-26
     *
     * @param time
     * @param oldFormat
     * @param newFormat
     * @return
     */
    public static String getStrToStr(String time, String oldFormat, String newFormat) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            SimpleDateFormat sf = getSimpleDateFormat(oldFormat);
            return date2String(sf.parse(time), newFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        SimpleDateFormat format = local.get();
        format.applyPattern(pattern);
        return format;
    }

    public static String date2String(Date date, String pattern) {
        SimpleDateFormat format = getSimpleDateFormat(pattern);
        return format.format(date);
    }

    private static final ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat();
        }
    };
}
