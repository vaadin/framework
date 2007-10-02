/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.demo.features;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.*;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.*;
import com.itmill.toolkit.terminal.*;
import com.itmill.toolkit.ui.*;

public class PropertyPanel extends Panel implements Button.ClickListener,
		Property.ValueChangeListener {

	private Select addComponent;

	private OrderedLayout formsLayout = new OrderedLayout();

	private LinkedList forms = new LinkedList();

	private Button setButton = new Button("Set", this);

	private Button discardButton = new Button("Discard changes", this);

	private Table allProperties = new Table();

	private Object objectToConfigure;

	private BeanItem config;

	protected static final int COLUMNS = 3;

	/** Contruct new property panel for configuring given object. */
	public PropertyPanel(Object objectToConfigure) {
		super();

		// Layout
		setCaption("Properties");
		addComponent(formsLayout);

		// Target object
		this.objectToConfigure = objectToConfigure;
		config = new BeanItem(objectToConfigure);

		// Control buttons
		OrderedLayout buttons = new OrderedLayout(
				OrderedLayout.ORIENTATION_HORIZONTAL);
		buttons.addComponent(setButton);
		buttons.addComponent(discardButton);
		addComponent(buttons);

		// Add default properties
		addBasicComponentProperties();
		if (objectToConfigure instanceof Select)
			addSelectProperties();
		if (objectToConfigure instanceof AbstractField
				&& !(objectToConfigure instanceof Table || objectToConfigure instanceof Tree))
			addFieldProperties();
		if ((objectToConfigure instanceof AbstractComponentContainer)
				&& !(objectToConfigure instanceof FrameWindow))
			addComponentContainerProperties();

		// The list of all properties
		allProperties.addContainerProperty("Name", String.class, "");
		allProperties.addContainerProperty("Type", String.class, "");
		allProperties.addContainerProperty("R/W", String.class, "");
		allProperties.addContainerProperty("Demo", String.class, "");
		allProperties.setColumnAlignments(new String[] { Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_CENTER });
		allProperties.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
		allProperties.setPageLength(0);
		allProperties.setWidth(100);
		allProperties.setWidthUnits(Table.UNITS_PERCENTAGE);
		// TODO Add as soon as supported
		//allProperties.setHeight(100);
		//allProperties.setHeightUnits(Table.UNITS_PERCENTAGE);
		updatePropertyList();

	}

	/** Add a formful of properties to property panel */
	public void addProperties(String propertySetCaption, Form properties) {

		// Create new panel containing the form
		Panel p = new Panel();
		p.setCaption(propertySetCaption);
		p.setStyle("light");
		p.addComponent(properties);
		formsLayout.addComponent(p);

		// Setup buffering
		setButton.dependsOn(properties);
		discardButton.dependsOn(properties);
		properties.setWriteThrough(false);
		// TODO change this to false, and test it is suitable for FeatureBrowser
		// demo
		properties.setReadThrough(true);

		// Maintain property lists
		forms.add(properties);
		updatePropertyList();
	}

	/** Recreate property list contents */
	public void updatePropertyList() {

		allProperties.removeAllItems();

		// Collect demoed properties
		HashSet listed = new HashSet();
		for (Iterator i = forms.iterator(); i.hasNext();)
			listed.addAll(((Form) i.next()).getItemPropertyIds());

		// Resolve all properties
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(objectToConfigure.getClass());
		} catch (IntrospectionException e) {
			throw new RuntimeException(e.toString());
		}
		PropertyDescriptor[] pd = info.getPropertyDescriptors();

		// Fill the table
		for (int i = 0; i < pd.length; i++) {
			allProperties.addItem(new Object[] { pd[i].getName(),
					pd[i].getPropertyType().getName(),
					(pd[i].getWriteMethod() == null ? "R" : "R/W"),
					(listed.contains(pd[i].getName()) ? "x" : "") }, pd[i]);
		}
	}

	/** Add basic properties implemented most often by abstract component */
	private void addBasicComponentProperties() {

		// Set of properties
		Form set = createBeanPropertySet(new String[] { "caption", "icon",
				"componentError", "description", "enabled", "visible", "style",
				"readOnly", "immediate" });

		// Icon
		set.replaceWithSelect("icon", new Object[] { null,
				new ThemeResource("icon/files/file.gif") }, new Object[] {
				"No icon", "Sample icon" });

		// Component error
		Throwable sampleException;
		try {
			throw new NullPointerException("sample exception");
		} catch (NullPointerException e) {
			sampleException = e;
		}
		set
				.replaceWithSelect(
						"componentError",
						new Object[] {
								null,
								new UserError("Sample text error message."),
								new UserError(
										"<h3>Error message formatting</h3><p>Error messages can "
												+ "contain any UIDL formatting, like: <ul><li><b>Bold"
												+ "</b></li><li><i>Italic</i></li></ul></p>",
										UserError.CONTENT_UIDL,
										ErrorMessage.INFORMATION),
								new SystemError(
										"This is an example of exception error reposting",
										sampleException) },
						new Object[] { "No error", "Sample text error",
								"Sample Formatted error", "Sample System Error" });

		// Style
		String currentStyle = ((Component) objectToConfigure).getStyle();
		if (currentStyle == null)
			set.replaceWithSelect("style", new Object[] { null },
					new Object[] { "Default" }).setNewItemsAllowed(true);
		else
			set.replaceWithSelect("style", new Object[] { null, currentStyle },
					new Object[] { "Default", currentStyle })
					.setNewItemsAllowed(true);

		// Set up descriptions
		set
				.getField("caption")
				.setDescription(
						"Component caption is the title of the component. Usage of the caption is optional and the "
								+ "exact behavior of the propery is defined by the component. Setting caption null "
								+ "or empty disables the caption.");
		set
				.getField("enabled")
				.setDescription(
						"Enabled property controls the usage of the component. If the component is disabled (enabled=false),"
								+ " it can not receive any events from the terminal. In most cases it makes the usage"
								+ " of the component easier, if the component visually looks disbled (for example is grayed), "
								+ "when it can not be used.");
		set
				.getField("icon")
				.setDescription(
						"Icon of the component selects the main icon of the component. The usage of the icon is identical "
								+ "to caption and in most components caption and icon are kept together. Icons can be "
								+ "loaded from any resources (see Terminal/Resources for more information). Some components "
								+ "contain more than just the captions icon. Those icons are controlled through their "
								+ "own properties.");
		set
				.getField("visible")
				.setDescription(
						"Visibility property says if the component is renreded or not. Invisible components are implicitly "
								+ "disabled, as there is no visible user interface to send event.");
		set
				.getField("description")
				.setDescription(
						"Description is designed to allow easy addition of short tooltips, like this. Like the caption,"
								+ " setting description null or empty disables the description.");
		set
				.getField("readOnly")
				.setDescription(
						"Those components that have internal state that can be written are settable to readOnly-mode,"
								+ " where the object can only be read, not written.");
		set
				.getField("componentError")
				.setDescription(
						"IT Mill Toolkit supports extensive error reporting. One part of the error reporting are component"
								+ " errors that can be controlled by the programmer. This example only contains couple of "
								+ "sample errors; to get the full picture, read browse ErrorMessage-interface implementors "
								+ "API documentation.");
		set
				.getField("immediate")
				.setDescription(
						"Not all terminals can send the events immediately to server from all action. Web is the most "
								+ "typical environment where many events (like textfield changed) are not sent to server, "
								+ "before they are explicitly submitted. Setting immediate property true (by default this "
								+ "is false for most components), the programmer can assure that the application is"
								+ " notified as soon as possible about the value change in this component.");
		set
				.getField("style")
				.setDescription(
						"Themes specify the overall looks of the user interface. In addition component can have a set of "
								+ "styles, that can be visually very different (like datefield calendar- and text-styles), "
								+ "but contain the same logical functionality. As a rule of thumb, theme specifies if a "
								+ "component is blue or yellow and style determines how the component is used.");

		// Add created fields to property panel
		addProperties("Component Basics", set);

		// Customization for Window component
		if (objectToConfigure instanceof Window) {
			disableField(set.getField("enabled"), new Boolean(true));
			disableField(set.getField("visible"), new Boolean(true));
			disableField(set.getField("componentError"));
			disableField(set.getField("icon"));
		}
	}

	/** Add properties for selecting */
	private void addSelectProperties() {
		Form set = createBeanPropertySet(new String[] { "newItemsAllowed",
				"lazyLoading", "multiSelect" });
		addProperties("Select Properties", set);

		set.getField("multiSelect").setDescription(
				"Specified if multiple items can be selected at once.");
		set
				.getField("newItemsAllowed")
				.setDescription(
						"Select component (but not Tree or Table) can allow the user to directly "
								+ "add new items to set of options. The new items are constrained to be "
								+ "strings and thus feature only applies to simple lists.");
		Button ll = (Button) set.getField("lazyLoading");
		ll
				.setDescription("In Ajax rendering mode select supports lazy loading and filtering of options.");
		ll.addListener((ValueChangeListener) this);
		ll.setImmediate(true);
		if (((Boolean) ll.getValue()).booleanValue()) {
			set.getField("multiSelect").setVisible(false);
			set.getField("newItemsAllowed").setVisible(false);
		}
		if (objectToConfigure instanceof Tree
				|| objectToConfigure instanceof Table) {
			set.removeItemProperty("newItemsAllowed");
			set.removeItemProperty("lazyLoading");
		}
	}

	/** Field special properties */
	private void addFieldProperties() {
		// TODO bug #211 states that setFocus works only for Button and
		// Textfield UI components
		Form set = new Form(new GridLayout(COLUMNS, 1));
		set.addField("focus", new Button("Focus", objectToConfigure, "focus"));
		set.getField("focus").setDescription(
				"Focus the cursor to this field. Not all "
						+ "components and/or terminals support this feature.");
		addProperties("Field Features", set);
	}

	/**
	 * Add and remove some miscellaneous example component to/from component
	 * container
	 */
	private void addComponentContainerProperties() {
		Form set = new Form(new OrderedLayout(
				OrderedLayout.ORIENTATION_VERTICAL));

		addComponent = new Select();
		addComponent.setImmediate(true);
		addComponent.addItem("Add component to container");
		addComponent.setNullSelectionItemId("Add component to container");
		addComponent.addItem("Text field");
		addComponent.addItem("Option group");
		addComponent.addListener(this);

		set.addField("component adder", addComponent);
		set.addField("remove all components", new Button(
				"Remove all components", objectToConfigure,
				"removeAllComponents"));

		addProperties("ComponentContainer Features", set);
	}

	/** Value change listener for listening selections */
	public void valueChange(Property.ValueChangeEvent event) {

		// FIXME: navigation statistics
		try {
			FeatureUtil.debug(getApplication().getUser().toString(),
					"valueChange "
							+ ((AbstractComponent) event.getProperty())
									.getTag() + ", " + event.getProperty());
		} catch (Exception e) {
			// ignored, should never happen
		}

		// Adding components to component container
		if (event.getProperty() == addComponent) {
			String value = (String) addComponent.getValue();

			if (value != null) {
				// TextField component
				if (value.equals("Text field"))
					((AbstractComponentContainer) objectToConfigure)
							.addComponent(new TextField("Test field"));

				// DateField time style
				if (value.equals("Time")) {
					DateField d = new DateField("Time", new Date());
					d
							.setDescription("This is a DateField-component with text-style");
					d.setResolution(DateField.RESOLUTION_MIN);
					d.setStyle("text");
					((AbstractComponentContainer) objectToConfigure)
							.addComponent(d);
				}

				// Date field calendar style
				if (value.equals("Calendar")) {
					DateField c = new DateField("Calendar", new Date());
					c
							.setDescription("DateField-component with calendar-style and day-resolution");
					c.setStyle("calendar");
					c.setResolution(DateField.RESOLUTION_DAY);
					((AbstractComponentContainer) objectToConfigure)
							.addComponent(c);
				}

				// Select option group style
				if (value.equals("Option group")) {
					Select s = new Select("Options");
					s.setDescription("Select-component with optiongroup-style");
					s.addItem("Linux");
					s.addItem("Windows");
					s.addItem("Solaris");
					s.addItem("Symbian");
					s.setStyle("optiongroup");

					((AbstractComponentContainer) objectToConfigure)
							.addComponent(s);
				}

				addComponent.setValue(null);
			}
		} else if (event.getProperty() == getField("lazyLoading")) {
			boolean newValue = ((Boolean) event.getProperty().getValue())
					.booleanValue();
			Field multiselect = getField("multiSelect");
			Field newitems = getField("newItemsAllowed");
			if (newValue) {
				newitems.setValue(Boolean.FALSE);
				newitems.setVisible(false);
				multiselect.setValue(Boolean.FALSE);
				multiselect.setVisible(false);
			} else {
				newitems.setVisible(true);
				multiselect.setVisible(true);
			}
		}
	}

	/** Handle all button clicks for this panel */
	public void buttonClick(Button.ClickEvent event) {
		// FIXME: navigation statistics
		try {
			FeatureUtil.debug(getApplication().getUser().toString(),
					"buttonClick " + event.getButton().getTag() + ", "
							+ event.getButton().getCaption() + ", "
							+ event.getButton().getValue());
		} catch (Exception e) {
			// ignored, should never happen
		}
		// Commit all changed on all forms
		if (event.getButton() == setButton) {
			commit();
		}

		// Discard all changed on all forms
		if (event.getButton() == discardButton) {
			for (Iterator i = forms.iterator(); i.hasNext();)
				((Form) i.next()).discard();
		}

	}

	/**
	 * Helper function for creating forms from array of propety names.
	 */
	protected Form createBeanPropertySet(String names[]) {

		Form set = new Form(new OrderedLayout(
				OrderedLayout.ORIENTATION_VERTICAL));

		for (int i = 0; i < names.length; i++) {
			Property p = config.getItemProperty(names[i]);
			if (p != null) {
				set.addItemProperty(names[i], p);
				Field f = set.getField(names[i]);
				if (f instanceof TextField) {
					if (Integer.class.equals(p.getType()))
						((TextField) f).setColumns(4);
					else {
						((TextField) f).setNullSettingAllowed(true);
						((TextField) f).setColumns(24);
					}
				}
			}
		}

		return set;
	}

	/** Find a field from all forms */
	public Field getField(Object propertyId) {
		for (Iterator i = forms.iterator(); i.hasNext();) {
			Form f = (Form) i.next();
			Field af = f.getField(propertyId);
			if (af != null)
				return af;
		}
		return null;
	}

	public Table getAllProperties() {
		return allProperties;
	}

	protected void commit() {
		for (Iterator i = forms.iterator(); i.hasNext();)
			((Form) i.next()).commit();
	}

	private void disableField(Field field) {
		field.setEnabled(false);
		field.setReadOnly(true);
	}

	private void disableField(Field field, Object value) {
		field.setValue(value);
		disableField(field);
	}

}
