package com.ctfo.mvapi.utils;


/**
 * @author fangwei
 * 
 * native回调事件定义。
 * 
 */
public class EventType
{
	/** 设置回调监听接口 */
    public static final String EVENT_SET_CBLISTENER = "Event_Set_CallbackListener";
    
	/** DrawTileMap Finish */
	public static final String EVENT_TILEMAP = "Event_DrawTileMapFinished";

    /** 放大 */
    public static final String EVENT_ZOOMIN = "Event_Zoomin";

    /** 缩小 */
    public static final String EVENT_ZOOMOUT = "Event_Zoomout";

    /** 地图刷新 */
    public static final String EVENT_REFRESH_MAP = "Event_Refresh_Map";

    /** 改变地图模式 */
    public static final String EVENT_CHANGE_MAPVIEWMODE = "Event_Change_MapViewMode";


}
