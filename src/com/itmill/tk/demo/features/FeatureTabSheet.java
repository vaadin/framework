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

package com.itmill.tk.demo.features;

import com.itmill.tk.ui.*;

public class FeatureTabSheet extends Feature {

	public FeatureTabSheet() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("TabSheet component");
		
		TabSheet ts = new TabSheet();
		ts.addTab(new Label("Tab 1 Body"),"Tab 1 caption",null);
		ts.addTab(new Label("Tab 2 Body"),"Tab 2 caption",null);
		ts.addTab(new Label("Tab 3 Body"),"Tab 3 caption",null);
				
		show.addComponent(ts);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(ts);
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "TabSheet ts = new TabSheet();"+
		"ts.addTab(new Label(\"Tab 1 Body\"),\"Tab 1 caption\",null);"+
		"ts.addTab(new Label(\"Tab 2 Body\"),\"Tab 2 caption\",null);"+
		"ts.addTab(new Label(\"Tab 3 Body\"),\"Tab 3 caption\",null);";
	}

	protected String getDescriptionXHTML() {
		return "A multicomponent container with tabs for switching between them.<br/>"+
				"In the normal case, one would place a layout component on each tab.<br/><br />"+
				"On the demo tab you can try out how the different properties affect "+
				"the presentation of the component.";
	}
	
	protected String getImage() {
		return "tabsheet.jpg";
	}

	protected String getTitle() {
		return "TabSheet";
	}

}
