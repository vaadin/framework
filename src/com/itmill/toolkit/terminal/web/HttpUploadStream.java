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

package com.itmill.toolkit.terminal.web;

import java.io.InputStream;

/** 
 * WebAdapter implementation of the UploadStream interface.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class HttpUploadStream
	implements com.itmill.toolkit.terminal.UploadStream {

	/** 
	 * Holds value of property variableName. 
	 */
	private String streamName;
	private String contentName;
	private String contentType;

	/** 
	 * Holds value of property variableValue. 
	 */
	private InputStream stream;

	/** 
	 * Creates a new instance of UploadStreamImpl
	 * @param name the name of the stream.
	 * @param stream the input stream.
	 * @param contentName the name of the content.
	 * @param contentType the type of the content.
	 */
	public HttpUploadStream(
		String name,
		InputStream stream,
		String contentName,
		String contentType) {
		this.streamName = name;
		this.stream = stream;
		this.contentName = contentName;
		this.contentType = contentType;
	}

	/** 
	 * Gets the name of the stream.
	 * @return the name of the stream.
	 */
	public String getStreamName() {
		return this.streamName;
	}

	/** 
	 * Gets the input stream.
	 * @return the Input stream.
	 */
	public InputStream getStream() {
		return this.stream;
	}

	/** 
	 * Gets the input stream content type.
	 * @return the content type of the input stream.
	 */
	public String getContentType() {
		return this.contentType;
	}

	/** 
	 * Gets the stream content name.
	 * Stream content name usually differs from the actual stream name.
	 * It is used to identify the content of the stream.
	 * @return the Name of the stream content.
	 */
	public String getContentName() {
		return this.contentName;
	}
}
