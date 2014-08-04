package com.ctfo.mviewer.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ctfo.mvapi.entities.GeoPoint;
import com.ctfo.mvapi.entities.POINT;
import com.ctfo.mvapi.map.ItemizedOverlay;
import com.ctfo.mvapi.map.MapView;
import com.ctfo.mvapi.map.OverlayItem;
import com.ctfo.mviewer.R;
import com.ctfo.mviewer.activity.MVMapActivity;
import com.ctfo.mviewer.entity.BusStation;

public class BusStationOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mBusStationList = new ArrayList<OverlayItem>();
	private MVMapActivity mContext;
	private List<BusStation> mBusStations;
	public BusStationOverlay( Bitmap marker, Context context, List<BusStation> busStations,Resources resources) 
	{
		this.mContext = (MVMapActivity) context;
				
		for (int i=0, len=busStations.size(); i<len; i++)
		{
			BusStation busStation = busStations.get(i);

			GeoPoint pt = new GeoPoint( (int)(busStation.gPoint.getLongitude() * 1E6),(int)(busStation.gPoint.getLatitude() * 1E6) );
			OverlayItem overlayItem = new OverlayItem( pt, busStation.name, busStation.buslinename );
			overlayItem.setMarker(marker);
			mBusStationList.add( overlayItem );
		}
		mBusStations = busStations;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) 
	{
		super.draw(canvas, mapView, shadow);
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setAntiAlias(true);
		for (int i=0, len=mBusStations.size(); i<len; i++)
		{
			BusStation busStation = mBusStations.get(i);
			GeoPoint pt = new GeoPoint( (int)(busStation.gPoint.getLongitude() * 1E6),(int)(busStation.gPoint.getLatitude() * 1E6) );
			POINT points = mapView.map2Display(pt.getLongitude(), pt.getLatitude());
			canvas.drawCircle(points.x, points.y, 8, paint);
		}
	}

	@Override
	protected OverlayItem createItem(int i) 
	{
		return mBusStationList.get(i);
	}


	@Override
	public int size() 
	{
		return mBusStationList.size();
	}

	
	@Override
	public View onTap(int position) 
	{
		View poiPopView = LayoutInflater.from(mContext).inflate(R.layout.popup_poi, null);
		OverlayItem  oOverlayItem =  createItem(position);
		TextView tv1 = (TextView) poiPopView.findViewById(R.id.PoiName);
		tv1.setText(oOverlayItem.getTitle());
		
		return poiPopView;
	}

	@Override
	public boolean getMViewDraw() 
	{
		return false;
	}

}
