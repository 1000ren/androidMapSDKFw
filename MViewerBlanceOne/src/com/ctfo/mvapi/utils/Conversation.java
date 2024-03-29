package com.ctfo.mvapi.utils;

import android.os.Message;


/**
 * @author fangwei
 * 
 * Native与UI层的回调交互控制转换类。
 * 
 */
public class Conversation 
{

    /** Conversation */
    private static Conversation sInstance = new Conversation();

    /** Conversation 
     * @return Conversation
     * */
    public static Conversation getInstance()
    {
        return sInstance;
    }

    /**
     * 控制器之间，控制器与界面之间的会话接口，该接口被调用且接口的内部操作完成后若侦听接口实例不为空则会调用
     * AsyncCallListener的onComplete接口返回数据
     * 
     * @param token
     *            自定义的令牌标志
     * @param cookie
     *            发送消息时传入的信息
     * @param asyncListener
     *            侦听接口实例，若为null则没有回调
     * @param msg
     *            消息，其成员obj为消息字符串
     */
    public static void start(int token, Object cookie,
            AsyncCallListener asyncListener, Message msg)
    {
        if (null == EventManager.getInstance())
        {
            return;
        }
        EventManager.getInstance().postEvent(token, cookie, asyncListener, msg);
    }

    /**
     * 控制器之间，控制器与界面之间的会话接口，该接口被调用且接口的内部操作完成后不会回调界面
     * 
     * @param token
     *            自定义的令牌标志
     * @param cookie
     *            发送消息时传入的信息
     * @param msg
     *            消息，其成员obj为消息字符串
     */
    public static void start(int token, Object cookie, Message msg)
    {
        if (null == EventManager.getInstance())
        {
            return;
        }
        EventManager.getInstance().postEvent(token, cookie, null, msg);
    }

    /**
     * 控制器之间，控制器与界面之间的会话接口，该接口被调用且接口的内部操作完成后不会回调界面
     * 
     * @param token
     *            自定义的令牌标志
     * @param msg
     *            消息，其成员obj为消息字符串
     */
    public static void start(int token, Message msg) 
    {
        if (null == EventManager.getInstance())
        {
            return;
        }
        EventManager.getInstance().postEvent(token, null, null, msg);
    }

    /**
     * 控制器之间，控制器与界面之间的会话接口，该接口被调用且接口的内部操作完成后不会回调界面
     * 
     * @param msg
     *            消息，其成员obj为消息字符串
     */
    public static void start(Message msg) 
    {
        if (null == EventManager.getInstance()) 
        {
            return;
        }
        EventManager.getInstance().postEvent(0, null, null, msg);
    }

    /**
     * 控制器与本地动态库之间的会话接口，
     * 
     * @param event
     *            字符串数组，默认数组的第一个字符串为消息字符串，第二个字符串为附带的返回结果
     */
    public void postEventForNative(final String[] event)
    {
        if (null != EventManager.getInstance() && null != event)
        {
            Message msg = Message.obtain();
            msg.obj = event[0];
            Conversation.start(0, event[1], msg);
        }
    }

    /**
     * 控制器与本地动态库之间的会话接口，用于地图刷新回调
     */
    public void refreshMapDone()
    {
        //Message msg = Message.obtain();
        //msg.obj = EventType.EVENT_UPDATE_SCALE;
        //Conversation.start(msg);

//        if (MainMapView.pthis != null)
//        {
//            MainMapView.pthis.refreshView();
//        }
    }
}
