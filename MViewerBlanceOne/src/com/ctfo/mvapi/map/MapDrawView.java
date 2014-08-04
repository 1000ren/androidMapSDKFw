package com.ctfo.mvapi.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ctfo.mvapi.NativeMVAPI;
import com.ctfo.mvapi.entities.BNPoint;
import com.ctfo.mvapi.entities.GridRect;
import com.ctfo.mvapi.entities.SIZE;
import com.ctfo.mvapi.entities.TileMapInfo;
import com.ctfo.mvapi.entities.TileTextInfo;
import com.ctfo.mvapi.location.LocationManager;
import com.ctfo.mvapi.utils.AsyncCallListener;
import com.ctfo.mvapi.utils.ConstVariable;
import com.ctfo.mvapi.utils.Conversation;
import com.ctfo.mvapi.utils.EventType;
import com.ctfo.mvapi.utils.PrefCache;



/**
 * @author fangwei
 * 
 * 底层地图绘制类
 * 
 */
public class MapDrawView extends View
{
	// 主图对象
    public static MapDrawView mthis = null;
    private Context mContext = null;
    private MapView mMapView = null;
    // 屏幕管理
    private DisplayMetrics mMetrics = null;

    // 地图显示控制对象
    private MapDisplay mMapDisplay = null;
    //
    private MapController mMapController = null;
    // 地图刷新回调消息
    private RefreshHandler mRefreshHandler = null;
    // 传递到底层进行Tile绘制的bitmap
    public static Bitmap mTileBitmap = null;
    // 源图片的有效地图位置
    private Rect mSrcRect = new Rect( 2, 2, 258, 258 );

    // 缓存bitmap
    private Bitmap mBufBitmap = null;
    private Canvas mBufCanvas = null;
    private Canvas mOverlayCanvas = null;
    private Bitmap mOverlayBitmap = null;
    // 默认背景图
    private Bitmap mDefaultBKBitmap = null;
    // 默认背景底色
    private int mDefaultBKColor = 0;

    // TileMap缓存管理
    private TileMapCache mTileMapCache = null;
    // TileText绘制
    private TileTextRender mTileTextRender = null;

    // 当前的GridRect信息
    private HashMap<String,GridRect> mCurGridRectHashMap = null;
    // 当前的有序格网编号
    private List<String> mTileList = null;
    // 当前需要下载的有序格网
    private List<GridRect> mNeedDrawTileList = null;

    // 屏幕变换处理
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // 最终绘制matrix
    private Matrix mFinalMatrix = new Matrix();
    // 初始状态
    private static final int NONE = 0;
    // 拖动
    private static final int DRAG = 1;
    // 缩放
    private static final int ZOOM = 2;
    // 缩放
    private static final int STATICS = 3;
    private int mode = NONE;
    private PointF prev = new PointF();
    private PointF mid = new PointF();
    private float dist = 1f;
    private float newDist  = 0f;
    //多点触碰点击时两点间的距离
    private double mStartPointer = 0;
    //多点触碰松开时两点间的距离
    private double mEndPointer = 0;
    //屏幕左上角到右下角的距离
    private double mScreenPointer = 0;
    //图片旋转用，记录点信息
    private HashMap<Integer,BNPoint> mBNPointMap=new HashMap<Integer,BNPoint>();
    //初始旋转角度
    private float mAngle = 0;
    //原图片左上角的坐标
    private float mX = 0.0f;
    private float mY = 0.0f;

    // 主图画刷
    private Paint mDrawPaint = new Paint();
    // 绘制图层
    private Overlay mOverlay = null;

    // 定位管理
    private LocationManager mLocationManager = null;
    
    // 绘制比例尺标识
    private boolean mDrawScaleFlag = true;
    // 绘制版权信息标识
    private boolean mDrawCopyRightFlag = true;

    //监听双击
  	private GestureDetector mGestureDetector;
  	//是否双击放大地图
  	private boolean isDoubleZoomIn = false;
  	
  	// 两个手指down的时间
  	private long mDownClick;
  	// Up的时间
  	private long mUpClick;
  	
  	//两个手指触控的模式
  	private int zoomType = NONE;
  	//放大缩小模式
    private int zoomScale = 1;
    //旋转地图模式
    private int zoomAngle = 2;
    //是否透视
    private boolean mIsPerspectiveTrans = false;
    //指南针
    private CompassView mCompassView;

    
    public MapDrawView(Context context)
    {
    	super(context);
        mthis = this;
        mContext = context;
        mRefreshHandler = new RefreshHandler(Looper.myLooper());
        mRefreshHandler.removeMessages(0);
        // 设置回调处理方法
        Message msg = Message.obtain(null, 0, EventType.EVENT_SET_CBLISTENER);
        Conversation.start(ConstVariable.INT_0, null, mHandleListener, msg);
    }

    public boolean initMapDrawView( final MapView mapView, final MapDisplay mapDisplay, 
    		final DisplayMetrics metrics, final SIZE bufSize ,final View compassView, MapController mapController)
    {
    	mCompassView = (CompassView) compassView;
    	mMapView = mapView;
    	mMapDisplay = mapDisplay;
    	mMapController = mapController;
    	mMetrics = metrics;

    	// 初始化透视矩阵
    	PerspectiveTrans.setMatrixValue( bufSize.getX(), bufSize.getY(), 
    			metrics.widthPixels, metrics.heightPixels );

    	// 设置bitmap左上角的位置
        mX = -((bufSize.getX()/2)-(metrics.widthPixels/2));
        mY = -((bufSize.getY()/2)-(metrics.heightPixels/2));

        //计算手机屏幕左上角到右下角的距离
        mScreenPointer = MapUtil.getPointDistance(0, 0, mMetrics.widthPixels, mMetrics.heightPixels);
        
        // 创建屏幕缓存
        mBufBitmap = Bitmap.createBitmap(bufSize.getX(), bufSize.getY(), Config.ARGB_4444);
        mBufCanvas = new Canvas();
        mBufCanvas.setBitmap( mBufBitmap );
        
        mOverlayCanvas = new Canvas();
        mOverlayBitmap = Bitmap.createBitmap(bufSize.getX(), bufSize.getY(), Config.ARGB_4444);
        mOverlayCanvas.setBitmap(mOverlayBitmap);

        mTileMapCache = new TileMapCache(MapUtil.getBitmapNum(bufSize.getX(), bufSize.getY()));
        mTileTextRender = new TileTextRender( mContext, new Point(bufSize.getX()/2, bufSize.getY()/2), 
        		mMapDisplay, mTileMapCache );
        // 设置手势监听
    	mGestureDetector = new GestureDetector(new LearnGestureListener());

        mCurGridRectHashMap = new HashMap<String,GridRect>();
        mTileList = new ArrayList<String>();
        mNeedDrawTileList = new ArrayList<GridRect>();
    	
        mDefaultBKColor = Color.argb(255, 237, 237, 237);
        mDrawPaint.setAntiAlias( true );
        mDrawPaint.setFilterBitmap(true);
        mDrawPaint.setStyle(Style.FILL);
        mDrawPaint.setTextAlign(Align.RIGHT);

        //获取assets文件夹中的背景图片
        try
        {
        	AssetManager am = mContext.getResources().getAssets();
            InputStream oInputStream = am.open("map_background.png");
            mDefaultBKBitmap = BitmapFactory.decodeStream(oInputStream);
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }

    	refreshTileMap();
    	return true;
    }
    
    public static Bitmap getBitmap(final DisplayMetrics metrics, final int width, final int height)
    {
        if (mTileBitmap == null)
        {
        	mTileBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_4444);
        }
        // 当前屏幕属性
        mTileBitmap.setDensity(metrics.densityDpi);
        PrefCache.density = metrics.density;
        // warnning:ubuntu上编译时则删除该赋值
        // (因输出的坐标已经是实际的在手机屏幕上的坐标
        // 在eclipse下编译则是dpi值，原因暂时未知)
        // densityOffset = metrics.density;
        return mTileBitmap;
    }

	public void setTouchEvent(final MotionEvent event)
	{
    	try
    	{
    	int ix = (int)(event.getX()); // * event.getXPrecision()
    	int iy = (int)(event.getY()); // * event.getYPrecision()
    	//监听双击
    	mGestureDetector.onTouchEvent(event);
    	if(isDoubleZoomIn)
		{
			isDoubleZoomIn = false;
			mMapController.zoomIn();
			return;
		}
    	if(isSingleTapUp)
    	{
    		isSingleTapUp = false;
    		return ;
    	}

    	// 获取主、辅点id（down时主辅点id皆正确，up时辅点id正确，主点id要查询Map中剩下的一个点的id）
		int id=(event.getAction()&MotionEvent.ACTION_POINTER_ID_MASK)>>>MotionEvent.ACTION_POINTER_ID_SHIFT;
        switch (event.getAction())
        {
	        case MotionEvent.ACTION_DOWN:
	        	mode = DRAG;
	        	savedMatrix.set(matrix);
	        	prev.set(ix, iy);
	        	// 不论是主点还是辅点按下皆向Map中记录一个新点
				mBNPointMap.put(id, new BNPoint(event.getX(id),event.getY(id)));
				
	            break;
	         // 副点按下
			case MotionEvent.ACTION_POINTER_2_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				// 两个手指按下的时间
				mDownClick = System.currentTimeMillis();
				zoomType = NONE;
				
				dist = MapUtil.spacing(event);
				mStartPointer = MapUtil.getPointDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
				mEndPointer = 0;
				// 如果连续两点距离大于10，则判定为多点模式
				if (dist > 10f) 
				{
					savedMatrix.set(matrix);
					mid = MapUtil.midPoint(mid, event);
					mode = ZOOM;
				}
				// 不论是主点还是辅点按下皆向Map中记录一个新点
				mBNPointMap.put(id, new BNPoint(event.getX(id),event.getY(id)));
				break;
	        case MotionEvent.ACTION_MOVE:
	        	
	        	if ( checkMapBound( ix-prev.x, iy-prev.y ) )
	        	{
	        		return;
				}

	        	//不论主/辅点Move都更新其位置
	        	Set<Integer> ks=mBNPointMap.keySet();
				for(int i:ks)
				{
					mBNPointMap.get(i).setLocation(event.getX(i), event.getY(i));					
				}
	            if ( mode == DRAG )
	            {
					matrix.set(savedMatrix);
					matrix.postTranslate(ix - prev.x, iy - prev.y);
				}
	            else if (mode == ZOOM) 
	        	{
	 				newDist = MapUtil.spacing(event);
	 				if (newDist > 10f && zoomType != zoomAngle) 
	 				{
	 					matrix.set(savedMatrix);
	 					float tScale = newDist / dist;
	 					
	 					//放大做特殊处理
	 					if(mIsPerspectiveTrans)
	 					{
	 						tScale = tScale > 2 ? 2 : tScale;
	 					}
	 					else
	 					{
	 						tScale = tScale > 3 ? 3 : tScale;
	 					}
	 					
	 					if(zoomType == zoomScale)
	 					{
	 						matrix.postScale(tScale, tScale, mid.x, mid.y);
	 					}
	 					else if(tScale>1.2 || tScale<0.9)
	 					{
	 						zoomType = zoomScale;
	 						matrix.postScale(tScale, tScale, mid.x, mid.y);
	 					}
	 				}
	 				
	 				//旋转地图
	 				if(mBNPointMap.size()==2 && zoomType != zoomScale)
					{
		            	//多点触控
		            	BNPoint bpTempA=mBNPointMap.get(0);
						BNPoint bpTempB=mBNPointMap.get(1);
						//计算旋转角度
						if(bpTempA.hasOld||bpTempB.hasOld)
						{
							double alphaOld= Math.atan2((bpTempA.oldY- bpTempB.oldY), (bpTempA.oldX- bpTempB.oldX));
							double alphaNew= Math.atan2((bpTempA.y- bpTempB.y), (bpTempA.x- bpTempB.x));
							float angle = (float)Math.toDegrees(alphaNew-alphaOld);
							
							if(zoomType == zoomAngle)
							{
								matrix.postRotate(angle, mMetrics.widthPixels/2, mMetrics.heightPixels/2);
								savedMatrix.postRotate(angle, mMetrics.widthPixels/2, mMetrics.heightPixels/2);
								mAngle += angle;	
							}
							else if(mAngle-mAngle+angle > 4 ||mAngle-mAngle+angle < -4)
							{
								zoomType = zoomAngle;
								matrix.postRotate(angle, mMetrics.widthPixels/2, mMetrics.heightPixels/2);
								savedMatrix.postRotate(angle, mMetrics.widthPixels/2, mMetrics.heightPixels/2);
								mAngle += angle;	
							}
							setCompassViewAngle(mAngle);
						}
					}
	 			}
	            invalidate();
	            break;
	        case MotionEvent.ACTION_UP:
	        	
	        	mUpClick = System.currentTimeMillis();
	        	if ( checkMapBound( ix-prev.x, iy-prev.y ) )
	        	{
	        		return;
				}
	        	
	        	if(mode != STATICS)
	        	{
	        		double dx = 0.0;
	        		double dy = 0.0;
	        		if ( mAngle != 0 )
	        		{
	        			// 根据当前的屏幕触碰点，获取以当前屏幕中心点进行旋转后的坐标
	        			double x1 = (prev.x-mMetrics.widthPixels/2)*Math.cos(Math.toRadians(mAngle))
	        					-(mMetrics.heightPixels/2-prev.y)*Math.sin(Math.toRadians(mAngle));
						double y1 = (prev.x-mMetrics.widthPixels/2)*Math.sin(Math.toRadians(mAngle))
								+(mMetrics.heightPixels/2-prev.y)*Math.cos(Math.toRadians(mAngle));
						x1 += mMetrics.widthPixels/2;
						y1 = mMetrics.heightPixels/2 - y1;

						double x2 = (ix-mMetrics.widthPixels/2)*Math.cos(Math.toRadians(mAngle))
								-(mMetrics.heightPixels/2-iy)*Math.sin(Math.toRadians(mAngle));
						double y2 = (ix-mMetrics.widthPixels/2)*Math.sin(Math.toRadians(mAngle))
								+(mMetrics.heightPixels/2-iy)*Math.cos(Math.toRadians(mAngle));
						x2 += mMetrics.widthPixels/2;
						y2 = mMetrics.heightPixels/2 - y2;

						dx = x1 - x2;
						dy = y2 - y1;
					}
	        		else
	        		{
	        			dx = prev.x - ix;
						dy = iy - prev.y;
					}
	        		mMapDisplay.moveMap(dx, dy);
	        	}
	        	else
	        	{
	        		zoomMap();
	        	}
	        	mode = NONE;
	        	refreshTileMap();
	        	mBNPointMap.clear();
	        	break;
	        case MotionEvent.ACTION_POINTER_UP:
	        case MotionEvent.ACTION_POINTER_2_UP:
	        	
	        	if ( checkMapBound( ix-prev.x, iy-prev.y ) )
	        	{
	        		return;
				}
	        	mode = STATICS;
	        	mEndPointer = MapUtil.getPointDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
	        	mBNPointMap.remove(id);
	        	break;
	        default:
	            super.onTouchEvent(event);
	            break;
        }
    	}
    	catch(IllegalArgumentException e)
    	{
    	}
    	isDoubleZoomIn = false;
	}
	
	/**
	 * 监听双击事件类
	 *
	 */
	class LearnGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onDown(MotionEvent e) 
		{
			return super.onDown(e);
		}
		
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) 
		{
			isDoubleZoomIn = true;
			return super.onDoubleTap(e);
		}
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) 
		{
			if(e.findPointerIndex(0) !=-1)
			{
				isSingleTapUp = true;
			}
			return super.onSingleTapUp(e);
		}
	}
	
	private boolean isSingleTapUp = false;
	
	 /**
     * 放大或缩小地图
     */
    private void zoomMap()
    {
    	//判断是否拉伸或缩放
    	double disPointer = mStartPointer - mEndPointer;
    	if(zoomType == zoomScale && mEndPointer != 0 && mStartPointer!= 0 && (disPointer>10 || disPointer<-10))
    	{
    		//取屏幕对角线的二分之一
    		double aveLenght = mScreenPointer/2;
    		//放大或缩小的级别
    		int level = 1;
    		//是否缩小
    		boolean isZoomOut = true;
    		if(disPointer<0)
    		{
    			isZoomOut = false;
    			disPointer = -disPointer;
    		}
    		//判断级别
    		if(aveLenght > disPointer)
    		{
    			//级别提高为1
    			level = 1;
    		}
    		else if(aveLenght*2>disPointer)
    		{
    			//级别提高为2
    			level = 2;
    		}
    		else
    		{
				level = 3;
			}
    		//执行放大或缩小
    		if(isZoomOut)
    		{
    			mMapController.zoomOut( level );
    		}
    		else
    		{
    			mMapController.zoomIn( level );
    		}
    		mStartPointer = 0;
    		mEndPointer = 0;
    	}
    	else
    	{
    		// 判断是否缩小地图(如果时间在300毫秒以内，并且期间没有放大、缩小、旋转地图，则判定为双指单击事件)
    		if(zoomType == NONE)
    		{
    			if(mUpClick - mDownClick <= 300)
            	{
    				mMapController.zoomOut();
            	}
    		}
    	}
    }
	
    @Override
    protected void onDraw(final Canvas canvas)
    {
    	if ( mBufBitmap.isRecycled() )
    	{
    		return;
		}
    	super.onDraw(canvas);
        canvas.drawColor( mDefaultBKColor );
        canvas.save();

        mFinalMatrix.set(matrix);
        // 透视投影
        if(mIsPerspectiveTrans)
        {
        	 mFinalMatrix.postConcat(PerspectiveTrans.getMatrix());
        }
        // 绘制底图
        canvas.drawBitmap(mBufBitmap, mFinalMatrix, mDrawPaint);
        canvas.setMatrix(mFinalMatrix);
        mTileTextRender.drawTileText(canvas, mFinalMatrix,mAngle, mIsPerspectiveTrans,1);
        canvas.restore();
        
        
        // 绘制文字及路名信息
        mTileTextRender.drawTileText(canvas, mFinalMatrix,mAngle, mIsPerspectiveTrans,0);
        // 绘制overlay
        canvas.drawBitmap(mOverlayBitmap, mFinalMatrix, mDrawPaint);

        // 绘制当前位置点
        //POINT point = mMapDisplay.map2Display( MapDef.cLon, MapDef.cLat );
        //mDrawPaint.setColor( Color.RED );
        //canvas.drawCircle( point.x, point.y, 6, mDrawPaint );

        // 恢复canvas
        canvas.restore();

    	mScale = mMapDisplay.getMapScale();
        
        // 绘制屏幕其他信息
        if ( mDrawCopyRightFlag )
        {
        	// 绘制COPYRIGHT
            // 手机屏幕的右下角
            mDrawPaint.setColor( Color.DKGRAY );
            mDrawPaint.setTextSize( 20 );
            canvas.drawText( MapDef.API_COPYRIGHT, mMetrics.widthPixels-20, mMetrics.heightPixels-100, mDrawPaint );
		}
        if ( mDrawScaleFlag )
        {
        	// 绘制比例尺
        	// 放置在COPYRIGHT信息上方
        	mDrawPaint.setColor( Color.DKGRAY );
        	String scaleDesc = MapScaleParam.SCALE_DESCS[MapDef.maxZoom-mMapDisplay.getMapScale()];
            int x = mMetrics.widthPixels - 20;
            int y = mMetrics.heightPixels - 128;

            // 暂时固定一个纬度点
            // mMapDisplay.getCenterPos().getLatitude()
            int iWidth = MapScaleParam.meterToPixels( MapDef.cLat, mMapDisplay.getMapScale() );
            iWidth = (int)(iWidth/2.5);
            Rect scaleRect = new Rect( x-iWidth, y, x, y+4 );
            canvas.drawRect( scaleRect, mDrawPaint );
            canvas.drawLine( x-iWidth+1, y, x-iWidth+1, y-5, mDrawPaint );
            canvas.drawLine( x-1, y, x-1, y-5, mDrawPaint );
            x -= 10;
            y -= 8;
            canvas.drawText( scaleDesc, x, y, mDrawPaint );

            // 测试--当前等级
            /*
            mDrawPaint.setColor( Color.RED );
            y -= 30;
            canvas.drawText( ""+mMapDisplay.getMapScale(), x, y, mDrawPaint );
            */
		}
    }

    private int mScale = 0;
    
//    private Bitmap getSmallBitmap()
//    {
//    	Matrix matrix = new Matrix();
//    	matrix.postScale(0.9f, 0.9f);
//    	return Bitmap.createBitmap(mBufBitmap, mMetrics.widthPixels/2,mMetrics.heightPixels/2,mMetrics.widthPixels+100,mMetrics.heightPixels+100,matrix,true);
//    }
//    
//    private Bitmap getBigBitmap()
//    {
//    	Matrix matrix = new Matrix();
//    	matrix.postScale(1.1f, 1.1f);
//    	return Bitmap.createBitmap(mBufBitmap, mMetrics.widthPixels/2,mMetrics.heightPixels/2,mMetrics.widthPixels,mMetrics.heightPixels,matrix,true);
//    }

    
    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }
   
//    private Bitmap mBGBitmap;
    /**
     * 刷新绘制TileMap
     */
    public void refreshTileMap()
    {
//    	if(mScale != mMapDisplay.getMapScale() && mScale!=0)
//    	{
//    		if(mScale < mMapDisplay.getMapScale())
//    		{
//    			mBGBitmap = getBigBitmap();
//    		}
//    		else
//    		{
//    			mBGBitmap = getSmallBitmap();
//    		}
//    	}
		// 全部刷新
		drawAllTileMap();
		
		drawOverlays();
		matrix.setTranslate(mX, mY);
		matrix.postRotate(mAngle, mMetrics.widthPixels/2, mMetrics.heightPixels/2);
    	invalidate();
    }

    public void refreshTileMap( String tile )
    {
		// 单张刷新
		GridRect grc = mCurGridRectHashMap.get( tile );
		if ( grc != null )
		{
			Bitmap bmp = mTileMapCache.queryTileMap(tile);
			if ( bmp != null )
			{
				mBufCanvas.drawBitmap(bmp, mSrcRect, grc.mSrcRect, null);
				mBufCanvas.drawBitmap(bmp, null, grc.mSrcRect, null);
			}
		}
    	invalidate();
    }
    
    /**
     * 绘制查询的线路
     */
    private void drawOverlays()
    {
    	mOverlayBitmap.eraseColor(Color.TRANSPARENT);
    	if(null != mOverlay)
    	{
    		mOverlay.draw(mOverlayCanvas, mMapView, false);
    	}
    	if(null != mLocationManager)
    	{
    		mLocationManager.draw(mOverlayCanvas, mMapView);
    	}
    }

    private synchronized void drawAllTileMap()
    {
    	mBufCanvas.drawColor( mDefaultBKColor );
    	if(mScale != mMapDisplay.getMapScale() && mScale>12 )
    	{
//    		mBufCanvas.drawBitmap(mBGBitmap, mMetrics.widthPixels/2, mMetrics.heightPixels/2, null);
    	}

    	// 更新数据
    	List<GridRect> grcList = mMapDisplay.getGridRectList();
    	mCurGridRectHashMap.clear();
    	for ( GridRect grc : grcList )
    	{
    		String tile = grc.mScale + "_" + grc.mTileX + "_" + grc.mTileY;
    		mCurGridRectHashMap.put( tile, grc );
    	}
    	mNeedDrawTileList.clear();
    	mTileList = MapUtil.sortThreadList(grcList);
    	mTileMapCache.updateTileList( mTileList );
    	mTileMapCache.clearCache();

    	for ( String tile : mTileList )
    	{
    		GridRect grc = mCurGridRectHashMap.get(tile);
    		if ( grc == null )
    		{
    			continue;
			}
    		// 绘制图片
    		Bitmap tileBitmap = mTileMapCache.queryTileMap(tile);
//    		TileTextInfo tileTextInfo = mTileMapCache.queryTileTextInfo(tile);
    		if ( tileBitmap != null  )
    		{
    		 /*src.left = bx;
    	  src.top = by;
    	  src.right = bx + w;
    	  src.bottom = by + h;
    	  dst.left = x;
    	  dst.top = y;
    	  dst.right = x + w;
    		 dst.bottom = y + h;
    	  */
    			mBufCanvas.drawBitmap(tileBitmap, null, grc.mSrcRect, null);
    		}
    		else
    		{
    			mNeedDrawTileList.add( grc );
    			mBufCanvas.drawBitmap(mDefaultBKBitmap, grc.mSrcRect.left,grc.mSrcRect.top, null);
    		}
    	}
    	if ( mNeedDrawTileList.size() > 0 )
    	{
    		mMapDisplay.DrawMap(mNeedDrawTileList);
		}
    }

    // 检查当前中心点与地图的边界
    private boolean checkMapBound( float fx, float fy )
    {
    	if ( fx > 0 && mMapDisplay.getCenterPos().getLongitude() < MapDef.LEFTLON )
    	{
			return true;
		}
    	if ( fx < 0 && mMapDisplay.getCenterPos().getLongitude() > MapDef.RIGHTLON )
    	{
			return true;
		}
    	if ( fy < 0 && mMapDisplay.getCenterPos().getLatitude() < MapDef.DOWNLAT )
    	{
    		return true;
		}
    	if ( fy > 0 && mMapDisplay.getCenterPos().getLatitude() > MapDef.UPLAT )
    	{
    		return true;
		}
    	return false;
    }

    // 绘图引擎底层异步回调事件监听
    private AsyncCallListener mHandleListener = new AsyncCallListener()
    {
        @Override
        public void onComplete(int token, String event, Object data)
        {
        	if ( event.equals( EventType.EVENT_TILEMAP ) )
        	{
        		if ( mRefreshHandler != null )
            	{
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.obj = data;
                    Message.obtain(mRefreshHandler);
                    mRefreshHandler.sendMessage(msg);
    			}
            }
        }
    };

    public void updateTileBitmap( String tile, Bitmap tileBitmap )
    {
    	if ( mRefreshHandler != null )
    	{
    		TileMapInfo tileMapInfo = new TileMapInfo( tile, tileBitmap);
            Message msg = new Message();
            msg.arg1 = 5;
            msg.obj = tileMapInfo;
            Message.obtain(mRefreshHandler);
            mRefreshHandler.sendMessage(msg);
		}
    }

    class RefreshHandler extends Handler
    {
        public RefreshHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg)
        {
			try
			{
				switch ( msg.arg1 )
				{
				case 1:
					String tile = msg.obj + "";
					if ( !tile.equals("-1") )
					{
						synchronized (mTileBitmap)
						{
							Bitmap tileBitmap = Bitmap.createBitmap(mTileBitmap);
							if (null != tileBitmap)
							{
								mTileMapCache.addCache(tile, tileBitmap, true);
							}
							TileTextInfo tileTextInfo = NativeMVAPI
									.nativeGetTileTextInfo();
							if (null != tileTextInfo)
							{
								mTileMapCache.addCache(tile, tileTextInfo, true);
							}
							refreshTileMap(tile);
						}
					}
					mMapDisplay.setDrawFlag(true);
					//MVApiLog.e( "handleMessage 1", "set true" );
					break;
				case 5:
					TileMapInfo tileMapInfo = (TileMapInfo) msg.obj;
					mTileMapCache.addCache(tileMapInfo.code, tileMapInfo.tileBitmap, false);
					mTileMapCache.addCache(tileMapInfo.code, tileMapInfo.tileTextInfo, false);
					refreshTileMap(tileMapInfo.code);
					//MVApiLog.e( "handleMessage 5", "set true" );
					break;
				default:
					break;
				}
				
			}
            catch (Exception e)
            {
            }
        }
    }
    
    /**
     * 清除虚拟内存中TileMapCache中的缓存
     */
    public void clearTileMapCache()
    {
    	mTileMapCache.clearAllCache();
    	mBufBitmap.recycle();
    }
    
    /**
     * 设置线路的overlay
     * @param overlay
     */
    public void setOverlay(Overlay overlay)
    {
    	mOverlay = overlay;
    	refreshTileMap();
    }
    
    /**
     * 设置地图旋转的角度
     * @param angle 
     * 			旋转的角度
     */
    public void setMapAngle(float angle)
    {
    	mAngle = angle;
    	setCompassViewAngle(angle);
    	refreshTileMap();
    }
    
    /**
	 * 获得地图角度
	 * @return angle
	 * 			旋转的角度
	 */
    public float getMapAngle()
    {
    	return mAngle;
    }
    
    /**
     * 设置指北针角度
     * @param angle
     */
    private void setCompassViewAngle(float angle)
    {
    	if(angle!=0)
    	{
    		mCompassView.setVisibility(0);
    	}
    	else
    	{
    		mCompassView.setVisibility(8);
    	}
    	mCompassView.setAngle(angle);
    }
    
    /**
     * 设置我的位置
     * @param locationManager
     */
    public void setMyLocation(LocationManager locationManager)
    {
    	mLocationManager = locationManager;
    	refreshTileMap();
    }
    
    /**
     * 设置是否开启透视投影功能
     * @param isPerspectiveTrans
     */
    public void setPerspectiveTrans(boolean isPerspectiveTrans)
    {
    	this.mIsPerspectiveTrans = isPerspectiveTrans;
    	refreshTileMap();
    }

}
