/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

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
