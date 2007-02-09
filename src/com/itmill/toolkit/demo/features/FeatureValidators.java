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
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class FeatureValidators extends Feature {

	public FeatureValidators() {
		super();
	}
	
	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Label lab = new Label();
		lab.setStyle("featurebrowser-none");
		l.addComponent(lab);

		// Properties
		propertyPanel = null;
		
		setJavadocURL("data/Validator.html");
		
		return l;
	}

	protected String getExampleSrc() {
		return "";
	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "<p>IT Mill Toolkit contains simple, yet powerful validation interface, "
				+ "that consists of two parts: Validator and Validatable. Validator is "
				+ "any class that can check validity of an Object. Validatable is "
				+ "a class with configurable validation. "
				+ "Validation errors are passed as special exceptions that implement "
				+ "ErrorMessage interface. This way the validation errors can be "
				+ "automatically added to components.</p>"
				+ "<p>Utilities for simple string and null validation are provided, as "
				+ "well as combinative validators. The validation interface can also "
				+ "be easily implemented by the applications for more complex "
				+ "validation needs.</p>";
	}

	protected String getImage() {
		return "validators.gif";
	}

	protected String getTitle() {
		return "Introduction of Data Model Validators";
	}

}
