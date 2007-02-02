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

public class FeatureTabSheet extends Feature {

	public FeatureTabSheet() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("TabSheet component");

		TabSheet ts = new TabSheet();
		ts.addTab(new Label("Tab 1 Body"), "Tab 1 caption", null);
		ts.addTab(new Label("Tab 2 Body"), "Tab 2 caption", null);
		ts.addTab(new Label("Tab 3 Body"), "Tab 3 caption", null);

		show.addComponent(ts);
		l.addComponent(show);

		// Properties
		propertyPanel = new PropertyPanel(ts);

		return l;
	}

	protected String getExampleSrc() {
		return "TabSheet ts = new TabSheet();"
				+ "ts.addTab(new Label(\"Tab 1 Body\"),\"Tab 1 caption\",null);"
				+ "ts.addTab(new Label(\"Tab 2 Body\"),\"Tab 2 caption\",null);"
				+ "ts.addTab(new Label(\"Tab 3 Body\"),\"Tab 3 caption\",null);";
	}

	protected String getDescriptionXHTML() {
		return "A multicomponent container with tabs for switching between them.<br/>"
				+ "In the normal case, one would place a layout component on each tab.<br/><br />"
				+ "On the demo tab you can try out how the different properties affect "
				+ "the presentation of the component.";
	}

	protected String getImage() {
		return "tabsheet.jpg";
	}

	protected String getTitle() {
		return "TabSheet";
	}

}
