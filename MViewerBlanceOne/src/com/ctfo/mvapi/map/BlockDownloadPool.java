package com.ctfo.mvapi.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.ctfo.mvapi.MVApi;
import com.ctfo.mvapi.entities.GridRect;
import com.ctfo.mvapi.utils.FileUtil;


/**
 * @author fangwei
 *
 * Block文件的下载任务管理池
 */
public class BlockDownloadPool extends Thread
{
	public static BlockDownloadPool mthis = null;
	// 需要下载的block
	//List<GridRect> grclist
	private List<GridRect> mBlockList = new ArrayList<GridRect>();
	// 最大线程数
	private static final int THREAD_MAX = 3;
	// 下载线程
	private BlockDownloadThread[] mBlockDownloadThread = new BlockDownloadThread[THREAD_MAX];
	// 最大等待任务数
	//private static final int TASK_WAIT_MAX = 100;
	// 当前等待下载的任务
	private LinkedList<BlockDownloadTask> mTaskLinkedList = new LinkedList<BlockDownloadTask>();
	// 当前正在下载的任务
	private HashMap<String, BlockDownloadTask> mTaskDownloadingHashMap = new HashMap<String, BlockDownloadTask>();
	// 最近完成的任务数
	private static final int TASK_FINISH_MAX = 50;
	// 最近完成的任务
	private LinkedList<String> mFinishTaskList = new LinkedList<String>();
	
 private URL url=null;

	public BlockDownloadPool()
	{
		mthis = this;
	}

	public void updateBlockList( List<GridRect> blocklist )
	{
		synchronized ( mBlockList )
		{
			mBlockList.clear();
			mBlockList.addAll(blocklist);
			// 更新下载任务
			updateTask();
		}
	}

	private void updateTask()
	{
		synchronized ( mBlockList )
		{
			synchronized ( mTaskLinkedList )
			{
				mTaskLinkedList.clear();
			}
			for (int i = 0; i < mBlockList.size(); i++)
			{
			    GridRect blockRect = mBlockList.get(i);
				 
				if ( blockRect == null )
				{
					continue;
				}
				BlockDownloadTask task = new BlockDownloadTask( blockRect );
				synchronized ( mTaskDownloadingHashMap )
				{
					if ( mTaskDownloadingHashMap.containsKey( task.id ) )
					{
						continue;
					}
				}
				synchronized ( mFinishTaskList )
				{
					if ( mFinishTaskList.contains( task.id ) )
					{
						continue;
					}
				}
				synchronized ( mTaskLinkedList )
				{
					mTaskLinkedList.addLast(task);
					/*while ( mTaskLinkedList.size() > TASK_WAIT_MAX )
					{
						mTaskLinkedList.pollLast();
					}*/
				}
			}
			mBlockList.clear();
		}
	}

	public BlockDownloadTask getTask()
	{
		synchronized ( mTaskLinkedList )
		{
			return mTaskLinkedList.poll();
		}
	}
	
	public synchronized void finishTask( int id, BlockDownloadTask task )
	{
		synchronized ( mFinishTaskList )
		{
			mFinishTaskList.addFirst( task.id );
			while ( mFinishTaskList.size() > TASK_FINISH_MAX )
			{
				mFinishTaskList.removeLast();
			}
		}
		synchronized ( mTaskDownloadingHashMap )
		{
			mTaskDownloadingHashMap.remove( task.id );
		}
		synchronized ( mBlockDownloadThread )
		{
			if ( id >= 0 && id < THREAD_MAX )
			{
				mBlockDownloadThread[id] = null;
			}
		}
	}

	public void run()
	{
		while ( true )
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			for (int i = 0; i < THREAD_MAX; i++)
			{
				if ( mBlockDownloadThread[i] == null )
				{
					BlockDownloadTask task = getTask();
					if ( task != null )
					{
						synchronized ( mTaskDownloadingHashMap )
						{
							mTaskDownloadingHashMap.put( task.id, task );
						}
						mBlockDownloadThread[i] = new BlockDownloadThread(i, task);
						mBlockDownloadThread[i].start();
						//MVApiLog.e( "startTask", "id="+i+";task="+task.id );
					}
				}
			}
		}
	}

	// 下载任务
	public class BlockDownloadTask
	{
		public String id;
		public GridRect block;

		public BlockDownloadTask( GridRect block )
		{
			this.id = block.mScale + "_"+ block.mTileY + "_" + block.mTileX;
			this.block = block;
		}
	}

	// 工作线程
	public class BlockDownloadThread extends Thread
	{
		public int mThdID = -1;
		private BlockDownloadTask mTask = null;
		private String mHttpUrl = null;

		public BlockDownloadThread( int id, BlockDownloadTask task )
		{
			mThdID = id;
			mTask = task;
			//http://59.108.127.194:8088/MCMDN/GetMCMDNData.py?block=10386.842
			//http://tileni.transmap.com.cn/17/49650/107896.png
			mHttpUrl = MVApi.getServerUrlSetting().strVectorMapUrl + 
					+ task.block.mScale +"/"+ 
					task.block.mTileY + "/" + 
					task.block.mTileX+".png";
		}

		public void run()
		{
//			boolean bFileOk = false;
			String filePath = checkFilePath( mTask );
			
		 String tile = mTask.block.mScale+"_"+ mTask.block.mTileX + "_" + mTask.block.mTileY;
//			String bitmapFilePath = MVApi.mTilePath + mTask.block.mScale + "/"+tile+".png";
			File fileDest = new File( filePath );
			if ( fileDest.exists() )
			{
				BlockDownloadPool.mthis.finishTask( mThdID, mTask );
				return;
			}
//			String filePath2 = bitmapFilePath+ "_tmp";
//			File file = new File( filePath2 );
//			try
//			{
//				file.createNewFile();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//				BlockDownloadPool.mthis.finishTask( mThdID, mTask );
//				return;
//			}

			
//		Bitmap	result = ImageGetFromHttp.downloadBitmap(mHttpUrl);  
//		
//		saveBitmapToSDCard(result, bitmapFilePath);
		
//			HttpURLConnection conn = null;
//			InputStream is = null;
//			FileOutputStream fos= null;
//			try
//			{
////				URL url = new URL(mHttpUrl);
//			
//			
//			}
//			catch (MalformedURLException e)
//			{
////				file.delete();
//				//file.renameTo( fileDest );
//				e.printStackTrace();
//			}
			downFile(mHttpUrl,MVApi.mTilePath+mTask.block.mScale + "/"  ,tile+".png");
			//MVApiLog.e( "DownloadMgrThd", "mThdID="+mThdID+";mTask="+mTask.id );
			//MVApiLog.e( "DownloadMgrThd", "filepath="+filePath );
			BlockDownloadPool.mthis.finishTask( mThdID, mTask );
		}

		
		
		/**
	  * 下载文件并写SD卡
	  * @param urlStr
	  * @param path
	  * @param fileName
	  * @return 0-success,-1-fail,1-existed
	  */
	 public int downFile(String urlStr,String path,String fileName){
	  InputStream inputStream= null;
	  try{
	   FileUtil fileUtil = new FileUtil();
	    inputStream = getInputStreamFromUrl(urlStr);

	    File resultFile = fileUtil.write2SDFromInput(path, fileName, inputStream);
	    if(resultFile == null)
	     return -1;
	   
	   
	  }catch(Exception e){
	   e.printStackTrace();
	  }finally{
	   try{
	    inputStream.close();
	   }catch(Exception e){
	    e.printStackTrace();
	   }
	   
	  }
	  return 0;
	 }
	 
	 public InputStream getInputStreamFromUrl(String urlStr) throws MalformedURLException,IOException{
	  url = new URL(urlStr);
	  HttpURLConnection urlCon =(HttpURLConnection)url.openConnection();
	  InputStream inputStream = urlCon.getInputStream();
	  return inputStream;
	  
	 }
		
		
		
   
		private String checkFilePath( BlockDownloadTask task )
		{
			String filePath = MVApi.mVectorPath + mTask.block.mScale + "00/";
			MVApi.checkFolderExist( filePath );

			String str = MCMDNCoordTrans.toHexString(mTask.block.mTileY/16);
		    filePath += str;
		    filePath += "/";
		    MVApi.checkFolderExist( filePath );

		    str = MCMDNCoordTrans.toHexString(mTask.block.mTileX/16);
		    filePath += str;
		    filePath += "/";
		    MVApi.checkFolderExist( filePath );

		    str = MCMDNCoordTrans.toHexString(mTask.block.mTileY/4);
		    filePath += str;
		    str = MCMDNCoordTrans.toHexString(mTask.block.mTileX/4);
		    filePath += str;
		    filePath += "/";
		    MVApi.checkFolderExist( filePath );

		    str = MCMDNCoordTrans.toHexString(mTask.block.mTileY);
		    filePath += str;
		    str = MCMDNCoordTrans.toHexString(mTask.block.mTileX);
		    filePath += str;
		    filePath += ".mm";
			return filePath;
		}
		
	}
	
	
	
}

