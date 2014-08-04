package com.ctfo.mviewer.entity;

import java.io.Serializable;

import com.ctfo.mvapi.entities.GeoPoint;

public class BusStation implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String stopid;
	public String name;
	public GeoPoint gPoint;
	public String cat;
	public String admincode;
	public String adminname;
	
	//收藏标识
    public boolean favoriteflag = false; 
	public String[] busline={};
	//非 json 返回数据 用来存储 线路名称,逗号间隔（例：691路,670路,）
	public String buslinename="";

}
