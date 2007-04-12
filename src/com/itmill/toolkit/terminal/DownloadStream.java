/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license.pdf. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see licensing-guidelines.html

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** 
 * Downloadable stream.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class DownloadStream {

	/** 
	 * Maximum cache time. 
	 */
	public static final long MAX_CACHETIME = Long.MAX_VALUE;
	
	/** 
	 * Default cache time. 
	 */
	public static final long DEFAULT_CACHETIME = 1000*60*60*24;
	
	private InputStream stream;
	private String contentType;
	private String fileName;
	private Map params;
	private long cacheTime = DEFAULT_CACHETIME;
	private int bufferSize = 0;

	/** 
	 * Creates a new instance of DownloadStream. 
	 */
	public DownloadStream(
		InputStream stream,
		String contentType,
		String fileName) {
		setStream(stream);
		setContentType(contentType);
		setFileName(fileName);
	}

	/** 
	 * Gets downloadable stream.
	 * @return output stream.
	 */
	public InputStream getStream() {
		return this.stream;
	}

	/** 
	 * Sets the stream.
	 * @param stream The stream to set
	 */
	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	/** 
	 * Gets stream content type.
	 * @return type of the stream content.
	 */
	public String getContentType() {
		return this.contentType;
	}

	/** 
	 * Sets stream content type.
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/** 
	 * Returns the file name.
	 * @return the name of the file.
	 */
	public String getFileName() {
		return fileName;
	}

	/** 
	 * Sets the file name.
	 * @param fileName the file name to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/** 
	 * Sets a paramater for download stream.
	 * Parameters are optional information about the downloadable stream
	 * and their meaning depends on the used adapter. For example in
	 * WebAdapter they are interpreted as HTTP response headers.
	 * 	
	 * If the parameters by this name exists, the old value is replaced.
	 * 
	 * @param name the Name of the parameter to set.
	 * @param value the Value of the parameter to set.
	 */
	public void setParameter(String name, String value) {
		if (this.params == null) {
			this.params = new HashMap();
		}
		this.params.put(name, value);
	}

	/** 
	 * Gets a paramater for download stream.
	 * Parameters are optional information about the downloadable stream
	 * and their meaning depends on the used adapter. For example in
	 * WebAdapter they are interpreted as HTTP response headers.
	 * @param name the Name of the parameter to set.
	 * @return Value of the parameter or null if the parameter does not exist.
	 */
	public String getParameter(String name) {
		if (this.params != null)
			return (String) this.params.get(name);
		return null;
	}

	/** 
	 * Gets the names of the parameters.
	 * @return Iterator of names or null if no parameters are set.
	 */
	public Iterator getParameterNames() {
		if (this.params != null)
			return this.params.keySet().iterator();
		return null;
	}
	
	/** 
	 * Gets length of cache expiration time.
	 * This gives the adapter the possibility cache streams sent to the client.
	 * The caching may be made in adapter or at the client if the client supports
	 * caching. Default is <code>DEFAULT_CACHETIME</code>.
	 * @return Cache time in milliseconds
	 */
	public long getCacheTime() {
		return cacheTime;
	}

	/** 
	 * Sets length of cache expiration time.
	 * This gives the adapter the possibility cache streams sent to the client.
	 * The caching may be made in adapter or at the client if the client supports
	 * caching. Zero or negavive value disbales the caching of this stream.
	 * @param cacheTime the cache time in milliseconds.
	 */
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	/** 
	 * Gets the size of the download buffer.
	 * @return int The size of the buffer in bytes.
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/** 
	 * Sets the size of the download buffer.
	 * @param bufferSize the size of the buffer in bytes.
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

}
