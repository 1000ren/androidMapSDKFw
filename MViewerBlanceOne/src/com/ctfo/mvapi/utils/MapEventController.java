
package com.ctfo.mvapi.utils;

import android.content.Context;
import android.os.Message;


/**
 * @author fangwei
 * 
 * 地图消息事件控制类
 * 
 */
public class MapEventController extends BaseController
{
	// 需要处理的事件消息定义
    static final String[] EVENTS = {

    	EventType.EVENT_SET_CBLISTENER,
    	EventType.EVENT_TILEMAP,
    	EventType.EVENT_ZOOMIN,
    	EventType.EVENT_ZOOMOUT,
    	EventType.EVENT_REFRESH_MAP,
    	EventType.EVENT_CHANGE_MAPVIEWMODE,

        };

    public static MapEventController pthis;

    public AsyncCallListener mCallBack;

    protected MapEventController(Context context)
    {
        super(context);
        mCallBack = null;
        this.mEvents = EVENTS;
        this.mContext = context;
    }

    public static MapEventController getInstance() {
        return pthis;
    }

    public static MapEventController createInstance(final Context context)
    {
        if (pthis == null) {
            pthis = new MapEventController(context);

            if (pthis.init() == false) {
                pthis = null;
            }
        }

        return pthis;
    }

    protected boolean init()
    {
        if (super.init() == false)
        {
            return false;
        }

        return true;
    }

    public static void destoryInstance()
    {
        if (pthis == null) {
            return;
        }

        pthis.clear();
        pthis = null;
    }

    @Override
    public void handleMessage(final Message msg)
    {
        try {
            WorkArgs args = (WorkArgs) msg.obj;
            int token = args.token;
            AsyncCallListener cb = (AsyncCallListener) args.callBack;
            if (null != cb) {
                mCallBack = cb;
            }

            int eventId = this.mMap.get(args.event);

            switch (eventId)
            {
            case ConstVariable.INT_0: // EVENT_SET_CBLISTENER
                break;
            case ConstVariable.INT_1: // EVENT_TILEMAP
            {
                if ( mCallBack != null )
                {
                	mCallBack.onComplete(token, args.event, args.cookie);
                }
                break;
            }
            case ConstVariable.INT_2: // EVENT_ZOOMIN
            	break;
            case ConstVariable.INT_3: // EVENT_ZOOMOUT
                break;
            case ConstVariable.INT_4: // EVENT_REFRESH_MAP
                break;
            case ConstVariable.INT_5: // EVENT_CHANGE_MAPVIEWMODE
                break;
            default:
                break;
            }
        }
        catch (Exception e)
        {
        	MVApiLog.w( "MapController", "handleMessage error Exception e == "
                    + e.toString());
            e.printStackTrace();
        }
    }

}

