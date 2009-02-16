package com.itmill.toolkit.demo.tutorial.addressbook.ui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.demo.tutorial.addressbook.AddressBookApplication;
import com.itmill.toolkit.demo.tutorial.addressbook.data.Person;
import com.itmill.toolkit.demo.tutorial.addressbook.data.PersonContainer;
import com.itmill.toolkit.demo.tutorial.addressbook.validators.EmailValidator;
import com.itmill.toolkit.demo.tutorial.addressbook.validators.PostalCodeValidator;
import com.itmill.toolkit.ui.BaseFieldFactory;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class PersonForm extends Form implements ClickListener {

	private Button save = new Button("Save", (ClickListener) this);
	private Button cancel = new Button("Cancel", (ClickListener) this);
	private Button edit = new Button("Edit", (ClickListener) this);
	private final ComboBox cities = new ComboBox("City");

	private AddressBookApplication app;
	private boolean newContactMode = false;
	private Person newPerson = null;

	public PersonForm(AddressBookApplication app) {
		this.app = app;

		/*
		 * Enable buffering so that commit() must be called for the form before
		 * input is written to the data. (Form input is not written immediately
		 * through to the underlying object.)
		 */
		setWriteThrough(false);

		HorizontalLayout footer = new HorizontalLayout();
		footer.setSpacing(true);
		footer.addComponent(save);
		footer.addComponent(cancel);
		footer.addComponent(edit);
		footer.setVisible(false);

		setFooter(footer);

		/* Allow the user to enter new cities */
		cities.setNewItemsAllowed(true);
		/* We do not want to use null values */
		cities.setNullSelectionAllowed(false);
		/* Add an empty city used for selecting no city */
		cities.addItem("");

		/* Populate cities select using the cities in the data container */
		PersonContainer ds = app.getDataSource();
		for (Iterator<Person> it = ds.getItemIds().iterator(); it.hasNext();) {
			String city = (it.next()).getCity();
			cities.addItem(city);
		}

		/*
		 * Field factory for overriding how the component for city selection is
		 * created
		 */
		setFieldFactory(new BaseFieldFactory() {
			@Override
			public Field createField(Item item, Object propertyId,
					Component uiContext) {
				if (propertyId.equals("city")) {
					cities.setWidth("200px");
					return cities;
				}

				Field field = super.createField(item, propertyId, uiContext);
				if (propertyId.equals("postalCode")) {
					TextField tf = (TextField) field;
					/*
					 * We do not want to display "null" to the user when the
					 * field is empty
					 */
					tf.setNullRepresentation("");

					/* Add a validator for postalCode and make it required */
					tf.addValidator(new PostalCodeValidator());
					tf.setRequired(true);
				} else if (propertyId.equals("email")) {
					/* Add a validator for email and make it required */
					field.addValidator(new EmailValidator());
					field.setRequired(true);

				}

				field.setWidth("200px");
				return field;
			}
		});
	}

	public void buttonClick(ClickEvent event) {
		Button source = event.getButton();

		if (source == save) {
			/* If the given input is not valid there is no point in continuing */
			if (!isValid()) {
				return;
			}

			commit();
			if (newContactMode) {
				/* We need to add the new person to the container */
				Item addedItem = app.getDataSource().addItem(newPerson);
				/*
				 * We must update the form to use the Item from our datasource
				 * as we are now in edit mode (no longer in add mode)
				 */
				setItemDataSource(addedItem);

				newContactMode = false;
			}
			setReadOnly(true);
		} else if (source == cancel) {
			if (newContactMode) {
				newContactMode = false;
				/* Clear the form and make it invisible */
				setItemDataSource(null);
			} else {
				discard();
			}
			setReadOnly(true);
		} else if (source == edit) {
			setReadOnly(false);
		}
	}

	@Override
	public void setItemDataSource(Item newDataSource) {
		newContactMode = false;

		if (newDataSource != null) {
			List<Object> orderedProperties = Arrays
					.asList(PersonContainer.NATURAL_COL_ORDER);
			super.setItemDataSource(newDataSource, orderedProperties);

			setReadOnly(true);
			getFooter().setVisible(true);
		} else {
			super.setItemDataSource(null);
			getFooter().setVisible(false);
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		save.setVisible(!readOnly);
		cancel.setVisible(!readOnly);
		edit.setVisible(readOnly);
	}

	public void addContact() {
		// Create a temporary item for the form
		newPerson = new Person();
		setItemDataSource(new BeanItem(newPerson));
		newContactMode = true;
		setReadOnly(false);
	}

}