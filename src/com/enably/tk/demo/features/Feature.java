/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.enably.tk.demo.features;

import com.enably.tk.terminal.ClassResource;
import com.enably.tk.terminal.Resource;
import com.enably.tk.ui.*;

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