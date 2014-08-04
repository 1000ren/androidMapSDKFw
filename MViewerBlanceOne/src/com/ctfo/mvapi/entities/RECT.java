package com.ctfo.mvapi.entities;

/**
 * @author fangwei
 * 
 * 屏幕坐标范围
 *
 */
public class RECT
{
	// left
	private int mleft = 0;

	// top
	private int mtop = 0;

	// right
	private int mright = 0;

	// bottom
	private int mbottom = 0;

	public RECT()
	{
		// 
	}
	
	public RECT(RECT rect)
	{
		mleft = rect.mleft;
		mtop = rect.mtop;
		mright = rect.mright;
		mbottom = rect.mbottom;
	}

	public RECT(int left, int top, int right, int bottom)
	{
		mleft = left;
		mtop = top;
		mright = right;
		mbottom = bottom;
	}

	public void ReSet(int left, int top, int right, int bottom)
	{
		mleft = left;
		mtop = top;
		mright = right;
		mbottom = bottom;
	}

	public void SetLeft(int left)
	{
		mleft = left;
	}

	public int GetLeft()
	{
		return mleft;
	}

	public void SetTop(int top)
	{
		mtop = top;
	}

	public int GetTop()
	{
		return mtop;
	}

	public void SetRight(int right)
	{
		mright = right;
	}

	public int GetRight()
	{
		return mright;
	}

	public void SetBottom(int bottom)
	{
		mbottom = bottom;
	}

	public int GetBottom()
	{
		return mbottom;
	}

	public int GetWidth()
	{
		return ((mright - mleft) > 0) ? (mright - mleft)
				: (mleft - mright);
	}

	public int GetHeight()
	{
		return ((mbottom - mtop) > 0) ? (mbottom - mtop)
				: (mtop - mbottom);
	}

	/**
	 * 区域融合
	 * 
	 * @param otherRect
	 *            指定区域
	 * @return
	 */
	public void UniteRect(RECT otherRect)
	{

		mleft = mleft < otherRect.GetLeft() ? mleft : otherRect.GetLeft();
		mright = mright < otherRect.GetRight() ? otherRect.GetRight()
				: mright;
		mtop = mtop < otherRect.GetTop() ? mtop : otherRect.GetTop();
		mbottom = mbottom < otherRect.GetBottom() ? otherRect.GetBottom()
				: mbottom;
	}

	/**
	 * 区域融合
	 * 
	 * @param otherRect
	 *            指定区域
	 * @return
	 */
	public void UnuniteRect(RECT otherRect)
	{

		mleft = mleft > otherRect.GetLeft() ? mleft : otherRect.GetLeft();
		mright = mright > otherRect.GetRight() ? otherRect.GetRight()
				: mright;
		mtop = mtop > otherRect.GetTop() ? mtop : otherRect.GetTop();
		mbottom = mbottom > otherRect.GetBottom() ? otherRect.GetBottom()
				: mbottom;
	}

	public boolean clipRect(RECT otherRect)
	{
		if ((mleft > otherRect.mright) || (mright < otherRect.mleft)
				|| (mtop > otherRect.mbottom) || (mbottom < otherRect.mtop)) {
			return false;
		}
		mleft = mleft < otherRect.mleft ? otherRect.mleft : mleft;
		mright = mright < otherRect.mright ? mright : otherRect.mright;
		mtop = mtop < otherRect.mtop ? otherRect.mtop : mtop;
		mbottom = mbottom < otherRect.mbottom ? mbottom
				: otherRect.mbottom;
		return true;
	}

	/**
	 * 判断区域是否与指定区域相交
	 * 
	 * @param otherRect
	 *            指定区域
	 * @return
	 */
	public boolean IsIntersected(RECT otherRect)
	{

		if (otherRect == null)
			return false;
		if (otherRect.GetRight() < mleft || otherRect.GetLeft() > mright
				|| otherRect.GetBottom() < mtop
				|| otherRect.GetTop() > mbottom)
			return false;
		return true;
	}

	public boolean IsInclude(RECT otherRect)
	{

		if (otherRect == null)
			return false;
		if (otherRect.GetRight() < mright && otherRect.GetLeft() > mleft
				&& otherRect.GetBottom() < mbottom
				&& otherRect.GetTop() > mtop)
			return false;
		return true;
	}

	/**
	 * 区域移动
	 * 
	 * @param x
	 *            x值
	 * @param y
	 *            y值
	 * @return
	 */
	public void MoveRect(long x, long y)
	{

		mleft -= x;
		mtop -= y;
		mright -= x;
		mbottom -= y;
	}
}
