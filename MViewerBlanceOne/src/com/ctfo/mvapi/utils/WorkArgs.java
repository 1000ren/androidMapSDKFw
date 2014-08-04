package com.ctfo.mvapi.utils;

/**
 * @author fangwei
 * 
 * 工作参数封装类。
 * 
 */
public class WorkArgs
{
    public int token;
    public Object callBack;
    public Object cookie;
    public String event;

    public WorkArgs()
    {
    }

    /** 
     * @param token token
     * @param cookie cookie
     * @param callBack callBack
     * @param strEvent strEvent
     * TODO */
    public WorkArgs(int token, Object cookie, Object callBack, String strEvent) {
        this.token = token;
        this.cookie = cookie;
        this.callBack = callBack;
        this.event = strEvent;
    }
}
