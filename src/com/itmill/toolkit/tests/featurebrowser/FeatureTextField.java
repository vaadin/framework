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

public class FeatureTextField extends Feature {

	public FeatureTextField() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout(
				OrderedLayout.ORIENTATION_HORIZONTAL);

		// Test component
		TextField tf = new TextField("Caption");
		l.addComponent(tf);

		// Properties
		propertyPanel = new PropertyPanel(tf);
		Form f = propertyPanel.createBeanPropertySet(new String[] { "columns",
				"rows", "wordwrap", "writeThrough", "readThrough",
				"nullRepresentation", "nullSettingAllowed", "secret" });
		propertyPanel.addProperties("Text field properties", f);

		setJavadocURL("ui/TextField.html");

		return l;
	}

	protected String getExampleSrc() {
		return "TextField tf = new TextField(\"Caption\");\n"
				+ "tf.setValue(\"Contents\");";
	}

	/**
	 * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "TextField combines the logic of both the single line text-entry field and the multi-line "
				+ "text-area into one component. "
				+ "As with all Data-components of IT Mill Toolkit, the TextField can also be bound to an "
				+ "underlying data source, both directly or in a buffered (asynchronous) "
				+ "mode. In buffered mode its background color will change to indicate "
				+ "that the value has changed but is not committed."
				+ "<br /><br />Furthermore a validators may be bound to the component to "
				+ "check and validate the given input before it is actually committed."
				+ "<br /><br />On the demo tab you can try out how the different properties affect the "
				+ "presentation of the component.";
	}

	protected String getImage() {
		return "icon_demo.png";
	}

	protected String getTitle() {
		return "TextField";
	}

}
