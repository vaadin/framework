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

import com.itmill.toolkit.ui.*;

public class FeatureButton extends Feature {

	public FeatureButton() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Button b = new Button("Caption");
		l.addComponent(b);

		// Properties
		propertyPanel = new PropertyPanel(b);
		Select themes = (Select) propertyPanel.getField("style");
		themes.addItem("link").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("link");
		Form ap = propertyPanel
				.createBeanPropertySet(new String[] { "switchMode" });
		propertyPanel.addProperties("Button Properties", ap);

		setJavadocURL("ui/Button.html");

		return l;
	}

	protected String getExampleSrc() {
		return "Button b = new Button(\"Caption\");\n";

	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "In IT Mill Toolkit, boolean input values are represented by buttons. "
				+ "Buttons may function either as a push buttons or switches. (checkboxes)<br/><br/>"
				+ "Button can be directly connected to any method of an object, which "
				+ "is an easy way to trigger events: <code> new Button(\"Play\", myPiano \"playIt\")</code>. "
				+ "Or in checkbox-mode they can be bound to a boolean proterties and create "
				+ " simple selectors.<br /><br /> "
				+ "See the demo and try out how the different properties affect "
				+ "the presentation of the component.";
	}

	protected String getImage() {
		return "icon_demo.png";
	}

	protected String getTitle() {
		return "Button";
	}

}
