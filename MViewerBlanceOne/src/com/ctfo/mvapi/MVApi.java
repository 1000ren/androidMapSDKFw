package com.ctfo.mvapi;

import java.io.File;

import android.content.Context;

import com.ctfo.mvapi.entities.ServerUrlSetting;
import com.ctfo.mvapi.map.MapDef;
import com.ctfo.mvapi.map.MapView;
import com.ctfo.mvapi.utils.AssetUtil;
import com.ctfo.mvapi.utils.Conversation;
import com.ctfo.mvapi.utils.EventManager;
import com.ctfo.mvapi.utils.MapEventController;
import com.ctfo.mvapi.utils.Util;

/**
 * @author fangwei
 * 
 * MVAPI的管理类。负责初始化及销毁操作。
 * 
 */
public class MVApi
{
	// MVApi配置及缓存路径
	public static String mMVApiAppPath = null;
	// Config目录
	public static String mConfigPath = null;
	// Log目录
	public static String mLogPath = null;
	// MapData目录
	public static String mMapDataPath = null;
	// Tile缓存位置
    public static String mTilePath = null;
    // Vector缓存位置
    public static String mVectorPath = null;
    // TMC缓存位置
    public static String mTMCPath = null;
    // Satelite缓存位置
    public static String mSatelitePath = null;
    // Tmp目录
    public static String mTmpPath = null;

    
    // MapMode
    private static int mMapMode = 0;
    // ServerUrlSetting
    private static ServerUrlSetting mServerUrlSetting;

    

	public static boolean init( final Context context )
	{
		EventManager.createInstance(context);
        MapEventController.createInstance(context);

        MVApiVersion.MPATH_MVAPI_INAND = context.getApplicationContext().getFilesDir().getParentFile().getAbsolutePath()+"/mvapi";
        // 检查路径及配置信息
        checkMVApiPathConfigInfo(context);

        // ServerUrlSetting
        setServerUrl();

        return true;
	}

	public static int initMap( Context context, int mapMode )
	{
		mMapMode = mapMode;
		// 设置回调对象
//		NativeMVAPI.nativeSetCallback(Conversation.getInstance());
		// 初始化绘图引擎
//		int error = NativeMVAPI.nativeInitEngine( MapView.getBitmap( context, MapDef.bufTileW, MapDef.bufTileH ), 
//				MVApi.mMVApiAppPath, mMapMode );
//        return error;
        return 0;
        
        
	}

	public static void quit()
	{
		EventManager.destoryInstance();
		MapEventController.destoryInstance();
		NativeMVAPI.nativeQuit();
	}

	public static int getMapMode()
	{
		return mMapMode;
	}
	
	public static ServerUrlSetting getServerUrlSetting()
	{
		return mServerUrlSetting;
	}


	private static void checkMVApiPathConfigInfo(Context context)
	{
		boolean hasSDCard = Util.isSDCardExist();
        if ( hasSDCard )
        {
        	mMVApiAppPath = MVApiVersion.MPATH_MVAPI_SDCARD;
        }
        else
        {
        	mMVApiAppPath = MVApiVersion.MPATH_MVAPI_INAND;
        }
        checkFolderExist( mMVApiAppPath );

        // config目录
        mConfigPath = mMVApiAppPath + "/" + MVApiVersion.MPATH_MAPCONFIG + "/";
        checkFolderExist( mConfigPath );
        checkConfigFile( mConfigPath ,context);

        // Log目录
        mLogPath = mMVApiAppPath + "/" + MVApiVersion.MPATH_LOG + "/";
        checkFolderExist( mLogPath );

        // MapData目录
        mMapDataPath = mMVApiAppPath + "/" + MVApiVersion.MPATH_MAPDATA + "/";
        checkFolderExist( mMapDataPath );
        checkMapData( mMapDataPath ,context);

        // Tile缓存位置
        mTilePath = mMVApiAppPath + "/" + MVApiVersion.MPATH_TILEDATA + "/";
        checkFolderExist( mTilePath );
        for (int i = MapDef.minZoom; i <= MapDef.maxZoom; i++)
        {
        	// 创建Tile的临时目录
        	String scalePath = mTilePath + i + "/";
        	checkFolderExist(scalePath);
		}

        // Vector缓存位置
        mVectorPath = mMVApiAppPath + "/" + MVApiVersion.MPATH_VECTORDATA + "/";
        checkFolderExist( mVectorPath );

        // TMC缓存位置
        mTMCPath = mMVApiAppPath + "/" + MVApiVersion.MPATH_TMCDATA + "/";
        checkFolderExist( mTMCPath );

        // Satelite缓存位置
        mSatelitePath = mMVApiAppPath + "/" + MVApiVersion.MPATH_SATELITEDATA + "/";
        checkFolderExist( mSatelitePath );
        
        // Tmp目录
        mTmpPath = mMVApiAppPath + "/" + MVApiVersion.MPATH_TMP + "/";
        checkFolderExist( mTmpPath );
	}

	public static void checkFolderExist( String strPath )
	{
		File file = new File( strPath );
        if ( !file.exists() && !file.isDirectory() )
        {
            file.mkdirs();
        }
	}

	public static boolean checkFileExist( String strFile )
	{
		File file = new File( strFile );
        return file.exists();
	}

	public static void clearTileCache()
	{
		CacheManager.clearTileCache(mTilePath);
	}

	public static void clearVectorCache()
	{
		CacheManager.clearVectorCache(mVectorPath);
	}
	
	private static void checkConfigFile( String strPath ,Context context)
	{
		// 检查相关配置文件是否存在，如果不存在，则解压创建。
		// MPATH_MAINCFG = "MViewConfig.xml";
		// MPATH_MAPDISPLAYCFG = "MapDisplayConfig.xml";
		// MPATH_DISPLAYMODECFG = "DisplayModeConfig.xml";
		// MPATH_FREETYPEFONT = "msyh.ttf";

		String strMainCfgPath = strPath + MVApiVersion.MPATH_MAINCFG;
		File mainCfgFile = new File( strMainCfgPath );
		
		String strMapCfgPath = strPath + MVApiVersion.MPATH_MAPDISPLAYCFG;
		File mapCfgFile = new File( strMapCfgPath );
		
		String strDispCfgPath = strPath + MVApiVersion.MPATH_DISPLAYMODECFG;
		File dispCfgFile = new File( strDispCfgPath );
		
		if ( !dispCfgFile.exists() || !mainCfgFile.exists() || !mapCfgFile.exists() )
		{
			// 如果有任何一个文件没有，则开始从工程文件夹中复制到SD卡上
			AssetUtil.setAssetFile(context, strPath ,MVApiVersion.MPATH_ASSETS_CONFIG);
		}
		
		/*String strFontPath = strPath + MVApiVersion.MPATH_FREETYPEFONT;
		File fontFile = new File( strFontPath );
		if ( !fontFile.exists() )
		{
			// 
		}*/
	}

	private static void checkMapData( String strPath ,Context context)
	{
		// 检查初始的地图数据文件是否存在，如果不存在，则解压创建
		// o p q 三层的数据
		String strO = strPath + "o00";
		File ofile = new File( strO );

		String strP = strPath + "p00";
		File pfile = new File( strP );

		String strQ = strPath + "q00";
		File qfile = new File( strQ );
		
		if ( !qfile.exists() || !ofile.exists() || !pfile.exists())
		{
			// 如果有任何一个文件没有，则开始从工程文件夹中复制到SD卡上
			AssetUtil.setAssetFile(context, strPath ,MVApiVersion.MPATH_ASSETS_OPQ);
		}
	}

	private static void setServerUrl()
	{
		mServerUrlSetting = new ServerUrlSetting();
//		mServerUrlSetting.strVectorMapUrl = "http://59.108.127.194:8088/MCMDN/";
		mServerUrlSetting.strVectorMapUrl = "http://tileni.transmap.com.cn/";
		//mServerUrlSetting.strTileUrl = ;
		
	}
	
}

