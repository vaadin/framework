/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.Buffered;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Validatable;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.Validator.InvalidValueException;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.terminal.CompositeErrorMessage;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * Form component provides easy way of creating and managing sets fields.
 * 
 * <p>
 * <code>Form</code> is a container for fields implementing {@link Field}
 * interface. It provides support for any layouts and provides buffering
 * interface for easy connection of commit and discard buttons. All the form
 * fields can be customized by adding validators, setting captions and icons,
 * setting immediateness, etc. Also direct mechanism for replacing existing
 * fields with selections is given.
 * </p>
 * 
 * <p>
 * <code>Form</code> provides customizable editor for classes implementing
 * {@link com.itmill.toolkit.data.Item} interface. Also the form itself
 * implements this interface for easier connectivity to other items. To use the
 * form as editor for an item, just connect the item to form with
 * {@link Form#setItemDataSource(Item)}. If only a part of the item needs to be
 * edited, {@link Form#setItemDataSource(Item,Collection)} can be used instead.
 * After the item has been connected to the form, the automatically created
 * fields can be customized and new fields can be added. If you need to connect
 * a class that does not implement {@link com.itmill.toolkit.data.Item}
 * interface, most properties of any class following bean pattern, can be
 * accessed trough {@link com.itmill.toolkit.data.util.BeanItem}.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Form extends AbstractField implements Item.Editor, Buffered, Item,
        Validatable {

    private Object propertyValue;

    /**
     * Layout of the form.
     */
    private Layout layout;

    /**
     * Item connected to this form as datasource.
     */
    private Item itemDatasource;

    /**
     * Ordered list of property ids in this editor.
     */
    private final LinkedList propertyIds = new LinkedList();

    /**
     * Current buffered source exception.
     */
    private Buffered.SourceException currentBufferedSourceException = null;

    /**
     * Is the form in write trough mode.
     */
    private boolean writeThrough = true;

    /**
     * Is the form in read trough mode.
     */
    private boolean readThrough = true;

    /**
     * Mapping from propertyName to corresponding field.
     */
    private final HashMap fields = new HashMap();

    /**
     * Field factory for this form.
     */
    private FieldFactory fieldFactory;

    /**
     * Visible item properties.
     */
    private Collection visibleItemProperties;

    /**
     * Form needs to repaint itself if child fields value changes due possible
     * change in form validity.
     */
    private final ValueChangeListener fieldValueChangeListener = new ValueChangeListener() {
        public void valueChange(
                com.itmill.toolkit.data.Property.ValueChangeEvent event) {
            requestRepaint();
        }
    };

    private Layout formFooter;

    /**
     * Contructs a new form with default layout.
     * 
     * <p>
     * By default the form uses <code>OrderedLayout</code> with
     * <code>form</code>-style.
     * </p>
     * 
     * @param formLayout
     *                the layout of the form.
     */
    public Form() {
        this(null);
        setValidationVisible(false);
    }

    /**
     * Contructs a new form with given layout.
     * 
     * @param formLayout
     *                the layout of the form.
     */
    public Form(Layout formLayout) {
        this(formLayout, new BaseFieldFactory());
    }

    /**
     * Contructs a new form with given layout and FieldFactory.
     * 
     * @param formLayout
     *                the layout of the form.
     * @param fieldFactory
     *                the FieldFactory of the form.
     */
    public Form(Layout formLayout, FieldFactory fieldFactory) {
        super();
        setLayout(formLayout);
        setFieldFactory(fieldFactory);
        setValidationVisible(false);

    }

    /* Documented in interface */
    public String getTag() {
        return "form";
    }

    /* Documented in interface */
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        layout.paint(target);
        if (formFooter != null) {
            formFooter.paint(target);
        }
    }

    /**
     * The error message of a Form is the error of the first field with a
     * non-empty error.
     * 
     * Empty error messages of the contained fields are skipped, because an
     * empty error indicator would be confusing to the user, especially if there
     * are errors that have something to display. This is also the reason why
     * the calculation of the error message is separate from validation, because
     * validation fails also on empty errors.
     */
    public ErrorMessage getErrorMessage() {

        // Reimplement the checking of validation error by using
        // getErrorMessage() recursively instead of validate().
        ErrorMessage validationError = null;
        if (isValidationVisible()) {
            for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
                try {
                    AbstractComponent field = (AbstractComponent) fields.get(i
                            .next());
                    validationError = field.getErrorMessage();
                    if (validationError != null) {
                        // Skip empty errors
                        if ("".equals(validationError.toString())) {
                            continue;
                        }
                        break;
                    }
                } catch (ClassCastException ignored) {
                }
            }
        }

        // Check if there are any systems errors
        final ErrorMessage superError = super.getErrorMessage();

        // Return if there are no errors at all
        if (superError == null && validationError == null
                && currentBufferedSourceException == null) {
            return null;
        }

        // Throw combination of the error types
        return new CompositeErrorMessage(new ErrorMessage[] { superError,
                validationError, currentBufferedSourceException });
    }

    /*
     * Commit changes to the data source Don't add a JavaDoc comment here, we
     * use the default one from the interface.
     */
    public void commit() throws Buffered.SourceException {

        LinkedList problems = null;

        // Only commit on valid state if so requested
        if (!isInvalidCommitted() && !isValid()) {
            return;
        }

        // Try to commit all
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            try {
                final Field f = ((Field) fields.get(i.next()));
                // Commit only non-readonly fields.
                if (!f.isReadOnly()) {
                    f.commit();
                }
            } catch (final Buffered.SourceException e) {
                if (problems == null) {
                    problems = new LinkedList();
                }
                problems.add(e);
            }
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
        final Throwable[] causes = new Throwable[problems.size()];
        int index = 0;
        for (final Iterator i = problems.iterator(); i.hasNext();) {
            causes[index++] = (Throwable) i.next();
        }
        final Buffered.SourceException e = new Buffered.SourceException(this,
                causes);
        currentBufferedSourceException = e;
        requestRepaint();
        throw e;
    }

    /*
     * Discards local changes and refresh values from the data source Don't add
     * a JavaDoc comment here, we use the default one from the interface.
     */
    public void discard() throws Buffered.SourceException {

        LinkedList problems = null;

        // Try to discard all changes
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            try {
                ((Field) fields.get(i.next())).discard();
            } catch (final Buffered.SourceException e) {
                if (problems == null) {
                    problems = new LinkedList();
                }
                problems.add(e);
            }
        }

        // No problems occurred
        if (problems == null) {
            if (currentBufferedSourceException != null) {
                currentBufferedSourceException = null;
                requestRepaint();
            }
            return;
        }

        // Discards problems occurred
        final Throwable[] causes = new Throwable[problems.size()];
        int index = 0;
        for (final Iterator i = problems.iterator(); i.hasNext();) {
            causes[index++] = (Throwable) i.next();
        }
        final Buffered.SourceException e = new Buffered.SourceException(this,
                causes);
        currentBufferedSourceException = e;
        requestRepaint();
        throw e;
    }

    /*
     * Is the object modified but not committed? Don't add a JavaDoc comment
     * here, we use the default one from the interface.
     */
    public boolean isModified() {
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            final Field f = (Field) fields.get(i.next());
            if (f != null && f.isModified()) {
                return true;
            }

        }
        return false;
    }

    /*
     * Is the editor in a read-through mode? Don't add a JavaDoc comment here,
     * we use the default one from the interface.
     */
    public boolean isReadThrough() {
        return readThrough;
    }

    /*
     * Is the editor in a write-through mode? Don't add a JavaDoc comment here,
     * we use the default one from the interface.
     */
    public boolean isWriteThrough() {
        return writeThrough;
    }

    /*
     * Sets the editor's read-through mode to the specified status. Don't add a
     * JavaDoc comment here, we use the default one from the interface.
     */
    public void setReadThrough(boolean readThrough) {
        if (readThrough != this.readThrough) {
            this.readThrough = readThrough;
            for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
                ((Field) fields.get(i.next())).setReadThrough(readThrough);
            }
        }
    }

    /*
     * Sets the editor's read-through mode to the specified status. Don't add a
     * JavaDoc comment here, we use the default one from the interface.
     */
    public void setWriteThrough(boolean writeThrough) {
        if (writeThrough != this.writeThrough) {
            this.writeThrough = writeThrough;
            for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
                ((Field) fields.get(i.next())).setWriteThrough(writeThrough);
            }
        }
    }

    /**
     * Adds a new property to form and create corresponding field.
     * 
     * @see com.itmill.toolkit.data.Item#addItemProperty(Object, Property)
     */
    public boolean addItemProperty(Object id, Property property) {

        // Checks inputs
        if (id == null || property == null) {
            throw new NullPointerException("Id and property must be non-null");
        }

        // Checks that the property id is not reserved
        if (propertyIds.contains(id)) {
            return false;
        }

        // Gets suitable field
        final Field field = fieldFactory.createField(property, this);
        if (field == null) {
            return false;
        }

        // Configures the field
        try {
            field.setPropertyDataSource(property);
            String caption = id.toString();
            if (caption.length() > 50) {
                caption = caption.substring(0, 47) + "...";
            }
            if (caption.length() > 0) {
                caption = "" + Character.toUpperCase(caption.charAt(0))
                        + caption.substring(1, caption.length());
            }
            field.setCaption(caption);
        } catch (final Throwable ignored) {
            return false;
        }

        addField(id, field);

        return true;
    }

    /**
     * Adds the field to form.
     * 
     * <p>
     * The property id must not be already used in the form.
     * </p>
     * 
     * <p>
     * This field is added to the form layout in the default position (the
     * position used by {@link Layout#addComponent(Component)} method. In the
     * special case that the underlying layout is a custom layout, string
     * representation of the property id is used instead of the default
     * location.
     * </p>
     * 
     * @param propertyId
     *                the Property id the the field.
     * @param field
     *                the New field added to the form.
     */
    public void addField(Object propertyId, Field field) {

        if (propertyId != null && field != null) {
            fields.put(propertyId, field);
            field.addListener(fieldValueChangeListener);
            propertyIds.addLast(propertyId);
            field.setReadThrough(readThrough);
            field.setWriteThrough(writeThrough);
            if (isImmediate() && field instanceof AbstractComponent) {
                ((AbstractComponent) field).setImmediate(true);
            }
            if (layout instanceof CustomLayout) {
                ((CustomLayout) layout).addComponent(field, propertyId
                        .toString());
            } else {
                layout.addComponent(field);
            }

            requestRepaint();
        }
    }

    /**
     * The property identified by the property id.
     * 
     * <p>
     * The property data source of the field specified with property id is
     * returned. If there is a (with specified property id) having no data
     * source, the field is returned instead of the data source.
     * </p>
     * 
     * @see com.itmill.toolkit.data.Item#getItemProperty(Object)
     */
    public Property getItemProperty(Object id) {
        final Field field = (Field) fields.get(id);
        if (field == null) {
            return null;
        }
        final Property property = field.getPropertyDataSource();

        if (property != null) {
            return property;
        } else {
            return field;
        }
    }

    /**
     * Gets the field identified by the propertyid.
     * 
     * @param propertyId
     *                the id of the property.
     */
    public Field getField(Object propertyId) {
        return (Field) fields.get(propertyId);
    }

    /* Documented in interface */
    public Collection getItemPropertyIds() {
        return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * Removes the property and corresponding field from the form.
     * 
     * @see com.itmill.toolkit.data.Item#removeItemProperty(Object)
     */
    public boolean removeItemProperty(Object id) {

        final Field field = (Field) fields.get(id);

        if (field != null) {
            propertyIds.remove(id);
            fields.remove(id);
            layout.removeComponent(field);
            field.removeListener(fieldValueChangeListener);
            return true;
        }

        return false;
    }

    /**
     * Removes all properties and fields from the form.
     * 
     * @return the Success of the operation. Removal of all fields succeeded if
     *         (and only if) the return value is <code>true</code>.
     */
    public boolean removeAllProperties() {
        final Object[] properties = propertyIds.toArray();
        boolean success = true;

        for (int i = 0; i < properties.length; i++) {
            if (!removeItemProperty(properties[i])) {
                success = false;
            }
        }

        return success;
    }

    /* Documented in the interface */
    public Item getItemDataSource() {
        return itemDatasource;
    }

    /**
     * Sets the item datasource for the form.
     * 
     * <p>
     * Setting item datasource clears any fields, the form might contain and
     * adds all the properties as fields to the form.
     * </p>
     * 
     * @see com.itmill.toolkit.data.Item.Viewer#setItemDataSource(Item)
     */
    public void setItemDataSource(Item newDataSource) {
        setItemDataSource(newDataSource, newDataSource != null ? newDataSource
                .getItemPropertyIds() : null);
    }

    /**
     * Set the item datasource for the form, but limit the form contents to
     * specified properties of the item.
     * 
     * <p>
     * Setting item datasource clears any fields, the form might contain and
     * adds the specified the properties as fields to the form, in the specified
     * order.
     * </p>
     * 
     * @see com.itmill.toolkit.data.Item.Viewer#setItemDataSource(Item)
     */
    public void setItemDataSource(Item newDataSource, Collection propertyIds) {

        // Removes all fields first from the form
        removeAllProperties();

        // Sets the datasource
        itemDatasource = newDataSource;

        // If the new datasource is null, just set null datasource
        if (itemDatasource == null) {
            return;
        }

        // Adds all the properties to this form
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            final Object id = i.next();
            final Property property = itemDatasource.getItemProperty(id);
            if (id != null && property != null) {
                final Field f = fieldFactory.createField(itemDatasource, id,
                        this);
                if (f != null) {
                    f.setPropertyDataSource(property);
                    addField(id, f);
                }
            }
        }
    }

    /**
     * Gets the layout of the form.
     * 
     * <p>
     * By default form uses <code>OrderedLayout</code> with <code>form</code>-style.
     * </p>
     * 
     * @return the Layout of the form.
     */
    public Layout getLayout() {
        return layout;
    }

    /**
     * Sets the layout of the form.
     * 
     * <p>
     * By default form uses <code>OrderedLayout</code> with <code>form</code>-style.
     * </p>
     * 
     * @param newLayout
     *                the Layout of the form.
     */
    public void setLayout(Layout newLayout) {

        // Use orderedlayout by default
        if (newLayout == null) {
            newLayout = new FormLayout();
        }

        // Move components from previous layout
        if (layout != null) {
            newLayout.moveComponentsFrom(layout);
            layout.setParent(null);
        }

        // Replace the previous layout
        newLayout.setParent(this);
        layout = newLayout;
    }

    /**
     * Sets the form field to be selectable from static list of changes.
     * 
     * <p>
     * The list values and descriptions are given as array. The value-array must
     * contain the current value of the field and the lengths of the arrays must
     * match. Null values are not supported.
     * </p>
     * 
     * @param propertyId
     *                the id of the property.
     * @param values
     * @param descriptions
     * @return the select property generated
     */
    public Select replaceWithSelect(Object propertyId, Object[] values,
            Object[] descriptions) {

        // Checks the parameters
        if (propertyId == null || values == null || descriptions == null) {
            throw new NullPointerException("All parameters must be non-null");
        }
        if (values.length != descriptions.length) {
            throw new IllegalArgumentException(
                    "Value and description list are of different size");
        }

        // Gets the old field
        final Field oldField = (Field) fields.get(propertyId);
        if (oldField == null) {
            throw new IllegalArgumentException("Field with given propertyid '"
                    + propertyId.toString() + "' can not be found.");
        }
        final Object value = oldField.getValue();

        // Checks that the value exists and check if the select should
        // be forced in multiselect mode
        boolean found = false;
        boolean isMultiselect = false;
        for (int i = 0; i < values.length && !found; i++) {
            if (values[i] == value
                    || (value != null && value.equals(values[i]))) {
                found = true;
            }
        }
        if (value != null && !found) {
            if (value instanceof Collection) {
                for (final Iterator it = ((Collection) value).iterator(); it
                        .hasNext();) {
                    final Object val = it.next();
                    found = false;
                    for (int i = 0; i < values.length && !found; i++) {
                        if (values[i] == val
                                || (val != null && val.equals(values[i]))) {
                            found = true;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException(
                                "Currently selected value '" + val
                                        + "' of property '"
                                        + propertyId.toString()
                                        + "' was not found");
                    }
                }
                isMultiselect = true;
            } else {
                throw new IllegalArgumentException("Current value '" + value
                        + "' of property '" + propertyId.toString()
                        + "' was not found");
            }
        }

        // Creates the new field matching to old field parameters
        final Select newField = new Select();
        if (isMultiselect) {
            newField.setMultiSelect(true);
        }
        newField.setCaption(oldField.getCaption());
        newField.setReadOnly(oldField.isReadOnly());
        newField.setReadThrough(oldField.isReadThrough());
        newField.setWriteThrough(oldField.isWriteThrough());

        // Creates the options list
        newField.addContainerProperty("desc", String.class, "");
        newField.setItemCaptionPropertyId("desc");
        for (int i = 0; i < values.length; i++) {
            Object id = values[i];
            if (id == null) {
                id = new Object();
                newField.setNullSelectionItemId(id);
            }
            final Item item = newField.addItem(id);
            if (item != null) {
                item.getItemProperty("desc").setValue(
                        descriptions[i].toString());
            }
        }

        // Sets the property data source
        final Property property = oldField.getPropertyDataSource();
        oldField.setPropertyDataSource(null);
        newField.setPropertyDataSource(property);

        // Replaces the old field with new one
        layout.replaceComponent(oldField, newField);
        fields.put(propertyId, newField);
        newField.addListener(fieldValueChangeListener);
        oldField.removeListener(fieldValueChangeListener);

        return newField;
    }

    /**
     * Notifies the component that it is connected to an application
     * 
     * @see com.itmill.toolkit.ui.Component#attach()
     */
    public void attach() {
        super.attach();
        layout.attach();
    }

    /**
     * Notifies the component that it is detached from the application.
     * 
     * @see com.itmill.toolkit.ui.Component#detach()
     */
    public void detach() {
        super.detach();
        layout.detach();
    }

    /**
     * Tests the current value of the object against all registered validators
     * 
     * @see com.itmill.toolkit.data.Validatable#isValid()
     */
    public boolean isValid() {
        boolean valid = true;
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            valid &= ((Field) fields.get(i.next())).isValid();
        }
        return valid && super.isValid();
    }

    /**
     * Checks the validity of the validatable.
     * 
     * @see com.itmill.toolkit.data.Validatable#validate()
     */
    public void validate() throws InvalidValueException {
        super.validate();
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            ((Field) fields.get(i.next())).validate();
        }
    }

    /**
     * Checks the validabtable object accept invalid values.
     * 
     * @see com.itmill.toolkit.data.Validatable#isInvalidAllowed()
     */
    public boolean isInvalidAllowed() {
        return true;
    }

    /**
     * Should the validabtable object accept invalid values.
     * 
     * @see com.itmill.toolkit.data.Validatable#setInvalidAllowed(boolean)
     */
    public void setInvalidAllowed(boolean invalidValueAllowed)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the component's to read-only mode to the specified state.
     * 
     * @see com.itmill.toolkit.ui.Component#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
            ((Field) fields.get(i.next())).setReadOnly(readOnly);
        }
    }

    /**
     * Sets the field factory of Form.
     * 
     * <code>FieldFactory</code> is used to create fields for form properties.
     * By default the form uses BaseFieldFactory to create Field instances.
     * 
     * @param fieldFactory
     *                the New factory used to create the fields.
     * @see Field
     * @see FieldFactory
     */
    public void setFieldFactory(FieldFactory fieldFactory) {
        this.fieldFactory = fieldFactory;
    }

    /**
     * Get the field factory of the form.
     * 
     * @return the FieldFactory Factory used to create the fields.
     */
    public FieldFactory getFieldFactory() {
        return fieldFactory;
    }

    /**
     * Gets the field type.
     * 
     * @see com.itmill.toolkit.ui.AbstractField#getType()
     */
    public Class getType() {
        if (getPropertyDataSource() != null) {
            return getPropertyDataSource().getType();
        }
        return Object.class;
    }

    /**
     * Sets the internal value.
     * 
     * This is relevant when the Form is used as Field.
     * 
     * @see com.itmill.toolkit.ui.AbstractField#setInternalValue(java.lang.Object)
     */
    protected void setInternalValue(Object newValue) {
        // Stores the old value
        final Object oldValue = propertyValue;

        // Sets the current Value
        super.setInternalValue(newValue);
        propertyValue = newValue;

        // Ignores form updating if data object has not changed.
        if (oldValue != newValue) {
            setFormDataSource(newValue, getVisibleItemProperties());
        }
    }

    /**
     * Gets the first field in form.
     * 
     * @return the Field.
     */
    private Field getFirstField() {
        Object id = null;
        if (getItemPropertyIds() != null) {
            id = getItemPropertyIds().iterator().next();
        }
        if (id != null) {
            return getField(id);
        }
        return null;
    }

    /**
     * Updates the internal form datasource.
     * 
     * Method setFormDataSource.
     * 
     * @param data
     * @param properties
     */
    protected void setFormDataSource(Object data, Collection properties) {

        // If data is an item use it.
        Item item = null;
        if (data instanceof Item) {
            item = (Item) data;
        } else if (data != null) {
            item = new BeanItem(data);
        }

        // Sets the datasource to form
        if (item != null && properties != null) {
            // Shows only given properties
            this.setItemDataSource(item, properties);
        } else {
            // Shows all properties
            this.setItemDataSource(item);
        }
    }

    /**
     * Returns the visibleProperties.
     * 
     * @return the Collection of visible Item properites.
     */
    public Collection getVisibleItemProperties() {
        return visibleItemProperties;
    }

    /**
     * Sets the visibleProperties.
     * 
     * @param visibleProperties
     *                the visibleProperties to set.
     */
    public void setVisibleItemProperties(Collection visibleProperties) {
        visibleItemProperties = visibleProperties;
        Object value = getValue();
        if (value == null) {
            value = itemDatasource;
        }
        setFormDataSource(value, getVisibleItemProperties());
    }

    /**
     * Focuses the first field in the form.
     * 
     * @see com.itmill.toolkit.ui.Component.Focusable#focus()
     */
    public void focus() {
        final Field f = getFirstField();
        if (f != null) {
            f.focus();
        }
    }

    /**
     * Sets the Tabulator index of this Focusable component.
     * 
     * @see com.itmill.toolkit.ui.Component.Focusable#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        super.setTabIndex(tabIndex);
        for (final Iterator i = getItemPropertyIds().iterator(); i.hasNext();) {
            (getField(i.next())).setTabIndex(tabIndex);
        }
    }

    /**
     * Setting the form to be immediate also sets all the fields of the form to
     * the same state.
     */
    public void setImmediate(boolean immediate) {
        super.setImmediate(immediate);
        for (Iterator i = fields.values().iterator(); i.hasNext();) {
            Field f = (Field) i.next();
            if (f instanceof AbstractComponent) {
                ((AbstractComponent) f).setImmediate(immediate);
            }
        }
    }

    /** Form is empty if all of its fields are empty. */
    protected boolean isEmpty() {

        for (Iterator i = fields.values().iterator(); i.hasNext();) {
            Field f = (Field) i.next();
            if (f instanceof AbstractField) {
                if (!((AbstractField) f).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Adding validators directly to form is not supported.
     * 
     * Add the validators to form fields instead.
     */
    public void addValidator(Validator validator) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a layout that is rendered below normal form contents. This area
     * can be used for example to include buttons related to form contents.
     * 
     * @return layout rendered below normal form contents.
     */
    public Layout getFooter() {
        if (formFooter == null) {
            formFooter = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
            formFooter.setParent(this);
        }
        return formFooter;
    }

    /**
     * Sets the layout that is rendered below normal form contens.
     * 
     * @param newFormFooter
     *                the new Layout
     */
    public void setFooter(Layout newFormFooter) {
        if (formFooter != null) {
            formFooter.setParent(null);
        }
        formFooter = newFormFooter;
        formFooter.setParent(this);
    }

}
