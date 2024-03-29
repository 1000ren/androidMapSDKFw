/**
		* FileUtil.java V1.0 2014-7-29 上午12:09:43
		*
		* Copyright Talkweb Information System Co. ,Ltd. All rights reserved.
		*
		* Modification history(By Time Reason):
		*
		* Description:
		*/

		package com.ctfo.mvapi.utils;

		import java.io.File;
		import java.io.FileOutputStream;
		import java.io.IOException;
		import java.io.InputStream;
		import java.io.OutputStream;

		import android.os.Environment;

		public class FileUtil{
		 private String SDPATH;
		 
		 public String getSDPATH(){
		  return SDPATH;
		 }
		 
		 public FileUtil(){
		  SDPATH= Environment.getExternalStorageDirectory()+"/";
		 }
		 
		 public File createSDFile(String fileName) throws IOException{
		  File file = new File(fileName);
		  file.createNewFile();
		  return file;
		 }
		 public File createSDDir(String dirName) {
		  File dir = new File(dirName);
		  dir.mkdir();
		  return dir;
		 }
		 public boolean isFileExist(String fileName){
		  File file = new File(fileName);
		  return file.exists();
		 }
		 public File write2SDFromInput(String path,String fileName,InputStream input){
		  File file = null;
		  OutputStream output = null;
		  try{
		   createSDDir(path);
		   file = createSDFile(path+fileName);
		   output = new FileOutputStream(file); 
		   byte buffer[] = new byte[1024];
		   while((input.read(buffer))!=-1){
		    output.write(buffer);
		   }
		   output.flush();
		  }catch(Exception e){
		   e.printStackTrace();
		  }finally{
		   try{
		    output.close();
		   }
		   catch(Exception e){
		    e.printStackTrace();
		   }
		  }
		  
		  return file;
		 }
		  
		 public static void makeRootDirectory(String filePath) {  
       File file = null;  
       try {  
           file = new File(filePath);  
           if (!file.exists()) {  
               file.mkdir();  
           }  
       } catch (Exception e) {  
 
       }  
   }  
		 
		}
