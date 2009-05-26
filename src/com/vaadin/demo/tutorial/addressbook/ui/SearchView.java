package com.vaadin.demo.tutorial.addressbook.ui;

import com.vaadin.demo.tutorial.addressbook.AddressBookApplication;
import com.vaadin.demo.tutorial.addressbook.data.PersonContainer;
import com.vaadin.demo.tutorial.addressbook.data.SearchFilter;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class SearchView extends Panel {

	private TextField tf;
	private NativeSelect fieldToSearch;
	private CheckBox saveSearch;
	private TextField searchName;
	private AddressBookApplication app;

	public SearchView(final AddressBookApplication app) {
		this.app = app;
		addStyleName("view");

		setCaption("Search contacts");
		setSizeFull();

		/* Use a FormLayout as main layout for this Panel */
		FormLayout formLayout = new FormLayout();
		setContent(formLayout);

		/* Create UI components */
		tf = new TextField("Search term");
		fieldToSearch = new NativeSelect("Field to search");
		saveSearch = new CheckBox("Save search");
		searchName = new TextField("Search name");
		Button search = new Button("Search");

		/* Initialize fieldToSearch */
		for (int i = 0; i < PersonContainer.NATURAL_COL_ORDER.length; i++) {
			fieldToSearch.addItem(PersonContainer.NATURAL_COL_ORDER[i]);
			fieldToSearch.setItemCaption(PersonContainer.NATURAL_COL_ORDER[i],
					PersonContainer.COL_HEADERS_ENGLISH[i]);
		}

		fieldToSearch.setValue("lastName");
		fieldToSearch.setNullSelectionAllowed(false);

		/* Initialize save checkbox */
		saveSearch.setValue(true);
		saveSearch.setImmediate(true);
		saveSearch.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				searchName.setVisible(event.getButton().booleanValue());
			}
		});

		search.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				performSearch();
			}

		});

		/* Add all the created components to the form */
		addComponent(tf);
		addComponent(fieldToSearch);
		addComponent(saveSearch);
		addComponent(searchName);
		addComponent(search);
	}

	private void performSearch() {
		String searchTerm = (String) tf.getValue();
		if (searchTerm == null || searchTerm.equals("")) {
			getWindow().showNotification("Search term cannot be empty!",
					Notification.TYPE_WARNING_MESSAGE);
			return;
		}

		SearchFilter searchFilter = new SearchFilter(fieldToSearch.getValue(),
				searchTerm, (String) searchName.getValue());
		if (saveSearch.booleanValue()) {
			if (searchName.getValue() == null
					|| searchName.getValue().equals("")) {
				getWindow().showNotification(
						"Please enter a name for your search!",
						Notification.TYPE_WARNING_MESSAGE);
				return;
			}
			app.saveSearch(searchFilter);
		}
		app.search(searchFilter);
	}

}
