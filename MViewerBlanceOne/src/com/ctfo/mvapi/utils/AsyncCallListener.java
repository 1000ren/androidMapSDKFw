package com.ctfo.mvapi.utils;

/**
 * @author fangwei
 * 
 * 异步调用抽象类
 * 
 */
public abstract class AsyncCallListener
{
    public AsyncCallListener()
    {
    }

    public abstract void onComplete(int token, String event, Object data);
}
