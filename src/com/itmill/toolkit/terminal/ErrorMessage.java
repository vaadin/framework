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

package com.itmill.toolkit.terminal;

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
	 * @see com.itmill.toolkit.terminal.Paintable#addListener(Paintable.RepaintRequestListener)
	 */
	public void addListener(RepaintRequestListener listener);

	/** Error messages are inmodifiable and thus listeners are not needed. This
	 * method should be implemented as empty.
	 *
	 * @see com.itmill.toolkit.terminal.Paintable#removeListener(Paintable.RepaintRequestListener)
	 */
	public void removeListener(RepaintRequestListener listener);

	/** Error messages are inmodifiable and thus listeners are not needed. This
	 * method should be implemented as empty.
	 *
	 * @see com.itmill.toolkit.terminal.Paintable#requestRepaint()
	 */
	public void requestRepaint();

}
