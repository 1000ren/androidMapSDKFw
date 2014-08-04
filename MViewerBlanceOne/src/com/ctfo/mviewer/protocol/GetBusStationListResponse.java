package com.ctfo.mviewer.protocol;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cm.framework.protocol.BaseJSONRsponse;

import com.ctfo.mvapi.entities.GeoPoint;
import com.ctfo.mviewer.entity.BusStation;
import com.ctfo.mviewer.entity.BusStationRes;

public class GetBusStationListResponse extends BaseJSONRsponse implements Serializable
{
	private static final long serialVersionUID = 1L;

	public BusStationRes mBusStationRes = null;
	public int mTotal = 0;

	@Override
	protected boolean extractBody(JSONObject arg0)
	{
		try 
		{
			JSONObject dataJsonObject = arg0.getJSONObject("res");
			mTotal = dataJsonObject.getInt("numFound");
			JSONArray listJsonArray = dataJsonObject.getJSONArray("detail");
			if ( mTotal > 0 && listJsonArray != null && listJsonArray.length() > 0)
			{
				mBusStationRes = new BusStationRes();
				mBusStationRes.mBusStationList = new ArrayList<BusStation>();
				for (int i=0,len=listJsonArray.length(); i<len; i++)
				{
					JSONObject busStationJsonObject = listJsonArray.getJSONObject(i);
					BusStation busStation=new BusStation();
					busStation.stopid = busStationJsonObject.getString( "stopid" );
					busStation.name = busStationJsonObject.getString( "name" );
					double lon = busStationJsonObject.getDouble( "lon" );
					double lat = busStationJsonObject.getDouble( "lat" );
					busStation.gPoint = new GeoPoint(lon, lat);
					busStation.cat = busStationJsonObject.getString( "cat" );
					busStation.admincode = busStationJsonObject.getString( "admincode" );
					busStation.adminname = busStationJsonObject.getString( "adminname" );
					JSONArray jsonArray = busStationJsonObject.getJSONArray("busline");
					busStation.busline=new String[jsonArray.length()];
					for (int j = 0; j < jsonArray.length(); j++)
					{
						busStation.busline[j]=jsonArray.getString(j);
						int lateIndex = busStation.busline[j].indexOf("(");
						lateIndex = (lateIndex>0)?lateIndex:busStation.busline[j].length();
								
						busStation.buslinename+=busStation.busline[j].subSequence(busStation.busline[j].indexOf(":")+1, lateIndex)+",";
					}
					if (busStation.buslinename.length()>0)
					{
						busStation.buslinename=busStation.buslinename.substring(0, busStation.buslinename.length()-1);
					}
					mBusStationRes.mBusStationList.add( busStation );
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
