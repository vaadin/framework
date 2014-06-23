/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.Action.ShortcutNotifier;
import com.vaadin.event.ActionManager;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.form.FormState;

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
 * {@link com.vaadin.data.Item} interface. Also the form itself implements this
 * interface for easier connectivity to other items. To use the form as editor
 * for an item, just connect the item to form with
 * {@link Form#setItemDataSource(Item)}. If only a part of the item needs to be
 * edited, {@link Form#setItemDataSource(Item,Collection)} can be used instead.
 * After the item has been connected to the form, the automatically created
 * fields can be customized and new fields can be added. If you need to connect
 * a class that does not implement {@link com.vaadin.data.Item} interface, most
 * properties of any class following bean pattern, can be accessed trough
 * {@link com.vaadin.data.util.BeanItem}.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 * @deprecated As of 7.0, use {@link FieldGroup} instead of {@link Form} for
 *             more flexibility.
 */
@Deprecated
public class Form extends AbstractField<Object> implements Item.Editor,
        Buffered, Item, Validatable, Action.Notifier, HasComponents,
        LegacyComponent {

    private Object propertyValue;

    /**
     * Item connected to this form as datasource.
     */
    private Item itemDatasource;

    /**
     * Ordered list of property ids in this editor.
     */
    private final LinkedList<Object> propertyIds = new LinkedList<Object>();

    /**
     * Current buffered source exception.
     */
    private Buffered.SourceException currentBufferedSourceException = null;

    /**
     * Is the form in buffered mode.
     */
    private boolean buffered = false;

    /**
     * Mapping from propertyName to corresponding field.
     */
    private final HashMap<Object, Field<?>> fields = new HashMap<Object, Field<?>>();

    /**
     * Form may act as an Item, its own properties are stored here.
     */
    private final HashMap<Object, Property<?>> ownProperties = new HashMap<Object, Property<?>>();

    /**
     * Field factory for this form.
     */
    private FormFieldFactory fieldFactory;

    /**
     * Visible item properties.
     */
    private Collection<?> visibleItemProperties;

    /**
     * Form needs to repaint itself if child fields value changes due possible
     * change in form validity.
     * 
     * TODO introduce ValidityChangeEvent (#6239) and start using it instead.
     * See e.g. DateField#notifyFormOfValidityChange().
     */
    private final ValueChangeListener fieldValueChangeListener = new ValueChangeListener() {
        @Override
        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
            markAsDirty();
        }
    };

    /**
     * If this is true, commit implicitly calls setValidationVisible(true).
     */
    private boolean validationVisibleOnCommit = true;

    // special handling for gridlayout; remember initial cursor pos
    private int gridlayoutCursorX = -1;
    private int gridlayoutCursorY = -1;

    /**
     * Keeps track of the Actions added to this component, and manages the
     * painting and handling as well. Note that the extended AbstractField is a
     * {@link ShortcutNotifier} and has a actionManager that delegates actions
     * to the containing window. This one does not delegate.
     */
    private ActionManager ownActionManager = new ActionManager(this);

    /**
     * Constructs a new form with default layout.
     * 
     * <p>
     * By default the form uses {@link FormLayout}.
     * </p>
     */
    public Form() {
        this(null);
        setValidationVisible(false);
    }

    /**
     * Constructs a new form with given {@link Layout}.
     * 
     * @param formLayout
     *            the layout of the form.
     */
    public Form(Layout formLayout) {
        this(formLayout, DefaultFieldFactory.get());
    }

    /**
     * Constructs a new form with given {@link Layout} and
     * {@link FormFieldFactory}.
     * 
     * @param formLayout
     *            the layout of the form.
     * @param fieldFactory
     *            the FieldFactory of the form.
     */
    public Form(Layout formLayout, FormFieldFactory fieldFactory) {
        super();
        setLayout(formLayout);
        setFooter(new HorizontalLayout());
        setFormFieldFactory(fieldFactory);
        setValidationVisible(false);
        setWidth(100, UNITS_PERCENTAGE);
    }

    @Override
    protected FormState getState() {
        return (FormState) super.getState();
    }

    @Override
    protected FormState getState(boolean markAsDirty) {
        return (FormState) super.getState(markAsDirty);
    }

    /* Documented in interface */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (ownActionManager != null) {
            ownActionManager.paintActions(null, target);
        }
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        // Actions
        if (ownActionManager != null) {
            ownActionManager.handleActions(variables, this);
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
    @Override
    public ErrorMessage getErrorMessage() {

        // Reimplement the checking of validation error by using
        // getErrorMessage() recursively instead of validate().
        ErrorMessage validationError = null;
        if (isValidationVisible()) {
            for (final Iterator<Object> i = propertyIds.iterator(); i.hasNext();) {
                Object f = fields.get(i.next());
                if (f instanceof AbstractComponent) {
                    AbstractComponent field = (AbstractComponent) f;

                    validationError = field.getErrorMessage();
                    if (validationError != null) {
                        // Show caption as error for fields with empty errors
                        if ("".equals(validationError.toString())) {
                            validationError = new UserError(field.getCaption());
                        }
                        break;
                    } else if (f instanceof Field && !((Field<?>) f).isValid()) {
                        // Something is wrong with the field, but no proper
                        // error is given. Generate one.
                        validationError = new UserError(field.getCaption());
                        break;
                    }
                }
            }
        }

        // Return if there are no errors at all
        if (getComponentError() == null && validationError == null
                && currentBufferedSourceException == null) {
            return null;
        }

        // Throw combination of the error types
        return new CompositeErrorMessage(
                new ErrorMessage[] {
                        getComponentError(),
                        validationError,
                        AbstractErrorMessage
                                .getErrorMessageForException(currentBufferedSourceException) });
    }

    /**
     * Controls the making validation visible implicitly on commit.
     * 
     * Having commit() call setValidationVisible(true) implicitly is the default
     * behaviour. You can disable the implicit setting by setting this property
     * as false.
     * 
     * It is useful, because you usually want to start with the form free of
     * errors and only display them after the user clicks Ok. You can disable
     * the implicit setting by setting this property as false.
     * 
     * @param makeVisible
     *            If true (default), validation is made visible when commit() is
     *            called. If false, the visibility is left as it is.
     */
    public void setValidationVisibleOnCommit(boolean makeVisible) {
        validationVisibleOnCommit = makeVisible;
    }

    /**
     * Is validation made automatically visible on commit?
     * 
     * See setValidationVisibleOnCommit().
     * 
     * @return true if validation is made automatically visible on commit.
     */
    public boolean isValidationVisibleOnCommit() {
        return validationVisibleOnCommit;
    }

    /*
     * Commit changes to the data source Don't add a JavaDoc comment here, we
     * use the default one from the interface.
     */
    @Override
    public void commit() throws Buffered.SourceException, InvalidValueException {

        LinkedList<SourceException> problems = null;

        // Only commit on valid state if so requested
        if (!isInvalidCommitted() && !isValid()) {
            /*
             * The values are not ok and we are told not to commit invalid
             * values
             */
            if (validationVisibleOnCommit) {
                setValidationVisible(true);
            }

            // Find the first invalid value and throw the exception
            validate();
        }

        // Try to commit all
        for (final Iterator<Object> i = propertyIds.iterator(); i.hasNext();) {
            try {
                final Field<?> f = (fields.get(i.next()));
                // Commit only non-readonly fields.
                if (!f.isReadOnly()) {
                    f.commit();
                }
            } catch (final Buffered.SourceException e) {
                if (problems == null) {
                    problems = new LinkedList<SourceException>();
                }
                problems.add(e);
            }
        }

        // No problems occurred
        if (problems == null) {
            if (currentBufferedSourceException != null) {
                currentBufferedSourceException = null;
                markAsDirty();
            }
            return;
        }

        // Commit problems
        final Throwable[] causes = new Throwable[problems.size()];
        int index = 0;
        for (final Iterator<SourceException> i = problems.iterator(); i
                .hasNext();) {
            causes[index++] = i.next();
        }
        final Buffered.SourceException e = new Buffered.SourceException(this,
                causes);
        currentBufferedSourceException = e;
        markAsDirty();
        throw e;
    }

    /*
     * Discards local changes and refresh values from the data source Don't add
     * a JavaDoc comment here, we use the default one from the interface.
     */
    @Override
    public void discard() throws Buffered.SourceException {

        LinkedList<SourceException> problems = null;

        // Try to discard all changes
        for (final Iterator<Object> i = propertyIds.iterator(); i.hasNext();) {
            try {
                (fields.get(i.next())).discard();
            } catch (final Buffered.SourceException e) {
                if (problems == null) {
                    problems = new LinkedList<SourceException>();
                }
                problems.add(e);
            }
        }

        // No problems occurred
        if (problems == null) {
            if (currentBufferedSourceException != null) {
                currentBufferedSourceException = null;
                markAsDirty();
            }
            return;
        }

        // Discards problems occurred
        final Throwable[] causes = new Throwable[problems.size()];
        int index = 0;
        for (final Iterator<SourceException> i = problems.iterator(); i
                .hasNext();) {
            causes[index++] = i.next();
        }
        final Buffered.SourceException e = new Buffered.SourceException(this,
                causes);
        currentBufferedSourceException = e;
        markAsDirty();
        throw e;
    }

    /*
     * Is the object modified but not committed? Don't add a JavaDoc comment
     * here, we use the default one from the interface.
     */
    @Override
    public boolean isModified() {
        for (final Iterator<Object> i = propertyIds.iterator(); i.hasNext();) {
            final Field<?> f = fields.get(i.next());
            if (f != null && f.isModified()) {
                return true;
            }

        }
        return false;
    }

    /*
     * Sets the editor's buffered mode to the specified status. Don't add a
     * JavaDoc comment here, we use the default one from the interface.
     */
    @Override
    public void setBuffered(boolean buffered) {
        if (buffered != this.buffered) {
            this.buffered = buffered;
            for (final Iterator<Object> i = propertyIds.iterator(); i.hasNext();) {
                (fields.get(i.next())).setBuffered(buffered);
            }
        }
    }

    /**
     * Adds a new property to form and create corresponding field.
     * 
     * @see com.vaadin.data.Item#addItemProperty(Object, Property)
     */
    @Override
    public boolean addItemProperty(Object id, Property property) {

        // Checks inputs
        if (id == null || property == null) {
            throw new NullPointerException("Id and property must be non-null");
        }

        // Checks that the property id is not reserved
        if (propertyIds.contains(id)) {
            return false;
        }

        propertyIds.add(id);
        ownProperties.put(id, property);

        // Gets suitable field
        final Field<?> field = fieldFactory.createField(this, id, this);
        if (field == null) {
            return false;
        }

        // Configures the field
        bindPropertyToField(id, property, field);

        // Register and attach the created field
        addField(id, field);

        return true;
    }

    /**
     * Registers the field with the form and adds the field to the form layout.
     * 
     * <p>
     * The property id must not be already used in the form.
     * </p>
     * 
     * <p>
     * This field is added to the layout using the
     * {@link #attachField(Object, Field)} method.
     * </p>
     * 
     * @param propertyId
     *            the Property id the the field.
     * @param field
     *            the field which should be added to the form.
     */
    public void addField(Object propertyId, Field<?> field) {
        registerField(propertyId, field);
        attachField(propertyId, field);
        markAsDirty();
    }

    /**
     * Register the field with the form. All registered fields are validated
     * when the form is validated and also committed when the form is committed.
     * 
     * <p>
     * The property id must not be already used in the form.
     * </p>
     * 
     * 
     * @param propertyId
     *            the Property id of the field.
     * @param field
     *            the Field that should be registered
     */
    private void registerField(Object propertyId, Field<?> field) {
        if (propertyId == null || field == null) {
            return;
        }

        fields.put(propertyId, field);
        field.addListener(fieldValueChangeListener);
        if (!propertyIds.contains(propertyId)) {
            // adding a field directly
            propertyIds.addLast(propertyId);
        }

        // Update the buffered mode and immediate to match the
        // form.
        // Should this also include invalidCommitted (#3993)?
        field.setBuffered(buffered);
        if (isImmediate() && field instanceof AbstractComponent) {
            ((AbstractComponent) field).setImmediate(true);
        }
    }

    /**
     * Adds the field to the form layout.
     * <p>
     * The field is added to the form layout in the default position (the
     * position used by {@link Layout#addComponent(Component)}. If the
     * underlying layout is a {@link CustomLayout} the field is added to the
     * CustomLayout location given by the string representation of the property
     * id using {@link CustomLayout#addComponent(Component, String)}.
     * </p>
     * 
     * <p>
     * Override this method to control how the fields are added to the layout.
     * </p>
     * 
     * @param propertyId
     * @param field
     */
    protected void attachField(Object propertyId, Field field) {
        if (propertyId == null || field == null) {
            return;
        }

        Layout layout = getLayout();
        if (layout instanceof CustomLayout) {
            ((CustomLayout) layout).addComponent(field, propertyId.toString());
        } else {
            layout.addComponent(field);
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
     * @see com.vaadin.data.Item#getItemProperty(Object)
     */
    @Override
    public Property getItemProperty(Object id) {
        final Field<?> field = fields.get(id);
        if (field == null) {
            // field does not exist or it is not (yet) created for this property
            return ownProperties.get(id);
        }
        final Property<?> property = field.getPropertyDataSource();

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
     *            the id of the property.
     */
    public Field getField(Object propertyId) {
        return fields.get(propertyId);
    }

    /* Documented in interface */
    @Override
    public Collection<?> getItemPropertyIds() {
        return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * Removes the property and corresponding field from the form.
     * 
     * @see com.vaadin.data.Item#removeItemProperty(Object)
     */
    @Override
    public boolean removeItemProperty(Object id) {
        ownProperties.remove(id);

        final Field<?> field = fields.get(id);

        if (field != null) {
            propertyIds.remove(id);
            fields.remove(id);
            detachField(field);
            field.removeListener(fieldValueChangeListener);
            return true;
        }

        return false;
    }

    /**
     * Called when a form field is detached from a Form. Typically when a new
     * Item is assigned to Form via {@link #setItemDataSource(Item)}.
     * <p>
     * Override this method to control how the fields are removed from the
     * layout.
     * </p>
     * 
     * @param field
     *            the field to be detached from the forms layout.
     */
    protected void detachField(final Field field) {
        Component p = field.getParent();
        if (p instanceof ComponentContainer) {
            ((ComponentContainer) p).removeComponent(field);
        }
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
    @Override
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
     * @see com.vaadin.data.Item.Viewer#setItemDataSource(Item)
     */
    @Override
    public void setItemDataSource(Item newDataSource) {
        setItemDataSource(newDataSource,
                newDataSource != null ? newDataSource.getItemPropertyIds()
                        : null);
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
     * @see com.vaadin.data.Item.Viewer#setItemDataSource(Item)
     */
    public void setItemDataSource(Item newDataSource, Collection<?> propertyIds) {

        if (getLayout() instanceof GridLayout) {
            GridLayout gl = (GridLayout) getLayout();
            if (gridlayoutCursorX == -1) {
                // first setItemDataSource, remember initial cursor
                gridlayoutCursorX = gl.getCursorX();
                gridlayoutCursorY = gl.getCursorY();
            } else {
                // restore initial cursor
                gl.setCursorX(gridlayoutCursorX);
                gl.setCursorY(gridlayoutCursorY);
            }
        }

        // Removes all fields first from the form
        removeAllProperties();

        // Sets the datasource
        itemDatasource = newDataSource;

        // If the new datasource is null, just set null datasource
        if (itemDatasource == null) {
            markAsDirty();
            return;
        }

        // Adds all the properties to this form
        for (final Iterator<?> i = propertyIds.iterator(); i.hasNext();) {
            final Object id = i.next();
            final Property<?> property = itemDatasource.getItemProperty(id);
            if (id != null && property != null) {
                final Field<?> f = fieldFactory.createField(itemDatasource, id,
                        this);
                if (f != null) {
                    bindPropertyToField(id, property, f);
                    addField(id, f);
                }
            }
        }
    }

    /**
     * Binds an item property to a field. The default behavior is to bind
     * property straight to Field. If Property.Viewer type property (e.g.
     * PropertyFormatter) is already set for field, the property is bound to
     * that Property.Viewer.
     * 
     * @param propertyId
     * @param property
     * @param field
     * @since 6.7.3
     */
    protected void bindPropertyToField(final Object propertyId,
            final Property property, final Field field) {
        // check if field has a property that is Viewer set. In that case we
        // expect developer has e.g. PropertyFormatter that he wishes to use and
        // assign the property to the Viewer instead.
        boolean hasFilterProperty = field.getPropertyDataSource() != null
                && (field.getPropertyDataSource() instanceof Property.Viewer);
        if (hasFilterProperty) {
            ((Property.Viewer) field.getPropertyDataSource())
                    .setPropertyDataSource(property);
        } else {
            field.setPropertyDataSource(property);
        }
    }

    /**
     * Gets the layout of the form.
     * 
     * <p>
     * By default form uses <code>OrderedLayout</code> with <code>form</code>
     * -style.
     * </p>
     * 
     * @return the Layout of the form.
     */
    public Layout getLayout() {
        return (Layout) getState(false).layout;
    }

    /**
     * Sets the layout of the form.
     * 
     * <p>
     * If set to null then Form uses a FormLayout by default.
     * </p>
     * 
     * @param layout
     *            the layout of the form.
     */
    public void setLayout(Layout layout) {

        // Use orderedlayout by default
        if (layout == null) {
            layout = new FormLayout();
        }

        // reset cursor memory
        gridlayoutCursorX = -1;
        gridlayoutCursorY = -1;

        // Move fields from previous layout
        if (getLayout() != null) {
            final Object[] properties = propertyIds.toArray();
            for (int i = 0; i < properties.length; i++) {
                Field<?> f = getField(properties[i]);
                detachField(f);
                if (layout instanceof CustomLayout) {
                    ((CustomLayout) layout).addComponent(f,
                            properties[i].toString());
                } else {
                    layout.addComponent(f);
                }
            }

            getLayout().setParent(null);
        }

        // Replace the previous layout
        layout.setParent(this);
        getState().layout = layout;
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
     * Note: since Vaadin 7.0, returns an {@link AbstractSelect} instead of a
     * {@link Select}.
     * 
     * @param propertyId
     *            the id of the property.
     * @param values
     * @param descriptions
     * @return the select property generated
     */
    public AbstractSelect replaceWithSelect(Object propertyId, Object[] values,
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
        final Field<?> oldField = fields.get(propertyId);
        if (oldField == null) {
            throw new IllegalArgumentException("Field with given propertyid '"
                    + propertyId.toString() + "' can not be found.");
        }
        final Object value = oldField.getPropertyDataSource() == null ? oldField
                .getValue() : oldField.getPropertyDataSource().getValue();

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
                for (final Iterator<?> it = ((Collection<?>) value).iterator(); it
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
        final AbstractSelect newField = isMultiselect ? new ListSelect()
                : new Select();
        newField.setCaption(oldField.getCaption());
        newField.setReadOnly(oldField.isReadOnly());
        newField.setBuffered(oldField.isBuffered());

        // Creates the options list
        newField.addContainerProperty("desc", String.class, "");
        newField.setItemCaptionPropertyId("desc");
        for (int i = 0; i < values.length; i++) {
            Object id = values[i];
            final Item item;
            if (id == null) {
                id = newField.addItem();
                item = newField.getItem(id);
                newField.setNullSelectionItemId(id);
            } else {
                item = newField.addItem(id);
            }

            if (item != null) {
                item.getItemProperty("desc").setValue(
                        descriptions[i].toString());
            }
        }

        // Sets the property data source
        final Property<?> property = oldField.getPropertyDataSource();
        oldField.setPropertyDataSource(null);
        newField.setPropertyDataSource(property);

        // Replaces the old field with new one
        getLayout().replaceComponent(oldField, newField);
        fields.put(propertyId, newField);
        newField.addListener(fieldValueChangeListener);
        oldField.removeListener(fieldValueChangeListener);

        return newField;
    }

    /**
     * Checks the validity of the Form and all of its fields.
     * 
     * @see com.vaadin.data.Validatable#validate()
     */
    @Override
    public void validate() throws InvalidValueException {
        super.validate();
        for (final Iterator<Object> i = propertyIds.iterator(); i.hasNext();) {
            (fields.get(i.next())).validate();
        }
    }

    /**
     * Checks the validabtable object accept invalid values.
     * 
     * @see com.vaadin.data.Validatable#isInvalidAllowed()
     */
    @Override
    public boolean isInvalidAllowed() {
        return true;
    }

    /**
     * Should the validabtable object accept invalid values.
     * 
     * @see com.vaadin.data.Validatable#setInvalidAllowed(boolean)
     */
    @Override
    public void setInvalidAllowed(boolean invalidValueAllowed)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the component's to read-only mode to the specified state.
     * 
     * @see com.vaadin.ui.Component#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        for (final Iterator<?> i = propertyIds.iterator(); i.hasNext();) {
            (fields.get(i.next())).setReadOnly(readOnly);
        }
    }

    /**
     * Sets the field factory used by this Form to genarate Fields for
     * properties.
     * 
     * {@link FormFieldFactory} is used to create fields for form properties.
     * {@link DefaultFieldFactory} is used by default.
     * 
     * @param fieldFactory
     *            the new factory used to create the fields.
     * @see Field
     * @see FormFieldFactory
     */
    public void setFormFieldFactory(FormFieldFactory fieldFactory) {
        this.fieldFactory = fieldFactory;
    }

    /**
     * Get the field factory of the form.
     * 
     * @return the FormFieldFactory Factory used to create the fields.
     */
    public FormFieldFactory getFormFieldFactory() {
        return fieldFactory;
    }

    /**
     * Gets the field type.
     * 
     * @see com.vaadin.ui.AbstractField#getType()
     */
    @Override
    public Class<?> getType() {
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
     * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
     */
    @Override
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
     * Gets the first focusable field in form. If there are enabled,
     * non-read-only fields, the first one of them is returned. Otherwise, the
     * field for the first property (or null if none) is returned.
     * 
     * @return the Field.
     */
    private Field<?> getFirstFocusableField() {
        Collection<?> itemPropertyIds = getItemPropertyIds();
        if (itemPropertyIds != null && itemPropertyIds.size() > 0) {
            for (Object id : itemPropertyIds) {
                if (id != null) {
                    Field<?> field = getField(id);
                    if (field.isEnabled() && !field.isReadOnly()) {
                        return field;
                    }
                }
            }
            // fallback: first field if none of the fields is enabled and
            // writable
            Object id = itemPropertyIds.iterator().next();
            if (id != null) {
                return getField(id);
            }
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
    protected void setFormDataSource(Object data, Collection<?> properties) {

        // If data is an item use it.
        Item item = null;
        if (data instanceof Item) {
            item = (Item) data;
        } else if (data != null) {
            item = new BeanItem<Object>(data);
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
    public Collection<?> getVisibleItemProperties() {
        return visibleItemProperties;
    }

    /**
     * Sets the visibleProperties.
     * 
     * @param visibleProperties
     *            the visibleProperties to set.
     */
    public void setVisibleItemProperties(Collection<?> visibleProperties) {
        visibleItemProperties = visibleProperties;
        Object value = getValue();
        if (value == null) {
            value = itemDatasource;
        }
        setFormDataSource(value, getVisibleItemProperties());
    }

    /**
     * Sets the visibleProperties.
     * 
     * @param visibleProperties
     *            the visibleProperties to set.
     */
    public void setVisibleItemProperties(Object... visibleProperties) {
        LinkedList<Object> v = new LinkedList<Object>();
        for (int i = 0; i < visibleProperties.length; i++) {
            v.add(visibleProperties[i]);
        }
        setVisibleItemProperties(v);
    }

    /**
     * Focuses the first field in the form.
     * 
     * @see com.vaadin.ui.Component.Focusable#focus()
     */
    @Override
    public void focus() {
        final Field<?> f = getFirstFocusableField();
        if (f != null) {
            f.focus();
        }
    }

    /**
     * Sets the Tabulator index of this Focusable component.
     * 
     * @see com.vaadin.ui.Component.Focusable#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        super.setTabIndex(tabIndex);
        for (final Iterator<?> i = getItemPropertyIds().iterator(); i.hasNext();) {
            (getField(i.next())).setTabIndex(tabIndex);
        }
    }

    /**
     * Setting the form to be immediate also sets all the fields of the form to
     * the same state.
     */
    @Override
    public void setImmediate(boolean immediate) {
        super.setImmediate(immediate);
        for (Iterator<Field<?>> i = fields.values().iterator(); i.hasNext();) {
            Field<?> f = i.next();
            if (f instanceof AbstractComponent) {
                ((AbstractComponent) f).setImmediate(immediate);
            }
        }
    }

    /** Form is empty if all of its fields are empty. */
    @Override
    protected boolean isEmpty() {

        for (Iterator<Field<?>> i = fields.values().iterator(); i.hasNext();) {
            Field<?> f = i.next();
            if (f instanceof AbstractField) {
                if (!((AbstractField<?>) f).isEmpty()) {
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
    @Override
    public void addValidator(Validator validator) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a layout that is rendered below normal form contents. This area
     * can be used for example to include buttons related to form contents.
     * 
     * @return layout rendered below normal form contents or null if no footer
     *         is used
     */
    public Layout getFooter() {
        return (Layout) getState(false).footer;
    }

    /**
     * Sets the layout that is rendered below normal form contents. No footer is
     * rendered if this is set to null, .
     * 
     * @param footer
     *            the new footer layout
     */
    public void setFooter(Layout footer) {
        if (getFooter() != null) {
            getFooter().setParent(null);
        }
        getState().footer = footer;
        if (footer != null) {
            footer.setParent(this);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (getParent() != null && !getParent().isEnabled()) {
            // some ancestor still disabled, don't update children
            return;
        } else {
            getLayout().markAsDirtyRecursive();
        }
    }

    /*
     * ACTIONS
     */

    /**
     * Gets the {@link ActionManager} responsible for handling {@link Action}s
     * added to this Form.<br/>
     * Note that Form has another ActionManager inherited from
     * {@link AbstractField}. The ownActionManager handles Actions attached to
     * this Form specifically, while the ActionManager in AbstractField
     * delegates to the containing Window (i.e global Actions).
     * 
     * @return
     */
    protected ActionManager getOwnActionManager() {
        if (ownActionManager == null) {
            ownActionManager = new ActionManager(this);
        }
        return ownActionManager;
    }

    @Override
    public void addActionHandler(Handler actionHandler) {
        getOwnActionManager().addActionHandler(actionHandler);
    }

    @Override
    public void removeActionHandler(Handler actionHandler) {
        if (ownActionManager != null) {
            ownActionManager.removeActionHandler(actionHandler);
        }
    }

    /**
     * Removes all action handlers
     */
    public void removeAllActionHandlers() {
        if (ownActionManager != null) {
            ownActionManager.removeAllActionHandlers();
        }
    }

    @Override
    public <T extends Action & com.vaadin.event.Action.Listener> void addAction(
            T action) {
        getOwnActionManager().addAction(action);
    }

    @Override
    public <T extends Action & com.vaadin.event.Action.Listener> void removeAction(
            T action) {
        if (ownActionManager != null) {
            ownActionManager.removeAction(action);
        }
    }

    @Override
    public Iterator<Component> iterator() {
        return new ComponentIterator();
    }

    /**
     * Modifiable and Serializable Iterator for the components, used by
     * {@link Form#getComponentIterator()}.
     */
    private class ComponentIterator implements Iterator<Component>,
            Serializable {

        int i = 0;

        @Override
        public boolean hasNext() {
            if (i < getComponentCount()) {
                return true;
            }
            return false;
        }

        @Override
        public Component next() {
            if (!hasNext()) {
                return null;
            }
            i++;
            if (i == 1) {
                if (getLayout() != null) {
                    return getLayout();
                }
                if (getFooter() != null) {
                    return getFooter();
                }
            } else if (i == 2) {
                if (getFooter() != null) {
                    return getFooter();
                }
            }
            return null;
        }

        @Override
        public void remove() {
            if (i == 1) {
                if (getLayout() != null) {
                    setLayout(null);
                    i = 0;
                } else {
                    setFooter(null);
                }
            } else if (i == 2) {
                setFooter(null);
            }
        }
    }

    /**
     * @deprecated As of 7.0, use {@link #iterator()} instead.
     */
    @Deprecated
    public Iterator<Component> getComponentIterator() {
        return iterator();
    }

    public int getComponentCount() {
        int count = 0;
        if (getLayout() != null) {
            count++;
        }
        if (getFooter() != null) {
            count++;
        }

        return count;
    }

}
