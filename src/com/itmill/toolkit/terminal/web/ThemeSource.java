/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Intarfaces Made Easy

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
	 *  
	 *  This implicitly operates in xslt mode.
	 *  
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

		private static final long serialVersionUID = -7823850742197580285L;

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
