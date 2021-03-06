package com.android.tacu.utils;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * Created by jiazhen on 2018/11/9.
 */
public class FormatterUtils {

    private static int[] values = {1000, 1000000, 1000000000};
    private static String[] units = {"K", "M", "B"};

    /**
     * 科学计数法转string
     *
     * @param value
     * @return
     */
    public static String getFormatValue(double value) {
        try {
            return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    public static String getFormatValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        try {
            return new BigDecimal(value).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 直接删除多余的小数位
     * 例如 3.45 变成 3.4
     *
     * @param newScale
     * @param value
     * @return
     */
    public static String getFormatRoundDown(int newScale, double value) {
        try {
            return BigDecimal.valueOf(value).setScale(newScale, BigDecimal.ROUND_DOWN).toPlainString();
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    public static String getFormatRoundDown(int newScale, String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        try {
            return BigDecimal.valueOf(Double.parseDouble(value)).setScale(newScale, BigDecimal.ROUND_DOWN).toPlainString();
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 多余的小数位进位处理
     * 例如 3.41 变成 3.5
     *
     * @param newScale
     * @param value
     * @return
     */
    public static String getFormatRoundUp(int newScale, double value) {
        try {
            return BigDecimal.valueOf(value).setScale(newScale, BigDecimal.ROUND_UP).toPlainString();
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    public static String getFormatRoundUp(int newScale, String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        try {
            return BigDecimal.valueOf(Double.parseDouble(value)).setScale(newScale, BigDecimal.ROUND_UP).toPlainString();
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 多余的小数位进行四舍五入处理
     * 例如 3.41 变成 3.4  3.45 变成 3.5
     */
    public static String getFormatRoundHalfUp(int newScale, double value) {
        try {
            return BigDecimal.valueOf(value).setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString();
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    public static String getFormatRoundHalfUp(int newScale, String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        try {
            return BigDecimal.valueOf(Double.parseDouble(value)).setScale(newScale, BigDecimal.ROUND_HALF_UP).toPlainString();
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 大额的数字进行优化
     */
    public static String getBigValueFormatter(int newScale, double value) {
        try {
            String unit = "";
            int i = values.length - 1;
            while (i >= 0) {
                if (value > values[i]) {
                    value /= values[i];
                    unit = units[i];
                    break;
                }
                i--;
            }
            if (TextUtils.isEmpty(unit)) {
                return getFormatRoundDown(newScale, value);
            }
            return String.format("%.2f", value) + unit;
        } catch (Exception e) {
            return getFormatRoundDown(newScale, value);
        }
    }

    public static String getBigValueFormatter(int newScale, String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        try {
            String unit = "";
            double valueD = Double.parseDouble(value);
            int i = values.length - 1;
            while (i >= 0) {
                if (valueD > values[i]) {
                    valueD /= values[i];
                    unit = units[i];
                    break;
                }
                i--;
            }
            if (TextUtils.isEmpty(unit)) {
                return getFormatRoundDown(newScale, value);
            }
            return String.format("%.2f", valueD) + unit;
        } catch (Exception e) {
            return value;
        }
    }

    public static String getBigValueFormatter(double value) {
        try {
            String unit = "";
            int i = values.length - 1;
            while (i >= 0) {
                if (value > values[i]) {
                    value /= values[i];
                    unit = units[i];
                    break;
                }
                i--;
            }
            if (TextUtils.isEmpty(unit)) {
                return getFormatValue(value);
            }
            return String.format("%.2f", value) + unit;
        } catch (Exception e) {
            return getFormatValue(value);
        }
    }

    public static String getBigValueFormatter(String value) {
        if (TextUtils.isEmpty(value)) {
            return value;
        }
        try {
            String unit = "";
            double valueD = Double.parseDouble(value);
            int i = values.length - 1;
            while (i >= 0) {
                if (valueD > values[i]) {
                    valueD /= values[i];
                    unit = units[i];
                    break;
                }
                i--;
            }
            if (TextUtils.isEmpty(unit)) {
                return getFormatValue(value);
            }
            return String.format("%.2f", valueD) + unit;
        } catch (Exception e) {
            return getFormatValue(value);
        }
    }
}
