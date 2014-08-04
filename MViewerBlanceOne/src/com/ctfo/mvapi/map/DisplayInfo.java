package com.ctfo.mvapi.map;

import com.ctfo.mvapi.entities.GeoPoint;
import com.ctfo.mvapi.entities.GeoRect;
import com.ctfo.mvapi.entities.GeoTilePoint;
import com.ctfo.mvapi.entities.POINT;
import com.ctfo.mvapi.entities.PixelPoint;
import com.ctfo.mvapi.entities.SIZE;

/**
 * @author fangwei
 * 
 * 地图显示参数处理类
 * 
 */
public class DisplayInfo
{
	private SIZE 		mBufSize;
	private GeoRect 	mGeoRect;
	private int 		mScale = MapDef.cZoom;
	private GeoPoint 	mGeoPtCenter;
	private PixelPoint  mPixelPtCenter;
	private PixelPoint  mPxlPtLB;

	public DisplayInfo()
	{
		mBufSize = new SIZE();
		mGeoRect = new GeoRect();
		mGeoPtCenter = new GeoPoint();
		mPixelPtCenter = new PixelPoint();
		mPxlPtLB = new PixelPoint();
	}
	
	public boolean init( SIZE bufSize )
	{
		mBufSize = bufSize;
		return true;
	}
	
	public synchronized boolean setMapPos( GeoPoint pos, MapPosType mapPos )
	{
	    mGeoPtCenter = pos;
	    PixelPoint pxlpt = MapTile.latLongToPixelXY( pos.getLatitude(), pos.getLongitude(), mScale );

		switch (mapPos) {
		case MP_CENTER: {
			mPxlPtLB.x = pxlpt.x - mBufSize.getX() / 2;
			mPxlPtLB.y = pxlpt.y + mBufSize.getY() / 2;
			break;
		}
		case MP_LEFT_BOTTOM: {
			mPxlPtLB.x = pxlpt.x;
			mPxlPtLB.y = pxlpt.y;
			break;
		}
		case MP_LEFT_TOP: {
			mPxlPtLB.x = pxlpt.x;
			mPxlPtLB.y = pxlpt.y + mBufSize.getY();
			break;
		}
		}

		mPixelPtCenter = pxlpt;

		return resetMap();
	}

	public synchronized boolean setMapScale( int scale )
	{
	    mScale = scale;
	    mPixelPtCenter = MapTile.latLongToPixelXY( mGeoPtCenter.getLatitude(), mGeoPtCenter.getLongitude(), mScale );

		mPxlPtLB.x = mPixelPtCenter.x - mBufSize.getX()/2;
		mPxlPtLB.y = mPixelPtCenter.y + mBufSize.getY()/2;

		return resetMap();
	}

	public synchronized boolean moveMap( double dOffsetX, double dOffsetY )
	{
	    mPixelPtCenter.x += dOffsetX;
	    mPixelPtCenter.y -= dOffsetY;
	    mPxlPtLB.x = mPixelPtCenter.x - mBufSize.getX()/2;
	    mPxlPtLB.y = mPixelPtCenter.y + mBufSize.getY()/2;
	    mGeoPtCenter = MapTile.pixelXYToLatLong(mPixelPtCenter.x, mPixelPtCenter.y, mScale);
	    return resetMap();
	}

	// 根据屏幕坐标获得经纬度坐标
	public GeoPoint GetLLPos( POINT ptPoint )
	{
		POINT pt = new POINT();
		pt.x = mPxlPtLB.x + ptPoint.x;
		pt.y = mPxlPtLB.y + ptPoint.y - mBufSize.getY();
		return MapTile.pixelXYToLatLong( pt.x, pt.y, mScale );
	}

	// 根据屏幕坐标获得Tile坐标
	public GeoTilePoint GetTileXY( POINT ptPoint )
	{
		POINT pt = new POINT();
		pt.x = mPxlPtLB.x + ptPoint.x;
		pt.y = mPxlPtLB.y + ptPoint.y - mBufSize.getY();
		return MapTile.pixelXYToTileXY( pt.x, pt.y );
	}

	// 获得当前中心点坐标
	public GeoPoint getCenterPos()
	{
		return mGeoPtCenter;
	}

	// 获得当前屏幕经纬度范围
	public GeoRect getGeoRect()
	{
		return mGeoRect;
	}

	// 经纬度坐标转为屏幕坐标
	public POINT map2Display( double dLon, double dLat )
	{
		PixelPoint pixelPt = MapTile.latLongToPixelXY( dLat, dLon, mScale );
		return new POINT( pixelPt.x-mPxlPtLB.x, mBufSize.getY()-(mPxlPtLB.y-pixelPt.y) );
	}

	private boolean resetMap()
	{
	    GeoPoint geoPt1 = MapTile.pixelXYToLatLong(mPxlPtLB.x, mPxlPtLB.y, mScale);
	    GeoPoint geoPt2 = MapTile.pixelXYToLatLong(mPxlPtLB.x+mBufSize.getX(), mPxlPtLB.y-mBufSize.getY(), mScale );
	    mGeoRect.minLongitude = geoPt1.getLongitude();
	    mGeoRect.minLatitude = geoPt1.getLatitude();
	    mGeoRect.maxLongitude = geoPt2.getLongitude();
	    mGeoRect.maxLatitude = geoPt2.getLatitude();
	    return true;
	}
	
	
	
	
}

