package com.ctfo.mviewer;

import android.app.Application;

import com.ctfo.mvapi.MVApi;

/**
 * @author fangwei
 *
 */
public class CommonApplication extends Application
{

	@Override
	public void onCreate()
	{
		super.onCreate();
		MVApi.init( this );
	}

	public void quit()
	{
		MVApi.quit();
	}


	
	
	
}
