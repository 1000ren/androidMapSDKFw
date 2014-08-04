package com.ctfo.mvapi.map;

import android.graphics.Matrix;

import com.ctfo.mvapi.entities.POINT;


public class PerspectiveTrans
{
	private static Matrix mMatrix = new Matrix();
	private static double mMatrixValue[][] = null;

	public static void setMatrixValue( float width, float height, 
			float srcWidth, float srcHeight )
	{
		//float[] src = { -480f, -800f, 960f, -800f, 960f, 1600f, -480f, 1600f };
		//float[] dst = { 0f, -100f, 480f, -100f, 960f, 800f, -480f, 800f };

		// 480 * 800 3倍缓存
		//float[] src = { -480f, -800f, 960f, -800f, 960f, 1600f, -480f, 1600f };
		//float[] dst = { 0f, 0f, 480f, 0f, 960f, 1600f, -480f, 1600f };
		// 2倍缓存
		//float[] src = { -240f, -400f, 720f, -400f, 720f, 1200f, -240f, 1200f };
		//float[] dst = { 0f, 0f, 480f, 0f, 720f, 1200f, -240f, 1200f };

		// 通用
		float dw = width / 2 - srcWidth / 2;
		float dh = height / 2 - srcHeight / 2;
		float[] src = { -dw, -dh, srcWidth+dw, -dh, srcWidth+dw, srcHeight+dh, -dw, srcHeight+dh };
		float[] dst = { 0f, 0f, srcWidth, 0f, srcWidth+dw, srcHeight+dh, -dw, srcHeight+dh };

		mMatrix.setPolyToPoly(src, 0, dst, 0, 4);
		updateMatrixValue( mMatrix );
	}

	public static Matrix getMatrix()
	{
		return mMatrix;
	}

	public static void updateMatrixValue( final Matrix matrix )
	{
		float values[] = new float[10];
		matrix.getValues(values);
		mMatrixValue = new double[3][3];
		mMatrixValue[0][0] = values[0];
		mMatrixValue[0][1] = values[1];
		mMatrixValue[0][2] = values[2];
		mMatrixValue[1][0] = values[3];
		mMatrixValue[1][1] = values[4];
		mMatrixValue[1][2] = values[5];
		mMatrixValue[2][0] = values[6];
		mMatrixValue[2][1] = values[7];
		mMatrixValue[2][2] = values[8];
	}
	
	public static POINT transCoord( float x, float y )
	{
		POINT pt = new POINT();
		double u , v, w;
		u = mMatrixValue[0][0]*x + mMatrixValue[0][1]*y + mMatrixValue[0][2];
		v = mMatrixValue[1][0]*x + mMatrixValue[1][1]*y + mMatrixValue[1][2];
		w = mMatrixValue[2][0]*x + mMatrixValue[2][1]*y + mMatrixValue[2][2];
		if ( w != 0 )
		{
			pt.x = (int)( u / w + 0.5 );
			pt.y = (int)( v / w + 0.5 );
		}
		else
		{
			pt.x = (int)( u + 0.5 );
			pt.y = (int)( v + 0.5 );
		}
		return pt;
	}

}
