package com.itmill.toolkit.demo;

import java.sql.SQLException;

import com.itmill.toolkit.data.util.QueryContainer;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.demo.util.SampleDatabase;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

/**
 * Similar to QueryContainerDemo
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class TableDemo extends com.itmill.toolkit.Application implements
		Action.Handler {

	private static final String ACTION_DESCRIPTION = "Use right mouse button to initiate "
			+ "actions menu.<br />Note: on Opera use meta key "
			+ "and left mouse button.";

	private static final String TABLE_CAPTION = SampleDatabase.ROWCOUNT
			+ " dynamically loaded rows from example SQL table";

	private Link menuLink = new Link("Go back to menu", new ExternalResource(
			"index.html"));

	// Table component where SQL rows are attached (using QueryContainer)
	private Table table = new Table();

	// Label which displays last performed action against table row
	private Label tableLastAction = new Label("No action selected for table.");

	// Database provided with sample data
	private SampleDatabase sampleDatabase;

	// Example Actions for table
	private Action ACTION1 = new Action("Upload");

	private Action ACTION2 = new Action("Download");

	private Action ACTION3 = new Action("Show history");

	private Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

	// Button which is used to disable or enable table
	// note: when button click event occurs, tableEnabler() method is called
	private Button tableEnabler = new Button("Disable table", this,
			"tableEnabler");

	// Button which is used to hide or show table
	// note: when button click event occurs, tableVisibility() method is called
	private Button tableVisibility = new Button("Hide table", this,
			"tableVisibility");

	// Button which is used to hide or show table
	// note: when button click event occurs, tableVisibility() method is called
	private Button tableCaption = new Button("Hide caption", this,
			"tableCaption");

	/**
	 * Initialize Application. Demo components are added to main window.
	 */
	public void init() {
		Window main = new Window("Table demo");
		setMainWindow(main);

		// set the application to use Corporate -theme
		setTheme("corporate");

		// Add link back to index.html
		main.addComponent(menuLink);

		// create demo database
		sampleDatabase = new SampleDatabase();

		// Main window contains heading, two buttons, table and label
		main
				.addComponent(new Label(
						"<h2>Table demo</h2>"
								+ "<b>Rows are loaded from the server as they are needed.<br />"
								+ "Try scrolling the table to see it in action.</b><br />"
								+ ACTION_DESCRIPTION, Label.CONTENT_XHTML));
		OrderedLayout layout = new OrderedLayout(
				OrderedLayout.ORIENTATION_HORIZONTAL);
		// TODO: disabled until #655 fixed
		// layout.addComponent(tableVisibility);
		layout.addComponent(tableEnabler);
		layout.addComponent(tableCaption);
		main.addComponent(layout);
		main.addComponent(table);
		main.addComponent(tableLastAction);

		// initialize demo components
		initTable();
	}

	/**
	 * Populates table component with all rows from employee table.
	 * 
	 */
	private void initTable() {
		// init table
		table.setCaption(TABLE_CAPTION);
		table.setPageLength(10);
		table.setSelectable(true);
		table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		// this class handles table actions (see handleActions method below)
		table.addActionHandler(this);
		table.setDescription(ACTION_DESCRIPTION);

		// populate Toolkit table component with test SQL table rows
		try {
			QueryContainer qc = new QueryContainer("SELECT * FROM employee",
					sampleDatabase.getConnection());
			table.setContainerDataSource(qc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// define which columns should be visible on Table component
		table.setVisibleColumns(new Object[] { "FIRSTNAME", "LASTNAME",
				"TITLE", "UNIT" });
		table.setItemCaptionPropertyId("ID");
	}

	public void tableVisibility() {
		if (table.isVisible()) {
			tableVisibility.setCaption("Show table");
			table.setVisible(false);
			tableEnabler.setEnabled(false);
			tableCaption.setEnabled(false);
		} else {
			tableVisibility.setCaption("Hide table");
			table.setVisible(true);
			tableEnabler.setEnabled(true);
			tableCaption.setEnabled(true);
		}
	}

	public void tableEnabler() {
		if (table.isEnabled()) {
			tableEnabler.setCaption("Enable table");
			table.setEnabled(false);
		} else {
			tableEnabler.setCaption("Disable table");
			table.setEnabled(true);
		}
	}

	public void tableCaption() {
		if (table.getCaption().equals("")) {
			table.setCaption(TABLE_CAPTION);
			tableCaption.setCaption("Hide caption");
		} else {
			table.setCaption("");
			tableCaption.setCaption("Show caption");
		}
	}

	/**
	 * URIHandler Return example actions
	 */
	public Action[] getActions(Object target, Object sender) {
		return actions;
	}

	/**
	 * Executed by right mouse button on table or tree component.
	 */
	public void handleAction(Action action, Object sender, Object target) {
		if (sender == table) {
			tableLastAction.setValue("Last action clicked was '"
					+ action.getCaption() + "' on item " + target);
		}
	}

}
