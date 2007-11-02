package com.itmill.toolkit.tests;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

public class TableSelectTest extends CustomComponent implements
		Table.ValueChangeListener {

	public TableSelectTest() {
		OrderedLayout main = new OrderedLayout();
		setCompositionRoot(main);
		main.addComponent(new Label("Hello World!"));

		Table t;
		t = new Table("single nullsel");
		main.addComponent(t);
		t(t);
		t.setMultiSelect(false);
		t.setNullSelectionAllowed(true);
		t.addListener(this);

		t = new Table("single NO-nullsel");
		main.addComponent(t);
		t(t);
		t.setMultiSelect(false);
		t.setNullSelectionAllowed(false);
		t.addListener(this);

		t = new Table("multi nullsel");
		main.addComponent(t);
		t(t);
		t.setMultiSelect(true);
		t.setNullSelectionAllowed(true);
		t.addListener(this);

		t = new Table("multi NO-nullsel");
		main.addComponent(t);
		t(t);
		t.setMultiSelect(true);
		t.setNullSelectionAllowed(false);
		t.addListener(this);

		// --

		t = new Table("single nullsel nullselid");
		main.addComponent(t);
		Object id = t(t);
		t.setNullSelectionItemId(id);
		t.setMultiSelect(false);
		t.setNullSelectionAllowed(true);
		t.addListener(this);

		t = new Table("single NO-nullsel nullselid");
		main.addComponent(t);
		id = t(t);
		t.setNullSelectionItemId(id);
		t.setMultiSelect(false);
		t.setNullSelectionAllowed(false);
		t.addListener(this);

		t = new Table("multi nullsel nullselid");
		main.addComponent(t);
		id = t(t);
		t.setNullSelectionItemId(id);
		try {
			t.setMultiSelect(true);
		} catch (Exception e) {
			System.err.println("failed ok");
		}
		t.setNullSelectionAllowed(true);
		t.addListener(this);

		t = new Table("multi NO-nullsel nullselid");
		main.addComponent(t);
		id = t(t);
		t.setNullSelectionItemId(id);
		try {
			t.setMultiSelect(true);
		} catch (Exception e) {
			System.err.println("failed ok");
		}
		t.setNullSelectionAllowed(false);
		t.addListener(this);

		/*
		 * And that's it! The framework will display the main window and its
		 * contents when the application is accessed with the terminal.
		 */

	}

	private Object t(Table t) {
		t.setImmediate(true);
		t.setSelectable(true);

		Object id = null;
		for (int i = 0; i < 5; i++) {
			id = t.addItem();
		}
		t.addContainerProperty("asd", String.class, "the asd thing");
		t.addContainerProperty("foo", String.class, "foo stuff");
		t.addContainerProperty("Alonger column header", String.class, "short");

		return id;
	}

	public void valueChange(ValueChangeEvent event) {
		Object val = event.getProperty().getValue();

		System.err.println("Value: " + val);

	}

}
