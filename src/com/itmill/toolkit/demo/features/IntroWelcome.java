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

import com.itmill.toolkit.terminal.web.ApplicationServlet;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class IntroWelcome extends Feature {

	private static final String WELCOME_TEXT = ""
			+ "In this application you may view and play with some features of IT Mill Toolkit.<br/>"
			+ "Most of the features can be tested online and include simple example of their "
			+ "usage associated with it.<br/><br/>"
			+ "Start your tour by selecting features from the list on the left.<br/><br/>"
			+ "For more information, point your browser to: <a href=\"http://www.itmill.com\""
			+ " target=\"_new\">www.itmill.com</a>";

	public IntroWelcome() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Label lab = new Label();
		lab.setStyle("featurebrowser-none");
		Label version = new Label();
		version.setValue("IT Mill Toolkit version: "+ApplicationServlet.VERSION);
		l.addComponent(version);
		l.addComponent(lab);

		// Properties
		propertyPanel = null;
		
		setJavadocURL("package-summary.html");
	
		return l;
	}

	protected String getExampleSrc() {
		return "";
	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return WELCOME_TEXT;
	}

	protected String getImage() {
		return null;
	}

	protected String getTitle() {
		return "Welcome to the IT Mill Toolkit feature tour! (TODO)";
	}

}
