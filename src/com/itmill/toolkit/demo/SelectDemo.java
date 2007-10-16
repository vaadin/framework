package com.itmill.toolkit.demo;

import java.sql.SQLException;
import com.itmill.toolkit.data.util.QueryContainer;
import com.itmill.toolkit.demo.util.SampleDatabase;
import com.itmill.toolkit.ui.*;

/**
 * This example demonstrates what is lazy loading feature on Select component.
 * Demo Uses similar concepts to QueryContainerDemo.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class SelectDemo extends com.itmill.toolkit.Application {

	// Select component where SQL rows are attached (using QueryContainer)
	private Select select = new Select();

	private Select lazySelect = new Select();

	// Database provided with sample data
	private SampleDatabase sampleDatabase;

	/**
	 * Initialize Application. Demo components are added to main window.
	 */
	public void init() {
		Window main = new Window("Select demo");
		setMainWindow(main);

		// Main window contains heading, table, select and tree
		Panel panel = new Panel("Select demo (a.k.a Google Suggests)");
		panel.addComponent(lazySelect);
		panel.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		panel.addComponent(select);
		main.addComponent(panel);

		// create demo database
		sampleDatabase = new SampleDatabase();

		initSelects();
	}

	private void initSelects() {
		// init select
		select.setCaption("All employees default functionality.");
		select.setItemCaptionPropertyId("WORKER");
		// populate Toolkit select component with test SQL table rows
		try {
			QueryContainer qc = new QueryContainer(
					"SELECT ID, UNIT||', '||LASTNAME||' '||FIRSTNAME"
							+ " AS WORKER FROM employee ORDER BY WORKER",
					sampleDatabase.getConnection());
			select.setContainerDataSource(qc);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// init lazySelect
		lazySelect.setCaption("All employees with lazy loading "
				+ "(a.k.a Google Suggests) activated.");
		lazySelect.setItemCaptionPropertyId("WORKER");
		// use lazy loading (a.k.a Google Suggest)
		lazySelect.setLazyLoading(true);
		// use same datasource as select object uses
		lazySelect.setContainerDataSource(select.getContainerDataSource());
	}

}
