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

public class FeatureLabel extends Feature {

	public FeatureLabel() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Label lab = new Label("Label text");
		l.addComponent(lab);

		// Properties
		propertyPanel = new PropertyPanel(lab);
		Form ap = propertyPanel.createBeanPropertySet(new String[] {
				"contentMode", "value" });
		ap.replaceWithSelect("contentMode", new Object[] {
				new Integer(Label.CONTENT_PREFORMATTED),
				new Integer(Label.CONTENT_TEXT),
				new Integer(Label.CONTENT_UIDL),
				new Integer(Label.CONTENT_XHTML),
				new Integer(Label.CONTENT_XML) },
				new Object[] { "Preformatted", "Text", "UIDL (Must be valid)",
						"XHTML Fragment(Must be valid)",
						"XML (Subtree with namespace)" });
		propertyPanel.addProperties("Label Properties", ap);

		return l;
	}

	protected String getExampleSrc() {
		return "Label l = new Label(\"Caption\");\n";
	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "Labels components are for captions and plain text. "
				+ "By default, it is a light-weight component for presenting "
				+ "text content in application, but it can be also used to present "
				+ "formatted information and even XML."
				+ "<br /><br />"
				+ "Label can also be directly associated with data property to display "
				+ "information from different data sources automatically. This makes it "
				+ "trivial to present the current user in the corner of applications main window. "
				+ "<br /><br />"
				+ "On the demo tab you can try out how the different properties affect "
				+ "the presentation of the component.";
	}

	protected String getImage() {
		return "label.jpg";
	}

	protected String getTitle() {
		return "Label";
	}

}
