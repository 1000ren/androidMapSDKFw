package com.ctfo.mvapi.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


/**
 * @author fangwei
 * 
 * 事件管理类
 * 
 */
public class EventManager
{
    private static final String LOG_TAG = "EventManager";

    public static final int EVENTPOST = Integer.MIN_VALUE;

    public static final int EVENTSEND = 0xcfffffff;

    public static EventManager pthis;

    Context mContext;

    private Map<String, Vector<Handler>> mEventLoop;

    private EventManager(Context context)
    {
        mContext = context;
    }

    public static EventManager getInstance()
    {
        return pthis;
    }

    public static EventManager createInstance(final Context context)
    {
        if (pthis == null) {
            pthis = new EventManager(context);
            if(pthis == null)
            {
            	System.out.println("EventManager createInstance error");
            }
            if (pthis.init() == false) {
                pthis = null;
            }
        }

        return pthis;
    }

    public static void destoryInstance()
    {
        if (pthis == null) {
            return;
        }
        pthis.clear();
        pthis = null;
    }

    private boolean init()
    {
        mEventLoop = new HashMap<String, Vector<Handler>>();
        if (mEventLoop == null) {
            return false;
        }
        return true;
    }

    public boolean registerEventHandler(final Handler handler,
            final String event)
    {
        Vector<Handler> vector = null;

        if (mEventLoop.containsKey(event) == false)
        {
            vector = new Vector<Handler>();
            mEventLoop.put(event, vector);
        }

        vector = mEventLoop.get(event);

        if (vector.indexOf(handler) != -1) {
            this.unregisterEventHandler(handler, event);
        }

        vector.add(handler);
        return true;
    }

    public boolean registerEventHandler(final Handler handler,
            final String[] events)
    {
        for (int i = 0; i < events.length; i++) {
            if (this.registerEventHandler(handler, events[i]) == false) {
                return false;
            }
        }

        return true;
    }

    public void unregisterEventHandler(final Handler handler, final String event)
    {
        try {
            if (mEventLoop.containsKey(event)) {
                Vector<Handler> vector = mEventLoop.get(event);
                int index = vector.indexOf(handler);

                vector.remove(index);
            }
        } catch (Exception e) {
            MVApiLog.e(LOG_TAG, "unregisterEventHandler exception");
        }
    }

    public void unregisterEventHandler(final Handler handler,
            final String[] events)
    {
        for (int i = 0; i < events.length; i++) {
            this.unregisterEventHandler(handler, events[i]);
        }
    }

    public void clear() 
    {
        this.clearEventHandler();
    }

    public void clearEventHandler()
    {
        this.mEventLoop.clear();
    }

    /**
     * 默认第一个字符串是event
     * 
     * @param token
     *            token
     * @param cookie
     *            cookie
     * @param asyncListener
     *            asyncListener
     * @param msg
     *            msg
     * @return true 表示有人注册
     */
    private boolean dispatchPostEvent(int token, Object cookie,
            AsyncCallListener asyncListener, Message msg)
    {

        String event = (String) msg.obj;
        Vector<Handler> vector = mEventLoop.get(event);

        if (vector == null)
        {
            return false;
        }

        WorkArgs args = new WorkArgs(token, cookie, asyncListener, event);

        for (int i = 0; i < vector.size(); i++) {
            try {
                Handler handler = vector.elementAt(i);
                Message message = Message.obtain(handler, EVENTPOST, args);
                message.what = msg.what;
                message.arg1 = msg.arg1;
                message.arg2 = msg.arg2;
                handler.sendMessage(message);

            } catch (Exception e) {
            	MVApiLog.e(LOG_TAG, "dispatchPostEvent exception");
            }
        }

        return true;
    }

    /**
     * 默认第一个字符串是event
     * @param token
     *            token
     * @param cookie
     *            cookie
     * @param asyncListener
     *            asyncListener
     * @param msg
     *            msg
     * @return true 表示至少有一个消息接收者
     */
    public boolean postEvent(int token, Object cookie,
            AsyncCallListener asyncListener, Message msg)
    {
        return this.dispatchPostEvent(token, cookie, asyncListener, msg);
    }
}
