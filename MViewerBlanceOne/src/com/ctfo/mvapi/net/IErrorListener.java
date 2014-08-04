package com.ctfo.mvapi.net;

/**
 * 错误处理接口
 * @author gyx
 *
 */

public interface IErrorListener {
	/**
	 * 错误处理接口 注：当errorResponse为null时表示没有错误，与服务器的整个交互完全顺利，
	 * 这里将没有错误当做一种特殊错误来处理，方便状态接收器能够获知整个交互过程，包括到最后成功.
	 * 
	 * @param errorResponse 错误回复
	 * @param request       对应请求
	 * @param currentThread 对应执行线程
	 */
	void onError(ErrorResponse errorResponse, BaseHttpRequest request,
			ControlRunnable currentThread);
}
