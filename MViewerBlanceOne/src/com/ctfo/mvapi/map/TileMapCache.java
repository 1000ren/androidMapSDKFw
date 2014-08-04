package com.ctfo.mvapi.map;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.graphics.Bitmap;

import com.ctfo.mvapi.MVApi;
import com.ctfo.mvapi.entities.TileTextInfo;

public class TileMapCache 
{
	/**按照顺序存放该屏幕显示的所有编码list*/
	private List<String> mTileList;
	
	/**存放网格编码和对应的Bitmap*/
	private HashMap<String,Bitmap> mTileHashMap;
	
	/**存放网格编码和对应的文字信息*/
	private HashMap<String,TileTextInfo> mTileTextInfoMap;
	
	/**图片存储的位置*/
    private String mBitMapPath = null;
    
    /**图片后缀名*/
    private String mBitMapLastName = ".png";
    // 注记文件后缀名
    private String mTileTextInfoLastName = ".idx";

	/**
	 * 自定义存放编码的数量
	 * @param size
	 */
	public TileMapCache(int size)
	{
		mTileHashMap = new HashMap<String,Bitmap>();
		mTileTextInfoMap = new HashMap<String,TileTextInfo>();
		mTileList = new ArrayList<String>();
        mBitMapPath = MVApi.mTilePath;
	}

	/**
	 * 查询TileMap
	 */
	public Bitmap queryTileMap( String tile )
	{
		synchronized ( mTileHashMap )
		{
 			return mTileHashMap.get( tile );
		}
	}

	/**
	 * 查询TileTextInfo
	 */
	public TileTextInfo queryTileTextInfo( String tile )
	{
		synchronized ( mTileTextInfoMap )
		{
			return mTileTextInfoMap.get( tile );
		}
	}
	
	/**
	 * 更新当前的TileList
	 */
	public void updateTileList( List<String> tileList )
	{
		synchronized ( mTileList )
		{
			if ( tileList != null && tileList.size() > 0 )
			{
				mTileList.clear();
				mTileList.addAll( tileList );
			}
		}
	}

	public List<String> getTileList()
	{
		synchronized ( mTileList )
		{
			return mTileList;
		}
	}
	
	/**
	 * 清理当前的TileCache
	 */
	public synchronized void clearCache()
	{
		List<String> delList = new ArrayList<String>();
		List<String> tileList = new ArrayList<String>();
		tileList.addAll( getTileList() );

		// 清理TileMap
		if(null != mTileHashMap && mTileHashMap.size()>0)
		{
			Iterator<Entry<String, Bitmap>> iter = mTileHashMap.entrySet().iterator();
			while ( iter.hasNext() )
			{
    			Entry<String, Bitmap> entry = (Entry<String, Bitmap>) iter.next();
    			String key = (String)entry.getKey();
    			if(!tileList.contains(key))
    			{
    				delList.add(key);
    			}
			}
			if(null != delList && delList.size()>0)
			{
				for(String str : delList)
				{
					mTileHashMap.get(str).recycle();
					mTileHashMap.remove(str);
				}
			}
		}
		delList.clear();

		// 清理TileTextInfoMap
		if ( null != mTileTextInfoMap && mTileTextInfoMap.size()>0 )
		{
			Iterator<Entry<String, TileTextInfo>> iter = mTileTextInfoMap.entrySet().iterator();
			while ( iter.hasNext() )
			{
    			Entry<String, TileTextInfo> entry = (Entry<String, TileTextInfo>) iter.next();
    			String key = (String)entry.getKey();
    			if(!tileList.contains(key))
    			{
    				delList.add(key);
    			}
			}
			if(null != delList && delList.size()>0)
			{
				for(String str : delList)
				{
					mTileTextInfoMap.remove(str);
				}
			}
		}
		tileList.clear();
		delList.clear();
	}

	/**
	 * 添加Bitmap
	 */
	public void addCache( String code, Bitmap tileBitmap, boolean bSave )
	{
		synchronized ( mTileHashMap )
		{
			if ( !mTileHashMap.containsKey(code) )
			{
				mTileHashMap.put(code, tileBitmap);
			}
		}
		if ( bSave )
		{
			String path = mBitMapPath + MapUtil.getScale(code)+"/"+code+mBitMapLastName;
			saveBitmap(path,tileBitmap);
		}
	}

	/**
	 * 添加注记
	 */
	public void addCache( String code, TileTextInfo tileTextInfo, boolean bSave )
	{
		synchronized ( mTileTextInfoMap )
		{
			if ( !mTileTextInfoMap.containsKey(code) )
			{
				mTileTextInfoMap.put( code, tileTextInfo );
			}
		}
		if ( bSave )
		{
			String path = mBitMapPath + MapUtil.getScale(code)+"/"+code+mTileTextInfoLastName;
			saveTileTextInfo(path, tileTextInfo);
		}
	}

    /**
     * 存储Bitmap
     */
    private void saveBitmap(String path, Bitmap bitmap)
    {
    	FileOutputStream out = null;
    	if ( null == path )
    	{
    		path = "";
    	}
		try 
		{
			out = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(null != out)
			{
				try 
				{
					out.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
    }

    /**
     * 存储AnnotationInfo
     */
    private void saveTileTextInfo(String path, TileTextInfo tileTextInfo)
    {
    	FileOutputStream out = null;
    	if ( null == path )
    	{
    		path = "";
    	}
		try 
		{
			out = new FileOutputStream(path);
			byte[] bytes = tileTextInfo.packageJson().toString().getBytes();
			out.write(bytes);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(null != out)
			{
				try 
				{
					out.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
    }
    
    
    /**
	 * 清除所有缓存
	 */
	public void clearAllCache()
	{
		synchronized ( mTileHashMap )
		{
			if ( null != mTileHashMap )
			{
				mTileHashMap.clear();
			}
		}
		
		synchronized ( mTileTextInfoMap )
		{
			if ( null != mTileTextInfoMap )
			{
				mTileTextInfoMap.clear();
			}
		}
		
		synchronized ( mTileList )
		{
			if ( null != mTileList )
			{
				mTileList.clear();
			}
		}
	}
    
}
