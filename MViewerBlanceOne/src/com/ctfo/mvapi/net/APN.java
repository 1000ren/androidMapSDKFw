package com.ctfo.mvapi.net;

/**
 * 接入点信息类
 * @author gyx
 *
 */
public class APN {
	
	public String apn;
	public String name;
	public int port;
	public String proxy;

	public String getApn() {
		return this.apn;
	}

	public String getName() {
		return this.name;
	}

	public int getPort() {
		return this.port;
	}

	public String getProxy() {
		return this.proxy;
	}
}

