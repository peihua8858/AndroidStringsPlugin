/*
 * Copyright (C) Globalegrow E-Commerce Co. , Ltd. 2007-2016.
 * All rights reserved.
 * This software is the confidential and proprietary information
 * of Globalegrow E-Commerce Co. , Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with Globalegrow.
 */

package com.fz.plugin.utils;

/**
 * Create
 *
 * @author dingpeihua
 * @version 1
 * @since 2015/12/9.
 */
public class ParseUtil {
    /**
     * 将Object对象转成boolean类型
     *
     * @param value
     * @return 如果value不能转成boolean，则默认false
     */
    public static Boolean toBoolean(Object value) {
        return toBoolean(value, false);
    }

    /**
     * 将Object对象转成boolean类型
     *
     * @param value
     * @return 如果value不能转成boolean，则默认defaultValue
     */
    public static Boolean toBoolean(Object value, boolean defaultValue) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return "true".equalsIgnoreCase((String) value);
        }
        return defaultValue;
    }

    /**
     * 将Object对象转成Double类型
     *
     * @param value
     * @return 如果value不能转成Double，则默认0.00
     */
    public static Double toDouble(Object value) {
        return toDouble(value, 0.00);
    }

    /**
     * 将Object对象转成Double类型
     *
     * @param value
     * @return 如果value不能转成Double，则默认defaultValue
     */
    public static Double toDouble(Object value, double defaultValue) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 将Object对象转成Double类型
     *
     * @param value
     * @return 如果value不能转成Float，则默认0.00
     */
    public static Float toFloat(Object value) {
        return toFloat(value, 0.00f);
    }

    /**
     * 将Object对象转成Double类型
     *
     * @param value
     * @return 如果value不能转成Float，则默认defaultValue
     */
    public static Float toFloat(Object value, float defaultValue) {
        if (value instanceof Double) {
            return (Float) value;
        } else if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            try {
                return Float.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 将Object对象转成Integer类型
     *
     * @param value
     * @return 如果value不能转成Integer，则默认0
     */
    public static Integer toInteger(Object value) {
        return toInteger(value, 0);
    }

    /**
     * 将Object对象转成Integer类型
     *
     * @param value
     * @return 如果value不能转成Integer，则默认defaultValue
     */
    public static Integer toInteger(Object value, int defaultValue) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return (int) Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 将Object对象转成Long类型
     *
     * @param value
     * @return 如果value不能转成Long，则默认0
     */
    public static Long toLong(Object value) {
        return toLong(value, 0L);
    }

    /**
     * 将Object对象转成Long类型
     *
     * @param value
     * @return 如果value不能转成Long，则默认defaultValue
     */
    public static Long toLong(Object value, long defaultValue) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return (long) Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 将Object对象转成String类型
     *
     * @param value
     * @return 如果value不能转成String，则默认""
     */
    public static String toString(Object value) {
        return toString(value, "");
    }

    /**
     * 将Object对象转成String类型
     *
     * @param value
     * @return 如果value不能转成String，则默认defaultValue
     */
    public static String toString(Object value, String defaultValue) {
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return String.valueOf(value);
        }
        return defaultValue;
    }
}
