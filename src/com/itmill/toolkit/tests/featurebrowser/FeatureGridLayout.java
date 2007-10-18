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

import java.util.Date;

import com.itmill.toolkit.ui.*;

public class FeatureGridLayout extends Feature {

	public FeatureGridLayout() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		GridLayout gl = new GridLayout(3, 3);
		DateField cal = new DateField("Test component 1", new Date());
		cal.setStyle("calendar");
		gl.addComponent(cal, 1, 0, 2, 1);
		for (int i = 2; i < 7; i++)
			gl.addComponent(new TextField("Test component " + i));
		l.addComponent(gl);

		// Properties
		propertyPanel = new PropertyPanel(gl);
		Form ap = propertyPanel.createBeanPropertySet(new String[] { "width",
				"height" });
		ap.addField("new line", new Button("New Line", gl, "newLine"));
		ap.addField("space", new Button("Space", gl, "space"));
		propertyPanel.addProperties("GridLayout Features", ap);
		propertyPanel.getField("height").dependsOn(
				propertyPanel.getField("add component"));

		setJavadocURL("ui/GridLayout.html");

		return l;
	}

	protected String getExampleSrc() {
		return "GridLayout gl = new GridLayout(2,2);\n"
				+ "gl.addComponent(new Label(\"Label 1 in GridLayout\"));\n"
				+ "gl.addComponent(new Label(\"Label 2 in GridLayout\"));\n"
				+ "gl.addComponent(new Label(\"Label 3 in GridLayout\"));\n"
				+ "gl.addComponent(new Label(\"Label 4 in GridLayout\"));\n";
	}

	/**
	 * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "This feature provides a container that lays out components "
				+ "into a grid of given width and height."
				+ "<br /><br />On the demo tab you can try out how the different "
				+ "properties affect the presentation of the component.";
	}

	protected String getImage() {
		return "icon_demo.png";
	}

	protected String getTitle() {
		return "GridLayout";
	}
}