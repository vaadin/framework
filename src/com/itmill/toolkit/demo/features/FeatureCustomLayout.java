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

public class FeatureCustomLayout extends Feature {

	private static final String INTRO_TEXT = ""
			+ "A container component with freely designed layout and style. The "
			+ "container consists of items with textually represented locations. Each "
			+ "item contains one sub-component. The adapter and theme are resposible "
			+ "for rendering the layout with given style by placing the items on the "
			+ "screen in defined locations."
			+ "<br /><br />The definition of locations is not fixed - the each style can define its "
			+ "locations in a way that is suitable for it. One typical example would be "
			+ "to create visual design for a website as a custom layout: the visual design "
			+ "could define locations for \"menu\", \"body\" and \"title\" for example. "
			+ "The layout would then be implemented e.g. as plain HTML file."
			+ "<br /><br />The default theme handles the styles that are not defined by just drawing "
			+ "the subcomponents with flowlayout.";

	protected Component getDemoComponent() {
		OrderedLayout l = new OrderedLayout();

		Panel panel = new Panel();
		panel.setCaption("Custom Layout");
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

		setJavadocURL("ui/CustomLayout.html");

		return l;
	}

	protected String getDescriptionXHTML() {
		return null;
	}

	protected String getExampleSrc() {
		return "CustomLayout c = new CustomLayout(\"mystyle\");\n"
				+ "c.addComponent(new Label(\"Example description\"),\"label1-location\");\n"
				+ "c.addComponent(new Button(\"Example action\"),\"example-action-location\");\n";
	}

	protected String getImage() {
		return null;
	}

	protected String getTitle() {
		return "Custom Layout";
	}

}
