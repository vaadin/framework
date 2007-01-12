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

package com.itmill.toolkit.terminal.web;

import java.io.InputStream;

/** AjaxAdapter implementation of the UploadStream interface.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.1
 */
public class AjaxHttpUploadStream
	implements com.itmill.toolkit.terminal.UploadStream {

	/** Holds value of property variableName. */
	private String streamName;
	private String contentName;
	private String contentType;

	/** Holds value of property variableValue. */
	private InputStream stream;

	/** Creates a new instance of UploadStreamImpl
	 *  @param name of the stream
	 *  @param stream input stream
	 *  @param contentName name of the content
	 *  @param contentType type of the content
	 *  @param time Time of event creation 
	 *       (for parallel events (for example in 
	 *       same http request), times are equal)
	 */
	public AjaxHttpUploadStream(
		String name,
		InputStream stream,
		String contentName,
		String contentType) {
		this.streamName = name;
		this.stream = stream;
		this.contentName = contentName;
		this.contentType = contentType;
	}

	/** Get the name of the stream.
	 * @return name of the stream.
	 */
	public String getStreamName() {
		return this.streamName;
	}

	/** Get input stream.
	 * @return Input stream.
	 */
	public InputStream getStream() {
		return this.stream;
	}

	/** Get input stream content type.
	 * @return content type of the input stream.
	 */
	public String getContentType() {
		return this.contentType;
	}

	/** Get stream content name.
	 *  Stream content name usually differs from the actual stream name.
	 *  it is used toi identify the content of the stream.
	 * @return Name of the stream content.
	 */
	public String getContentName() {
		return this.contentName;
	}
}
