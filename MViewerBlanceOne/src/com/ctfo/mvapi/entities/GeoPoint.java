package com.ctfo.mvapi.entities;

/**
 * 地理位置点，即经纬度坐标点
 * 
 * @author fangwei
 * 
 */
public class GeoPoint
{
	private double mLongitude;
	private double mLatitude;

	public GeoPoint()
	{
		mLatitude = 0.0;
		mLongitude = 0.0;
	}

	public GeoPoint(GeoPoint point)
	{
		mLatitude = point.getLatitude();
		mLongitude = point.getLongitude();
	}

	public GeoPoint(int LongitudeE6, int LatitudeE6)
	{
		this((double) LongitudeE6 / 1000000.0D,
				(double) LatitudeE6 / 1000000.0D);
	}

	public GeoPoint(double Longitude, double Latitude)
	{
		this.mLongitude = Longitude;
		this.mLatitude = Latitude;
	}

	public int getLatitudeE6()
	{
		return (int) (this.mLatitude * 1000000.0D);
	}

	public int getLongitudeE6()
	{
		return (int) (this.mLongitude * 1000000.0D);
	}

	public double getLatitude()
	{
		return this.mLatitude;
	}

	public double getLongitude()
	{
		return this.mLongitude;
	}

	public void setLatitude( double dlat )
	{
		this.mLatitude = dlat;
	}

	public void setLongitude( double dlon )
	{
		this.mLongitude = dlon;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public String toString()
	{
		return "GeoPoint: Latitude: " + this.mLatitude + ", Longitude: "
				+ this.mLongitude;
	}

	@Override
	public boolean equals(Object paramObject)
	{
		if (paramObject == null)
			return false;
		if (paramObject.getClass() != super.getClass())
			return false;
		return (this.mLatitude == ((GeoPoint) paramObject).mLatitude)
				&& (this.mLongitude == ((GeoPoint) paramObject).mLongitude);
	}

}

