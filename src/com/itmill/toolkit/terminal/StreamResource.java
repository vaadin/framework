/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.terminal;

import java.io.InputStream;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.service.FileTypeResolver;

/** Stream resource is a resource provided to the client directly
 * by the application. The strean resource is fetched from URI
 * that is most often in the context of the application or window.
 * The resource is automatically registered to window in creation.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class StreamResource implements ApplicationResource {

	/** Source streamthe downloaded content is fetched from */
	private StreamSource streamSource = null;

	/** Explicit mime-type */
	private String MIMEType = null;

	/** Filename */
	private String filename;

	/** Application */
	private Application application;

	/** Default buffer size for this stream resource */
	private int bufferSize = 0;

	/** Default cache time for this stream resource */
	private long cacheTime = DEFAULT_CACHETIME;	

	/** Create new stream resource for downloading from stream. */
	public StreamResource(
		StreamSource streamSource,
		String filename,
		Application application) {

		this.application = application;
		setFilename(filename);
		setStreamSource(streamSource);

		// Register to application
		application.addResource(this);

	}

	public String getMIMEType() {
		if (MIMEType != null)
			return MIMEType;
		return FileTypeResolver.getMIMEType(filename);
	}

	/** Set the mime type of the resource */
	public void setMIMEType(String MIMEType) {
		this.MIMEType = MIMEType;
	}

	/** Returns the source for this StreamResource.
	 *  StreamSource is queried when the resource is about to be streamed 
	 *  to the client.
	 * @return Source of the StreamResource.
	 */
	public StreamSource getStreamSource() {
		return streamSource;
	}

	/** Sets the source for this StreamResource.
	 *  StreamSource is queried when the resource is about to be streamed 
	 *  to the client.
	 * @param streamSource The source to set
	 */
	public void setStreamSource(StreamSource streamSource) {
		this.streamSource = streamSource;
	}

	/** Returns the filename.
	 * @return String
	 */
	public String getFilename() {
		return filename;
	}

	/** Sets the filename.
	 * @param filename The filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @see com.itmill.toolkit.terminal.ApplicationResource#getApplication()
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * @see com.itmill.toolkit.terminal.ApplicationResource#getStream()
	 */
	public DownloadStream getStream() {
		StreamSource ss = getStreamSource();
		if (ss == null)
			return null;
		DownloadStream ds = new DownloadStream(ss.getStream(), getMIMEType(), getFilename());
		ds.setBufferSize(getBufferSize());
		ds.setCacheTime(cacheTime);
		return ds;
	}

	/** Interface implemented by the source of a StreamResource.
	 * @author IT Mill Ltd.
     * @version @VERSION@
     * @since 3.0
	 */
	public interface StreamSource {

		/** Return new input stream that is used for reading
		 * the resource */
		public InputStream getStream();
	}

	/* documented in superclass */
	public int getBufferSize() {
		return bufferSize;
	}

	/** Set the size of the download buffer used for this resource.
	 * @param bufferSize The size of the buffer in bytes.
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/* documented in superclass */
	public long getCacheTime() {
		return cacheTime;
	}

	/** Set lenght of cache expiracy time.
	 * 
	 * <p>This gives the adapter the possibility cache streams sent to the
	 * client. The caching may be made in adapter or at the client if the 
	 * client supports caching. Zero or negavive value disbales the 
	 * caching of this stream.</p>
	 * 
	 * @param cacheTime The cache time in milliseconds.
	 * 
	 */
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

}
