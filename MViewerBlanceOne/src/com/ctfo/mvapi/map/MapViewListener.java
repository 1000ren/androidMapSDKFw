package com.ctfo.mvapi.map;

/**
 * 监听MapView的接口
 * @author fangwei
 *
 */
public interface MapViewListener 
{
	/**移动地图完成监听*/
	public void onMapMoveFinish();
	
	/**地图级别发生变化监听*/
	public void onMapZoomChanged();
}
