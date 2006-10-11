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
