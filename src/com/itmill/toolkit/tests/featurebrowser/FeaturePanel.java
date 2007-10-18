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

package com.itmill.toolkit.tests.featurebrowser;

import com.itmill.toolkit.ui.*;

public class FeaturePanel extends Feature {

	public FeaturePanel() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Panel caption");
		show
				.addComponent(new Label(
						"This is an example Label component that is added into Panel."));
		l.addComponent(show);

		// Properties
		propertyPanel = new PropertyPanel(show);
		Form ap = propertyPanel.createBeanPropertySet(new String[] { "width",
				"height" });
		Select themes = (Select) propertyPanel.getField("style");
		themes.addItem("light").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("light");
		themes.addItem("strong").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("strong");
		propertyPanel.addProperties("Panel Properties", ap);

		setJavadocURL("ui/Panel.html");

		return l;
	}

	protected String getExampleSrc() {
		return "Panel show = new Panel(\"Panel caption\");\n"
				+ "show.addComponent(new Label(\"This is an example Label component that is added into Panel.\"));";

	}

	protected String getDescriptionXHTML() {
		return "Panel is a container for other components, by default it draws a frame around it's "
				+ "extremities and may have a caption to clarify the nature of the contained components' purpose."
				+ " Panel contains an layout where the actual contained components are added, "
				+ "this layout may be switched on the fly.";
	}

	protected String getImage() {
		return "icon_demo.png";
	}

	protected String getTitle() {
		return "Panel";
	}

}
