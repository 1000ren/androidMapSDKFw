package com.ctfo.mvapi;

import android.os.Environment;


//软件版本信息,每次发布新版本时,都要修改此文件.
public class MVApiVersion 
{
	// 软件版本更新时必须要进行的修改项
	// 产品名称
	public static final String proName = "MVAPI_Android"; // 更新产品名称标记
	public static final String minName = "MVAPI";
	// 产品版本号
	public static final String proVer = "1.0.0.0_20130315_Android"; // 主要验证是否有新版本变量
	// 产品小版本号
	public static final String minVer = "1.0.0.0";
	// 产品发布日期 - 年月日时分秒
	public static final String pubDate = "20130315093000"; // 主要验证是否有新版本变量
	// 渠道ID
	public static final String channelID = "ctfo"; // 渠道ID:默认 palmgo

	// 是否为标准版
	public static final boolean isStandard = false;
	// 终端厂家_产品系列名_产品型号_产品商业名称
	public static final String terminal = "?_?_?_?";
	// 运行环境
	public static final String runEnv_name = "Android";
	// 运行环境厂家名称
	public static final String runEnv_vendor = "google";
	// 运行语言
	public static final String language = "chinese";
	// 屏幕物理尺寸
	public static final double screenSize = 3.6;
	// 屏幕分辨率宽度
	public static final int screenX = 480;
	// 屏幕分辨率高度
	public static final int screenY = 800;

	// 应用程序文件名称
	public static final String APP_NAME = "MVAPI.jar";


	////////////////////////////////////////////////////////////
	// 缓存及配置的相关路径设置
	////////////////////////////////////////////////////////////
	// mvapi主目录
	// 存在sd card
	public static final String MPATH_MVAPI_SDCARD = Environment.getExternalStorageDirectory()+"/mvapi";
	// 没有sd card
	public static String MPATH_MVAPI_INAND = "";
	// 配置文件目录
	public static final String MPATH_MAPCONFIG = "config";
	// 配置文件
	public static final String MPATH_MAINCFG = "MViewConfig.xml";
	public static final String MPATH_MAPDISPLAYCFG = "MapDisplayConfig.xml";
	public static final String MPATH_DISPLAYMODECFG = "DisplayModeConfig.xml";
	//public static final String MPATH_FREETYPEFONT = "msyh.ttf";
	// ICO目录
	public static final String MPATH_ICO = "ico";
	// Log目录
	public static final String MPATH_LOG = "log";
	// 本地数据文件目录
	public static final String MPATH_MAPDATA = "mapdata";
	// Vector数据目录
	public static final String MPATH_VECTORDATA = "vector";
	// Tile数据目录
	public static final String MPATH_TILEDATA = "tile";
	// TMC数据目录
	public static final String MPATH_TMCDATA = "tmc";
	// Satelite数据目录
	public static final String MPATH_SATELITEDATA = "satelite";
	// Tmp目录
	public static final String MPATH_TMP = "tmp";
	
	//assets中配置文件zip的名称
	public static final String MPATH_ASSETS_CONFIG = "config";
	//assets中配置文件zip的名称
	public static final String MPATH_ASSETS_OPQ = "opq";
}


