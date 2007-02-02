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

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.*;

public class FeatureLink extends Feature {

	public FeatureLink() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Link component");
		Link lnk = new Link("Link caption", new ExternalResource(
				"http://www.itmill.com"));
		show.addComponent(lnk);
		l.addComponent(show);

		// Properties
		propertyPanel = new PropertyPanel(lnk);
		Form ap = propertyPanel.createBeanPropertySet(new String[] {
				"targetName", "targetWidth", "targetHeight", "targetBorder" });
		ap.replaceWithSelect("targetBorder", new Object[] {
				new Integer(Link.TARGET_BORDER_DEFAULT),
				new Integer(Link.TARGET_BORDER_MINIMAL),
				new Integer(Link.TARGET_BORDER_NONE) }, new Object[] {
				"Default", "Minimal", "None" });
		propertyPanel.addProperties("Link Properties", ap);

		return l;
	}

	protected String getExampleSrc() {
		return "Link lnk = new Link(\"Link caption\",new ExternalResource(\"http://www.itmill.com\"));\n";
	}

	protected String getDescriptionXHTML() {
		return "The link feature allows for making refences to both internal and external resources. "
				+ "The link can open the new resource in a new window, allowing for control of the newly "
				+ "opened windows attributes, such as size and border. "
				+ "<br /><br />"
				+ " For example you can create an application pop-up or create link to external resources.";

	}

	protected String getImage() {
		return "link.jpg";
	}

	protected String getTitle() {
		return "Link";
	}
}
