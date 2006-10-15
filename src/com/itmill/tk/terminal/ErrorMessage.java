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

package com.itmill.tk.terminal;

/** Interface for rendering error messages to terminal. All the visible errors
 * shown to user must implement this interface.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface ErrorMessage extends Paintable {

	/** Error code for system errors and bugs. */
	public static final int SYSTEMERROR = 5000;

	/** Error code for critical error messages. */
	public static final int CRITICAL = 4000;

	/** Error code for regular error messages. */
	public static final int ERROR = 3000;

	/** Error code for warning messages. */
	public static final int WARNING = 2000;

	/** Error code for informational messages. */
	public static final int INFORMATION = 1000;

	/** Gets the errors level.
	 * 
	 *  @return the level of error as an integer.
	 */
	public int getErrorLevel();

	/** Error messages are inmodifiable and thus listeners are not needed. This
	 * method should be implemented as empty.
	 * 
	 * @see com.itmill.tk.terminal.Paintable#addListener(Paintable.RepaintRequestListener)
	 */
	public void addListener(RepaintRequestListener listener);

	/** Error messages are inmodifiable and thus listeners are not needed. This
	 * method should be implemented as empty.
	 *
	 * @see com.itmill.tk.terminal.Paintable#removeListener(Paintable.RepaintRequestListener)
	 */
	public void removeListener(RepaintRequestListener listener);

	/** Error messages are inmodifiable and thus listeners are not needed. This
	 * method should be implemented as empty.
	 *
	 * @see com.itmill.tk.terminal.Paintable#requestRepaint()
	 */
	public void requestRepaint();

}
