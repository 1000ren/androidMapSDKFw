package com.ctfo.mvapi.map;

import android.graphics.Bitmap;

import com.ctfo.mvapi.entities.GeoPoint;

public class OverlayItem
{
	//表明该overlay item处于焦点选中状态。 
	public static final int ITEM_STATE_FOCUSED_MASK = 4;
	//表明该overlay item处于选中状态。 
	public static final int ITEM_STATE_PRESSED_MASK = 1;
	//表明该overlay item处于选中状态。
	public static final int ITEM_STATE_SELECTED_MASK = 2;
	
	private Bitmap mMarker;
	private GeoPoint mPoint;
	private String mTitle;
	private String mSnippet;
	
	private int mDrawStyle;
	
	/**
	 * 构造器
	 * @param point item的位置
	 * @param title 标题文本
	 * @param snippet 文字片段
	 */
	public OverlayItem(GeoPoint point, java.lang.String title, java.lang.String snippet) 
	{
		mPoint = point;
		mTitle = title;
		mSnippet = snippet;
	}
	
	/**
	 * 返回标记点，该标记点在地图上绘制该item时使用
	 * @param stateBitset 当前的状态
	 * @return 当前状态的标记点，如果overlay使用默认标记点，则返回null
	 */
	public Bitmap getMarker(int stateBitset)
	{
		return mMarker;
	}

	/**
	 * 返回标记点，该标记点在地图上绘制该item时使用
	 * @return 当前状态的标记点，如果overlay使用默认标记点，则返回null
	 */
	public Bitmap getmMarker()
	{
		return mMarker;
	}
	
	/**
	 * 返回该overlay的经纬度，以GeoPoint方式
	 * @return 该overlay的经纬度，以GeoPoint方式
	 */
	public GeoPoint getPoint()
	{
		return mPoint;
	}
	
	/**
	 * 返回该overlay的文本片段
	 * @return 返回该overlay的文本片段
	 */
	public java.lang.String getSnippet()
	{
		return mSnippet;
	}
	
	/**
	 * 返回该overlay的标题文本
	 * @return 返回该overlay的标题文本
	 */
	public java.lang.String getTitle()
	{
		return mTitle;
	}
	
	/**
	 * 返回该item在可路由地图格式下的位置。
	 * @return 该item在可路由地图格式下的位置。默认情况下，返回一个逗号分割的经度、纬度字符串，
	 * 			单位是度，注意不是微度
	 */
	public void setMarker(Bitmap marker)
	{
		this.mMarker = marker;
	}
	
	/**
	 * 获取画点的方式(系统缺省画图时使用)
	 * @return
	 * 		0:小图片,不需要设置位置
	 * 		1:底部有小尖点或者大头针等图片，需要让底部在原点之上，并且整个图片居中
	 */
	public int getDrawStyle() 
	{
		return mDrawStyle;
	}
	
	/**
	 * 设置画点的方式(系统缺省画图时使用)
	 * @param drawStyle
	 * 				0:小图片,不需要设置位置
	 * 				1:底部有小尖点或者大头针等图片，需要让底部在原点之上，并且整个图片居中
	 */
	public void setDrawStyle(int drawStyle) 
	{
		this.mDrawStyle = drawStyle;
	}
}
