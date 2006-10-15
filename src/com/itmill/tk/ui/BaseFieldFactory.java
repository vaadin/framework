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

import java.util.Date;

import com.itmill.tk.data.Container;
import com.itmill.tk.data.Item;
import com.itmill.tk.data.Property;

/** Default implementation of the 

 * 	 
 * 
 *  The following Field types are used by default:
 * <p>
 *  <b>Boolean</b>: Button(switchMode:true)<br/> 
 *  <b>Date</b>:  DateField(resolution: day) 
 *  <b>Item</b>:  Form<br/>   
 *  <b>default   field type</b>: TextField
 * <p>
 * @author IT Mill Ltd.
 * @version  @VERSION@
 * @since 3.1
 */

public class BaseFieldFactory implements FieldFactory {

	/** Creates field based on type of data.
	 * 	
	 *  
	 * @param type The type of data presented in field
	 * @param uiContext The context where the Field is presented.
	 * 
	 * @see com.itmill.tk.ui.FieldFactory#createField(Class, Component)
	 */
	public Field createField(Class type, Component uiContext) {
		// Null typed properties can not be edited
		if (type == null)
			return null;

		// Item field
		if (Item.class.isAssignableFrom(type)) {
			return new Form();
		}


		// Date field
		if (Date.class.isAssignableFrom(type)) {
			DateField df = new DateField();
			df.setResolution(DateField.RESOLUTION_DAY);
			return df;
		}

		// Boolean field
		if (Boolean.class.isAssignableFrom(type)) {
			Button button = new Button();
			button.setSwitchMode(true);
			button.setImmediate(false);
			return button;
		}

		// Nested form is used by default
		return new TextField();
	}

	/** Create field based on the datasource property.
	 * 
	 * @see com.itmill.tk.ui.FieldFactory#createField(Property, Component)
	 */
	public Field createField(Property property, Component uiContext) {
		if (property != null)
			return createField(property.getType(),uiContext);
		else
			return null;
	}

	/** Creates field based on the item and property id.
	 * 
	 * @see com.itmill.tk.ui.FieldFactory#createField(Item, Object, Component)
	 */
	public Field createField(Item item, Object propertyId, Component uiContext) {
		if (item != null && propertyId != null){
			Field f= createField(item.getItemProperty(propertyId),uiContext);
			if (f instanceof AbstractComponent)
				((AbstractComponent)f).setCaption(propertyId.toString());
			return f;
		}
		else
			return null;
	}
	
	/**
	 * @see com.itmill.tk.ui.FieldFactory#createField(com.itmill.tk.data.Container, java.lang.Object, java.lang.Object, com.itmill.tk.ui.Component)
	 */
	public Field createField(
		Container container,
		Object itemId,
		Object propertyId,
		Component uiContext) {
		return createField(container.getContainerProperty(itemId,propertyId),uiContext);
	}
	
}
