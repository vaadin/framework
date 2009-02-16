package com.itmill.toolkit.demo;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class SimpleAddressBook extends Application {

	private static String[] fields = { "First Name", "Last Name", "Company",
			"Mobile Phone", "Work Phone", "Home Phone", "Work Email",
			"Home Email", "Street", "Zip", "City", "State", "Country" };
	private static String[] visibleCols = new String[] { "Last Name", "First Name",
	"Company" };

	private Table contactList = new Table();
	private Form contactEditor = new Form();
	private HorizontalLayout bottomLeftCorner = new HorizontalLayout();
	private Button contactRemovalButton;
	private IndexedContainer addressBookData = createDummyData();

	public void init() {
		initLayout();
		initContactAddRemoveButtons();
		initAddressList();
		initFilteringControls();
	}

	private void initLayout() {
		SplitPanel splitPanel = new SplitPanel(
				SplitPanel.ORIENTATION_HORIZONTAL);
		setMainWindow(new Window("Address Book", splitPanel));
		VerticalLayout left = new VerticalLayout();
		left.setSizeFull();
		left.addComponent(contactList);
		contactList.setSizeFull();
		left.setExpandRatio(contactList, 1);
		splitPanel.addComponent(left);
		splitPanel.addComponent(contactEditor);
		contactEditor.setSizeFull();
		contactEditor.getLayout().setMargin(true);
		contactEditor.setImmediate(true);
		bottomLeftCorner.setWidth("100%");
		left.addComponent(bottomLeftCorner);
	}	

	private void initContactAddRemoveButtons() {
		// New item button
		bottomLeftCorner.addComponent(new Button("+", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				Object id = contactList.addItem();
				contactList.setValue(id);
			}
		}));

		// Remove item button
		contactRemovalButton = new Button("-", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				contactList.removeItem(contactList.getValue());
				contactList.select(null);
			}
		});
		contactRemovalButton.setVisible(false);
		bottomLeftCorner.addComponent(contactRemovalButton);
	}

	private String[] initAddressList() {
		contactList.setContainerDataSource(addressBookData);
		contactList.setVisibleColumns(visibleCols);
		contactList.setSelectable(true);
		contactList.setImmediate(true);
		contactList.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Object id = contactList.getValue();
				contactEditor.setItemDataSource(id == null ? null : contactList
						.getItem(id));
				contactRemovalButton.setVisible(id != null);
			}
		});
		return visibleCols;
	}

	private void initFilteringControls() {
		for (final String pn : visibleCols) {
			final TextField sf = new TextField();
			bottomLeftCorner.addComponent(sf);
			sf.setWidth("100%");
			sf.setValue(pn);
			sf.setImmediate(true);
			bottomLeftCorner.setExpandRatio(sf, 1);
			sf.addListener(new Property.ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					addressBookData.removeContainerFilters(pn);
					if (sf.toString().length() > 0 && !pn.equals(sf.toString()))
						addressBookData.addContainerFilter(pn, sf.toString(),
								true, false);
					getMainWindow().showNotification(
							"" + addressBookData.size() + " matches found");
				}
			});
		}
	}
	private static IndexedContainer createDummyData() {

		String[] fnames = { "Peter", "Alice", "Joshua", "Mike", "Olivia",
				"Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene",
				"Lisa", "Marge" };
		String[] lnames = { "Smith", "Gordon", "Simpson", "Brown", "Clavel",
				"Simons", "Verne", "Scott", "Allison", "Gates", "Rowling",
				"Barks", "Ross", "Schneider", "Tate" };

		IndexedContainer ic = new IndexedContainer();

		for (String p : fields)
			ic.addContainerProperty(p, String.class, "");

		for (int i = 0; i < 1000; i++) {
			Object id = ic.addItem();
			ic.getContainerProperty(id, "First Name").setValue(
					fnames[(int) (fnames.length * Math.random())]);
			ic.getContainerProperty(id, "Last Name").setValue(
					lnames[(int) (lnames.length * Math.random())]);
		}

		return ic;
	}

}
