package com.itmill.toolkit.demo;

import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Window;

/**
 * The classic "hello, world!" example for IT Mill Toolkit. The class simply
 * implements the abstract {@link com.itmill.toolkit.Application#init() init()}
 * method in which it creates a Window and adds a Label to it.
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.Window
 * @see com.itmill.toolkit.ui.Label
 */
public class FilterSelect extends com.itmill.toolkit.Application {

	private static final String[] firstnames = new String[] { "John", "Mary",
			"Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Robert", "Paula",
			"Lenny", "Kenny", "Nathan", "Nicole", "Laura", "Jos", "Josie",
			"Linus" };

	private static final String[] lastnames = new String[] { "Torvalds",
			"Smith", "Adams", "Black", "Wilson", "Richards", "Thompson",
			"McGoff", "Halas", "Jones", "Beck", "Sheridan", "Picard", "Hill",
			"Fielding", "Einstein" };

	/**
	 * The initialization method that is the only requirement for inheriting the
	 * com.itmill.toolkit.service.Application class. It will be automatically
	 * called by the framework when a user accesses the application.
	 */
	public void init() {

		/*
		 * - Create new window for the application - Give the window a visible
		 * title - Set the window to be the main window of the application
		 */
		Window main = new Window("Filter select demo");
		setMainWindow(main);

		// default filter
		Select s1 = new Select();
		for (int i = 0; i < 105; i++) {
			s1
					.addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
							+ " "
							+ lastnames[(int) (Math.random() * (lastnames.length - 1))]);
		}
		s1.setImmediate(true);

		// contains filter
		Select s2 = new Select();
		for (int i = 0; i < 500; i++) {
			s2
					.addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
							+ " "
							+ lastnames[(int) (Math.random() * (lastnames.length - 1))]);
		}
		s2.setFilteringMode(Select.FILTERINGMODE_CONTAINS);

		// startswith filter
		Select s3 = new Select();
		for (int i = 0; i < 500; i++) {
			s3
					.addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
							+ " "
							+ lastnames[(int) (Math.random() * (lastnames.length - 1))]);
		}
		s3.setFilteringMode(Select.FILTERINGMODE_STARTSWITH);

		// Add selects to UI using ordered layout and panels
		OrderedLayout orderedLayout = new OrderedLayout(
				OrderedLayout.ORIENTATION_HORIZONTAL);

		Panel panel1 = new Panel("Select with default filter");
		Panel panel2 = new Panel("Select with contains filter");
		Panel panel3 = new Panel("Select with custom 'EndsWith' filter");

		panel1.addComponent(s1);
		panel2.addComponent(s2);
		panel3.addComponent(s3);

		orderedLayout.addComponent(panel1);
		orderedLayout.addComponent(panel2);
		orderedLayout.addComponent(panel3);
		main.addComponent(orderedLayout);

	}

}
