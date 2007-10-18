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
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;

public class FeatureBuffering extends Feature {

	private static final String INTRO_TEXT = ""
			+ "IT Mill Toolkit data model provides interface for implementing "
			+ "buffering in data components. The basic idea is that a component "
			+ "reading their state from data source can implement "
			+ "Buffered-interface, for storing the value internally. "
			+ "Buffering provides transactional access "
			+ "for setting data: data can be put to a component's buffer and "
			+ "afterwards committed to or discarded by re-reding it from the data source. "
			+ "The buffering can be used for creating interactive interfaces "
			+ "as well as caching the data for performance reasons."
			+ "<br /><br />Buffered interface contains methods for committing and discarding "
			+ "changes to an object and support for controlling buffering mode "
			+ "with read-through and write-through modes. "
			+ "Read-through mode means that the value read from the buffered "
			+ "object is constantly up to date with the data source. "
			+ "Respectively the write-through mode means that all changes to the object are "
			+ "immediately updated to the data source.";

	public FeatureBuffering() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();
		Panel panel = new Panel();
		panel.setCaption("Buffering");
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

		setJavadocURL("data/Buffered.html");

		return l;
	}

	protected String getExampleSrc() {
		return null;
	}

	/**
	 * @see com.itmill.toolkit.tests.featurebrowser.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return null;
	}

	protected String getImage() {
		return null;
	}

	protected String getTitle() {
		return null;
	}

}
