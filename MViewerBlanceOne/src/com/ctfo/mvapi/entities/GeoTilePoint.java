package com.ctfo.mvapi.entities;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author fangwei
 * 
 * 标准瓦片点参数
 * 
 */
public class GeoTilePoint implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 列编号
	public int x;
	// 行编号
	public int y;
	// 显示层级
	public int z;

	public GeoTilePoint()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public GeoTilePoint(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void SetData(int nX, int nY, int nZ)
	{
		this.x = nX;
		this.y = nY;
		this.z = nZ;
	}

	/**
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * @return the z
	 */
	public int getZ()
	{
		return z;
	}
	
	
	public JSONObject packageJson() 
	{
		JSONObject jo = new JSONObject();
		try 
		{
			jo.put("x", x);
			jo.put("y", y);
			jo.put("z", z);
		} 
		catch(JSONException e)
    	{
    		Log.d("packageJson",e.getMessage());
    	}
		return jo;
	}
	
	public void setJSONObjectToObject(JSONObject jsonObj)
	{
		try
		{
			this.x =  jsonObj.getInt("x");
			this.y =  jsonObj.getInt("y");
			this.z =  jsonObj.getInt("z");
		}
		catch(JSONException e)
		{
			Log.d("packageJson",e.getMessage());
		}
	}
}
