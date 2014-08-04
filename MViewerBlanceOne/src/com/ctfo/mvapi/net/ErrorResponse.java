package com.ctfo.mvapi.net;

/**
 * 错误回复包，所有与服务器交互后的错误均在此汇总， 
 * 包括网络错误，服务器错误，协议错误以及未知错误等
 * 
 * @author gyx
 * 
 */
public class ErrorResponse implements BaseResponse {
	
	public final static int ERROR_PARAM_INVALID           = 1000; // 请求地址参数格式错误
	public final static int ERROR_NET_NO_CONNECTION       = 1001; // 手机当前没有可用连接
	public final static int ERROR_NET_DISCONNECTED        = 1002; // 无法连接服务器或断开
	public final static int ERROR_NET_TIMEOUT             = 1003; // 网络连接超时
	public final static int ERROR_NULL_RESULT             = 1004; // 没有获取到任何结果
	public final static int ERROR_SERVER                  = 1005; // 服务器内部错误
	public final static int ERROR_PROTOTOL                = 1006; // 协议解析错误
	public final static int ERROR_THREAD                  = 1007; // 工作线程错误
	public final static int ERROR_UNKOWN                  = 1008; // 未知错误
	public final static int ERROR_INVALID_RESULT          = 1009; // 服务器无法获取有效结果
	public final static int ERROR_CLIENT_NET_DISCONNECTED = 1010;// 本地网络不可用

	public final static int ERROR_LOSE_PAR_APIUID = 1;	// 缺少参数：api_uid	检查是否传入api_uid
	public final static int ERROR_LOSE_PAR_TIME = 2;	// 缺少：time	检查是否传入time（时间戳）
	public final static int ERROR_LOSE_PAR_METHOD = 3;	// 缺少：method	检查是否传入method（要调用的api方法）
	public final static int ERROR_LOSE_PAR_SIGN = 4;	// 缺少：sign	检查是否传入sign（签名字符串）
	public final static int ERROR_NO_EXIST_APIUID = 5;	// 不存在此api_uid	检查api_uid是否正确
	public final static int ERROR_REQUEST_OVER_MAX = 6;	// 此api_uid的请求次数超过分配的最大请求次数	联系管理员
	public final static int ERROR_ILLEGAL_TIME = 7;	// 非法传值：time	检测（用户传过来的）time是否大于上一次time
	public final static int ERROR_TIME = 8;	// time值错误	timestamp 的值同服务器相比 误差允许在正负10分钟
	public final static int ERROR_SIGN = 9;	// 签名错误	检查sign是否合法
	public final static int ERROR_FORBIT_APIUID = 10;	// api_uid已经被禁止使用	请联系管理员
	public final static int ERROR_OVER_CON_MAX = 11;	// 超过允许的最大并发数	请联系管理员重新设置
	public final static int ERROR_SESSION = 12;	// username或sessionid的值无效	检查是sessionid是否是当前username的对应值，或者请联系管理员
	public final static int ERROR_LOGIN_OVERTIME = 13;	// 登录超时	sessionid过期，重新生成后再传
	
	
	public final static String ERROR_DESC_NET             = "网络错误,建议您检查网络连接后再试.";// 网络问题
	
	private int errorType = ERROR_UNKOWN;
	private String errorDesc = ERROR_DESC_NET;

	public ErrorResponse() {
		
	}
	
	public ErrorResponse(int type, String desc) {
		setError(type, desc);
	}

	public ErrorResponse(int type) {
		setError(type);
	}
	
	public final int getErrorType() {
		return errorType;
	}

	public final String getErrorDesc() {
		return errorDesc;
	}

	public final void setError(int type, String desc) {
		errorType = type;
		errorDesc = desc;
	}

	public final void setError(int type) {
		errorType = type;
	}

	
}
