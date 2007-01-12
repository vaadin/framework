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

package com.itmill.toolkit.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.*;
import com.itmill.toolkit.data.Validator.InvalidValueException;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/** Form component provides easy way of creating and managing sets fields.
 * 
 * <p>Form is a container for fields implementing {@link Field} interface. It
 * provides support for any layouts and provides buffering interface for easy
 * connection of commit- and discard buttons. All the form fields can be
 * customized by adding validators, setting captions and icons, setting
 * immediateness, etc. Also direct mechanism for replacing existing fields with
 * selections is given.
 * </p>
 * 
 * <p>Form provides customizable editor for classes implementing
 * {@link com.itmill.toolkit.data.Item} interface. Also the form itself
 * implements this interface for easier connectivity to other items.
 * To use the form as editor for an item, just connect the item to
 * form with {@link Form#setItemDataSource(Item)}. If only a part of the
 * item needs to be edited, {@link Form#setItemDataSource(Item,Collection)}
 * can be used instead. After the item has been connected to the form,
 * the automatically created fields can be customized and new fields can
 * be added. If you need to connect a class that does not implement
 * {@link com.itmill.toolkit.data.Item} interface, most properties of any
 * class following bean pattern, can be accessed trough 
 * {@link com.itmill.toolkit.data.util.BeanItem}.</p>
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Form
	extends AbstractField
	implements Item.Editor, Buffered, Item, Validatable {

	private Object propertyValue;

	/** Layout of the form */
	private Layout layout;

	/** Item connected to this form as datasource */
	private Item itemDatasource;

	/** Ordered list of property ids in this editor */
	private LinkedList propertyIds = new LinkedList();

	/** Current buffered source exception */
	private Buffered.SourceException currentBufferedSourceException = null;

	/** Is the form in write trough mode */
	private boolean writeThrough = true;

	/** Is the form in read trough mode */
	private boolean readThrough = true;

	/** Mapping from propertyName to corresponding field */
	private HashMap fields = new HashMap();

	/** Field factory for this form */
	private FieldFactory fieldFactory;

	/** Registered Validators */
	private LinkedList validators;

	/** Visible item properties */
	private Collection visibleItemProperties;

	/** Contruct a new form with default layout.
	 * 
	 * <p>By default the form uses <code>OrderedLayout</code>
	 * with <code>form</code>-style.
	 * 
	 * @param formLayout The layout of the form.
	 */
	public Form() {
		this(null);
	}

	/** Contruct a new form with given layout.
	 * 
	 * @param formLayout The layout of the form.
	 */
	public Form(Layout formLayout) {
		this(formLayout, new BaseFieldFactory());
	}

	/** Contruct a new form with given layout and FieldFactory.
	 *
	 * @param formLayout The layout of the form.
	 * @param fieldFactory FieldFactory of the form
	 */
	public Form(Layout formLayout, FieldFactory fieldFactory) {
		super();
		setLayout(formLayout);
		setFieldFactory(fieldFactory);
	}

	/* Documented in interface */
	public String getTag() {
		return "component";
	}

	/* Documented in interface */
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		layout.paint(target);

	}

	/* Commit changes to the data source
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void commit() throws Buffered.SourceException {

		LinkedList problems = null;

		// Try to commit all
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			try {
				Field f = ((Field) fields.get(i.next()));
				//Commit only non-readonly fields.
				if (!f.isReadOnly()) {
					f.commit();
				}
			} catch (Buffered.SourceException e) {
				if (problems == null)
					problems = new LinkedList();
				problems.add(e);
			}

		// No problems occurred
		if (problems == null) {
			if (currentBufferedSourceException != null) {
				currentBufferedSourceException = null;
				requestRepaint();
			}
			return;
		}

		// Commit problems
		Throwable[] causes = new Throwable[problems.size()];
		int index = 0;
		for (Iterator i = problems.iterator(); i.hasNext();)
			causes[index++] = (Throwable) i.next();
		Buffered.SourceException e = new Buffered.SourceException(this, causes);
		currentBufferedSourceException = e;
		requestRepaint();
		throw e;
	}

	/* Discard local changes and refresh values from the data source
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void discard() throws Buffered.SourceException {

		LinkedList problems = null;

		// Try to discard all changes
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			try {
				((Field) fields.get(i.next())).discard();
			} catch (Buffered.SourceException e) {
				if (problems == null)
					problems = new LinkedList();
				problems.add(e);
			}

		// No problems occurred
		if (problems == null) {
			if (currentBufferedSourceException != null) {
				currentBufferedSourceException = null;
				requestRepaint();
			}
			return;
		}

		// Discard problems occurred		
		Throwable[] causes = new Throwable[problems.size()];
		int index = 0;
		for (Iterator i = problems.iterator(); i.hasNext();)
			causes[index++] = (Throwable) i.next();
		Buffered.SourceException e = new Buffered.SourceException(this, causes);
		currentBufferedSourceException = e;
		requestRepaint();
		throw e;
	}

	/* Is the object modified but not committed?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isModified() {
		for (Iterator i = propertyIds.iterator(); i.hasNext();) {
			Field f = (Field) fields.get(i.next()); 
			if (f != null && f.isModified())
				return true;
			
		}
		return false;
	}

	/* Is the editor in a read-through mode?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isReadThrough() {
		return readThrough;
	}

	/* Is the editor in a write-through mode?
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public boolean isWriteThrough() {
		return writeThrough;
	}

	/* Sets the editor's read-through mode to the specified status.
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void setReadThrough(boolean readThrough) {
		if (readThrough != this.readThrough) {
			this.readThrough = readThrough;
			for (Iterator i = propertyIds.iterator(); i.hasNext();)
				 ((Field) fields.get(i.next())).setReadThrough(readThrough);
		}
	}

	/* Sets the editor's read-through mode to the specified status.
	 * Don't add a JavaDoc comment here, we use the default one from the
	 * interface.
	 */
	public void setWriteThrough(boolean writeThrough) {
		if (writeThrough != this.writeThrough) {
			this.writeThrough = writeThrough;
			for (Iterator i = propertyIds.iterator(); i.hasNext();)
				 ((Field) fields.get(i.next())).setWriteThrough(writeThrough);
		}
	}

	/** Add a new property to form and create corresponding field.
	 * 
	 * @see com.itmill.toolkit.data.Item#addItemProperty(Object, Property)
	 */
	public boolean addItemProperty(Object id, Property property) {

		// Check inputs
		if (id == null || property == null)
			throw new NullPointerException("Id and property must be non-null");

		// Check that the property id is not reserved
		if (propertyIds.contains(id))
			return false;

		// Get suitable field
		Field field = this.fieldFactory.createField(property, this);
		if (field == null)
			return false;

		// Configure the field
		try {
			field.setPropertyDataSource(property);
			String caption = id.toString();
			if (caption.length() > 50)
				caption = caption.substring(0, 47) + "...";
			if (caption.length() > 0)
				caption =
					""
						+ Character.toUpperCase(caption.charAt(0))
						+ caption.substring(1, caption.length());
			field.setCaption(caption);
		} catch (Throwable ignored) {
			return false;
		}

		addField(id, field);

		return true;
	}

	/** Add field to form. 
	 * 
	 * <p>The property id must not be already used in the form.  
	 * </p>
	 * 
	 * <p>This field is added to the form layout in the default position
	 * (the position used by {@link Layout#addComponent(Component)} method.
	 * In the special case that the underlying layout is a custom layout,
	 * string representation of the property id is used instead of the
	 * default location.</p>
	 * 
	 * @param propertyId Property id the the field.
	 * @param field New field added to the form.
	 */
	public void addField(Object propertyId, Field field) {

		if (propertyId != null && field != null) {
			this.dependsOn(field);
			field.dependsOn(this);
			fields.put(propertyId, field);
			propertyIds.addLast(propertyId);
			field.setReadThrough(readThrough);
			field.setWriteThrough(writeThrough);

			if (layout instanceof CustomLayout)
				((CustomLayout) layout).addComponent(
					field,
					propertyId.toString());
			else
				layout.addComponent(field);

			requestRepaint();
		}
	}

	/** The property identified by the property id.
	 * 
	 * <p>The property data source of the field specified with
	 * property id is returned. If there is a (with specified property id) 
	 * having no data source,
	 * the field is returned instead of the data source.</p>
	 * 
	 * @see com.itmill.toolkit.data.Item#getItemProperty(Object)
	 */
	public Property getItemProperty(Object id) {
		Field field = (Field) fields.get(id);
		if (field == null)
			return null;
		Property property = field.getPropertyDataSource();

		if (property != null)
			return property;
		else
			return field;
	}

	/** Get the field identified by the propertyid */
	public Field getField(Object propertyId) {
		return (Field) fields.get(propertyId);
	}

	/* Documented in interface */
	public Collection getItemPropertyIds() {
		return Collections.unmodifiableCollection(propertyIds);
	}

	/** Removes the property and corresponding field from the form.
	 * 
	 * @see com.itmill.toolkit.data.Item#removeItemProperty(Object)
	 */
	public boolean removeItemProperty(Object id) {

		Field field = (Field) fields.get(id);

		if (field != null) {
			propertyIds.remove(id);
			fields.remove(id);
			this.removeDirectDependency(field);
			field.removeDirectDependency(this);
			layout.removeComponent(field);
			return true;
		}

		return false;
	}

	/** Removes all properties and fields from the form.
	 * 
	 * @return Success of the operation. Removal of all fields succeeded 
	 * if (and only if) the return value is true.
	 */
	public boolean removeAllProperties() {
		Object[] properties = propertyIds.toArray();
		boolean success = true;

		for (int i = 0; i < properties.length; i++)
			if (!removeItemProperty(properties[i]))
				success = false;

		return success;
	}

	/* Documented in the interface */
	public Item getItemDataSource() {
		return itemDatasource;
	}

	/** Set the item datasource for the form.
	 * 
	 * <p>Setting item datasource clears any fields, the form might contain
	 * and adds all the properties as fields to the form.</p>
	 * 
	 * @see com.itmill.toolkit.data.Item.Viewer#setItemDataSource(Item)
	 */
	public void setItemDataSource(Item newDataSource) {
		setItemDataSource(
			newDataSource,
			newDataSource != null ? newDataSource.getItemPropertyIds() : null);
	}

	/** Set the item datasource for the form, but limit the form contents
	 * to specified properties of the item.
	 * 
	 * <p>Setting item datasource clears any fields, the form might contain
	 * and adds the specified the properties as fields to the form, in the
	 * specified order.</p>
	 * 
	 * @see com.itmill.toolkit.data.Item.Viewer#setItemDataSource(Item)
	 */
	public void setItemDataSource(Item newDataSource, Collection propertyIds) {

		// Remove all fields first from the form
		removeAllProperties();

		// Set the datasource
		itemDatasource = newDataSource;

		//If the new datasource is null, just set null datasource
		if (itemDatasource == null)
			return;

		// Add all the properties to this form
		for (Iterator i = propertyIds.iterator(); i.hasNext();) {
			Object id = i.next();
			Property property = itemDatasource.getItemProperty(id);
			if (id != null && property != null) {
				Field f =
					this.fieldFactory.createField(itemDatasource, id, this);
				if (f != null) {
					f.setPropertyDataSource(property);
					addField(id, f);
				}
			}
		}
	}

	/** Get the layout of the form. 
	 * 
	 * <p>By default form uses <code>OrderedLayout</code> with <code>form</code>-style.</p>
	 * 
	 * @return Layout of the form.
	 */
	public Layout getLayout() {
		return layout;
	}

	/** Set the layout of the form.
	 *
	 * <p>By default form uses <code>OrderedLayout</code> with <code>form</code>-style.</p>
	 *
	 * @param layout Layout of the form.
	 */
	public void setLayout(Layout newLayout) {

		// Use orderedlayout by default
		if (newLayout == null) {
			newLayout = new OrderedLayout();
			newLayout.setStyle("form");
		}

		// Move components from previous layout
		if (this.layout != null) {
			newLayout.moveComponentsFrom(this.layout);
			this.layout.setParent(null);
		}

		// Replace the previous layout
		newLayout.setParent(this);
		this.layout = newLayout;
	}

	/** Set a form field to be selectable from static list of changes.
	 * 
	 * <p>The list values and descriptions are given as array. The value-array must contain the 
	 * current value of the field and the lengths of the arrays must match. Null values are not
	 * supported.</p>
	 * 
	 * @return The select property generated
	 */
	public Select replaceWithSelect(
		Object propertyId,
		Object[] values,
		Object[] descriptions) {

		// Check the parameters
		if (propertyId == null || values == null || descriptions == null)
			throw new NullPointerException("All parameters must be non-null");
		if (values.length != descriptions.length)
			throw new IllegalArgumentException("Value and description list are of different size");

		// Get the old field
		Field oldField = (Field) fields.get(propertyId);
		if (oldField == null)
			throw new IllegalArgumentException(
				"Field with given propertyid '"
					+ propertyId.toString()
					+ "' can not be found.");
		Object value = oldField.getValue();

		// Check that the value exists and check if the select should
		// be forced in multiselect mode
		boolean found = false;
		boolean isMultiselect = false;
		for (int i = 0; i < values.length && !found; i++)
			if (values[i] == value
				|| (value != null && value.equals(values[i])))
				found = true;
		if (value != null && !found) {
			if (value instanceof Collection) {
				for (Iterator it = ((Collection) value).iterator();
					it.hasNext();
					) {
					Object val = it.next();
					found = false;
					for (int i = 0; i < values.length && !found; i++)
						if (values[i] == val
							|| (val != null && val.equals(values[i])))
							found = true;
					if (!found) 
					throw new IllegalArgumentException(
						"Currently selected value '"
							+ val
							+ "' of property '"
							+ propertyId.toString()
							+ "' was not found");
				}
				isMultiselect = true;
			} else
				throw new IllegalArgumentException(
					"Current value '"
						+ value
						+ "' of property '"
						+ propertyId.toString()
						+ "' was not found");
		}

		// Create new field matching to old field parameters
		Select newField = new Select();
		if (isMultiselect) newField.setMultiSelect(true);
		newField.setCaption(oldField.getCaption());
		newField.setReadOnly(oldField.isReadOnly());
		newField.setReadThrough(oldField.isReadThrough());
		newField.setWriteThrough(oldField.isWriteThrough());

		// Create options list
		newField.addContainerProperty("desc", String.class, "");
		newField.setItemCaptionPropertyId("desc");
		for (int i = 0; i < values.length; i++) {
			Object id = values[i];
			if (id == null) {
				id = new Object();
				newField.setNullSelectionItemId(id);
			}
			Item item = newField.addItem(id);
			if (item != null)
				item.getItemProperty("desc").setValue(
					descriptions[i].toString());
		}

		// Set the property data source
		Property property = oldField.getPropertyDataSource();
		oldField.setPropertyDataSource(null);
		newField.setPropertyDataSource(property);

		// Replace the old field with new one
		layout.replaceComponent(oldField, newField);
		fields.put(propertyId, newField);
		this.removeDirectDependency(oldField);
		oldField.removeDirectDependency(this);
		this.dependsOn(newField);
		newField.dependsOn(this);

		return newField;
	}

	/**
	 * @see com.itmill.toolkit.ui.Component#attach()
	 */
	public void attach() {
		super.attach();
		layout.attach();
	}

	/**
	 * @see com.itmill.toolkit.ui.Component#detach()
	 */
	public void detach() {
		super.detach();
		layout.detach();
	}

	/**
	 * @see com.itmill.toolkit.data.Validatable#addValidator(com.itmill.toolkit.data.Validator)
	 */
	public void addValidator(Validator validator) {

		if (this.validators == null) {
			this.validators = new LinkedList();
		}
		this.validators.add(validator);
	}
	/**
	 * @see com.itmill.toolkit.data.Validatable#removeValidator(com.itmill.toolkit.data.Validator)
	 */
	public void removeValidator(Validator validator) {
		if (this.validators != null) {
			this.validators.remove(validator);
		}
	}
	/**
	 * @see com.itmill.toolkit.data.Validatable#getValidators()
	 */
	public Collection getValidators() {
		if (this.validators == null) {
			this.validators = new LinkedList();
		}
		return null;
	}
	/**
	 * @see com.itmill.toolkit.data.Validatable#isValid()
	 */
	public boolean isValid() {
		boolean valid = true;
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			valid &= ((Field) fields.get(i.next())).isValid();
		return valid;
	}
	/**
	 * @see com.itmill.toolkit.data.Validatable#validate()
	 */
	public void validate() throws InvalidValueException {
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			 ((Field) fields.get(i.next())).validate();
	}

	/**
	 * @see com.itmill.toolkit.data.Validatable#isInvalidAllowed()
	 */
	public boolean isInvalidAllowed() {
		return true;
	}
	/**
	 * @see com.itmill.toolkit.data.Validatable#setInvalidAllowed(boolean)
	 */
	public void setInvalidAllowed(boolean invalidValueAllowed)
		throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	/**
	 * @see com.itmill.toolkit.ui.Component#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		for (Iterator i = propertyIds.iterator(); i.hasNext();)
			 ((Field) fields.get(i.next())).setReadOnly(readOnly);
	}

	/** Set the field factory of Form.
	 *
	 * FieldFacroty is used to create fields for form properties.
	 * By default the form uses BaseFieldFactory to create Field instances.
	 *
	 * @param fieldFactory New factory used to create the fields
	 * @see Field
	 * @see FieldFactory
	 */
	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	/** Get the field factory of the form.
	 *
	 * @return FieldFactory Factory used to create the fields
	 */
	public FieldFactory getFieldFactory() {
		return this.fieldFactory;
	}

	/**
	 * @see com.itmill.toolkit.ui.AbstractField#getType()
	 */
	public Class getType() {
		if (getPropertyDataSource() != null)
			return getPropertyDataSource().getType();
		return Object.class;
	}

	/** Set the internal value.
	 * 
	 * This is relevant when the Form is used as Field.
	 * @see com.itmill.toolkit.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	protected void setInternalValue(Object newValue) {
		// Store old value
		Object oldValue = this.propertyValue;
		
		// Set the current Value
		super.setInternalValue(newValue);
		this.propertyValue = newValue;

		// Ignore form updating if data object has not changed.
		if (oldValue != newValue) {
			setFormDataSource(newValue, getVisibleItemProperties());
		}
	}

	/**Get first field in form.
	 * @return Field
	 */
	private Field getFirstField() {
		Object id = null;
		if (this.getItemPropertyIds() != null) {
			id = this.getItemPropertyIds().iterator().next();
		}
		if (id != null)
			return this.getField(id);
		return null;
	}

	/** Update the internal form datasource.
	 * 
	 * Method setFormDataSource.
	 * @param value
	 */
	protected void setFormDataSource(Object data, Collection properties) {

		// If data is an item use it.
		Item item = null;
		if (data instanceof Item) {
			item = (Item) data;
		} else if (data != null) {
			item = new BeanItem(data);
		}

		// Set the datasource to form
		if (item != null && properties != null) {
			// Show only given properties
			this.setItemDataSource(item, properties);
		} else {
			// Show all properties
			this.setItemDataSource(item);
		}
	}

	/**
	 * Returns the visibleProperties.
	 * @return Collection
	 */
	public Collection getVisibleItemProperties() {
		return visibleItemProperties;
	}

	/**
	 * Sets the visibleProperties.
	 * @param visibleProperties The visibleProperties to set
	 */
	public void setVisibleItemProperties(Collection visibleProperties) {
		this.visibleItemProperties = visibleProperties;
		Object value = getValue();
		setFormDataSource(value, getVisibleItemProperties());
	}

	/** Focuses the first field in the form.
	 * @see com.itmill.toolkit.ui.Component.Focusable#focus()
	 */
	public void focus() {
		Field f = getFirstField();
		if (f != null) {
			f.focus();
		}
	}

	/**
	 * @see com.itmill.toolkit.ui.Component.Focusable#setTabIndex(int)
	 */
	public void setTabIndex(int tabIndex) {
		super.setTabIndex(tabIndex);
		for (Iterator i = this.getItemPropertyIds().iterator(); i.hasNext();)
			 (this.getField(i.next())).setTabIndex(tabIndex);
	}
}
