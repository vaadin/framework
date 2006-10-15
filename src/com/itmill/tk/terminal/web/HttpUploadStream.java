/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.itmill.tk.terminal.web;

import java.io.InputStream;

/** WebAdapter implementation of the UploadStream interface.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class HttpUploadStream
	implements com.itmill.tk.terminal.UploadStream {

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
