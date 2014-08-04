package com.ctfo.mviewer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;

import com.ctfo.mvapi.MVApi;
import com.ctfo.mvapi.database.SaveManager;
import com.ctfo.mvapi.map.MapMode;
import com.ctfo.mviewer.CommonApplication;
import com.ctfo.mviewer.R;

public class MViewerActivity extends Activity
{
	public static MViewerActivity pthis;

	public CommonApplication mCommonApplication = null;

	private Context mContext;
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	requestWindowFeature( Window.FEATURE_NO_TITLE );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        pthis = this;
        this.mContext = this;
//        if(!isConnectingToInternet())
//        {
//        	sysExit("网络无法连接！");
//        	return ;
//        }
    	
    	new Thread(new Runnable() 
    	{
			@Override
			public void run() 
			{
				try
		    	{
					Thread.sleep(2000);
					Command.init(mContext);

			        // init mvapi
			        MVApi.initMap( mContext, MapMode.VECTOR_MODE );

			        // ???
			        //初始化数据库
			    	SaveManager.initDatabase(mContext);
			    	SaveManager.initDataBaseFile(R.raw.clientdata);
					startIntent();
				}
		    	catch (InterruptedException e) 
		    	{
					e.printStackTrace();
				}
			}
		}).start();
    }
    
    /**
	 * 判断是否可以连接网络
	 * @param context
	 * @return
	 */
	public boolean isConnectingToInternet() 
	{
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED) 
					{
						return true;
					}
				}
			}
		}
		return false;
	}
    
	/**
	 * 跳转到MVMap
	 */
	private void startIntent()
	{
		Intent intent = new Intent( MViewerActivity.this, MVMapActivity.class );
        startActivity( intent );
        finish();
	}

	/**
	 * 退出系统
	 * @param strErr
	 */
	public void sysExit( String strErr )
	{
		if(!((Activity)mContext).isFinishing())
		{
			new AlertDialog.Builder(this).setTitle(mContext.getResources().getString(R.string.app_name))
			.setMessage(strErr)
			.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					System.exit(0);
				}
				
			})
			.setOnCancelListener(new OnCancelListener() 
			{
				@Override
				public void onCancel(DialogInterface dialog) 
				{
					System.exit(0);
				}
			})
			.show();
		}

	}
}