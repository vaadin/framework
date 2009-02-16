package com.itmill.toolkit.demo.tutorial.addressbook.ui;

import com.itmill.toolkit.demo.tutorial.addressbook.AddressBookApplication;
import com.itmill.toolkit.demo.tutorial.addressbook.data.Person;
import com.itmill.toolkit.demo.tutorial.addressbook.data.PersonContainer;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Table;

public class PersonList extends Table {
	public PersonList(AddressBookApplication app) {
		setSizeFull();
		setContainerDataSource(app.getDataSource());

		setVisibleColumns(PersonContainer.NATURAL_COL_ORDER);
		setColumnHeaders(PersonContainer.COL_HEADERS_ENGLISH);

		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);

		/*
		 * Make table selectable, react immediatedly to user events, and pass
		 * events to the controller (our main application)
		 */
		setSelectable(true);
		setImmediate(true);
		addListener((ValueChangeListener) app);
		/* We don't want to allow users to de-select a row */
		setNullSelectionAllowed(false);

		// customize email column to have mailto: links using column generator
		addGeneratedColumn("email", new ColumnGenerator() {
			public Component generateCell(Table source, Object itemId,
					Object columnId) {
				Person p = (Person) itemId;
				Link l = new Link();
				l.setResource(new ExternalResource("mailto:" + p.getEmail()));
				l.setCaption(p.getEmail());
				return l;
			}
		});
	}

}