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

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Table;

public class FeatureTable extends Feature implements Action.Handler {

	private static final String[] firstnames = new String[] { "John", "Mary",
			"Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Josie", "Linus" };

	private static final String[] lastnames = new String[] { "Torvalds",
			"Smith", "Jones", "Beck", "Sheridan", "Picard", "Hill", "Fielding",
			"Einstein" };

	private static final String[] title = new String[] { "Project Manager",
			"Marketing Manager", "Sales Manager", "Trainer", "IT Support",
			"Account Manager", "Customer Support", "Testing Engineer",
			"Software Designer", "Programmer", "Consultant" };

	private static final String[] unit = new String[] { "Tokyo", "Mexico City",
			"Seoul", "New York", "Sao Paulo", "Bombay", "Delhi", "Shanghai",
			"Los Angeles", "London", "Bangalore", "Hong Kong", "Madrid",
			"Milano", "Beijing", "Paris", "Moscow", "Helsinki" };

	private Table t;

	private boolean actionsActive = false;

	private Button actionHandlerSwitch = new Button("Activate actions", this,
			"toggleActions");

	public void toggleActions() {
		if (actionsActive) {
			t.removeActionHandler(this);
			actionsActive = false;
			actionHandlerSwitch.setCaption("Activate Actions");
		} else {
			t.addActionHandler(this);
			actionsActive = true;
			actionHandlerSwitch.setCaption("Deactivate Actions");
		}
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Sample table
		t = new Table("Corporate Employees");
		t.setPageLength(10);
		l.addComponent(t);

		// Add columns to table
		t.addContainerProperty("Firstname", String.class, "");
		t.addContainerProperty("Lastname", String.class, "");
		t.addContainerProperty("Title", String.class, "");
		t.addContainerProperty("Unit", String.class, "");

		// set alignments to demonstrate features
		t.setColumnAlignment("Title", Table.ALIGN_CENTER);
		t.setColumnAlignment("Unit", Table.ALIGN_RIGHT);

		// Add random rows to table
		for (int j = 0; j < 300; j++) {
			t
					.addItem(
							new Object[] {
									firstnames[(int) (Math.random() * (firstnames.length - 1))],
									lastnames[(int) (Math.random() * (lastnames.length - 1))],
									title[(int) (Math.random() * title.length)],
									unit[(int) (Math.random() * unit.length)] },
							new Integer(j));
		}

		// Actions
		l.addComponent(this.actionHandlerSwitch);

		// Properties
		propertyPanel = new PropertyPanel(t);
		Form ap = propertyPanel.createBeanPropertySet(new String[] {
				"pageLength", "rowHeaderMode", "selectable",
				"columnHeaderMode", "columnCollapsingAllowed",
				"columnReorderingAllowed", "width", "height" });
		ap.replaceWithSelect("columnHeaderMode", new Object[] {
				new Integer(Table.COLUMN_HEADER_MODE_EXPLICIT),
				new Integer(Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID),
				new Integer(Table.COLUMN_HEADER_MODE_HIDDEN),
				new Integer(Table.COLUMN_HEADER_MODE_ID) }, new Object[] {
				"Explicit", "Explicit defaults ID", "Hidden", "ID" });
		ap.replaceWithSelect("rowHeaderMode", new Object[] {
				new Integer(Table.ROW_HEADER_MODE_EXPLICIT),
				new Integer(Table.ROW_HEADER_MODE_EXPLICIT_DEFAULTS_ID),
				new Integer(Table.ROW_HEADER_MODE_HIDDEN),
				new Integer(Table.ROW_HEADER_MODE_ICON_ONLY),
				new Integer(Table.ROW_HEADER_MODE_ID),
				new Integer(Table.ROW_HEADER_MODE_INDEX),
				new Integer(Table.ROW_HEADER_MODE_ITEM),
				new Integer(Table.ROW_HEADER_MODE_PROPERTY) }, new Object[] {
				"Explicit", "Explicit defaults ID", "Hidden", "Icon only",
				"ID", "Index", "Item", "Property" });

		Select themes = (Select) propertyPanel.getField("style");
		themes.addItem("list").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("list");
		themes.addItem("paging").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("paging");

		propertyPanel.addProperties("Table Properties", ap);

		// Set first name as item caption propertyId in cas somebody selecs it
		t.setItemCaptionPropertyId("Firstname");

		// this overrides previous
		t.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
		t.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID);

		t.setColumnCollapsingAllowed(true);
		t.setColumnReorderingAllowed(true);
		t.setSelectable(true);

		setJavadocURL("ui/Table.html");

		return l;
	}

	protected String getExampleSrc() {
		return "// Sample table\n"
				+ "t = new Table(\"Corporate Employees\");\n"
				+ "t.setPageLength(10);\n\n"
				+ "// Add columns to table\n"
				+ "t.addContainerProperty(\"Firstname\", String.class, \"\");\n"
				+ "t.addContainerProperty(\"Lastname\", String.class, \"\");\n"
				+ "t.addContainerProperty(\"Age\", String.class, \"\");\n"
				+ "t.addContainerProperty(\"Title\", String.class, \"\");\n"
				+ "t.addContainerProperty(\"Unit\", String.class, \"\");\n\n"
				+ "// Add random rows to table\n"
				+ "for (int j = 0; j < 50; j++) {\n" + "	t.addItem(\n"
				+ "		new Object[] {\n"
				+ "			firstnames[(int) (Math.random() * 9)],\n"
				+ "			lastnames[(int) (Math.random() * 9)],\n"
				+ "			title[(int) (Math.random() * title.length)],\n"
				+ "			unit[(int) (Math.random() * unit.length)] },\n"
				+ "		new Integer(j));\n" + "}\n";
	}

	protected String getDescriptionXHTML() {

		return "The Table component is designed for displaying large volumes of tabular data, "
				+ "in multiple pages whenever needed."
				+ "<br /><br />Selection of the displayed data is supported both in selecting exclusively one row "
				+ "or multiple rows at the same time. For each row, there may be a set of actions associated, "
				+ "depending on the theme these actions may be displayed either as a drop-down "
				+ "menu for each row or a set of command buttons."
				+ "<br /><br />Table may be connected to any datasource implementing the <code>Container</code> interface."
				+ "This way data found in external datasources can be directly presented in the table component."
				+ "<br /><br />"
				+ "Table implements a number of features and you can test most of them in the table demo tab.";
	}

	protected String getImage() {
		return "icon_demo.png";
	}

	protected String getTitle() {
		return "Table";
	}

	private Action ACTION1 = new Action("Action 1");

	private Action ACTION2 = new Action("Action 2");

	private Action ACTION3 = new Action("Action 3");

	private Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

	public Action[] getActions(Object target, Object sender) {
		return actions;
	}

	public void handleAction(Action action, Object sender, Object target) {
		t.setDescription("Last action clicked was '" + action.getCaption()
				+ "' on item '" + t.getItem(target).toString() + "'");
	}

}
