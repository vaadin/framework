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
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;

public class FeatureProperties extends Feature {

	private static final String INTRO_TEXT = ""
			+ "IT Mill Toolkit data model is one of the core concepts "
			+ "in the library and Property-interface is the base of that "
			+ "model. Property provides standardized API for a single data object "
			+ "that can be read (get) and written (set). A property is always typed, but can optionally "
			+ "support data type conversions. Optionally properties can provide "
			+ "value change events for following the state changes."
			+ "<br /><br />The most important function of the Property as well as other "
			+ "data models is to connect classes implementing the interface directly to "
			+ "editor and viewer classes. Typically this is used to connect different "
			+ "data sources to UI components for editing and viewing their contents."
			+ "<br /><br />Properties can be utilized either by implementing the interface "
			+ "or by using some of the existing property implementations. IT Mill Toolkit "
			+ "includes Property interface implementations for "
			+ "arbitrary function pairs or Bean-properties as well as simple object "
			+ "properties."
			+ "<br /><br />Many of the UI components also implement Property interface and allow "
			+ "setting of other components as their data-source. These UI-components "
			+ "include TextField, DateField, Select, Table, Button, "
			+ "Label and Tree.";

	public FeatureProperties() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Panel panel = new Panel();
		panel.setCaption("Data Model");
		l.addComponent(panel);

		Label label = new Label();
		panel.addComponent(label);

		label.setContentMode(Label.CONTENT_XHTML);
		label.setValue(INTRO_TEXT);

		// Properties
		propertyPanel = new PropertyPanel(panel);
		Form ap = propertyPanel.createBeanPropertySet(new String[] { "width",
				"height" });
		Select themes = (Select) propertyPanel.getField("style");
		themes.addItem("light").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("light");
		themes.addItem("strong").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("strong");
		propertyPanel.addProperties("Panel Properties", ap);

		setJavadocURL("data/Property.html");

		return l;
	}

	protected String getExampleSrc() {
		return null;
	}

	protected String getDescriptionXHTML() {
		return null;
	}

	protected String getImage() {
		return null;
	}

	protected String getTitle() {
		return null;
	}

}
