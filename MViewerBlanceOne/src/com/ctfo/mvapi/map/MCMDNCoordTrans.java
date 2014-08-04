package com.ctfo.mvapi.map;

import com.ctfo.mvapi.entities.GeoRect;

/**
 * @author fangwei
 * 
 * MCMDN数据的转换类
 * 
 */
public class MCMDNCoordTrans
{

	private static final int[] ZNMapBlockLongitude4 = 
	{
	    28125,          // g
	    112500,         // i
	    450000,         // j
	    1800000,        // k
	    3600000,        // l
	    7200000,        // m
	    14400000,       // n
	    28800000,	    // o
	    115200000,      // p
	    460800000       // q
	};
	
	private static final int[] ZNMapBlockLatitude4 = 
	{
	    18750,          // g
	    75000,          // i
	    300000,         // j
	    1200000,        // k
	    2400000,        // l
	    4800000,	    // m
	    9600000,        // n
	    19200000,	    // o
	    76800000,       // p
	    307200000       // q
	};

	// 以南纬90，西经180为原点坐标
	private static final int MESH_BASE_COORX = -648000000; // 180 * 3600000;
	private static final int MESH_BASE_COORY = -324000000; // 90 * 3600000;

	public static final int trans2Second1000( double val )
	{
		// 1/1000秒单位
		return (int)( val * 3600000 );
	}
	
	public static final double revTrans2Second1000( int val )
	{
		return (double)( val / 3600000.0 );
	}

	public static final int trans2Second24( double val )
	{
		// 1/24秒单位
		return (int)( val * 86400 );
	}
	
	public static final double revTrans2Second24( int val )
	{
		return (double)( val / 86400.0 );
	}

	public static String getMCMDNBlockFilePath( String level, int[] meshXY, int[] blockXY )
	{
		// ("%c00/%04x/%04x/%04x%04x/%04x%04x.mm"), cLevel, meshX/16, meshY/16, meshX/4, meshY/4, blockX, blockY );
	    String filePath = level + "00/";

	    String str = toHexString(meshXY[0]/16);
	    filePath += str;
	    filePath += "/";

	    str = toHexString(meshXY[1]/16);
	    filePath += str;
	    filePath += "/";

	    str = toHexString(meshXY[0]/4);
	    filePath += str;
	    str = toHexString(meshXY[1]/4);
	    filePath += str;
	    filePath += "/";

	    str = toHexString(blockXY[0]);
	    filePath += str;
	    str = toHexString(blockXY[1]);
	    filePath += str;

	    filePath += ".mm";

	    return filePath;
	}

	public static int[] getMeshXYOfLL( int level, int lon, int lat )
	{
		int[] mesh = new int[2];
		mesh[0] = (lon - MESH_BASE_COORX) / ZNMapBlockLongitude4[level] + 1;
		mesh[1] = (lat - MESH_BASE_COORY) / ZNMapBlockLatitude4[level] + 1;
		return mesh;
	}

	public static int[] getBlockXYOfLL( int level, int lon, int lat )
	{
		int[] block = new int[2];
	    int a = lon - MESH_BASE_COORX;
	    block[0] = ( (a / ZNMapBlockLongitude4[level]) << 2 ) + (int)( ( a % ZNMapBlockLongitude4[level] ) * 4 / ZNMapBlockLongitude4[level]);

	    int b = lat - MESH_BASE_COORY;
	    block[1] = ( (b / ZNMapBlockLatitude4[level]) << 2 ) + (int)( ( b % ZNMapBlockLatitude4[level] ) * 4 / ZNMapBlockLatitude4[level]);

	    block[0] += 4;
	    block[1] += 4;
	    return block;
	}

	public static GeoRect getLLRectOfBlock( final int level, int blockX, int blockY )
	{
		GeoRect geoRect = new GeoRect();
	    blockX -= 4;
	    blockY -= 4;

	    int minX = (int)(((blockX>>2) * 1.0 * ZNMapBlockLongitude4[level]) +
	        (((blockX&0x03) * 1.0 * ZNMapBlockLongitude4[level]) / 4)) + MESH_BASE_COORX;
	    blockX += 1;
	    int maxX = (int)(((blockX>>2) * 1.0 * ZNMapBlockLongitude4[level]) +
	        (((blockX&0x03) * 1.0 * ZNMapBlockLongitude4[level]) / 4)) + MESH_BASE_COORX;

	    int minY = (int)(((blockY>>2) * 1.0 * ZNMapBlockLatitude4[level]) +
	        (((blockY&0x03) * 1.0 * ZNMapBlockLatitude4[level]) / 4)) + MESH_BASE_COORY;
	    blockY += 1;
	    int maxY = (int)(((blockY>>2) * 1.0 * ZNMapBlockLatitude4[level]) +
	        (((blockY&0x03) * 1.0 * ZNMapBlockLatitude4[level]) / 4)) + MESH_BASE_COORY;
	    
	    geoRect.minLongitude = revTrans2Second1000( minX );
	    geoRect.minLatitude = revTrans2Second1000( minY );
	    geoRect.maxLongitude = revTrans2Second1000( maxX );
	    geoRect.maxLatitude = revTrans2Second1000( maxY );
	    return geoRect;
	}

	public static String toHexString( int val )
	{
		StringBuilder strBuilder = new StringBuilder();
		String str = Integer.toHexString(val);
	    if ( str.length() < 4 )
	    {
	    	strBuilder.append("0");
		}
	    if ( str.length() < 3 )
	    {
	    	strBuilder.append("0");
		}
	    if ( str.length() < 2 )
	    {
	    	strBuilder.append("0");
		}
	    strBuilder.append(str);
	    return strBuilder.toString();
	}


}
