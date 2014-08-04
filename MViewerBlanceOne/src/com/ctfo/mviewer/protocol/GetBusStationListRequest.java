package com.ctfo.mviewer.protocol;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.ctfo.mviewer.entity.Param;

import android.util.Log;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class GetBusStationListRequest extends BaseHttpRequest
{
	private String firstUrl = "http://59.108.127.194:8000/bussvc/index.php?" 
			+"getstop";
	private List<Param> params = new ArrayList<Param>();
	public GetBusStationListRequest( String sid, String keyword, int page, int pagenum ,String admincode)
	{
		setMethod(POST);
		setAbsoluteURI(firstUrl);
		params.add(new Param("key", keyword));
		params.add(new Param("num", String.valueOf(pagenum)));
		params.add(new Param("page",String.valueOf(page)));
		params.add(new Param("sid", sid));
		params.add(new Param("sourse", "androidBx"));
		params.add(new Param("admincode", admincode));
		Log.d(" busx ",firstUrl);
	}
	
	public GetBusStationListRequest( String stopid ,String sid ,String admincode)
	{
		setMethod(POST);
		setAbsoluteURI(firstUrl); 
		params.add(new Param("sid", sid));
		params.add(new Param("sourse", "androidBx"));
		params.add(new Param("stopid", stopid));
		params.add(new Param("admincode", admincode));
		Log.d(" busx ",firstUrl);
	}
	

	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetBusStationListResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		List <NameValuePair> params = new ArrayList<NameValuePair>();
		for (Param param : this.params) 
		{
			params.add(new BasicNameValuePair(param.key, param.value));
		}
		return params;

	}
	
}
