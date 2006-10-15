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
import java.util.Collection;

/** Interface implemented by theme sources.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface ThemeSource {

	/** Get the name of the ThemeSource.
	 *  @return Name of the theme source.
	 */
	public String getName();
	
	/** Get XSL stream for the specified theme and web-browser type.
	 *  Returns the XSL templates, which are used to process the
	 *  UIDL data. The <code>type</code> parameter is used to limit
	 *  the templates, which are returned based on the theme fileset
	 *  requirements.
	 *  @param theme Theme, which XSL should be returned
	 *  @param type The type of the current client.
	 *  @return Collection of ThemeSource.XSLStream objects.
	 *  @see Theme
	 */
	public Collection getXSLStreams(Theme theme, WebBrowser type)
		throws ThemeException;

	/** Get the last modification time, used to reload theme on changes.
	 *  @return Last modification time of the theme source.
	 */
	public long getModificationTime();

	/** Get input stream for the resource with the specified resource id.
	 *  @return Stream where the resource can be read.
	 *  @throws ThemeException If the resource is not found or there was
	 * 			 some problem finding the resource.
	 */
	public InputStream getResource(String resourceId) throws ThemeException;

	/** Get list of themes in the theme source. 
	 *  @return List of themes included in the theme source.
	 */
	public Collection getThemes();

	/** Return Theme instance by name.
	 *  @param name Theme name.
	 *  @return Theme instance matching the name, or null if not found.
	 */
	public Theme getThemeByName(String name);

	/** Theme exception.
	 *  Thrown by classes implementing the ThemeSource interface
	 *  if some error occurs during processing.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class ThemeException extends Exception {

		/** Create new theme exception.
		 *  @param message Error message.
		 */
		public ThemeException(String message) {
			super(message);
		}

        /** Createa new theme exception.
         * 
         * @param message
         * @param e
         */
        public ThemeException(String message, Throwable cause) {
           super(message,cause); 	
        }
	}

	/** Wrapper class for XSL InputStreams */
	public class XSLStream {
		private String id;
		private InputStream stream;

		public XSLStream(String id, InputStream stream) {
			this.id = id;
			this.stream = stream;
		}
		
		/** Return id of this stream.
		 * @return
		 */
		public String getId() {
			return id;
		}

		/** Return the actual XSL Stream.
		 * @return
		 */
		public InputStream getStream() {
			return stream;
		}

	}
}
