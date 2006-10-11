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

public class FeaturePanel extends Feature {

	public FeaturePanel() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Panel caption");
		show.addComponent(new Label("Label in Panel"));
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(show);
		Form ap = p.createBeanPropertySet(new String[] { "width", "height" });
		Select themes = (Select) p.getField("style");
		themes
			.addItem("light")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("light");
		themes
			.addItem("strong")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("strong");
		p.addProperties("Panel Properties", ap);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "Panel show = new Panel(\"Panel caption\");\n"
			+ "show.addComponent(new Label(\"Label in Panel\"));";

	}

	protected String getDescriptionXHTML() {
		return "The Panel is a container for other components, it usually draws a frame around it's "+
			"extremities and may have a caption to clarify the nature of the contained components purpose."+
			"A panel always contains firstly a layout onto which the actual contained components are added, "+
			"this layout may be switched on the fly. <br/><br/>"+
			"On the demo tab you can try out how the different properties "+
			"affect the presentation of the component.";
	}


	protected String getImage() {
		return "panel.jpg";
	}

	protected String getTitle() {
		return "Panel";
	}

}

