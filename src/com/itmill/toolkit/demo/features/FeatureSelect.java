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

import com.itmill.toolkit.ui.*;

public class FeatureSelect extends Feature {

	private static final String[] firstnames = new String[] { "John", "Mary",
			"Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Robert", "Paula",
			"Lenny", "Kenny", "Nathan", "Nicole", "Laura", "Jos", "Josie",
			"Linus" };

	private static final String[] lastnames = new String[] { "Torvalds",
			"Smith", "Adams", "Black", "Wilson", "Richards", "Thompson",
			"McGoff", "Halas", "Jones", "Beck", "Sheridan", "Picard", "Hill",
			"Fielding", "Einstein" };

	public FeatureSelect() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Select s = new Select("Select Person");
		for (int i = 0; i < 50; i++)
			s
					.addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
							+ " "
							+ lastnames[(int) (Math.random() * (lastnames.length - 1))]);
		l.addComponent(s);

		// Properties
		propertyPanel = new PropertyPanel(s);
		Select themes = (Select) propertyPanel.getField("style");
		themes.addItem("optiongroup").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("optiongroup");
		themes.addItem("twincol").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("twincol");

		setJavadocURL("ui/Select.html");

		return l;
	}

	protected String getExampleSrc() {
		return "Select s = new Select(\"Select Car\");\n"
				+ "s.addItem(\"Audi\");\n" + "s.addItem(\"BMW\");\n"
				+ "s.addItem(\"Chrysler\");\n" + "s.addItem(\"Volvo\");\n";

	}

	/**
	 * @see com.itmill.toolkit.demo.features.Feature#getDescriptionXHTML()
	 */
	protected String getDescriptionXHTML() {
		return "The select component combines two different modes of item selection.  "
				+ "Firstly it presents the single selection mode, which is usually represented as "
				+ "either a drop-down menu or a radio-group of switches, secondly it "
				+ "allows for multiple item selection, this is usually represented as either a "
				+ "listbox of selectable items or as a group of checkboxes."
				+ "<br/><br/>"
				+ "Data source can be associated both with selected item and the list of selections. "
				+ "This way you can easily present a selection based on items specified elsewhere in application. "
				+ "<br/><br/>"
				+ "On the demo tab you can try out how the different properties affect the"
				+ " presentation of the component.";
	}

	protected String getImage() {
		return "icon_demo.png";
	}

	protected String getTitle() {
		return "Select";
	}

}
