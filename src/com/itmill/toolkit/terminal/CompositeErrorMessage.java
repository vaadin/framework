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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Class for combining multiple error messages together.
 *
 * @author  IT Mill Ltd
 * @version @VERSION@
 * @since 3.0
 */
public class CompositeErrorMessage implements ErrorMessage {

	/** Array of all the errors */
	private List errors;

	/** Level of the error */
	private int level;

	/** Constructor for CompositeErrorMessage.
	 * 
	 * @param errorMessages Array of error messages that are listed togeter. 
	 * Nulls are ignored, but at least one message is required.
	 * @throws NullPointerException if errorMessages is null.
	 * 	 * @throws IllegalArgumentException if the array was empty. 	 
	  */
	public CompositeErrorMessage(ErrorMessage[] errorMessages) {
		errors = new ArrayList(errorMessages.length);
		level = Integer.MIN_VALUE;

		for (int i = 0; i < errorMessages.length; i++) {
			addErrorMessage(errorMessages[i]);
		}

		if (errors.size() == 0)
			throw new IllegalArgumentException("Composite error message must have at least one error");

	}

	/** Constructor for CompositeErrorMessage.
	 * @param errorMessages Collection of error messages that are listed
	 * togeter. At least one message is required.
	 * @throws NullPointerException if the collection is null.
	 * @throws IllegalArgumentException if the collection was empty.
	 */
	public CompositeErrorMessage(Collection errorMessages) {
		errors = new ArrayList(errorMessages.size());
		level = Integer.MIN_VALUE;

		for (Iterator i = errorMessages.iterator(); i.hasNext();) {
			addErrorMessage((ErrorMessage) i.next());
		}

		if (errors.size() == 0)
			throw new IllegalArgumentException("Composite error message must have at least one error");
	}

	/** The error level is the largest error level in 
	 * @see com.itmill.toolkit.terminal.ErrorMessage#getErrorLevel()
	 */
	public final int getErrorLevel() {
		return level;
	}

	/** Add a error message into this composite message.
	 *  Updates the level field.
	 * @param error The error message to be added. Duplicate errors are ignored.
	 */
	private void addErrorMessage(ErrorMessage error) {
		if (error != null && !errors.contains(error)) {
			this.errors.add(error);
			int l = error.getErrorLevel();
			if (l > level)
				level = l;
		}
	}

	/** Get Error Iterator. */
	public Iterator iterator() {
		return errors.iterator();
	}

	public void paint(PaintTarget target) throws PaintException {

		if (errors.size() == 1)
			 ((ErrorMessage) errors.iterator().next()).paint(target);
		else {
			target.startTag("error");

			if (level > 0 && level <= ErrorMessage.INFORMATION)
				target.addAttribute("level", "info");
			else if (level <= ErrorMessage.WARNING)
				target.addAttribute("level", "warning");
			else if (level <= ErrorMessage.ERROR)
				target.addAttribute("level", "error");
			else if (level <= ErrorMessage.CRITICAL)
				target.addAttribute("level", "critical");
			else
				target.addAttribute("level", "system");

			// Paint all the exceptions
			for (Iterator i = errors.iterator(); i.hasNext();) {
				((ErrorMessage) i.next()).paint(target);
			}

			target.endTag("error");
		}
	}

	/* Documented in super interface */
	public void addListener(RepaintRequestListener listener) {
	}

	/* Documented in super interface */
	public void removeListener(RepaintRequestListener listener) {
	}

	/* Documented in super interface */
	public void requestRepaint() {
	}

	/* Documented in super interface */
	public void requestRepaintRequests() {
	}

	/** Returns a comma separated list of the error messages.
	 * @return String, comma separated list of error messages.
	 */
	public String toString() {
		String retval = "[";
		int pos = 0;
		for (Iterator i = errors.iterator(); i.hasNext();) {
			if (pos > 0)
				retval += ",";
			pos++;
			retval += i.next().toString();
		}
		retval += "]";

		return retval;
	}
}
