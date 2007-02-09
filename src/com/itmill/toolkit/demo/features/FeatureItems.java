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

package com.itmill.toolkit.demo.features;

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class FeatureItems extends Feature {

	public FeatureItems() {
		super();
	}
	
	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Label lab = new Label();
		lab.setStyle("featurebrowser-none");
		l.addComponent(lab);

		// Properties
		propertyPanel = null;
		
		setJavadocURL("data/Items.html");
		
		return l;
	}

	protected String getExampleSrc() {
		return "";
	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "<p>Item is an object, which contains a set of named "
				+ "properties. Each property is identified by an "
				+ "id and a reference to the property can be queried from the Item. "
				+ "Item defines inner-interfaces for maintaining the item property "
				+ "set and listening the item property set changes.</p>"
				+ "<p>Items generally represent objects in the object-oriented "
				+ "model, but with the exception that they are configurable "
				+ "and provide an event mechanism. The simplest way of utilizing "
				+ "Item interface is to use existing Item implementations. "
				+ "Provided utility classes include configurable property set,"
				+ " bean to item adapter and Form UI component.</p>";
	}

	protected String getImage() {
		return "items.jpg";
	}

	protected String getTitle() {
		return "Introduction of Data Model Item";
	}

}
