package com.ctfo.mvapi.entities;


/**
 * @author Administrator
 *
 * Block的格网信息。
 */
public class BlockRect
{
	public String 	mLevel;
	public int 		mMeshXY[];
	public int 		mBlockXY[];

	public BlockRect()
	{
		mLevel = "";
		mMeshXY = new int[2];
		mBlockXY = new int[2];
	}
}
