package com.ctfo.mvapi.net;


/**
 * 网络状态接收接口
 * @author gyx
 *
 */
public interface INetStateListener {
	/**
	 * 开始网络连接
	 * @param request
	 * @param currentThread 对应执行线程
	 */
	public void onStartConnect(BaseHttpRequest request, ControlRunnable currentThread);
	/**
	 * 网络已连接
	 * @param request
	 * @param currentThread 对应执行线程
	 */
	public void onConnected(BaseHttpRequest request, ControlRunnable currentThread);
	/**
	 * 开始发送请求
	 * @param request
	 * @param currentThread 对应执行线程
	 * @param totalLen
	 */
	public void onStartSend(BaseHttpRequest request, ControlRunnable currentThread, int totalLen);
	/**
	 * 正在发送请求
	 * @param request
	 * @param currentThread 对应执行线程
	 * @param len
	 */
	public void onSend(BaseHttpRequest request, ControlRunnable currentThread, int len);
	/**
	 * 请求发送完成
	 * @param request
	 * @param currentThread 对应执行线程
	 */
	public void onSendFinish(BaseHttpRequest request, ControlRunnable currentThread);
	/**
	 * 开始接收回复
	 * @param request
	 * @param currentThread 对应执行线程
	 * @param totalLen
	 */
	public void onStartRecv(BaseHttpRequest request, ControlRunnable currentThread, int totalLen);
	/**
	 * 正在接收回复
	 * @param request
	 * @param currentThread 对应执行线程
	 * @param len
	 */
	public void onRecv(BaseHttpRequest request, ControlRunnable currentThread, int len);
	/**
	 * 接收完成
	 * @param request
	 * @param currentThread 对应执行线程
	 */
	public void onRecvFinish(BaseHttpRequest request, ControlRunnable currentThread);
	/**
	 * 网络错误
	 * @param request
	 * @param currentThread 对应执行线程
	 * @param errorInfo
	 */
	public void onNetError(BaseHttpRequest request, ControlRunnable currentThread, ErrorResponse errorInfo);
	/**
	 * 取消请求
	 * @param request
	 * @param currentThread 对应执行线程
	 */
	public void onCancel(BaseHttpRequest request, ControlRunnable currentThread);
	
}
