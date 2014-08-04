package com.ctfo.mvapi.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;

/**
 * 解压工程配置文件到sd卡的工具类
 * @author fangwei
 *
 */
public class AssetUtil 
{
	/**
	 * 把指定的asset文件夹中的文件复制到内存当中，并解压文件
	 * @param context
	 * @param strPath 
	 * 			目标路径
	 * @param name
	 * 			需要复制的asset文件夹下文件的名字
	 */
	public static void setAssetFile(Context context,String strPath,String name)
	{
		//删除目标目录下所有文件
		File file = new File(strPath);
		Util.deleteAllFile( file );
		file.mkdirs();
		
        strPath += name+".zip";
        copyFile(name, strPath,context);
	}
	
	/**
	 * 把assets下的文件夹复制到内存中
	 * 
	 * @param assetsName 
	 * 				需要复制的文件的名字
	 * @param newPath 
	 * 				目标路径
	 * @param context
	 */
	private static void copyFile(String assetsName, String newPath,Context context)
	{
		FileOutputStream fs = null;
		try
		{
			int bytesum = 0;
			int byteread = 0;
			InputStream inStream = context.getClass().getResourceAsStream("/assets/"+assetsName); //读入原文件
			fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[1444];
			while((byteread = inStream.read(buffer)) != -1)
			{
				bytesum += byteread; //字节数 文件大小
				System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			
			//解压缩
			unzip(newPath,"");
			
			//删除复制到sd卡上的压缩包
			File file = new File(newPath);
			if(file.exists())
			{
				file.delete();
			}
		}
		catch (Exception e) 
		{
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if(null != fs)
				{
					fs.close();
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	} 
	
	/**
	 * 解压缩zip包
	 * 
	 * @param zipFilePath
	 *            zip文件路径
	 * @param targetPath
	 *            解压缩到的位置，如果为null或空字符串则默认解压缩到跟zip包同目录跟zip包同名的文件夹下
	 * @throws IOException
	 */
	public static void unzip(String zipFilePath, String targetPath)
			throws IOException 
	{
		OutputStream os = null;
		InputStream is = null;
		ZipFile zipFile = null;
		try 
		{
			zipFile = new ZipFile(zipFilePath);
			String directoryPath = "";
			if (null == targetPath || "".equals(targetPath)) 
			{
				directoryPath = zipFilePath.substring(0,
						zipFilePath.lastIndexOf("/"));
			} 
			else 
			{
				directoryPath = targetPath;
			}
			Enumeration<? extends ZipEntry> entryEnum = zipFile.entries();
			if (null != entryEnum) 
			{
				ZipEntry zipEntry = null;
				String tempPath = "";
				while (entryEnum.hasMoreElements()) 
				{
					tempPath = new String(directoryPath);
					zipEntry = (ZipEntry) entryEnum.nextElement();
					if (zipEntry.isDirectory()) 
					{
						continue;
					}
					tempPath = tempPath + File.separator + zipEntry.getName();
					System.out.println(tempPath);
					if (zipEntry.getSize() > 0) 
					{
						// 文件
						File targetFile = buildFile(tempPath, false);
						os = new BufferedOutputStream(new FileOutputStream(
								targetFile));
						is = zipFile.getInputStream(zipEntry);
						byte[] buffer = new byte[4096];
						int readLen = 0;
						while ((readLen = is.read(buffer, 0, 4096)) >= 0) 
						{
							os.write(buffer, 0, readLen);
						}

						os.flush();
						os.close();
					} 
					else 
					{
						// 空目录
						buildFile(directoryPath + File.separator
								+ zipEntry.getName(), true);
					}
				}
			}
		} 
		catch (IOException ex) 
		{
			throw ex;
		} 
		finally 
		{
			if (null != zipFile) 
			{
				zipFile = null;
			}
			if (null != is) 
			{
				is.close();
			}
			if (null != os) 
			{
				os.close();
			}
		}
	}
	
	/**
     * 生产文件 如果文件所在路径不存在则生成路径
     *
     * @param fileName
     *            文件名 带路径
     * @param isDirectory 是否为路径
     * @return
     */
    private static File buildFile(String fileName, boolean isDirectory) 
    {
        File target = new File(fileName);
        if (isDirectory) 
        {
            target.mkdirs();
        } 
        else 
        {
            if (!target.getParentFile().exists()) 
            {
                target.getParentFile().mkdirs();
                target = new File(target.getAbsolutePath());
            }
        }
        return target;
    } 
    
}
