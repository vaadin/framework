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


/* This validator is used for validating properties that 
 * do or do not allow null values.
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class NullValidator implements Validator {

	private boolean allowNull;
	private String errorMessage;

	/** Create a new NullValidator
	 * @param errorMessage - The error message to display on invalidation.
	 * @param allowNull - Are nulls allowed?
	 */
	public NullValidator(String errorMessage,boolean allowNull) {
		setErrorMessage(errorMessage);
		setNullAllowed(allowNull);
	}

	/** Validate the data given in value.
	 * @param value - The value to validate.
	 * @throws Validator.InvalidValueException - The value was invalid.
	 */
	public void validate(Object value) throws Validator.InvalidValueException {
		if ((allowNull && value != null) || (!allowNull && value == null))
			throw new Validator.InvalidValueException(errorMessage);
	}

	/** True of the value is valid.
	 * @param value - The value to validate.
	 */
	public boolean isValid(Object value) {
		return allowNull ? value == null : value != null;
	}

	/** True if nulls are allowed.
	 */
	public final boolean isNullAllowed() {
		return allowNull;
	}

	/** Sets if nulls are to be allowed.
	 * @param allowNull - Do we allow nulls?
	 */
	public void setNullAllowed(boolean allowNull) {
		this.allowNull = allowNull;
	}
	
	/** Get the error message that is displayed in case the
	 * value is invalid.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/** Set the error message to be displayed on invalid
	 * value.
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
