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

/**
 * Interface for different terminal types.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Terminal {

	/**
	 * Gets the name of the default theme.
	 * 
	 * @return the Name of the terminal window.
	 */
	public String getDefaultTheme();

	/**
	 * Gets the width of the terminal window in pixels.
	 * 
	 * @return the Width of the terminal window.
	 */
	public int getScreenWidth();

	/**
	 * Gets the height of the terminal window in pixels.
	 * 
	 * @return the Height of the terminal window.
	 */
	public int getScreenHeight();

	/**
	 * Terminal error event.
	 */
	public interface ErrorEvent {

		/**
		 * Gets the contained throwable.
		 */
		public Throwable getThrowable();

	}

	/**
	 * Terminal error listener interface.
	 */
	public interface ErrorListener {

		/**
		 * Invoked when terminal error occurs.
		 * 
		 * @param event
		 *            the fired event.
		 */
		public void terminalError(Terminal.ErrorEvent event);
	}
}
