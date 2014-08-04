package com.ctfo.mvapi.entities;


/**
 * @author fangwei
 * 
 * 服务器URL设置类。
 * 
 */
public class ServerUrlSetting
{
	// 矢量地图URL设置
	public String strVectorMapUrl;
	
	// 栅格地图URL设置
	public String strTileUrl;
	
	// 卫星地图URL设置
	public String strSateliteUrl;
	
	// 栅格实时交通地图URL设置
	public String strTMCTileUrl;
	
	// 卫星实时交通地图URL设置
	public String strTMCSateliteUrl;
	
	// POI搜索URL设置
	public String strPoiSearchUrl;
	
	// 路径搜索URL设置
	public String strRouteSearchUrl;

	
	public ServerUrlSetting()
	{
		// 
	}
}
