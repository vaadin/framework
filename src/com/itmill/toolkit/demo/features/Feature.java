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

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.ui.*;

public class Feature extends CustomComponent {

	private TabSheet ts;

	private boolean initialized = false;

	private static Resource sampleIcon;
	
	/** Constuctor for the feature component */
	public Feature() {
		ts = new TabSheet();
		setCompositionRoot(ts);
	}

	/** Feature component initialization is lazily done when the 
	 * feature is attached to application */
	public void attach() {

		// Check if the feature is already initialized
		if (initialized) return;
		initialized = true;

		// Optional description with image
		String desc = getDescriptionXHTML();
		String title = getTitle();
		if (desc != null && title != null) {
			GridLayout gl = new GridLayout(2, 1);
			if (getImage() != null)
	 			gl.addComponent(
 					new Embedded(
						"", new ClassResource(getImage(), this.getApplication())));
			gl.addComponent(
				new Label(
					"<h2>" + title + "</h2>" + desc,
					Label.CONTENT_XHTML));
			ts.addTab(gl, "Description", null);
		}

		// Demo
		Component demo = getDemoComponent();
		if (demo != null)
			ts.addTab(demo, "Demo", null);

		// Example source
		String example = getExampleSrc();
		if (example != null) {
			OrderedLayout l = new OrderedLayout();
			l.addComponent(
				new Label(
					"<h2>" + getTitle() + " example</h2>",
					Label.CONTENT_XHTML));
			l.addComponent(new Label(example, Label.CONTENT_PREFORMATTED));
			ts.addTab(l, "Code Sample", null);
		}
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
	    if (sampleIcon == null) sampleIcon = new ClassResource("m.gif",this.getApplication());
	    return sampleIcon;
	}

}