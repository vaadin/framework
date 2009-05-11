/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.tutorial.addressbook;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.demo.tutorial.addressbook.data.PersonContainer;
import com.vaadin.demo.tutorial.addressbook.data.SearchFilter;
import com.vaadin.demo.tutorial.addressbook.ui.HelpWindow;
import com.vaadin.demo.tutorial.addressbook.ui.ListView;
import com.vaadin.demo.tutorial.addressbook.ui.NavigationTree;
import com.vaadin.demo.tutorial.addressbook.ui.PersonForm;
import com.vaadin.demo.tutorial.addressbook.ui.PersonList;
import com.vaadin.demo.tutorial.addressbook.ui.SearchView;
import com.vaadin.demo.tutorial.addressbook.ui.SharingOptions;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class AddressBookApplication extends Application implements
		ClickListener, ValueChangeListener, ItemClickListener {

	private NavigationTree tree = new NavigationTree(this);

	private Button newContact = new Button("Add contact");
	private Button search = new Button("Search");
	private Button share = new Button("Share");
	private Button help = new Button("Help");
	private SplitPanel horizontalSplit = new SplitPanel(
			SplitPanel.ORIENTATION_HORIZONTAL);

	// Lazyly created ui references
	private ListView listView = null;
	private SearchView searchView = null;
	private PersonList personList = null;
	private PersonForm personForm = null;
	private HelpWindow helpWindow = null;
	private SharingOptions sharingOptions = null;

	private PersonContainer dataSource = PersonContainer.createWithTestData();

	@Override
	public void init() {
		buildMainLayout();
		setMainComponent(getListView());
	}

	private void buildMainLayout() {
		setMainWindow(new Window("Address Book Demo application"));

		setTheme("contacts");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		layout.addComponent(createToolbar());
		layout.addComponent(horizontalSplit);
		layout.setExpandRatio(horizontalSplit, 1);

		horizontalSplit.setSplitPosition(200, SplitPanel.UNITS_PIXELS);
		horizontalSplit.setFirstComponent(tree);

		getMainWindow().setLayout(layout);
	}

	private HorizontalLayout createToolbar() {
		HorizontalLayout lo = new HorizontalLayout();
		lo.addComponent(newContact);
		lo.addComponent(search);
		lo.addComponent(share);
		lo.addComponent(help);

		search.addListener((ClickListener) this);
		share.addListener((ClickListener) this);
		help.addListener((ClickListener) this);
		newContact.addListener((ClickListener) this);

		search.setIcon(new ThemeResource("icons/32/folder-add.png"));
		share.setIcon(new ThemeResource("icons/32/users.png"));
		help.setIcon(new ThemeResource("icons/32/help.png"));
		newContact.setIcon(new ThemeResource("icons/32/document-add.png"));

		lo.setMargin(true);
		lo.setSpacing(true);

		lo.setStyleName("toolbar");

		lo.setWidth("100%");

		Embedded em = new Embedded("", new ThemeResource("images/logo.png"));
		lo.addComponent(em);
		lo.setComponentAlignment(em, Alignment.MIDDLE_RIGHT);
		lo.setExpandRatio(em, 1);

		return lo;
	}

	private void setMainComponent(Component c) {
		horizontalSplit.setSecondComponent(c);
	}

	/*
	 * View getters exist so we can lazily generate the views, resulting in
	 * faster application startup time.
	 */
	private ListView getListView() {
		if (listView == null) {
			personList = new PersonList(this);
			personForm = new PersonForm(this);
			listView = new ListView(personList, personForm);
		}
		return listView;
	}

	private SearchView getSearchView() {
		if (searchView == null) {
			searchView = new SearchView(this);
		}
		return searchView;
	}

	private HelpWindow getHelpWindow() {
		if (helpWindow == null) {
			helpWindow = new HelpWindow();
		}
		return helpWindow;
	}

	private SharingOptions getSharingOptions() {
		if (sharingOptions == null) {
			sharingOptions = new SharingOptions();
		}
		return sharingOptions;
	}

	public PersonContainer getDataSource() {
		return dataSource;
	}

	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source == search) {
			showSearchView();
		} else if (source == help) {
			showHelpWindow();
		} else if (source == share) {
			showShareWindow();
		} else if (source == newContact) {
			addNewContanct();
		}
	}

	private void showHelpWindow() {
		getMainWindow().addWindow(getHelpWindow());
	}

	private void showShareWindow() {
		getMainWindow().addWindow(getSharingOptions());
	}

	private void showListView() {
		setMainComponent(getListView());
	}

	private void showSearchView() {
		setMainComponent(getSearchView());
	}

	public void valueChange(ValueChangeEvent event) {
		Property property = event.getProperty();
		if (property == personList) {
			Item item = personList.getItem(personList.getValue());
			if (item != personForm.getItemDataSource()) {
				personForm.setItemDataSource(item);
			}
		}
	}

	public void itemClick(ItemClickEvent event) {
		if (event.getSource() == tree) {
			Object itemId = event.getItemId();
			if (itemId != null) {
				if (itemId == NavigationTree.SHOW_ALL) {
					// clear previous filters
					getDataSource().removeAllContainerFilters();
					showListView();
				} else if (itemId == NavigationTree.SEARCH) {
					showSearchView();
				} else if (itemId instanceof SearchFilter) {
					search((SearchFilter) itemId);
				}
			}
		}
	}

	private void addNewContanct() {
		showListView();
		personForm.addContact();
	}

	public void search(SearchFilter searchFilter) {
		// clear previous filters
		getDataSource().removeAllContainerFilters();
		// filter contacts with given filter
		getDataSource().addContainerFilter(searchFilter.getPropertyId(),
				searchFilter.getTerm(), true, false);
		showListView();

		getMainWindow().showNotification(
				"Searched for " + searchFilter.getPropertyId() + "=*"
						+ searchFilter.getTerm() + "*, found "
						+ getDataSource().size() + " item(s).",
				Notification.TYPE_TRAY_NOTIFICATION);
	}

	public void saveSearch(SearchFilter searchFilter) {
		tree.addItem(searchFilter);
		tree.setParent(searchFilter, NavigationTree.SEARCH);
		// mark the saved search as a leaf (cannot have children)
		tree.setChildrenAllowed(searchFilter, false);
		// make sure "Search" is expanded
		tree.expandItem(NavigationTree.SEARCH);
		// select the saved search
		tree.setValue(searchFilter);
	}

}
