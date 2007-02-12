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

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.ui.*;

public abstract class Feature extends CustomComponent {

	private static final String PROP_REMINDER_TEXT = ""
			+ "<br /><br />Note: Use <b>Properties</b> panel located at the top"
			+ " right corner to try out how different properties affect"
			+ " the presentation or functionality of currently selected component.";

	private boolean propsReminder = true;

	private OrderedLayout layout;

	private TabSheet ts;

	private boolean initialized = false;

	private static Resource sampleIcon;

	protected PropertyPanel propertyPanel;

	private Label javadoc;

	/** Constuctor for the feature component */
	public Feature() {
		layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
		setCompositionRoot(layout);
	}

	/**
	 * Actual URL consists of "/doc/api/com/itmill/toolkit/"+url
	 * 
	 * @param url
	 */
	public void setJavadocURL(String url) {
		// TODO: FIXME!!!
		javadoc
				.setValue("<iframe width=\"100%\" src=\"/doc/api/com/itmill/toolkit/"
						+ url + "\"></iframe>");
		javadoc
				.setValue("<iframe width=\"100%\" src=\"http://toolkit.itmill.com/doc/api/com/itmill/toolkit/"
						+ url + "\"></iframe>");
	}

	/**
	 * Feature component initialization is lazily done when the feature is
	 * attached to application
	 */
	public void attach() {
		super.attach();

		// Check if the feature is already initialized
		if (initialized)
			return;
		initialized = true;

		// Javadoc
		javadoc = new Label();
		javadoc.setContentMode(Label.CONTENT_RAW);

		// Demo
		Component demo = getDemoComponent();
		if (demo != null)
			layout.addComponent(demo);

		ts = new TabSheet();

		// Description tab
		String desc = getDescriptionXHTML();
		String title = getTitle();
		if (desc != null) {
			GridLayout gl = new GridLayout(2, 1);
			if (getImage() != null)
				gl.addComponent(new Embedded("", new ClassResource(getImage(),
						this.getApplication())));
			String label = "";
			if (title != null)
				label += "<h2>" + title + "</h2>";
			label += desc;
			if (propsReminder)
				label += PROP_REMINDER_TEXT;
			gl.addComponent(new Label(label, Label.CONTENT_XHTML));
			ts.addTab(gl, "Description", null);
		}

		// Javadoc tab
		if (!javadoc.getValue().equals(""))
			ts.addTab(javadoc, "Javadoc", null);

		// Code Sample tab
		String example = getExampleSrc();
		if (example != null) {
			OrderedLayout l = new OrderedLayout();
			if (getTitle() != null)
				l.addComponent(new Label("<b>// " + getTitle() + " example</b>",
						Label.CONTENT_XHTML));
			l.addComponent(new Label(example, Label.CONTENT_PREFORMATTED));
			ts.addTab(l, "Code Sample", null);
		}

		layout.addComponent(ts);

	}

	/** Get the desctiption of the feature as XHTML fragment */
	protected String getDescriptionXHTML() {
		return "<h2>Feature description is under construction</h2>";
	}

	/** Get the title of the feature */
	protected String getTitle() {
		return this.getClass().getName();
	}

	/** Get the name of the image file that will be put on description page */
	protected String getImage() {
		return null;
	}

	/** Get the example application source code */
	protected String getExampleSrc() {
		return null;
	}

	/** Get the feature demo component */
	protected Component getDemoComponent() {
		return null;
	}

	/** Get sample icon resource */
	protected Resource getSampleIcon() {
		if (sampleIcon == null)
			sampleIcon = new ClassResource("m.gif", this.getApplication());
		return sampleIcon;
	}

	public PropertyPanel getPropertyPanel() {
		return propertyPanel;
	}

	public void setPropsReminder(boolean propsReminder) {
		this.propsReminder = propsReminder;
	}

}