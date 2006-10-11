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

package com.enably.tk.terminal;

/** Interface for different terminal types.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface Terminal {
 
 
    /** Get name of the default theme
     * @return Name of the terminal window
     */    
    public String getDefaultTheme();
    
    /** Get width of the terminal window in pixels
     * @return Width of the terminal window
     */    
    public int getScreenWidth();
    
    /** Get height of the terminal window in pixels
     * @return Height of the terminal window
     */    
    public int getScreenHeight();    
    
	/** Terminal error event */
	public interface ErrorEvent {

		/** Get the contained throwable */
		public Throwable getThrowable();

	}

	/** Terminal error listener interface */
	public interface ErrorListener {

		/** Invoked when terminal error occurs. */
		public void terminalError(Terminal.ErrorEvent event);
	}    
}
