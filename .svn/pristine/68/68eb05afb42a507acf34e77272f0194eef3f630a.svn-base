package com.tv.xeeng.base.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thanhnvt on 23/06/2014.
 */
public class BlahBlahUtil {
    public static String getLogString(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return String.format("%s: %s\n", format.format(new Date()), s);
    }

    public static boolean isEmptyString(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean hasEmptyString(String... ss) {
        for (String s : ss) {
            if (isEmptyString(s)) {
                return true;
            }
        }

        return false;
    }
}
