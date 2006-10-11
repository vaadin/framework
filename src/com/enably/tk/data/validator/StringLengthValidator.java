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

package com.enably.tk.data.validator;

import com.enably.tk.data.*;


/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
/** This validator is used to validate the lenght of strings.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class StringLengthValidator implements Validator {

	private int minLength = -1;
	private int maxLength = -1;
	private boolean allowNull = true;
	private String errorMessage;

	/** Create a new StringLengthValidator with a given error message.
	 * 
	 * @param errorMessage - The message to display in case the value does not validate.
	 */
	public StringLengthValidator(String errorMessage) {
		setErrorMessage(errorMessage);
	}

	/** Create a new StringLenghtValidator with a given error message,
	 * permissable lenghts and null-string allowance.
	 * 
	 * @param errorMessage - The message to display in case the value does not validate.
	 * @param minLenght - The minimum permissable lenght of the string.
	 * @param maxLenght - The maximum permissable lenght of the string.
	 * @param allowNull - Are null strings permissable?
	 */
	public StringLengthValidator(
		String errorMessage,
		int minLength,
		int maxLength,
		boolean allowNull) {
		this(errorMessage);
		setMinLength(minLength);
		setMaxLength(maxLength);
		setNullAllowed(allowNull);
	}

	/** Validate the value.
	 * @param value - The value to validate.
	 */
	public void validate(Object value) throws Validator.InvalidValueException {
		if (value == null && !allowNull)
			throw new Validator.InvalidValueException(errorMessage);
		String s = value.toString();
		if (s == null && !allowNull)
			throw new Validator.InvalidValueException(errorMessage);
		int len = s.length();
		if ((minLength >= 0 && len < minLength)
			|| (maxLength >= 0 && len > maxLength))
			throw new Validator.InvalidValueException(errorMessage);
	}

	/** True if the value is valid.
	 * @param value - The value to validate.
	 */
	public boolean isValid(Object value) {
		if (value == null && !allowNull)
			return true;
		String s = value.toString();
		if (s == null && !allowNull)
			return true;
		int len = s.length();
		if ((minLength >= 0 && len < minLength)
			|| (maxLength >= 0 && len > maxLength))
			return false;
		return true;
	}

	/** True if null strings are allowed.
	 */
	public final boolean isNullAllowed() {
		return allowNull;
	}

	/** Get the maximum permissable length of the string.
	 */
	public final int getMaxLength() {
		return maxLength;
	}

	/** Get the minimum permissable lenght of the string.
	 */
	public final int getMinLength() {
		return minLength;
	}

	/** Sets wheter null-strings are to be allowed.
	 */
	public void setNullAllowed(boolean allowNull) {
		this.allowNull = allowNull;
	}
	
	/** Set the maximum permissable length of the string.
	 * @param maxLenght - The lenght to set.
	 */
	public void setMaxLength(int maxLength) {
		if (maxLength < -1)
			maxLength = -1;
		this.maxLength = maxLength;
	}

	/** Sets the minimum permissable lenght.
	 * @param minLenght - The lenght to set.
	 */
	public void setMinLength(int minLength) {
		if (minLength < -1)
			minLength = -1;
		this.minLength = minLength;
	}

	/** Gets the message to be displayed in case the
	 * value does not validate.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/** Sets the message to be displayer in case the
	 * value does not validate.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
