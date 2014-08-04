package com.ctfo.mvapi.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * http回复包接口
 * 
 * @author gyx
 * 
 */
public interface BaseHttpResponse extends BaseResponse {

	/**
	 * 从服务器端获取的http输入流解析接口
	 * 
	 * @param currentThread: 对应执行线程
	 * @param request：该回复对应的请求
	 * @param inputStream：回复输入流
	 * @param len: 输入流长度，为-1时表示无法获取输入流长度
	 * @param stateRecevier: 状态接收器
	 * @return: 如果解析成功返回null,否则返回相应错误回复包
	 * @throws IOException
	 */
	public ErrorResponse parseInputStream(ControlRunnable currentThread,
			BaseHttpRequest request, InputStream inputStream, int len,
			INetStateListener stateReceiver) throws IOException;
	
	/**
	 * 从服务器端获取的http输入流解析接口
	 * 
	 * @param currentThread: 对应执行线程
	 * @param request：该回复对应的请求
	 * @param responseContent：回复内容
	 * @param len: 输入流长度，为-1时表示无法获取输入流长度
	 * @param stateRecevier: 状态接收器
	 * @return: 如果解析成功返回null,否则返回相应错误回复包
	 * @throws IOException
	 */
	public ErrorResponse parseInputStream(ControlRunnable currentThread,
			BaseHttpRequest request, String responseContent, int len,
			INetStateListener stateReceiver) throws IOException;

	/**
	 * 获得输入流
	 * 
	 * @return
	 */
	public InputStream getInputStream();
	
	public String getResponseContent();
	
	public int getLength();
}
