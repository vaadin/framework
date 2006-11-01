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

package com.itmill.toolkit.data;


/** <p>This interface defines the combination of Validatable and Buffered interfaces.
 * The combination of the interfaces defines if the invalid data is committed to 
 * datasource.</p>
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public interface BufferedValidatable extends Buffered, Validatable {

	/** Is the invalid data committed to datasource. 
	 * The default is false. */
	public boolean isInvalidCommitted();
	
	/** Set if the invalid data should be committed to datasource. 
	 * The default is false. */	
	public void setInvalidCommitted(boolean isCommitted);
}
