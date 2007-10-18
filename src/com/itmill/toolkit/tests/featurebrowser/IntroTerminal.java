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

import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;

public class IntroTerminal extends Feature {

	public IntroTerminal() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Label lab = new Label();
		lab.setStyle("featurebrowser-none");
		l.addComponent(lab);

		// Properties
		propertyPanel = null;

		return l;
	}

	protected String getExampleSrc() {
		return null;
	}

	/**
	 * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "";
	}

	protected String getImage() {
		return null;
	}

	protected String getTitle() {
		return "Introduction for terminals (TODO)";
	}

}
