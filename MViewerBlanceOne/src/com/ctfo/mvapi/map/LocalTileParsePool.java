package com.ctfo.mvapi.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ctfo.mvapi.MVApi;
import com.ctfo.mvapi.entities.GridRect;
import com.ctfo.mvapi.entities.TileTextInfo;


/**
 * @author fangwei
 *
 * 本地Tile文件信息读取任务管理池
 */
public class LocalTileParsePool extends Thread
{
	public static LocalTileParsePool mthis = null;
	// 需要加载的Tile
	private LinkedList<GridRect> mGridRectLinkedList = new LinkedList<GridRect>();
	// 最大线程数
	private static final int THREAD_MAX = 3;
	// 加载线程
	private LocalTileParseThread[] mLocalTileParseThread = new LocalTileParseThread[THREAD_MAX];


	public LocalTileParsePool()
	{
		mthis = this;
	}

	public void updateLocalTileList( List<GridRect> grclist )
	{
		synchronized ( mGridRectLinkedList )
		{
			mGridRectLinkedList.clear();
			for (int i = 0; i < grclist.size(); i++)
			{
				GridRect grc = grclist.get(i);
				if ( grc == null )
				{
					continue;
				}
				mGridRectLinkedList.addLast(grc);
			}
		}
	}

	public GridRect getTask()
	{
		synchronized ( mGridRectLinkedList )
		{
			return mGridRectLinkedList.poll();
		}
	}
	
	public synchronized void finishTask( int id, GridRect grc )
	{
		synchronized ( mLocalTileParseThread )
		{
			if ( id >= 0 && id < THREAD_MAX )
			{
				mLocalTileParseThread[id] = null;
			}
		}
	}

	public void run()
	{
		while ( true )
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			for (int i = 0; i < THREAD_MAX; i++)
			{
				if ( mLocalTileParseThread[i] == null )
				{
					GridRect task = getTask();
					if ( task != null )
					{
						mLocalTileParseThread[i] = new LocalTileParseThread(i, task);
						mLocalTileParseThread[i].start();
						//MVApiLog.e( "startLocalTileTask", 
						//		"id="+i+";task="+task.mScale+"."+task.mTileX+"."+task.mTileY );
					}
				}
			}
		}
	}
	
	// 工作线程
	public class LocalTileParseThread extends Thread
	{
		public int mThdID = -1;
		private GridRect mGridRect = null;
		
		public LocalTileParseThread( int id, GridRect grc )
		{
			mThdID = id;
			mGridRect = grc;
		}

		public void run()
		{
			String tile = mGridRect.mScale + "_" + mGridRect.mTileX + "_" + mGridRect.mTileY;
			String bitmapFilePath = MVApi.mTilePath + mGridRect.mScale + "/" + tile + ".png";
//			String tileTextInfoFilePath = MVApi.mTilePath + mGridRect.mScale + "/" + tile + ".idx";
			Bitmap tileBitmap = getBitmapByName( bitmapFilePath );
//			TileTextInfo tileTextInfo = getTileTextInfoByName( tileTextInfoFilePath );
//			if ( tileBitmap != null && tileTextInfo != null )
			    if ( tileBitmap != null )
			{
				//MVApiLog.e( "run---", tile );
				MapDrawView.mthis.updateTileBitmap( tile, tileBitmap );
			}
			else
			{
				// 需要绘制
				DrawMapThread.mthis.addGridRect( mGridRect );
			}
			LocalTileParsePool.mthis.finishTask( mThdID, mGridRect );
		}
		
		private synchronized Bitmap getBitmapByName(String bitmapPath)
	    {
	    	FileInputStream fis = null;
	    	Bitmap oBitmap = null;
			try 
			{
				File file = new File(bitmapPath);
				if ( file.exists() )
				{
					fis = new FileInputStream(bitmapPath);
					oBitmap  = BitmapFactory.decodeStream(fis);
				}
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(null != fis)
				{
					try 
					{
						fis.close();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
			return oBitmap;
	    }
		
	    private synchronized TileTextInfo getTileTextInfoByName(String tileTextInfoPath)
	    {
	    	FileInputStream fin = null;
	    	TileTextInfo tileTextInfo = null;
	    	String res = "";
			try 
			{
				File file = new File(tileTextInfoPath);
				if ( file.exists() )
				{
					fin = new FileInputStream(tileTextInfoPath);
				    int length = fin.available(); 
				    byte [] buffer = new byte[length]; 
				    fin.read(buffer);     
				    res = EncodingUtils.getString(buffer, "UTF-8"); 
				    if(null != res && !res.equals(""))
				    {
				    	JSONObject object = new JSONObject(res);
				    	tileTextInfo = new TileTextInfo();
				    	tileTextInfo.setJSONObjectToObject(object);
				    }
				    
				}
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(null != fin)
				{
					try 
					{
						fin.close();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
			return tileTextInfo;
	    }
	}

	
}
