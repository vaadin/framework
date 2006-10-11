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


package com.enably.tk.data;

import java.util.Collection;

/** <p>Interface for validatable objects. Defines methods to verify if the
 * object's value is valid or not, and to add, remove and list registered
 * validators of the object.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 * @see com.enably.tk.data.Validator
 */ 
public interface Validatable {
    
    /** Adds a new validator for this object.  The validator's
     * {@link Validator#validate(Object)} method is activated every time the
     * object's value needs to be verified, that is, when the
     * {@link #isValid()} method is called. This usually happens when the
     * object's value changes.
     * 
     * @param validator the new validator
     */
    void addValidator(Validator validator);
    
    /** Removes a previously registered validator from the object. The
     * specified validator is removed from the object and its
     * <code>validate</code> method is no longer called in {@link #isValid()}.
     * 
     * @param validator the validator to remove
     */
    void removeValidator(Validator validator);
    
    /** List all validators currently registered for the object. If no
     * validators are registered, returns <code>null</code>.
     * 
     * @return collection of validators or <code>null</code>
     */
    public Collection getValidators();
    
    /** Tests the current value of the object against all registered
     * validators. The registered validators are iterated and for each the
     * {@link Validator#validate(Object)} method is called. If any validator
     * throws the {@link Validator.InvalidValueException} this method
     * returns <code>false</code>.
     * 
     * @return <code>true</code> if the registered validators concur that
     * the value is valid, <code>false</code> otherwise
     */
    public boolean isValid(); 

    /** Checks the validity of the validatable. If the validatable is valid
     * this method should do nothing, and if it's not valid, it should throw
     * <code>Validator.InvalidValueException</code>
     * 
     * @throws Validator.InvalidValueException if the value is not valid
     */
    public void validate()
    throws Validator.InvalidValueException;

	/** Does the validabtable object accept invalid values. The default is true. */
    public boolean isInvalidAllowed();

	/** Should the validabtable object accept invalid values. Supporting
	 * this configuration possibility is optional. By default invalid values are
	 * alloved.
	 */
    public void setInvalidAllowed(boolean invalidValueAllowed)
    throws UnsupportedOperationException;
    
}
