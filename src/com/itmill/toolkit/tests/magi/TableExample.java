package com.itmill.toolkit.tests.magi;
import com.itmill.toolkit.ui.*;

public class TableExample extends CustomComponent {
	/* Create the table with a caption. */
	Table table = new Table("This is a Table");

	/* A layout needed for the example. */
	OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);

	TableExample () {
		setCompositionRoot (layout);
		layout.addComponent(table);

		/* Define the names, data types, and default values of columns. */
		table.addContainerProperty("First Name", String.class, "(no first name)");
		table.addContainerProperty("Last Name", String.class, "(no last name)");
		table.addContainerProperty("Year", Integer.class, null);
		
		/* We use these entries to generate random items in a table. */
		final String[] firstnames = new String[]{"Donald", "Patty", "Sally", "Douglas"};
		final String[] lastnames  = new String[]{"Smith", "Jones", "Adams", "Knuth"};
		
		/* Add some items in the table and assign them an Item ID (IID). */
		for (int i=0; i<500; i++) {
			/* Add a randomly generated item in the Table. */
			table.addItem(new Object[]{firstnames[(int) (Math.random() * (firstnames.length-0.01))],
									   lastnames [(int) (Math.random() * (lastnames.length-0.01))],
									   (int) (1900+Math.random() * 100)}, i);
		}

		/* Set the number of items visible in the table. */
		table.setPageLength(10);
		
		table.setStyle("twincol");
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
	}
}
