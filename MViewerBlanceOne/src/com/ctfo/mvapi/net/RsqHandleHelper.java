package com.ctfo.mvapi.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


/**
 * 请求处理辅助类
 * 
 * @author gyx
 * 
 */
class RsqHandleHelper {

	public INetConnectionListener iNetConnectionListener;
	
	private DefaultHttpClient connection = null;
	
	public BaseResponse getResponseImpl(ControlRunnable currentThread, BaseHttpRequest request,
			INetStateListener stateReceiver) {
		
		return getResponseImpl(currentThread, request, stateReceiver, false);
	}
	public BaseResponse getResponseImpl(ControlRunnable currentThread, BaseHttpRequest request,
			INetStateListener stateReceiver, boolean httpsFlag) {

//		if(httpsFlag == true) {
//			return getResponseByHttpsImpl(currentThread, request, stateReceiver);
//		}
		
		BaseHttpResponse httpResponse = null;
		HttpUriRequest httpUriRequest = null;
		HttpResponse response = null;

		try {
			// 设置连接
			connection = connectServer(currentThread, request, stateReceiver);
			// 传递参数
			httpUriRequest = buildAndSendRsq(connection, currentThread, request,
					stateReceiver);
			// 执行，得到返回值
			response = connection.execute(httpUriRequest);
			// 解析返回结果
			BaseResponse rsp = recvAndParseRsp(response, currentThread, request,
					stateReceiver);
			if (rsp instanceof ErrorResponse) {
				return rsp;
			}
			httpResponse = (BaseHttpResponse) rsp;
			return httpResponse;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			ErrorResponse err = new ErrorResponse(
					ErrorResponse.ERROR_PARAM_INVALID, e.getMessage());
			return err;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			ErrorResponse err = new ErrorResponse(
					ErrorResponse.ERROR_PARAM_INVALID, e.getMessage());
			return err;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// 切换连接方式，返回false一般表明之前有切换尝试过，
			// 仍失败应该是手机当前没有可用连接,故返回错误回复.

			ErrorResponse err = new ErrorResponse(
					ErrorResponse.ERROR_NET_NO_CONNECTION);
			if (stateReceiver != null) {
				stateReceiver.onNetError(request, currentThread, err);
			}
			return err;

		} catch (SocketTimeoutException e) {
			e.printStackTrace();

			ErrorResponse err = new ErrorResponse(
					ErrorResponse.ERROR_NET_TIMEOUT, ErrorResponse.ERROR_DESC_NET);
			if (stateReceiver != null) {
				stateReceiver.onNetError(request, currentThread, err);
			}
			return err;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();

			ErrorResponse err = new ErrorResponse(
					ErrorResponse.ERROR_NET_TIMEOUT, ErrorResponse.ERROR_DESC_NET);
			if (stateReceiver != null) {
				stateReceiver.onNetError(request, currentThread, err);
			}
			return err;
		} catch (SocketException e) {
			e.printStackTrace();
			// 切换连接方式，返回false一般表明之前有切换尝试过，
			// 仍失败应该是手机当前没有可用连接,故返回错误回复.

			ErrorResponse err = new ErrorResponse(
					ErrorResponse.ERROR_NET_TIMEOUT, ErrorResponse.ERROR_DESC_NET);
			if (stateReceiver != null) {
				stateReceiver.onNetError(request, currentThread, err);
			}
			return err;

		} catch (IOException e) {
			e.printStackTrace();
			// 切换连接方式，返回false一般表明之前有切换尝试过，
			// 仍失败应该是手机当前没有可用连接,故返回错误回复.

			if (e instanceof UnknownHostException) {

				ErrorResponse err = new ErrorResponse(
						ErrorResponse.ERROR_NET_DISCONNECTED);
				if (stateReceiver != null) {
					stateReceiver.onNetError(request, currentThread, err);
				}
				return err;

			} else {

				ErrorResponse err = new ErrorResponse(
						ErrorResponse.ERROR_NET_DISCONNECTED);
				if (stateReceiver != null) {
					stateReceiver.onNetError(request, currentThread, err);
				}
				return err;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorResponse err = new ErrorResponse(
					ErrorResponse.ERROR_NET_DISCONNECTED);
			if (stateReceiver != null) {
				stateReceiver.onNetError(request, currentThread, err);
			}
			return err;
		} finally {
			closeConnection();
		}
	}

//	public BaseResponse getResponseByHttpsImpl(ControlRunnable currentThread, BaseHttpRequest request,
//			INetStateListener stateReceiver) {
//		
//		HttpsURLConnection urlCon = null;
//		OutputStream outStream = null;
//		
//		try {
//			
//			if (stateReceiver != null) {
//				stateReceiver.onStartConnect(request, currentThread);
//			}
//
//			urlCon = (HttpsURLConnection) new URL(request.getAbsoluteURI())
//					.openConnection();
//			
//			((HttpsURLConnection) urlCon).setRequestMethod("POST");
//			urlCon.setDoOutput(true);
//			urlCon.setDoInput(true);
//			urlCon.connect();
//
//			if (stateReceiver != null) {
//				stateReceiver.onConnected(request, currentThread);
//			}
//			
//			// 把封装好的实体数据发送到输出流
//			outStream = urlCon.getOutputStream();
//			
//			if (stateReceiver != null) {
//				stateReceiver.onStartSend(request, currentThread, -1);
//			}
//			
//			request.fillOutputStream(currentThread, outStream, stateReceiver);
//			outStream.flush();
//			
//			if (stateReceiver != null) {
//				stateReceiver.onSendFinish(request, currentThread);
//			}
//			
//			// 解析返回结果
//			BaseResponse rsp = recvAndParseRsp(urlCon.getInputStream(), currentThread, request, stateReceiver);
//			
//			if (rsp instanceof ErrorResponse) {
//				return rsp;
//			}
//			
//			return (BaseHttpResponse) rsp;
//			
//		} catch (MalformedURLException e) {
//
//			e.printStackTrace();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//			
//		} finally {
//			if(urlCon != null)
//				urlCon.disconnect();
//
//				try {
//					if(outStream != null)
//						outStream.close();
//					
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//		}
//
//		
//		
//		return null;
//	}
	/**
	 * 建立网络连接
	 * @param currentThread 请求cookie
	 * @param request 请求
	 * @param stateReceiver 状态接收器
	 * @return DefaultHttpClient
	 * @throws IOException
	 */
	private DefaultHttpClient connectServer(ControlRunnable currentThread,
			BaseHttpRequest request, INetStateListener stateReceiver)
			throws Exception {
		
		DefaultHttpClient connection = null;

		if (stateReceiver != null) {
			stateReceiver.onStartConnect(request, currentThread);
		}

		HttpParams my_httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(my_httpParams, request
				.getConnectionTimeout());
		HttpConnectionParams.setSoTimeout(my_httpParams, request
				.getConnectionTimeout());

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		schemeRegistry.register(new Scheme("https", sf, 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(my_httpParams, schemeRegistry);
		
		connection = new DefaultHttpClient(cm, my_httpParams);
		

		// 如果不是WIFI连接
		if (ClientSession.getInstance().getNetConnectionListener() != null &&
				!ClientSession.getInstance().getNetConnectionListener().getConnectivityManager().getNetworkInfo(1)
				.isConnected()) {
			APN currentAPN = ClientSession.getInstance().getNetConnectionListener().getCurrentAPN();
			if (currentAPN != null && currentAPN.proxy != null && !currentAPN.proxy.equals("")) {

				HttpHost proxy = new HttpHost(currentAPN.proxy, currentAPN.port);
				connection.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		}

		if (stateReceiver != null) {
			stateReceiver.onConnected(request, currentThread);
		}
		
		connection.getCookieSpecs().register("easy", csf);
		connection.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

		return connection;
	}
	
	 // customer cookie policy, ignore cookie check 
	 CookieSpecFactory csf = new CookieSpecFactory() {
	     public CookieSpec newInstance(HttpParams params) {
	         return new BrowserCompatSpec() {   
	             @Override
	             public void validate(Cookie cookie, CookieOrigin origin)
	             throws MalformedCookieException {
	                 // Oh, I am easy
	             }
	         };
	     }
	 };
	 
	/**
	 * 构建并传递请求参数
	 * @param connection 网络连接
	 * @param currentThread	请求cookie
	 * @param request 请求
	 * @param stateReceiver 状态接收器
	 * @return HttpUriRequest
	 * @throws IOException
	 */
	private HttpUriRequest buildAndSendRsq(DefaultHttpClient connection,
			ControlRunnable currentThread, BaseHttpRequest request,
			INetStateListener stateReceiver) throws IOException {

		HttpUriRequest httpUriRequest = null;

		if (stateReceiver != null) {
			stateReceiver.onStartSend(request, currentThread, -1);
		}

		if (request.getMethod() == BaseHttpRequest.GET) {
			httpUriRequest = new HttpGet(request.getAbsoluteURI());
			
			if(request.isNeedExtHeader()) {
				if(request.isNeedGZip()) {
					httpUriRequest.addHeader("Accept-Encoding", "gzip");
				}
	
				if(request.isNeedAuthorization()
						&& ClientSession.getInstance().getUserName() != null
						&& ClientSession.getInstance().getPassword() != null) {
					httpUriRequest.addHeader("Authorization", "Basic " 
							+ CodeUtil.encode(ClientSession.getInstance().getUserName()
									+":"+ClientSession.getInstance().getPassword()));
				}
				
				if(ClientSession.getInstance().getSessionId() != null 
						&& !ClientSession.getInstance().getSessionId().equals("")) {
					httpUriRequest.addHeader("SessionId", ClientSession.getInstance().getSessionId());
				}
				
				if(request.isNeedUserAgent())
					httpUriRequest.addHeader("User-Agent", ClientSession.sHeaderUserAgent);
				
				if(ClientSession.getInstance().getToken() != null 
						&& !ClientSession.getInstance().getToken().equals("")) {
					httpUriRequest.addHeader("token", ClientSession.getInstance().getToken());
				}
			
			}
			
			String[][] aryHeaders = request.getExtraHeaders();
			if (aryHeaders != null) {
				int length = aryHeaders.length;
				if (aryHeaders != null) {
					for (int i = 0; i < length; ++i) {
						if (aryHeaders[i].length != 2) {
							throw new IllegalArgumentException(
									"aryheader must be 2 columns!");
						}

						httpUriRequest.addHeader(aryHeaders[i][0], aryHeaders[i][1]);
							
					}
				}
				
			}
			
		} else {

			HttpPost httpPost = new HttpPost(request.getAbsoluteURI());
			AbstractHttpEntity entity = null;
			
			if(request.getPostParams() == null) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				request.fillOutputStream(currentThread, outputStream, stateReceiver);
	
				entity = new CustomMultiPartEntity(outputStream.toByteArray(), request.getProgressListener());
				
				if(request.getProgressListener() != null)
					request.getProgressListener().setContentLength((int)entity.getContentLength());
	
			} else {
				entity = new UrlEncodedFormEntity(request.getPostParams(), request.getEncoding());
			}
			httpPost.setEntity(entity);
			
			if(request.isNeedExtHeader()) {
					
				if (request.isNeedGZip())
					httpPost.addHeader("Accept-Encoding", "gzip");
	
				if (ClientSession.getInstance().getUserName() != null
						&& ClientSession.getInstance().getPassword() != null) {
					httpPost.addHeader("Authorization", "Basic " 
							+ CodeUtil.encode(ClientSession.getInstance().getUserName()
									+":"+ClientSession.getInstance().getPassword()));
				}
				
				if(ClientSession.getInstance().getSessionId() != null 
						&& !ClientSession.getInstance().getSessionId().equals("")) {
					httpPost.addHeader("SessionId", ClientSession.getInstance().getSessionId());
				}
				
				if(request.isNeedUserAgent())
					httpPost.addHeader("User-Agent", ClientSession.sHeaderUserAgent);
				
				if(ClientSession.getInstance().getToken() != null 
						&& !ClientSession.getInstance().getToken().equals("")) {
					httpPost.addHeader("token", ClientSession.getInstance().getToken());
				}
			}
			
			String[][] aryHeaders = request.getExtraHeaders();
			if (aryHeaders != null) {
				int length = aryHeaders.length;
				if (aryHeaders != null) {
					for (int i = 0; i < length; ++i) {
						if (aryHeaders[i].length != 2) {
							throw new IllegalArgumentException(
									"aryheader must be 2 columns!");
						}
						httpPost.addHeader(aryHeaders[i][0], aryHeaders[i][1]);

					}
				}
			}
			
			httpUriRequest = httpPost;
		}

		if (stateReceiver != null) {
			stateReceiver.onSendFinish(request, currentThread);
		}

		return httpUriRequest;
	}

	/**
	 * 接收回复内容并解析
	 * @param response 网络回复
	 * @param currentThread 请求Cookie
	 * @param request 请求
	 * @param stateReceiver 状态接收器
	 * @return 回复内容
	 * @throws IOException
	 */
	private BaseResponse recvAndParseRsp(HttpResponse response,
			ControlRunnable currentThread, BaseHttpRequest request,
			INetStateListener stateReceiver) throws IOException {
		
		BaseHttpResponse httpResponse = null;

		int code = response.getStatusLine().getStatusCode();
		if (code == HttpStatus.SC_OK) {

			request.headers = response.getAllHeaders();

			int len = (int) response.getEntity().getContentLength();

			// 针对当前协议，返回内容长度不应该为0,故出现此情况返回错误
			if (len <= 0) {
				return new ErrorResponse(ErrorResponse.ERROR_SERVER);
			}

			if (stateReceiver != null) {
				stateReceiver.onStartRecv(request, currentThread, len);
			}

			InputStream instream = response.getEntity().getContent();
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");

			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {

				instream = new GZIPInputStream(instream);
			}

			httpResponse = request.createResponse();
			ErrorResponse err = httpResponse.parseInputStream(currentThread,
					request, instream, len, stateReceiver);

			if (stateReceiver != null) {
				stateReceiver.onRecvFinish(request, currentThread);
			}

			if (err != null) {
				httpResponse = null;
				return err;
			}
			return httpResponse;
		} else {
			ErrorResponse errorResponse = new ErrorResponse(ErrorResponse.ERROR_SERVER);
			if (stateReceiver != null) {
				stateReceiver.onNetError(request, currentThread, errorResponse);
			}
			return errorResponse;
		}
	}

	/**
	 * 接收回复内容并解析
	 * @param response 网络回复
	 * @param currentThread 请求Cookie
	 * @param request 请求
	 * @param stateReceiver 状态接收器
	 * @return 回复内容
	 * @throws IOException 
	 * @throws IOException
	 */
	public BaseResponse recvAndParseRsp(InputStream instream,
			ControlRunnable currentThread, BaseHttpRequest request,
			INetStateListener stateReceiver) throws IOException {
		
		BaseHttpResponse httpResponse = null;

		if (stateReceiver != null) {
			stateReceiver.onStartRecv(request, currentThread, 0);
		}
		
		httpResponse = request.createResponse();
		ErrorResponse err = httpResponse.parseInputStream(currentThread,
				request, instream, 0, stateReceiver);

		if (stateReceiver != null) {
			stateReceiver.onRecvFinish(request, currentThread);
		}

		if (err != null) {
			httpResponse = null;
			return err;
		}
		return httpResponse;
		
	}
	
	/**
	 * 关闭网络连接
	 * @param connection
	 */
	public void closeConnection() {
		try {
			if (connection != null)
				connection.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
