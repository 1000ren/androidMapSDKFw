package com.ctfo.mvapi.protocol.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ctfo.mvapi.net.BaseHttpRequest;
import com.ctfo.mvapi.net.BaseHttpResponse;
import com.ctfo.mvapi.net.ControlRunnable;
import com.ctfo.mvapi.net.ErrorResponse;
import com.ctfo.mvapi.net.INetStateListener;

public class GetVectorDataResponse implements BaseHttpResponse
{
	private InputStream inputStream = null;
	private ByteArrayOutputStream memoryStream = null;

	GetVectorDataResponse()
	{
	}

	@Override
	public ErrorResponse parseInputStream(ControlRunnable currentThread,
			BaseHttpRequest request, InputStream inputStream, int len,
			INetStateListener stateReceiver) throws IOException 
			{
		this.inputStream = inputStream;

		int step = 100;
		int count = 0;
		synchronized (this) {
			memoryStream = new ByteArrayOutputStream(len);
			int ch;
			while ((ch = inputStream.read()) != -1) {
				memoryStream.write(ch);
				++count;
				if ((count == step) && (stateReceiver != null)) {
					stateReceiver.onRecv(request, currentThread, count);
					count = 0;
				}
			}
			if ((count != 0) && (stateReceiver != null)) {
				stateReceiver.onRecv(request, currentThread, count);
			}
		}

		return null;
	}

	@Override
	public ErrorResponse parseInputStream(ControlRunnable currentThread,
			BaseHttpRequest request, String responseContent, int len,
			INetStateListener stateReceiver) throws IOException 
			{
		return null;
	}

	@Override
	public InputStream getInputStream()
	{
		return inputStream;
	}

	@Override
	public String getResponseContent()
	{
		return null;
	}

	@Override
	public int getLength()
	{
		return 0;
	}

	public final byte[] getBytes()
	{
		if (memoryStream == null)
		{
			throw new IllegalStateException("you can't call this method until getting response!");
		}
		return memoryStream.toByteArray();
	}
}
