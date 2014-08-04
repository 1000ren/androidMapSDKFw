package com.ctfo.mvapi.entities;

/**
 * @author fangwei
 * 
 * 地理范围，即经纬度坐标范围
 * 
 */
public class GeoRect
{
	public GeoRect()
	{
		minLongitude = 0.0;
		minLatitude = 0.0;
		maxLongitude = 0.0;
		maxLatitude = 0.0;
	}

	/**
	 * 地理位置区域
	 * 
	 * @param min_x
	 * @param min_y
	 * @param max_x
	 * @param max_y
	 */
	public GeoRect(double min_lon, double min_lat, double max_lon,
			double max_lat)
	{
		minLongitude = min_lon;
		minLatitude = min_lat;
		maxLongitude = max_lon;
		maxLatitude = max_lat;
	}

	public GeoRect(GeoPoint luPoint, GeoPoint rbPoint)
	{
		minLongitude = luPoint.getLongitude();
		minLatitude = luPoint.getLatitude();
		maxLongitude = rbPoint.getLongitude();
		maxLatitude = rbPoint.getLatitude();
	}

	public GeoRect(GeoRect rect)
	{
		minLongitude = rect.minLongitude;
		minLatitude = rect.minLatitude;
		maxLongitude = rect.maxLongitude;
		maxLatitude = rect.maxLatitude;
	}

	/**
	 * 判断数据合法性
	 * 
	 * @return
	 */
	public boolean IsValidRect()
	{
		if (minLongitude == 0 && minLatitude == 0 && maxLongitude == 0
				&& maxLatitude == 0)
			return false;
		if (minLongitude == maxLongitude)
			return false;
		if (minLatitude == maxLatitude)
			return false;
		return true;
	}

	/**
	 * 判断区域是否包含指定点
	 * 
	 * @param point
	 *            指定点坐标
	 * @return
	 */
	public boolean PtInRect(GeoPoint point)
	{
		if (point == null)
			return false;
		if (point.getLongitude() < minLongitude
				|| point.getLongitude() > maxLongitude
				|| point.getLatitude() < minLatitude
				|| point.getLatitude() > maxLatitude)
			return false;
		return true;
	}

	/**
	 * 判断区域是否被指定区域包含
	 * 
	 * @param otherRect
	 *            指定区域
	 * @return
	 */
	public boolean IsIncluded(GeoRect otherRect)
	{
		if (otherRect == null)
			return false;
		if (otherRect.getMaxLon() <= maxLongitude
				&& otherRect.getMinLon() >= minLongitude
				&& otherRect.getMaxLat() <= maxLatitude
				&& otherRect.getMinLat() >= minLatitude)
			return true;
		return false;
	}

	/**
	 * 判断区域是否与指定区域相交
	 * 
	 * @param otherRect
	 *            指定区域
	 * @return
	 */
	public boolean IsIntersected(GeoRect otherRect)
	{
		if (otherRect == null)
			return false;
		if (otherRect.getMaxLon() < minLongitude
				|| otherRect.getMinLon() > maxLongitude
				|| otherRect.getMaxLat() < minLatitude
				|| otherRect.getMinLat() > maxLatitude)
			return false;
		return true;
	}

	/**
	 * 区域融合
	 * 
	 * @param otherRect
	 *            指定区域
	 * @return
	 */
	public void UniteRect(GeoRect otherRect)
	{
		minLongitude = minLongitude < otherRect.getMinLon() ? minLongitude
				: otherRect.getMinLon();
		maxLongitude = maxLongitude < otherRect.getMaxLon() ? otherRect
				.getMaxLon() : maxLongitude;
		minLatitude = minLatitude < otherRect.getMinLat() ? minLatitude
				: otherRect.getMinLat();
		maxLatitude = maxLatitude < otherRect.getMaxLat() ? otherRect
				.getMaxLat() : maxLatitude;
	}

	/**
	 * 区域融合
	 * 
	 * @param otherRect
	 *            指定区域
	 * @return
	 */
	public void UnuniteRect(GeoRect otherRect)
	{
		minLongitude = minLongitude > otherRect.getMinLon() ? minLongitude
				: otherRect.getMinLon();
		maxLongitude = maxLongitude > otherRect.getMaxLon() ? otherRect
				.getMaxLon() : maxLongitude;
		minLatitude = minLatitude > otherRect.getMinLat() ? minLatitude
				: otherRect.getMinLat();
		maxLatitude = maxLatitude > otherRect.getMaxLat() ? otherRect
				.getMaxLat() : maxLatitude;
	}

	public boolean ClipRect(GeoRect otherRect)
	{
		if ((minLongitude > otherRect.maxLongitude)
				|| (maxLongitude < otherRect.minLongitude)
				|| (minLatitude > otherRect.maxLatitude)
				|| (maxLatitude < otherRect.minLatitude)) {
			return false;
		}
		minLongitude = minLongitude < otherRect.minLongitude ? otherRect.minLongitude
				: minLongitude;
		maxLongitude = maxLongitude < otherRect.maxLongitude ? maxLongitude
				: otherRect.maxLongitude;
		minLatitude = minLatitude < otherRect.minLatitude ? otherRect.minLatitude
				: minLatitude;
		maxLatitude = maxLatitude < otherRect.maxLatitude ? maxLatitude
				: otherRect.maxLatitude;
		return true;
	}

	/**
	 * 区域扩大
	 * 
	 * @param x
	 *            x值
	 * @param y
	 *            y值
	 * @return
	 */
	public void InflateRect(double x, double y)
	{
		minLongitude -= x;
		minLatitude -= y;
		maxLongitude += x;
		maxLatitude += y;
	}

	/**
	 * 获取区域高度
	 * 
	 * @return 高度
	 */
	public double getHeight()
	{
		return maxLatitude - minLatitude;
	}

	/**
	 * 获取区域宽度
	 * 
	 * @return 宽度
	 */
	public double getWidth()
	{
		return maxLongitude - minLongitude;
	}

	/**
	 * 获取minX
	 * 
	 * @return minX
	 */
	public double getMinLon()
	{
		return minLongitude;
	}

	/**
	 * 获取minY
	 * 
	 * @return minY
	 */
	public double getMinLat()
	{
		return minLatitude;
	}

	/**
	 * 获取maxX
	 * 
	 * @return maxX
	 */
	public double getMaxLon()
	{
		return maxLongitude;
	}

	/**
	 * 获取maxY
	 * 
	 * @return maxY
	 */
	public double getMaxLat()
	{
		return maxLatitude;
	}

	// min x
	public double minLongitude = 0.0;

	// min y
	public double minLatitude = 0.0;

	// max x
	public double maxLongitude = 0.0;

	// max y
	public double maxLatitude = 0.0;

}
