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

package com.itmill.toolkit.data.validator;

import com.itmill.toolkit.data.*;

/** 
 * This <code>StringLengthValidator</code> is used to validate the length of strings.
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

	/** 
	 * Creates a new StringLengthValidator with a given error message.
	 * 
	 * @param errorMessage  the message to display in case the value does not validate.
	 */
	public StringLengthValidator(String errorMessage) {
		setErrorMessage(errorMessage);
	}

	/** 
	 * Creates a new StringLengthValidator with a given error message,
	 * permissable lengths and null-string allowance.
	 * 
	 * @param errorMessage the message to display in case the value does not validate.
	 * @param minLength the minimum permissable length of the string.
	 * @param maxLength the maximum permissable length of the string.
	 * @param allowNull  Are null strings permissable?
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

	/** 
	 * Validates the value.
	 * @param value the value to validate.
	 * @throws Validator.InvalidValueException
	 * 										if the value was invalid.
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

	/** 
	 * Checks if the given value is valid.
	 * @param value the value to validate.
	 * @return <code>true</code> for valid value, otherwise <code>false</code>.
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

	/** 
	 * Returns <code>true</code> if null strings are allowed.
	 * @return <code>true</code> if allows null string, otherwise <code>false</code>.
	 */
	public final boolean isNullAllowed() {
		return allowNull;
	}

	/** 
	 * Gets the maximum permissable length of the string.
	 * @return the maximum length of the string.
	 */
	public final int getMaxLength() {
		return maxLength;
	}

	/** 
	 * Gets the minimum permissable length of the string.
	 * @return the minimum length of the string.
	 */
	public final int getMinLength() {
		return minLength;
	}

	/**
	 * Sets whether null-strings are to be allowed.
	 */
	public void setNullAllowed(boolean allowNull) {
		this.allowNull = allowNull;
	}
	
	/** 
	 * Sets the maximum permissable length of the string.
	 * @param maxLength the length to set.
	 */
	public void setMaxLength(int maxLength) {
		if (maxLength < -1)
			maxLength = -1;
		this.maxLength = maxLength;
	}

	/** 
	 * Sets the minimum permissable length.
	 * @param minLength the length to set.
	 */
	public void setMinLength(int minLength) {
		if (minLength < -1)
			minLength = -1;
		this.minLength = minLength;
	}

	/** 
	 * Gets the message to be displayed in case the
	 * value does not validate.
	 * @return the Error Message.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/** 
	 * Sets the message to be displayer in case the
	 * value does not validate.
	 * @param errorMessage
	 * 						the Error Message to set.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
