package com.ctfo.mvapi.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.util.FloatMath;
import android.view.MotionEvent;

import com.ctfo.mvapi.entities.GridRect;

/**
 * 地图工具类
 * @author fangwei
 *
 */
public class MapUtil 
{
	/**
     * 根据文字的code获取文字前端的图标(10进制转16进制)
     * @param value
     * @return
     */
    public static String getAnnotationPic( int value)
	{
		String str = Integer.toHexString(value);
		while(str.length()<4)
		{
			str = "0"+str;
		}
		str = "ico_"+str; 
		return str;
	}
	
    
    /**
     * 按照绘制的格网排序list（回字型，从左上角开始，由外向内逐步排序）
     */
    public static List<String> sortThreadList(List<GridRect> grcList)
    {
    	List<String> resList = new ArrayList<String>();
    	if(null != grcList && grcList.size()>0)
    	{
    		//第一个为格网的左上角
    		GridRect grcFirst = grcList.get(0);
    		//最后一个为格网的右下角
    		GridRect grcLast = grcList.get(grcList.size()-1);
    		
    		List<String> list = getRectList(grcFirst,grcLast);
    		for(int i = list.size()-1;i>=0;i--)
			{
    			resList.add(list.get(i));
			}
    	}
    	return resList;
    }
	
	/**
     * 递归函数，根据回字型由外向内逐步遍历所有编码
     * @param grcFirst(左上角)
     * @param grcLast(右下角)
     */
    private static List<String> getRectList(GridRect grcFirst,GridRect grcLast)
    {
    	List<String> list = new ArrayList<String>();
    	
    	//根据起始点判断是否只有一行
    	if(grcFirst.mTileY != grcLast.mTileY && grcFirst.mTileX != grcLast.mTileX)
    	{
    		//按照上右下坐的顺序遍历
    		for(int i=0;i<4;i++)
    		{
    			if(i==0)
    			{
    				//遍历上方数组
    				for(int j=0;j<grcLast.mTileX-grcFirst.mTileX+1;j++)
    				{
    					//上下行代码规则，只有x变化
    					int titlex = grcFirst.mTileX + j;
    					int titley = grcFirst.mTileY;
    					String code = grcFirst.mScale +"_"+ titlex +"_"+ titley;
    					list.add(code);
    				}
    			}
    			else if(i==2)
    			{
    				//遍历下方数组
    				for(int j=0;j<grcLast.mTileX-grcFirst.mTileX+1;j++)
    				{
    					//上下行代码规则，只有x变化
    					int titlex = grcLast.mTileX - j;
    					int titley = grcLast.mTileY;
    					String code = grcFirst.mScale +"_"+ titlex +"_"+ titley;
    					list.add(code);
    				}
    			}
    			else if(i==1)
    			{
    				//遍历右方数据,考虑到上下左右四个角的code已经得到，所以j从1开始，并且循环的次数不减1
    				for(int j=1;j<grcLast.mTileY-grcFirst.mTileY;j++)
    				{
    					//左右行代码规则，只有Y变化
    					int titlex = grcLast.mTileX;
    					int titley = grcFirst.mTileY + j;
    					String code = grcFirst.mScale +"_"+ titlex +"_"+ titley;
    					list.add(code);
    				}
    			}
    			else
    			{
    				//遍历左方数据,考虑到上下左右四个角的code已经得到，所以j从1开始，并且循环的次数不减1
    				for(int j=1;j<grcLast.mTileY-grcFirst.mTileY;j++)
    				{
    					//左右行代码规则，只有Y变化
    					int titlex = grcFirst.mTileX;
    					int titley = grcLast.mTileY - j;
    					String code = grcFirst.mScale +"_"+ titlex +"_"+ titley;
    					list.add(code);
    				}
    			}
    		}
    		
    		//查看矩阵内部是否还有矩阵
    		if(grcLast.mTileY - grcFirst.mTileY>1 && grcLast.mTileX - grcFirst.mTileX>1)
    		{
    			//如果有则获得左上角和右下角的值传入回调函数中继续遍历
    			GridRect grcFirst1 = new GridRect();
    			GridRect grcLast1 = new GridRect();
    			
    			grcFirst1.mScale = grcFirst.mScale;
    			grcFirst1.mTileX = grcFirst.mTileX+1;
    			grcFirst1.mTileY = grcFirst.mTileY+1;
    			
    			grcLast1.mScale = grcLast.mScale;
    			grcLast1.mTileX = grcLast.mTileX-1;
    			grcLast1.mTileY = grcLast.mTileY-1;
    			
    			list.addAll(getRectList(grcFirst1,grcLast1));
    		}
    	}
    	else
    	{
    		
    		if(grcFirst.mTileY == grcLast.mTileY)
    		{
    			//如果只有一行（包涵情况:仅有一个图片）
    			for(int j=0;j<grcLast.mTileX-grcFirst.mTileX+1;j++)
    			{
    				//代码规则，只有x变化
    				int titlex = grcFirst.mTileX + j;
    				int titley = grcFirst.mTileY;
    				String code = grcFirst.mScale +"_"+ titlex +"_"+ titley;
    				list.add(code);
    			}
    		}
    		else
    		{
    			//如果只有一列
				for(int j=0;j<grcLast.mTileY-grcFirst.mTileY+1;j++)
				{
					//左右行代码规则，只有Y变化
					int titlex = grcLast.mTileX;
					int titley = grcFirst.mTileY + j;
					String code = grcFirst.mScale +"_"+ titlex +"_"+ titley;
					list.add(code);
				}
    		}
    	}
    	
    	return list;
    }
    
    /**
     * 计算手机所需要的bitmap的个数
     * @return
     */
    public static int getBitmapNum(int bufWidth,int bufHeight)
    {
        int withnum = 1;
        int heightnum = 1;
        while(withnum*256<bufWidth)
        {
        	withnum++;
        }
        while(heightnum*256<bufHeight)
        {
        	heightnum++;
        }
        return withnum * heightnum;
    }
    
    /**
     * 根据地图名称获取地图的级别
     * @param code
     * @return
     */
    public static String getScale(String code)
    {
    	if(null != code && code.split("_").length>0)
    	{
    		return code.split("_")[0];
    	}
    	return null;
    }
    
    /**
     * 根据宽、高、默认图片，生成一个Bitmap背景图片为传进的默认图片
     * @param width
     * @param height
     * @param src
     * @return
     */
    public static Bitmap getTransparentBitmap(int width,int height,Bitmap src) 
	{
		int bmWidth = width / src.getWidth() ;
		int bmHeight = height / src.getHeight();
		
		Bitmap bitmap = Bitmap.createBitmap(bmWidth * src.getWidth(), bmHeight * src.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		for(int i = 0; i<bmHeight;i++)
		{
			for(int j=0;j<bmWidth;j++)
			{
				canvas.drawBitmap(src, j * src.getWidth(), i * src.getHeight(), null);
			}
		}
		
		return bitmap;
	}
    
    /**
     * 根据两点的坐标，求两点的距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double getPointDistance(float x1,float y1,float x2,float y2)
    {
    	double temp_A, temp_B;
    	temp_A = x1>x2 ? (x1-x2) : (x2-x1);  // 横向距离 (取正数，因为边长不能是负数)
    	temp_B = y1>y2 ? (y1-y2) : (y2-y1);  // 竖向距离 (取正数，因为边长不能是负数)
    	return java.lang.Math.sqrt(temp_A*temp_A + temp_B*temp_B);  // 计算
    }
    
    /** 
     * 两点的距离 
     */  
    public static float spacing(MotionEvent event) 
    {  
        float x = event.getX(0) - event.getX(1);  
        float y = event.getY(0) - event.getY(1);  
        return FloatMath.sqrt(x * x + y * y);  
    }  
    /** 
     * 两点的中点 
     */  
    public static PointF midPoint(PointF point, MotionEvent event) 
    {  
        float x = event.getX(0) + event.getX(1);  
        float y = event.getY(0) + event.getY(1);  
        point.set(x / 2, y / 2);  
        return point;
    }
}
