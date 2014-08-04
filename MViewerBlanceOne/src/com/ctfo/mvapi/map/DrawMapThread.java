package com.ctfo.mvapi.map;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ctfo.mvapi.MVApi;
import com.ctfo.mvapi.entities.GridRect;

/**
 * @author fangwei
 *
 * 绘制线程
 */
public class DrawMapThread extends Thread
{
	public static DrawMapThread mthis = null;
	private MapManager mMapManager = null;
	private LinkedList<GridRect> mGridRectLinkedList = new LinkedList<GridRect>();
	private Boolean mDrawTileOk = true;
	// 本地Tile信息加载线程池
	private LocalTileParsePool mLocalTileParsePool = null;
	private List<GridRect> mLocalTileList = null;
	

	public DrawMapThread( MapManager mapManager )
	{
		mthis = this;
		mMapManager = mapManager;
		mLocalTileList = new ArrayList<GridRect>();
		
		mLocalTileParsePool = new LocalTileParsePool();
		mLocalTileParsePool.start();
	}

	public void updateGridList( List<GridRect> grclist )
	{
		synchronized ( mGridRectLinkedList )
		{
			mGridRectLinkedList.clear();
			mLocalTileList.clear();
			for (int i = 0; i < grclist.size(); i++)
			{
				GridRect grc = grclist.get(i);
				if ( grc == null )
				{
					continue;
				}
				if ( isTileInfoExist( grc ) )
				{
					mLocalTileList.add( grc );
				}
				else
				{
					mGridRectLinkedList.addLast( grc );
				}
			}
			mLocalTileParsePool.updateLocalTileList( mLocalTileList );
		}
	}

	public GridRect getGridRect()
	{
		synchronized ( mGridRectLinkedList )
		{
			return mGridRectLinkedList.poll();
		}
	}

	public void addGridRect( GridRect grc )
	{
		synchronized ( mGridRectLinkedList )
		{
			mGridRectLinkedList.addLast( grc );
		}
	}

	public void setDrawFlag( Boolean bOk )
	{
		synchronized (mDrawTileOk)
		{
			mDrawTileOk = bOk;
		}
	}

	public Boolean getDrawFlag()
	{
		synchronized (mDrawTileOk)
		{
			return mDrawTileOk;
		}
	}

	private boolean isTileInfoExist( GridRect grc )
	{
		String tile = grc.mScale + "_" + grc.mTileX + "_" + grc.mTileY;
		String bitmapFilePath = MVApi.mTilePath + grc.mScale + "/" + tile + ".png";
		String tileTextInfoFilePath = MVApi.mTilePath + grc.mScale + "/" + tile + ".idx";
		File file1 = new File(bitmapFilePath);
		File file2 = new File(tileTextInfoFilePath);
		if ( !file1.exists() )
		{
			return false;
		}
		return true;
	}

	public void run()
	{
		while ( true )
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			if ( getDrawFlag() )
			{
				GridRect grc = getGridRect();
				if ( grc != null )
				{
					setDrawFlag(false);
					//MVApiLog.e( "loadDataForDisplay", "tile="+tile );
					if ( mMapManager.loadDataForDisplay( grc ) == MapManager.DRAWTILE_AGAIN )
					{
						// 需要将当前格网重新加入队列进行绘制
						addGridRect( grc );
						setDrawFlag( true );
					}
				}
			}
		}
	}
}
