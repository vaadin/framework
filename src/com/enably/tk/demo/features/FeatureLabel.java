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

import com.enably.tk.ui.*;

public class FeatureLabel extends Feature {

	public FeatureLabel() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Label component");
		Label lab = new Label("Label text");
		show.addComponent(lab);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(lab);
		Form ap =
			p.createBeanPropertySet(new String[] { "contentMode", "value" });
		ap.replaceWithSelect(
			"contentMode",
			new Object[] {
				new Integer(Label.CONTENT_PREFORMATTED),
				new Integer(Label.CONTENT_TEXT),
				new Integer(Label.CONTENT_UIDL),
				new Integer(Label.CONTENT_XHTML),
				new Integer(Label.CONTENT_XML)},
			new Object[] {
				"Preformatted",
				"Text",
				"UIDL (Must be valid)",
				"XHTML Fragment(Must be valid)",
				"XML (Subtree with namespace)" });
		p.addProperties("Label Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "Label l = new Label(\"Caption\");\n";
	}
	/**
	 * @see com.enably.tk.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "Labels components are for captions and plain text. "
			+ "By default, it is a light-weight component for presenting "
			+ "text content in application, but it can be also used to present "
			+ "formatted information and even XML."
			+ "<br /><br />"
			+ "Label can also be directly associated with data property to display "
			+ "information from different data sources automatically. This makes it "
			+ "trivial to present the current user in the corner of applications main window. "
			+ "<br /><br />"
			+ "On the demo tab you can try out how the different properties affect "
			+ "the presentation of the component.";
	}

	protected String getImage() {
		return "label.jpg";
	}

	protected String getTitle() {
		return "Label";
	}

}
