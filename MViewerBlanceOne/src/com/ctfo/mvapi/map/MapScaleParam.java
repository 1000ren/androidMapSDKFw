package com.ctfo.mvapi.map;


public class MapScaleParam 
{
	/**
	 * 各级比例尺分母值数组
	 */
	public static final int[] SCALES = { 100, 200, 500, 1000, 2000, 
		5000, 10000, 20000, 25000, 50000, 100000, 200000, 500000, 1000000,
		2000000 };

	/**
	 * 各级比例尺上面的文字数组
	 * 比例尺 / 2 显示
	 */
	/*public static final String[] SCALE_DESCS = { "100米", "200米", 
		"500米", "1公里", "2公里", "5公里", "10公里", "20公里", "25公里", "50公里", "100公里", 
		"200公里", "500公里", "1000公里", "2000公里" };*/
	public static final String[] SCALE_DESCS = { "50米", "100米", 
		"200米", "500米", "1公里", "2公里", "5公里", "10公里", "20公里", "25公里", "50公里", 
		"100公里", "200公里", "500公里", "1000公里" };

	public static int meterToPixels( double dlat, int scale )
	{
		return (int)(MapTile.metersToEquatorPixels( SCALES[MapDef.maxZoom-scale], scale ) / Math.cos(Math.toRadians(dlat)));
	}
}




