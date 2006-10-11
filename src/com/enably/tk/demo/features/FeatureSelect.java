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

public class FeatureSelect extends Feature {

	public FeatureSelect() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Select component");
		Select s = new Select("Select Car");
		s.addItem("Audi");
		s.addItem("BMW");
		s.addItem("Chrysler");
		s.addItem("Volvo");
		show.addComponent(s);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(s);
		Select themes = (Select) p.getField("style");
		themes
			.addItem("optiongroup")
			.getItemProperty(themes.getItemCaptionPropertyId())
			.setValue("optiongroup");
		l.addComponent(p);

		return l;
	}

	protected String getExampleSrc() {
		return "Select s = new Select(\"Select Car\");\n"+
		"s.addItem(\"Audi\");\n"+
		"s.addItem(\"BMW\");\n"+
		"s.addItem(\"Chrysler\");\n"+
		"s.addItem(\"Volvo\");\n";

	}
	/**
	 * @see com.enably.tk.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "The select component combines two different modes of item selection.  "
			+ "Firstly it presents the single selection mode, which is usually represented as "
			+ "either a drop-down menu or a radio-group of switches, secondly it "
			+ "allows for multiple item selection, this is usually represented as either a "
			+ "listbox of selectable items or as a group of checkboxes."
			+ "<br/><br/>"
			+ "Data source can be associated both with selected item and the list of selections. "+
			  "This way you can easily present a selection based on items specified elsewhere in application. "
			+ "<br/><br/>"
			+ "On the demo tab you can try out how the different properties affect the"
			+ " presentation of the component.";
	}

	protected String getImage() {
		return "select.jpg";
	}

	protected String getTitle() {
		return "Select";
	}

}
