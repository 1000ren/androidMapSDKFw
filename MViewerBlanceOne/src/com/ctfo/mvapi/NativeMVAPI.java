package com.ctfo.mvapi;

import android.graphics.Bitmap;

import com.ctfo.mvapi.entities.TileTextInfo;
import com.ctfo.mvapi.utils.Conversation;

/**
 * @author fangwei
 */
public class NativeMVAPI {

    static {
        System.loadLibrary("MCMDNView");
    }

    /**
     * 初始化引擎
     * 
     * @param bitmap
     * @param strPath
     * @return int
     */
    public static native int nativeInitEngine(final Bitmap bitmap, final String strPath, final int iMapMode);

    /**
     * 退出
     * 
     * @return int
     */
    public static native int nativeQuit();

    /**
     * 设置回调
     * 
     * @param conv
     * @return int
     */
    public static native int nativeSetCallback(final Conversation conv);

    
    /**
     * 绘制Tile地图
     * 
     * @param iLon
     * @param iLat
     * @param iScale
     * @return int
     */
    public static native int nativeDrawTileMap(final int iLon, final int iLat, final int iScale);

    /**
     * 获取Tile内的注记信息
     * 
     * @return AnnotationInfo 
     */
    public static native TileTextInfo nativeGetTileTextInfo();

}
