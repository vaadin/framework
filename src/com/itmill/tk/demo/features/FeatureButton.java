/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2002 IT Mill Ltd
                     
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

package com.itmill.tk.demo.features;

import com.itmill.tk.ui.*;

public class FeatureButton extends Feature {

	public FeatureButton() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Button component");
		Button b = new Button("Caption");
		show.addComponent(b);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(b);
		Select themes = (Select) p.getField("style");
		themes
			.addItem("link")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("link");
		Form ap = p.createBeanPropertySet(new String[] { "switchMode" });
		p.addProperties("Button Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "Button b = new Button(\"Caption\");\n";

	}

	/**
	 * @see com.itmill.tk.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "In Millstone, boolean input values are represented by buttons. "
			+ "Buttons may function either as a push buttons or switches. (checkboxes)<br/><br/>"			
			+ "Button can be directly connected to any method of an object, which "
			+ "is an easy way to trigger events: <code> new Button(\"Play\", myPiano \"playIt\")</code>. "
			+ "Or in checkbox-mode they can be bound to a boolean proterties and create "
			+ " simple selectors.<br /><br /> "
			+ "See the demo and try out how the different properties affect "
			+ "the presentation of the component.";
	}

	protected String getImage() {
		return "button.jpg";
	}

	protected String getTitle() {
		return "Button";
	}

}
