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

import java.util.regex.Pattern;

/**
 * <p>
 * Interface implemented by all classes that can be identified by Unique User
 * Interface Identity (UUID). By default paintable ID's (PIDs) for all UI
 * components are generated with a growing sequence number starting from 0. PIDs
 * are unique but they are created when UI component is rendered to terminal and
 * therefore PIDs may change between subsequent application or session
 * initializations. Classes implementing this interface may set fixed PIDs for
 * any UI component.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 4.1.0
 */
public class Identifiable {

	/**
	 * Unique user interface identifier (UUID)
	 */
	private String UIID = null;

	/**
	 * Valid UUID pattern
	 */
	private static Pattern validPattern = Pattern.compile("[a-zA-Z0-9_]{1,32}");

	/**
	 * Set unique user interface identifier (UUID). This must be unique identity
	 * string consisting of characters 0-9, a-Z and _ and it's length must be
	 * from 1 to 32.
	 * 
	 */
	public void setUIID(String UIID) {

		// check for valid characters
		if (!((validPattern.matcher(UIID)).matches())) {
			String errMsg = "Ignored setUUID for " + this.getClass()
					+ " because of invalid value [" + UIID + "], ";
			if ((UIID.length() < 1) || (UIID.length() > 32)) {
				// TODO change this: warn of invalid UUID
				errMsg += "length must be from 1 to 32.";
			} else {
				errMsg += "invalid characters used.";
			}
			// TODO change this: warn of invalid UUID
			System.err.println(errMsg);
			// ignore UUID setting
			return;
		}

		this.UIID = UIID;
	}

	/**
	 * Get unique user interface identifier (UUID)
	 * 
	 */
	public String getUIID() {
		return UIID;
	}

}
