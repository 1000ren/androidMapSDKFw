package com.ctfo.mvapi.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.ctfo.mvapi.entities.GeoPoint;
import com.ctfo.mvapi.entities.GeoRect;
import com.ctfo.mvapi.entities.GeoTilePoint;
import com.ctfo.mvapi.entities.GridRect;
import com.ctfo.mvapi.entities.POINT;
import com.ctfo.mvapi.entities.SIZE;

/**
 * @author fangwei
 * 
 * 地图显示控制类
 * 
 */
public class MapDisplay
{
	private Context mContext = null;
	private DisplayInfo mDisplayInfo = null;
	private MapManager mMapManager = null;
	private int mScale = MapDef.cZoom;
	private List<GridRect> mGridRectList = null;
	private DrawMapThread mDrawMapThread = null;
	private List<GridRect> mDrawGridList = new ArrayList<GridRect>();
	//mapview监听
    private MapViewListener onMapViewListener = null;

	public MapDisplay()
	{
	}

	public boolean init( Context context, SIZE bufSize, int mapMode )
	{
		mContext = context;
		mDisplayInfo = new DisplayInfo();
		if ( !mDisplayInfo.init( bufSize ) )
		{
			return false;
		}

		mMapManager = new MapManager();
		if ( !mMapManager.init( mapMode ) )
		{
			return false;
		}

		mGridRectList = new ArrayList<GridRect>();

		mDrawMapThread = new DrawMapThread( mMapManager );
		mDrawMapThread.start();

		return true;
	}

	public synchronized boolean setMapPos( GeoPoint geoPt )
	{
		if ( !mDisplayInfo.setMapPos(geoPt, MapPosType.MP_CENTER) )
		{
			return false;
		}
		updateGridRect();
		return true;
	}

	public synchronized boolean setMapScale( int scale )
	{
	    if ( scale >= MapDef.maxZoom || scale < MapDef.minZoom )
	    {
	        return false;
	    }
		if ( !mDisplayInfo.setMapScale( scale ) )
		{
			return false;
		}
		mScale = scale;
		updateGridRect();
		if ( onMapViewListener != null )
		{
			onMapViewListener.onMapZoomChanged();
		}
		return true;
	}

	public synchronized boolean zoomIn()
	{
		int scale = mScale;
		if ( scale < MapDef.maxZoom )
		{
			scale++;
		}
		else
		{
			if ( mContext != null )
			{
				Toast.makeText( mContext, "已放大至最高级别", Toast.LENGTH_SHORT ).show();
			}
			return false;
		}
		if ( !mDisplayInfo.setMapScale( scale ) )
		{
			return false;
		}
		mScale = scale;
		updateGridRect();
		if ( onMapViewListener != null )
		{
			onMapViewListener.onMapZoomChanged();
		}
		return true;
	}

	public synchronized boolean zoomIn( int n )
	{
		int scale = mScale;
		if ( scale >= MapDef.maxZoom )
		{
			if ( mContext != null )
			{
				Toast.makeText( mContext, "已放大至最高级别", Toast.LENGTH_SHORT ).show();
			}
			return false;
		}
		if ( scale+n < MapDef.maxZoom )
		{
			scale += n;
		}
		else
		{
			scale = MapDef.maxZoom;
		}
		if ( !mDisplayInfo.setMapScale( scale ) )
		{
			return false;
		}
		mScale = scale;
		updateGridRect();
		if ( onMapViewListener != null )
		{
			onMapViewListener.onMapZoomChanged();
		}
		return true;
	}

	public synchronized boolean zoomOut()
	{
		int scale = mScale;
		if ( scale > MapDef.minZoom )
		{
			scale--;
		}
		else
		{
			if ( mContext != null )
			{
				Toast.makeText( mContext, "已缩小至最低级别", Toast.LENGTH_SHORT ).show();
			}
			return false;
		}
		if ( !mDisplayInfo.setMapScale( scale ) )
		{
			return false;
		}
		mScale = scale;
		updateGridRect();
		if ( onMapViewListener != null )
		{
			onMapViewListener.onMapZoomChanged();
		}
		return true;
	}

	public synchronized boolean zoomOut( int n )
	{
		int scale = mScale;
		if ( scale <= MapDef.minZoom )
		{
			if ( mContext != null )
			{
				Toast.makeText( mContext, "已缩小至最低级别", Toast.LENGTH_SHORT ).show();
			}
			return false;
		}
		if ( scale-n > MapDef.minZoom )
		{
			scale -= n;
		}
		else
		{
			scale = MapDef.minZoom;
		}
		if ( !mDisplayInfo.setMapScale( scale ) )
		{
			return false;
		}
		mScale = scale;
		updateGridRect();
		if ( onMapViewListener != null )
		{
			onMapViewListener.onMapZoomChanged();
		}
		return true;
	}

	public synchronized boolean moveMap( double dOffsetX, double dOffsetY )
	{
		if ( !mDisplayInfo.moveMap(dOffsetX, dOffsetY) )
		{
			return false;
		}
		updateGridRect();
		if ( onMapViewListener != null )
		{
			onMapViewListener.onMapMoveFinish();
		}
		return true;
	}

	public int getMapScale()
	{
		return mScale;
	}
	
	/**
	 * 经纬度坐标转为屏幕坐标
	 * @param dLon
	 * @param dLat
	 * @return
	 */
	public POINT map2Display( double dLon, double dLat )
	{
		return mDisplayInfo.map2Display( dLon, dLat ); 
	}

	/**
	 * 根据屏幕坐标获得经纬度坐标
	 * @param ptPoint
	 * @return
	 */
	public GeoPoint GetLLPos( POINT ptPoint )
	{
		return mDisplayInfo.GetLLPos(ptPoint);
	}
	
	/**
	 * 根据屏幕坐标获得Tile坐标
	 * @param ptPoint
	 * @return
	 */
	public GeoTilePoint GetTileXY( POINT ptPoint )
	{
		return mDisplayInfo.GetTileXY(ptPoint);
	}
	
	/**
	 * 获得当前中心点坐标
	 * @return
	 */
	public GeoPoint getCenterPos()
	{
		return mDisplayInfo.getCenterPos();
	}
	
	/**
	 * 获得当前屏幕经纬度范围
	 * @return
	 */
	public GeoRect getGeoRect()
	{
		return mDisplayInfo.getGeoRect();
	}

	/**
	 * 绘制地图
	 * @param grclist
	 */
	public synchronized void DrawMap(List<GridRect> grclist)
	{
		if ( grclist.size() <= 0 )
		{
			return;
		}
		mDrawGridList.clear();
		mDrawGridList.addAll(grclist);
		mDrawMapThread.updateGridList( mDrawGridList );
		mMapManager.updateBlockList(grclist);
	}
	
	public void setDrawFlag( Boolean bOk )
	{
		mDrawMapThread.setDrawFlag(bOk);
	}
	
	public List<GridRect> getGridRectList()
	{
		return mGridRectList;
	}
	
	private void updateGridRect()
	{
		GeoRect geoRC = mDisplayInfo.getGeoRect();
		if ( mGridRectList != null )
		{
			mGridRectList.clear();
			List<GridRect> list = mMapManager.getGridRect( geoRC, mScale );
			if ( list == null )
			{
				return;
			}
			mGridRectList = list;
			for (int i = 0; i < mGridRectList.size(); i++)
			{
				GridRect grc = mGridRectList.get(i);
				if ( grc == null )
				{
					continue;
				}
				for (int j = 0; j < 5; j++)
				{
					grc.mSrcPoint[j] = mDisplayInfo.map2Display( grc.mGeoPoint[j].getLongitude(), 
							grc.mGeoPoint[j].getLatitude() );
				}
				grc.mSrcRect.left = grc.mSrcPoint[0].x;
				grc.mSrcRect.right = grc.mSrcPoint[1].x;
				grc.mSrcRect.top = grc.mSrcPoint[2].y;
				grc.mSrcRect.bottom = grc.mSrcPoint[0].y;
			}
		}
	}

	public MapViewListener getOnMapViewListener() 
	{
		return onMapViewListener;
	}

	public void setOnMapViewListener(MapViewListener onMapViewListener) 
	{
		this.onMapViewListener = onMapViewListener;
	}

	
}
