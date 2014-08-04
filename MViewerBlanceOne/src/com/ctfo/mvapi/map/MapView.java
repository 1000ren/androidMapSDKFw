package com.ctfo.mvapi.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctfo.mvapi.MVApi;
import com.ctfo.mvapi.entities.GeoPoint;
import com.ctfo.mvapi.entities.POINT;
import com.ctfo.mvapi.entities.SIZE;
import com.ctfo.mvapi.location.LocationManager;
import com.ctfo.mvapi.utils.MVApiLog;

public class MapView extends ViewGroup  implements OnTouchListener
{
	public static MapView mthis = null;
	
    // 地图显示控制对象
    private MapDisplay mMapDisplay = null;

    // 地图操作控制对象
    private MapController mMapController = null;

    // 底层地图绘制对象
    private MapDrawView mMapDrawView = null;

	private int mBufWidth = 0;
	private int mBufHeight = 0;
	
	private int mBufImgWidth = 0;
	private int mBufImgHeight = 0;
	
	private Context mContext = null;
	private ImageView[] mImageViews = null;

    private static final int NONE = 0;// 初始状态
    private static final int DRAG = 1;// 拖动
    private int mode = NONE;
    private PointF prev = new PointF();
    
    private ItemizedOverlay<OverlayItem> mItemizedOverlay;
    
    //计算View的大小
    private int mMeasureWidth;
    private int mMeasureHeight;
    
    //点击的详情的位置
    private int mOnClickIndex = -1;
    
    private View mCompassView = null;
    
	
	public MapView(Context context) 
	{
		super(context);
		initMapView(context);
	}

	public MapView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		initMapView(context);
	}

	private void initMapView(Context context)
	{
		mthis = this;
		mContext = context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		if(metrics.heightPixels>1100)
		{
			mBufWidth = 768;
	        mBufHeight = 1024;
		}
		else
		{
			mBufWidth = metrics.widthPixels;
	        mBufHeight = metrics.heightPixels;	
		}

        int sizeWidth = mBufWidth*2;
        int sizeHeight = mBufHeight*2;
        // matrix.setTranslate后偏移的值，在显示POI的view时候需要计算上
        mBufImgWidth = (sizeWidth/2)-(metrics.widthPixels/2);
        mBufImgHeight = (sizeHeight/2)-(metrics.heightPixels/2)+70;
        // init mapdisplay
        SIZE bufSize = new SIZE( sizeWidth, sizeHeight );
        mMapDisplay = new MapDisplay();
        if ( !mMapDisplay.init( context, bufSize, MVApi.getMapMode() ) )
        {
        	MVApiLog.e( "MapView", "mMapDisplay.init fail" );
        }
        mMapDisplay.setMapPos( new GeoPoint(MapDef.cLon,MapDef.cLat) );
        // init MapController 
        mMapController = new MapController( mMapDisplay );
        
        mCompassView = new CompassView(context);
        mCompassView.setId(2);
        mCompassView.layout(10, 10, 84, 84);
        
        
        mMapDrawView = new MapDrawView(context);
        if ( !mMapDrawView.initMapDrawView( this, mMapDisplay, metrics, bufSize, mCompassView,mMapController) )
        {
        	MVApiLog.e( "MapDrawView", "init fail" );
        }
        mMapDrawView.setId(1);  
        mMapDrawView.layout(0, 0, metrics.widthPixels, metrics.heightPixels);
        mMapDrawView.setOnTouchListener(this);
        this.addView(mMapDrawView);
        this.addView(mCompassView);
        

        //计算View的大小
        mMeasureWidth = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
        mMeasureHeight = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
        
        getChildAt(1).setVisibility(4);
        mCompassView.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				mMapDrawView.setMapAngle(0);
			}
		});
	}

    public static Bitmap getBitmap(Context context, final int width, final int height)
    {
    	DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();
        return MapDrawView.getBitmap(metrics, width, height);
    }

    /**
     * 获取地图控制对象
     */
    public MapController getController()
    {
    	return mMapController;
    }

    /**
     * 清除虚拟内存中TileMapCache中的缓存
     */
    public void clearTileMapCache()
    {
    	mMapDrawView.clearTileMapCache();
    }

	/**
	 * 获得地图当前比例尺等级
	 */
    public int getZoomLevel()
    {
    	return mMapDisplay.getMapScale();
    }

    /**
     * 当前视图中心点的最大缩放级别
     */
    public int getMaxZoomLevel()
    {
    	return MapDef.maxZoom;
    }

    /**
     * 当前视图中心点的最小缩放级别
     */
    public int getMinZoomLevel()
    {
    	return MapDef.minZoom;
    }

	/**
	 * 设置地图角度
	 * @param angle
	 * 			旋转的角度
	 * @return
	 */
    public void setMapAngle( float angle )
    {
    	mMapDrawView.setMapAngle(angle);
    }
    
    /**
	 * 获得地图角度
	 * @return angle
	 * 			旋转的角度
	 */
    public float getMapAngle()
    {
    	return mMapDrawView.getMapAngle();
    }
    

    /**
     * 设置mapview是否支持双击放大效果
     * @param bDoubleClickZooming
     */
    public void setDoubleClickZooming(boolean bDoubleClickZooming)
    {
    	// 
    }
    
	/**
	 * 设置打开卫星图信息
	 * @param bActive
	 * @return
	 */
    public void setSatellite( boolean bActive )
    {
    	// 
    }

	/**
	 * 设置打开路况信息
	 * @param bActive
	 * @return
	 */
    public void setTraffic( boolean bActive )
    {
    	// 
    }

	/**
	 * 经纬度坐标转为屏幕坐标
	 * @param dLon
	 * @param dLat
	 * @return
	 */
	public POINT map2Display( double dLon, double dLat )
	{
		return mMapDisplay.map2Display( dLon, dLat ); 
	}

	/**
	 * 屏幕坐标转为经纬度坐标
	 * @param dLon
	 * @param dLat
	 * @return
	 */
	public GeoPoint screen2Map( POINT ptPoint )
	{
		return mMapDisplay.GetLLPos( ptPoint );
	}

	@Override
	public void addView(View child, LayoutParams params) 
	{
		super.addView(child, params);
	}

	@Override
	public void updateViewLayout(View view, LayoutParams params) 
	{
		super.updateViewLayout(view, params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count = this.getChildCount();
        for(int i = 0;i < count;i++)
        {
            View child = this.getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        } 
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) 
	{
		int count = this.getChildCount();
        
        for(int i = 0;i < count;i++)
        {
            View child = this.getChildAt(i);
            int left = child.getLeft();
            int top = child.getTop();
            int right = child.getRight();
            int bottom = child.getBottom();
            child.layout(left,top,right,bottom);
        }
	}

	/**
	 * 设置POI点
	 * @param itemizedOverlay
	 */
	public void setItemized(ItemizedOverlay<OverlayItem> itemizedOverlay)
	{
		mItemizedOverlay = itemizedOverlay;
		if(null != itemizedOverlay && itemizedOverlay.size()>0 && itemizedOverlay.getMViewDraw())
		{
			if(null != mImageViews && mImageViews.length>0)
			{
				for(int i=0;i<mImageViews.length;i++)
				{
					this.removeView(mImageViews[i]);
				}
			}
			mImageViews = new ImageView[itemizedOverlay.size()];
			for(int i=0;i<itemizedOverlay.size();i++)
			{
				final OverlayItem oOverlayItem = itemizedOverlay.createItem(i);
				//转换经纬度坐标
				POINT point = mMapDisplay.map2Display(oOverlayItem.getPoint().getLongitude(), oOverlayItem.getPoint().getLatitude());
				
				//绘制imageview
				mImageViews[i] = new ImageView(mContext);
				final Bitmap oBitmap = oOverlayItem.getmMarker();
				mImageViews[i].setImageBitmap(oBitmap);
				
				//设置imageview的位置,以图片的最低端底端正中间部分对齐点,由于buffer为9块屏幕的大小，所以需要减去一个屏幕的大小
				int width = 0;
				int height = 0;
				if(oOverlayItem.getDrawStyle()==1)
				{
					width = point.x-mBufImgWidth-(oBitmap.getWidth()/2);
					height = point.y-mBufImgHeight-oBitmap.getHeight();
				}
				else
				{
					width = point.x-mBufImgWidth;
					height = point.y-mBufImgHeight;
				}
				
				mImageViews[i].layout(width, height,width+oBitmap.getWidth(),height+oBitmap.getHeight());
				
				final int index = i;
				//设置监听，弹出详情
				mImageViews[i].setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						mOnClickIndex = index;
						//查看有没有其他详情弹出来，如果有则删除
						if(null != getTapView())
						{
							removeViewAt(getChildCount()-1);
						}
						
						//设置弹出详情事件
						View view = mItemizedOverlay.onTap(index);
						if(null != view)
						{
							//计算view的大小
					        view.measure(mMeasureWidth, mMeasureHeight); 
							int x = view.getMeasuredWidth(); 
							int y = view.getMeasuredHeight(); 
							//坐标转换
							POINT point = mMapDisplay.map2Display(oOverlayItem.getPoint().getLongitude(), oOverlayItem.getPoint().getLatitude());
							int width = point.x-mBufImgWidth-(x/2);
							int height = point.y-mBufImgHeight-y;
							
							view.layout(width, height, width+x, height+y);
							addView(view);
						}
					}
				});
				this.addView(mImageViews[i]);
			}
		}
		else
		{
			clearAllPOIs();
		}
		setOverlay(itemizedOverlay);
	}
	
	/**
	 * 在图层绘制overlay
	 * @param overlay
	 */
	public void setOverlay(Overlay overlay)
	{
		mMapDrawView.setOverlay(overlay);
	}
	
	/**
	 * 清除所有查询的POI点
	 */
	public void clearAllPOIs()
	{
		if(getChildCount()>1)
		{
			for(int i = getChildCount()-1; i>1; i--)
			{
				removeViewAt(i);
			}
		}
		mOnClickIndex = -1;
	}
	
	/**
	 * 清除所有查询的线路
	 */
	public void clearAllLines()
	{
		setOverlay(null);
	}
	
	/**
	 * 刷新所有点的位置
	 */
	private void refreshPoi()
	{
		if(null != mImageViews && mImageViews.length>0 && null != mItemizedOverlay && mItemizedOverlay.getMViewDraw())
		{
			for(int i=0;i<mItemizedOverlay.size();i++)
			{
				OverlayItem oOverlayItem = mItemizedOverlay.createItem(i);
				//转换经纬度坐标
				POINT point = mMapDisplay.map2Display(oOverlayItem.getPoint().getLongitude(), oOverlayItem.getPoint().getLatitude());
				//绘制imageview
				Bitmap oBitmap = oOverlayItem.getmMarker();
				//设置imageview的位置,以图片的最低端底端正中间部分对齐点,由于buffer为9块屏幕的大小，所以需要减去一个屏幕的大小
				int width = 0;
				int height = 0;
				if(oOverlayItem.getDrawStyle()==1)
				{
					width = point.x-mBufImgWidth-(oBitmap.getWidth()/2);
					height = point.y-mBufImgHeight-oBitmap.getHeight();
				}
				else
				{
					width = point.x-mBufImgWidth;
					height = point.y-mBufImgHeight;
				}
				mImageViews[i].layout(width, height,width+oBitmap.getWidth(),height+oBitmap.getHeight());
			}
			
			//查看有没有点击详情
			if(null != getTapView())
			{
				OverlayItem oOverlayItem = mItemizedOverlay.createItem(mOnClickIndex);
				//计算view的大小
				View view = getChildAt(getChildCount()-1);
				view.measure(mMeasureWidth, mMeasureHeight); 
				int x = view.getMeasuredWidth(); 
				int y = view.getMeasuredHeight(); 
				//坐标转换
				POINT point = mMapDisplay.map2Display(oOverlayItem.getPoint().getLongitude(), oOverlayItem.getPoint().getLatitude());
				int width = point.x-mBufImgWidth-(x/2);
				int height = point.y-mBufImgHeight-y;
				
				getChildAt(getChildCount()-1).layout(width, height, width+x, height+y);
			}
		}
	}
	
	/**
	 * 获取详情view信息，如果没有则返回null
	 */
	private View getTapView()
	{
		if(mItemizedOverlay.size()+2<getChildCount())
		{
			return getChildAt(getChildCount()-1);
		}
		return null;
	}
	
	/**
	 * 隐藏所有标记view
	 */
	private void setImageViewInvisible()
	{
		if(getChildCount()>1)
		{
			for(int i=2;i<getChildCount();i++)
			{
				getChildAt(i).setVisibility(INVISIBLE);
			}
		}
		
//		if(null != getTapView())
//		{
//			//把详情view转为bitmap，在图层绘制
//			mItemizedOverlay.addDraw(mItemizedOverlay.createItem(mOnClickIndex), getViewBitmap(getTapView()));
//		}
//		setOverlay(mItemizedOverlay);
	}
	
	/**
	 * View转Bitmap
	 * @param view
	 * @return
	 */
//	private Bitmap getViewBitmap(View view) 
//	{  
//        view.clearFocus();  
//        view.setPressed(false);  
//  
//        boolean willNotCache = view.willNotCacheDrawing();  
//        view.setWillNotCacheDrawing(false);  
//  
//        int color = view.getDrawingCacheBackgroundColor();  
//        view.setDrawingCacheBackgroundColor(0);  
//  
//        if (color != 0) {  
//            view.destroyDrawingCache();  
//        }  
//        view.buildDrawingCache();  
//        Bitmap cacheBitmap = view.getDrawingCache();  
//        if (cacheBitmap == null) {  
//            return null;  
//        }  
//  
//        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);  
//  
//        view.destroyDrawingCache();  
//        view.setWillNotCacheDrawing(willNotCache);  
//        view.setDrawingCacheBackgroundColor(color);  
//  
//        return bitmap;  
//    } 
	
	/**
	 * 显示所有标记view
	 */
	private void setImageViewVisible()
	{
		if(getChildCount()>1)
		{
			for(int i=2;i<getChildCount();i++)
			{
				getChildAt(i).setVisibility(VISIBLE);
			}
		}
		//取消在图层绘制的详情图片
//		if(null != getTapView())
//		{
//			mItemizedOverlay.addDraw(null, null);
//		}
//		setOverlay(null);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		try
		{
		int ix = (int) (event.getX()); // * event.getXPrecision()
		int iy = (int) (event.getY()); // * event.getYPrecision()

		switch (event.getAction()) 
		{
			case MotionEvent.ACTION_DOWN:
				mode = DRAG;
				//savedMatrix.set(matrix);
				prev.set(ix, iy);
				setImageViewInvisible();
				break;
	
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) 
				{
					//matrix.set(savedMatrix);
					//matrix.postTranslate(ix - prev.x, iy - prev.y);
					invalidate();
				}
				break;
	
			case MotionEvent.ACTION_UP:
	
				if (mode == DRAG) 
				{
					mode = NONE;
				}
				setImageViewVisible();
				break;
			default:
				super.onTouchEvent(event);
				break;
		}
		mMapDrawView.setTouchEvent(event);
		if(mode == NONE)
		{
			refreshPoi();
			}
		}
    	catch(IllegalArgumentException e)
    	{
    		
    	}
		return true;
	}
	
	/**
	 * 刷新地图
	 */
	public void refresh()
	{
		mMapDrawView.refreshTileMap();
		refreshPoi();
	}

	
	/**
	 * 设置MapView的地图监听事件
	 * @param onMapViewListener
	 */
	public void setOnMapViewListener(MapViewListener onMapViewListener) 
	{
		mMapDisplay.setOnMapViewListener(onMapViewListener);
	}
	
	/**
     * 设置我的位置
     * @param locationManager
     */
    public void setMyLocation(LocationManager locationManager)
    {
    	mMapDrawView.setMyLocation(locationManager);
    }
    
    /**
     * 设置是否开启透视投影功能
     * @param isPerspectiveTrans
     */
    public void setPerspectiveTrans(boolean isPerspectiveTrans)
    {
    	mMapDrawView.setPerspectiveTrans(isPerspectiveTrans);
    }
    
    /**
     * 设置指南针位置
     * @param width
     * @param height
     */
    public void setCompassSize(int width, int height)
    {
    	//目前固定加上74
    	getChildAt(1).layout(width, height, width+74, height+74);
    }
}
