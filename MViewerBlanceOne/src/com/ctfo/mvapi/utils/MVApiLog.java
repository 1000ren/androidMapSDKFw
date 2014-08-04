package com.ctfo.mvapi.utils;

import android.util.Log;

/**
 * @author fangwei
 * 
 * 日志输出封装。
 * 
 */
public class MVApiLog
{

    static final String LOG = "MVApi";

    static final String COLON = ":";

    public static void e(final String tag, final String msg) {
        Log.e(LOG, tag + ":e" + COLON + msg);
    }

    public static void v(final String tag, final String msg) {
        Log.v(LOG, tag + ":v" + COLON + msg);
    }

    public static void d(final String tag, final String msg) {
        Log.d(LOG, tag + ":d" + COLON + msg);
    }

    public static void i(final String tag, final String msg) {
        Log.i(LOG, tag + ":i" + COLON + msg);
    }

    public static void w(final String tag, final String msg) {
        Log.w(LOG, tag + ":w" + COLON + msg);
    }
}
