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

package com.itmill.tk.ui;

import com.itmill.tk.data.Container;
import com.itmill.tk.data.Item;
import com.itmill.tk.data.Property;

/** Factory for creating new Field-instances based on type,
 *  datasource and/or context.
 *
 * @author IT Mill Ltd.
 * @version  @VERSION@
 * @since 3.1
 */
public interface FieldFactory {


	/** Creates field based on type of data.
	 *
	 *
	 * @param type The type of data presented in field
	 * @param uiContext The component where the field is presented.
	 * @return Field The field suitable for editing the specified data.
	 *	 
	 */
	Field createField(Class type, Component uiContext);
	
	/** Creates field based on the property datasource.
	 *
	 * @param property The property datasource.
	 * @param uiContext The component where the field is presented.
	 * @return Field The field suitable for editing the specified data.
	 */
	Field createField(Property property, Component uiContext);

	/** Creates field based on the item and property id.
	 * 
	 * @param item The item where the property belongs to.
	 * @param propertyId Id of the property.
	 * @param uiContext The component where the field is presented.
	 * @return Field The field suitable for editing the specified data.
	 */
	Field createField(Item item, Object propertyId, Component uiContext);

	/** Creates field based on the container item id and property id.
	 *
	 * @param container Container where the property belongs to.
	 * @param itemId The item Id.
	 * @param propertyId Id of the property.
	 * @param uiContext The component where the field is presented.
	 * @return Field The field suitable for editing the specified data.
	 */
	Field createField(Container container, Object itemId, Object propertyId, Component uiContext);

}