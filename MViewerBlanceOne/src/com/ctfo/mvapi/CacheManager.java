package com.ctfo.mvapi;

import java.io.File;

import com.ctfo.mvapi.map.MapDef;
import com.ctfo.mvapi.utils.Util;

/**
 * 缓存管理
 * @author fangwei
 *
 */
public class CacheManager 
{
	
	/**
	 * 清除物理内存中所有Tile的缓存文件
	 * @param tilePath tile的地址
	 */
	public static void clearTileCache(String tilePath)
	{
		for (int i = MapDef.minZoom; i <= MapDef.maxZoom; i++)
        {
        	// 创建Tile的临时目录
        	String scalePath = tilePath + i + "/";
        	clearData(scalePath);
		}
	}

	/**
	 * 清除物理内存中vector的缓存文件
	 * @param vectorPath
	 */
	public static void clearVectorCache(String vectorPath)
	{
		File file = new File(vectorPath);
		Util.deleteAllFile( file );
	}

	
	/**
	 * 根据级别清除物理内存中Tile的缓存文件
	 * @param path 级别地址
	 */
	private static void clearData(String path)
	{
		File file = new File( path );
        boolean isExist = file.exists();
        if ( isExist )
        {
        	//遍历目录下所有的文件 
        	File[] files = file.listFiles();
        	for (int i = 0;i < files.length;i ++) 
        	{ 
        		files[i].delete();
            }
            file.mkdirs();
        }
	}
	
}
