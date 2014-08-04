//**************************************************************
//Copyright (C) 2011 Neusoft Co,Ltd. All Rights Reserved.
//-------------------------------------------------------------------
//项目名 ： CnOnFootNavi
//-------------------------------------------------------------------
//版本			作者			完成日			备注
//1.0.0			yangyang	2011/07/07		新規
//**************************************************************
package com.ctfo.mvapi.database;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 本地数据保存用的接口
 * @author 
 *
 *
 */
public interface SaveDataListener
{
	/**
	 * 表创建时使用的方法	 * @return 表的字段名称返回，第一个是标的title,第二个是数据类型
	 */
	public Hashtable<String,String> onCreateTable();

	/** 检索用的ｗｈｅｒｅ条件部分	 * @return 判断语句。比如、"id=4 and name='name'"；	 */
	public String onFilterData();

	/**
	 * 数据保存用的方法	 * @return 需要保存的ContentValues対象
	 * @throws Exception 异常发生时，返回异常内容
	 */
	public ContentValues onSaveData()throws Exception;

	/**
	 * 数据读取的方法	 * @param oCursor 一条记录的对象	 * @throws Exception 异常发生时，返回异常内容
	 */
	public void onReadData(Cursor oCursor)throws Exception;
	
	/**
	 * 清空所有数据
	 */
	public void clearData();
	
	/**
	 * 写入Context(为了避免缓存被清空)
	 */
	public Context getContext();
}
