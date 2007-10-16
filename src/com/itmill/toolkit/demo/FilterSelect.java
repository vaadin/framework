package com.itmill.toolkit.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.select.ContainsFilter;
import com.itmill.toolkit.ui.select.OptionFilter;

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
		for (int i = 0; i < 105; i++)
			s1
					.addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
							+ " "
							+ lastnames[(int) (Math.random() * (lastnames.length - 1))]);
		s1.setLazyLoading(true);
		s1.setImmediate(true);

		// contains filter
		Select s2 = new Select();
		for (int i = 0; i < 500; i++)
			s2
					.addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
							+ " "
							+ lastnames[(int) (Math.random() * (lastnames.length - 1))]);
		s2.setLazyLoading(true);
		s2.setOptionFilter(new ContainsFilter(s2));

		// custom filter
		Select s3 = new Select();
		for (int i = 0; i < 500; i++)
			s3
					.addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
							+ " "
							+ lastnames[(int) (Math.random() * (lastnames.length - 1))]);
		s3.setLazyLoading(true);
		s3.setOptionFilter(new FilterSelect.EndsWithFilter(s3));

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

	/**
	 * Custom filter that implements "ends with" functionality.
	 * 
	 * @author IT Mill Ltd.
	 * 
	 */
	public class EndsWithFilter implements OptionFilter {
		private Select s;

		private ArrayList filteredItemsBuffer;

		public EndsWithFilter(Select s) {
			this.s = s;
		}

		public List filter(String filterstring, int pagelength, int page) {
			// prefix MUST be in lowercase
			if (filterstring == null || "".equals(filterstring)) {
				this.filteredItemsBuffer = new ArrayList(s.getItemIds());
				return this.filteredItemsBuffer;

			} else if (s.getContainerDataSource() != null) {
				// all items will be iterated and tested.
				// SLOW when there are lot of items.
				this.filteredItemsBuffer = new ArrayList();
				for (Iterator iter = s.getItemIds().iterator(); iter.hasNext();) {
					Object id = iter.next();

					Item item = s.getItem(id);
					String test = "";
					if (s.getItemCaptionMode() == Select.ITEM_CAPTION_MODE_PROPERTY)
						test = item.getItemProperty(
								s.getItemCaptionPropertyId()).getValue()
								.toString().trim();
					else
						test = String.valueOf(id);

					if (test.toLowerCase().endsWith(filterstring)) {
						this.filteredItemsBuffer.add(id);
					}
				}
			}
			return this.filteredItemsBuffer;
		}

		public int getMatchCount() {
			return filteredItemsBuffer.size();
		}
	}

}
