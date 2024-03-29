package com.ctfo.mviewer.protocol;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ctfo.mviewer.entity.BusLine;
import com.ctfo.mviewer.entity.BusLineRes;

import cm.framework.protocol.BaseJSONRsponse;


public class GetBusLineListResponse extends BaseJSONRsponse implements Serializable {

	private static final long serialVersionUID = 1L;

	public BusLineRes mBusLineRes = null;
	public int mTotal = 0;

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			mTotal = dataJsonObject.getInt("numFound");
			JSONArray listJsonArray = dataJsonObject.getJSONArray("linelist");
			if ( mTotal > 0 && listJsonArray != null && listJsonArray.length() > 0 )
			{
				mBusLineRes = new BusLineRes();
				mBusLineRes.mBusLineList = new ArrayList<BusLine>();
				for (int i=0,len=listJsonArray.length(); i<len; i++)
				{
					JSONObject busLineJsonObject = listJsonArray.getJSONObject(i);
					BusLine busLine=new BusLine();
					busLine.lineid=busLineJsonObject.getString( "lineid" );
					busLine.linename=busLineJsonObject.getString( "linename" );
					busLine.length=busLineJsonObject.getString( "length" );
					busLine.linetype=busLineJsonObject.getString( "linetype" );
					busLine.linedire=busLineJsonObject.getString( "linedire" );
					busLine.firsttime=busLineJsonObject.getString( "timef" );
					busLine.lasttime=busLineJsonObject.getString( "timel" );
					busLine.intervalm=busLineJsonObject.getString( "intervalm" );
					busLine.intervaln=busLineJsonObject.getString( "intervaln" );
					busLine.hoursl=busLineJsonObject.getString( "hoursl" );
					busLine.admincode=busLineJsonObject.getString( "admincode" );
					busLine.adminname=busLineJsonObject.getString( "adminname" );
					
					mBusLineRes.mBusLineList.add( busLine );
				}
			}
			
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return true;
	}

}
