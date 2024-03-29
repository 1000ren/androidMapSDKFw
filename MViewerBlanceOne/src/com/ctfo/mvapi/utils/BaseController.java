package com.ctfo.mvapi.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author fangwei
 * 
 * 消息控制基类
 * 
 */
public abstract class BaseController
{
    protected String[] mEvents;

    Context mContext;

    EventHandler mHandler;

    Map<String, Integer> mMap;

    protected BaseController(Context context) {
        this.mContext = context;
        mHandler = null;
    }

    protected boolean init() {
        Looper looper;

        looper = Looper.myLooper();
        mHandler = new EventHandler(looper);
        mHandler.removeMessages(0);

        if (mEvents != null) {

            EventManager.getInstance()
                    .registerEventHandler(mHandler, mEvents);
            mMap = new HashMap<String, Integer>();

            for (int i = 0; i < this.mEvents.length; i++) {
                this.mMap.put(mEvents[i], i);
            }
        }

        return true;
    }

    public void clear() {
        if (EventManager.getInstance() != null && mEvents != null) {
            EventManager.getInstance()
                    .unregisterEventHandler(mHandler, mEvents);
        }
    }

    public abstract void handleMessage(final Message msg);

    class EventHandler extends Handler
    {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {
            BaseController.this.handleMessage(msg);
        }
    }
}
