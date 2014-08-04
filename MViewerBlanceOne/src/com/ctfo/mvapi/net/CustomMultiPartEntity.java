package com.ctfo.mvapi.net;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.entity.ByteArrayEntity;
 
public class CustomMultiPartEntity extends ByteArrayEntity
{
 
	private final ProgressListener listener;
 
	public CustomMultiPartEntity(byte[] content, final ProgressListener listener)
	{
		super(content);
		this.listener = listener;
	}
 
 
	@Override
	public void writeTo(final OutputStream outstream) throws IOException
	{
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}
 
	public static interface ProgressListener
	{
		void transferred(long num);
		
		void setContentLength(int length);
	}
 
	public static class CountingOutputStream extends FilterOutputStream
	{
 
		private final ProgressListener listener;
		private long transferred;
 
		public CountingOutputStream(final OutputStream out, final ProgressListener listener)
		{
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}
		
		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException
		{
			
			if(this.listener != null) {
				
			
				if (buffer == null) {
		            throw new NullPointerException("buffer is null"); //$NON-NLS-1$
		        }
		        if ((offset | count) < 0 || count > buffer.length - offset) {
		            throw new ArrayIndexOutOfBoundsException("buffer is IndexOutOfBounds"); //$NON-NLS-1$
		        }
		        
		        int k = 1;
		        // END android-changed
		        for (int i = 0; i < count; i++) {
		            // Call write() instead of out.write() since subclasses could
		            // override the write() method.
//		            write(buffer[offset + i]);
		        	out.write(buffer[offset + i]);
		        	this.transferred++;
		        	if(this.transferred == k*10240) {
		        		
		        		this.listener.transferred(this.transferred);
		        		k++;
		        		
		        	}
		        }
			} else {
				out.write(buffer, offset, count);
			}
		}
		
		@Override
		public void write(int b) throws IOException
		{
			out.write(b);
			
			this.transferred++;
			this.listener.transferred(this.transferred);
			
		}
	}
}