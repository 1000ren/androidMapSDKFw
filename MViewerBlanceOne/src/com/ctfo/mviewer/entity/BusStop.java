package com.ctfo.mviewer.entity;

import java.io.Serializable;

import com.ctfo.mvapi.entities.GeoPoint;

public class BusStop implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String id;
	public String stopid;
	public String stopname;
	public String tonextlen;
	public GeoPoint gPoint;
}
