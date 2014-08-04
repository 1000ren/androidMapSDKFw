package com.ctfo.mvapi.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;

import com.ctfo.mvapi.net.CustomMultiPartEntity.ProgressListener;

/**
 * http请求包抽象基类，提供构建特定http请求的基本接口
 * 
 * @author gyx
 * 
 */
public abstract class BaseHttpRequest {

	// 希望请求被发送的方式
	public static final int GET = 1;
	public static final int POST = 2;
	
	// 缓存方式--临时缓存
	public static final int CACHE_TEMP = 1;
	// 缓存方式--永久缓存
	public static final int CACHE_PERMANENT = 2;
	// 缓存方式
	private int cacheMethod = CACHE_TEMP;
	// 是否需要压缩
	protected boolean needGZIP = false;
	// http返回协议头
	public Header[] headers;
	// 请求地址
	private String absoluteURI;
	// 请求发送方式
	private int method = POST;
	// 连接超时时间, 默认为30秒
	private int connectionTimeout = 30000;
	
	public long contentLength = 0;
	// 是否需要扩展头信息
	private boolean needExtHeader = true;
	
	private ProgressListener progressListener;

	/**
	 * 设置请求资源绝对地址 格式为：http://www.xxx.com/xxxx/xxx.xx
	 * 
	 * @param absoluteURI
	 */
	public final void setAbsoluteURI(String absoluteURI) {
		this.absoluteURI = absoluteURI;
	}

	/**
	 * 设置请求方法
	 */
	public final void setMethod(int method) {
		this.method = method;
	}

	/**
	 * 获取请求主机地址接口 格式分两种: 1. IP地址，eg：192.168.12.12[:port] 2.
	 * 域名形式，eg：www.xxx.com[:port]
	 * 
	 * @return
	 */
	public final String getHost() {
		URLParser parser = URLParser.parse(absoluteURI);
		if (parser != null) {
			return parser.getServer();
		} else {
			return "";
		}
	}

	/**
	 * 获取请求资源相对地址接口 格式为：/xxxx/xxx.xx
	 * 
	 * @return
	 */
	public final String getRelativeURI() {
		URLParser parser = URLParser.parse(absoluteURI);
		if (parser != null) {
			return parser.getURL();
		} else {
			return "";
		}
	}

	/**
	 * 获取请求资源的绝对地址接口 格式为：http://www.xxx.com/xxxx/xxx.xx
	 * 
	 * @return
	 */
	public final String getAbsoluteURI() {
		return absoluteURI;
	}

	/**
	 * 创建该请求特定回复接口
	 * 
	 * @return
	 */
	public abstract BaseHttpResponse createResponse();

	/**
	 * 获取请求发送方式接口，默认返回POST方法 暂时只提供GET,POST两种方式
	 * 
	 * @return
	 */
	public int getMethod() {
		return method;
	}

	/**
	 * 获取请求附加头接口，默认返回null 返回二维数组应为2列，第一列为头属性名，第二列为头属性值。 
	 * 属性名及值应符合http协议的标准头格式，eg:
	 * 属性名：Content-Type 属性值：text/html;charset=utf-8
	 * 
	 * @return
	 */
	public String[][] getExtraHeaders() {
		return null;
	}
	
	/**
	 * 获取POST参数集
	 * @return
	 */
	public List<NameValuePair> getPostParams() {
		return null;
	}
	
	/**
	 * 获取字符编码
	 * @return
	 */
	public String getEncoding() {
		return "GBK";
	}
	
	/**
	 * 获取字符集编码
	 * @return
	 */
	public String getCharset() {
		return "GBK";
	}

	/**
	 * 填充待发送的请求内容接口，默认不填充任何内容
	 * 
	 * @param currentThread: 对应执行线程
	 * @param output: http输出流
	 * @param stateReceiver: 状态接收器
	 * @throws IOException
	 */
	public void fillOutputStream(ControlRunnable currentThread, OutputStream output,
			INetStateListener stateReceiver) throws IOException {
	}

	/**
	 * 是否需要缓存本请求回复接口
	 * 0: 只从网络取
	 * 1：从缓存读取，未取到从网络取，取到缓存
	 * 2：先从网络读取，未取到从缓存读取，取到缓存
	 * 3: 只从缓存取
	 */
	public int needCacheResponse() {
		return 0;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public boolean isNeedGZip() {
		return needGZIP;
	}

	public int getCacheMethod() {
		return cacheMethod;
	}

	public void setCacheMethod(int cacheMethod) {
		this.cacheMethod = cacheMethod;
	}


	public boolean isNeedExtHeader() {
		return needExtHeader;
	}

	public boolean isNeedUserAgent() {
		return true;
	}
	
	public boolean isNeedAuthorization() {
		return true;
	}
	
	public void setNeedExtHeader(boolean needExtHeader) {
		this.needExtHeader = needExtHeader;
	}

	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}
	
	// 上传错误信息
	public void uploadError(ErrorResponse errorResponse) {
		
	}
}
