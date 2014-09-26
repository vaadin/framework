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
package com.vaadin.data.fieldgroup;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.TransactionalPropertyWrapper;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.util.ReflectTools;

/**
 * FieldGroup provides an easy way of binding fields to data and handling
 * commits of these fields.
 * <p>
 * The functionality of FieldGroup is similar to {@link Form} but
 * {@link FieldGroup} does not handle layouts in any way. The typical use case
 * is to create a layout outside the FieldGroup and then use FieldGroup to bind
 * the fields to a data source.
 * </p>
 * <p>
 * {@link FieldGroup} is not a UI component so it cannot be added to a layout.
 * Using the buildAndBind methods {@link FieldGroup} can create fields for you
 * using a FieldGroupFieldFactory but you still have to add them to the correct
 * position in your layout.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public class FieldGroup implements Serializable {

    private Item itemDataSource;
    private boolean buffered = true;

    private boolean enabled = true;
    private boolean readOnly = false;

    private HashMap<Object, Field<?>> propertyIdToField = new HashMap<Object, Field<?>>();
    private LinkedHashMap<Field<?>, Object> fieldToPropertyId = new LinkedHashMap<Field<?>, Object>();
    private List<CommitHandler> commitHandlers = new ArrayList<CommitHandler>();

    /**
     * The field factory used by builder methods.
     */
    private FieldGroupFieldFactory fieldFactory = new DefaultFieldGroupFieldFactory();

    /**
     * Constructs a field binder. Use {@link #setItemDataSource(Item)} to set a
     * data source for the field binder.
     * 
     */
    public FieldGroup() {

    }

    /**
     * Constructs a field binder that uses the given data source.
     * 
     * @param itemDataSource
     *            The data source to bind the fields to
     */
    public FieldGroup(Item itemDataSource) {
        setItemDataSource(itemDataSource);
    }

    /**
     * Updates the item that is used by this FieldBinder. Rebinds all fields to
     * the properties in the new item.
     * 
     * @param itemDataSource
     *            The new item to use
     */
    public void setItemDataSource(Item itemDataSource) {
        this.itemDataSource = itemDataSource;

        for (Field<?> f : fieldToPropertyId.keySet()) {
            bind(f, fieldToPropertyId.get(f));
        }
    }

    /**
     * Gets the item used by this FieldBinder. Note that you must call
     * {@link #commit()} for the item to be updated unless buffered mode has
     * been switched off.
     * 
     * @see #setBuffered(boolean)
     * @see #commit()
     * 
     * @return The item used by this FieldBinder
     */
    public Item getItemDataSource() {
        return itemDataSource;
    }

    /**
     * Checks the buffered mode for the bound fields.
     * <p>
     * 
     * @see #setBuffered(boolean) for more details on buffered mode
     * 
     * @see Field#isBuffered()
     * @return true if buffered mode is on, false otherwise
     * 
     */
    public boolean isBuffered() {
        return buffered;
    }

    /**
     * Sets the buffered mode for the bound fields.
     * <p>
     * When buffered mode is on the item will not be updated until
     * {@link #commit()} is called. If buffered mode is off the item will be
     * updated once the fields are updated.
     * </p>
     * <p>
     * The default is to use buffered mode.
     * </p>
     * 
     * @see Field#setBuffered(boolean)
     * @param buffered
     *            true to turn on buffered mode, false otherwise
     */
    public void setBuffered(boolean buffered) {
        if (buffered == this.buffered) {
            return;
        }

        this.buffered = buffered;
        for (Field<?> field : getFields()) {
            field.setBuffered(buffered);
        }
    }

    /**
     * Returns the enabled status for the fields.
     * <p>
     * Note that this will not accurately represent the enabled status of all
     * fields if you change the enabled status of the fields through some other
     * method than {@link #setEnabled(boolean)}.
     * 
     * @return true if the fields are enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Updates the enabled state of all bound fields.
     * 
     * @param fieldsEnabled
     *            true to enable all bound fields, false to disable them
     */
    public void setEnabled(boolean fieldsEnabled) {
        enabled = fieldsEnabled;
        for (Field<?> field : getFields()) {
            field.setEnabled(fieldsEnabled);
        }
    }

    /**
     * Returns the read only status that is used by default with all fields that
     * have a writable data source.
     * <p>
     * Note that this will not accurately represent the read only status of all
     * fields if you change the read only status of the fields through some
     * other method than {@link #setReadOnly(boolean)}.
     * 
     * @return true if the fields are set to read only, false otherwise
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the read only state to the given value for all fields with writable
     * data source. Fields with read only data source will always be set to read
     * only.
     * 
     * @param fieldsReadOnly
     *            true to set the fields with writable data source to read only,
     *            false to set them to read write
     */
    public void setReadOnly(boolean fieldsReadOnly) {
        readOnly = fieldsReadOnly;
        for (Field<?> field : getFields()) {
            if (field.getPropertyDataSource() == null
                    || !field.getPropertyDataSource().isReadOnly()) {
                field.setReadOnly(fieldsReadOnly);
            } else {
                field.setReadOnly(true);
            }
        }
    }

    /**
     * Returns a collection of all fields that have been bound.
     * <p>
     * The fields are not returned in any specific order.
     * </p>
     * 
     * @return A collection with all bound Fields
     */
    public Collection<Field<?>> getFields() {
        return fieldToPropertyId.keySet();
    }

    /**
     * Binds the field with the given propertyId from the current item. If an
     * item has not been set then the binding is postponed until the item is set
     * using {@link #setItemDataSource(Item)}.
     * <p>
     * This method also adds validators when applicable.
     * </p>
     * 
     * @param field
     *            The field to bind
     * @param propertyId
     *            The propertyId to bind to the field
     * @throws BindException
     *             If the field is null or the property id is already bound to
     *             another field by this field binder
     */
    public void bind(Field<?> field, Object propertyId) throws BindException {
        throwIfFieldIsNull(field, propertyId);
        throwIfPropertyIdAlreadyBound(field, propertyId);

        fieldToPropertyId.put(field, propertyId);
        propertyIdToField.put(propertyId, field);
        if (itemDataSource == null) {
            // Will be bound when data source is set
            return;
        }

        field.setPropertyDataSource(wrapInTransactionalProperty(getItemProperty(propertyId)));
        configureField(field);
    }

    private void throwIfFieldIsNull(Field<?> field, Object propertyId) {
        if (field == null) {
            throw new BindException(
                    String.format(
                            "Cannot bind property id '%s' to a null field.",
                            propertyId));
        }
    }

    private void throwIfPropertyIdAlreadyBound(Field<?> field, Object propertyId) {
        if (propertyIdToField.containsKey(propertyId)
                && propertyIdToField.get(propertyId) != field) {
            throw new BindException("Property id " + propertyId
                    + " is already bound to another field");
        }
    }

    private <T> Property.Transactional<T> wrapInTransactionalProperty(
            Property<T> itemProperty) {
        return new TransactionalPropertyWrapper<T>(itemProperty);
    }

    /**
     * Gets the property with the given property id from the item.
     * 
     * @param propertyId
     *            The id if the property to find
     * @return The property with the given id from the item
     * @throws BindException
     *             If the property was not found in the item or no item has been
     *             set
     */
    protected Property getItemProperty(Object propertyId) throws BindException {
        Item item = getItemDataSource();
        if (item == null) {
            throw new BindException("Could not lookup property with id "
                    + propertyId + " as no item has been set");
        }
        Property<?> p = item.getItemProperty(propertyId);
        if (p == null) {
            throw new BindException("A property with id " + propertyId
                    + " was not found in the item");
        }
        return p;
    }

    /**
     * Detaches the field from its property id and removes it from this
     * FieldBinder.
     * <p>
     * Note that the field is not detached from its property data source if it
     * is no longer connected to the same property id it was bound to using this
     * FieldBinder.
     * 
     * @param field
     *            The field to detach
     * @throws BindException
     *             If the field is not bound by this field binder or not bound
     *             to the correct property id
     */
    public void unbind(Field<?> field) throws BindException {
        Object propertyId = fieldToPropertyId.get(field);
        if (propertyId == null) {
            throw new BindException(
                    "The given field is not part of this FieldBinder");
        }

        TransactionalPropertyWrapper<?> wrapper = null;
        Property fieldDataSource = field.getPropertyDataSource();
        if (fieldDataSource instanceof TransactionalPropertyWrapper) {
            wrapper = (TransactionalPropertyWrapper<?>) fieldDataSource;
            fieldDataSource = ((TransactionalPropertyWrapper<?>) fieldDataSource)
                    .getWrappedProperty();

        }
        if (fieldDataSource == getItemProperty(propertyId)) {
            if (null != wrapper) {
                wrapper.detachFromProperty();
            }
            field.setPropertyDataSource(null);
        }
        fieldToPropertyId.remove(field);
        propertyIdToField.remove(propertyId);
    }

    /**
     * Configures a field with the settings set for this FieldBinder.
     * <p>
     * By default this updates the buffered, read only and enabled state of the
     * field. Also adds validators when applicable. Fields with read only data
     * source are always configured as read only.
     * 
     * @param field
     *            The field to update
     */
    protected void configureField(Field<?> field) {
        field.setBuffered(isBuffered());

        field.setEnabled(isEnabled());

        if (field.getPropertyDataSource().isReadOnly()) {
            field.setReadOnly(true);
        } else {
            field.setReadOnly(isReadOnly());
        }
    }

    /**
     * Gets the type of the property with the given property id.
     * 
     * @param propertyId
     *            The propertyId. Must be find
     * @return The type of the property
     */
    protected Class<?> getPropertyType(Object propertyId) throws BindException {
        if (getItemDataSource() == null) {
            throw new BindException(
                    "Property type for '"
                            + propertyId
                            + "' could not be determined. No item data source has been set.");
        }
        Property<?> p = getItemDataSource().getItemProperty(propertyId);
        if (p == null) {
            throw new BindException(
                    "Property type for '"
                            + propertyId
                            + "' could not be determined. No property with that id was found.");
        }

        return p.getType();
    }

    /**
     * Returns a collection of all property ids that have been bound to fields.
     * <p>
     * Note that this will return property ids even before the item has been
     * set. In that case it returns the property ids that will be bound once the
     * item is set.
     * </p>
     * <p>
     * No guarantee is given for the order of the property ids
     * </p>
     * 
     * @return A collection of bound property ids
     */
    public Collection<Object> getBoundPropertyIds() {
        return Collections.unmodifiableCollection(propertyIdToField.keySet());
    }

    /**
     * Returns a collection of all property ids that exist in the item set using
     * {@link #setItemDataSource(Item)} but have not been bound to fields.
     * <p>
     * Will always return an empty collection before an item has been set using
     * {@link #setItemDataSource(Item)}.
     * </p>
     * <p>
     * No guarantee is given for the order of the property ids
     * </p>
     * 
     * @return A collection of property ids that have not been bound to fields
     */
    public Collection<Object> getUnboundPropertyIds() {
        if (getItemDataSource() == null) {
            return new ArrayList<Object>();
        }
        List<Object> unboundPropertyIds = new ArrayList<Object>();
        unboundPropertyIds.addAll(getItemDataSource().getItemPropertyIds());
        unboundPropertyIds.removeAll(propertyIdToField.keySet());
        return unboundPropertyIds;
    }

    /**
     * Commits all changes done to the bound fields.
     * <p>
     * Calls all {@link CommitHandler}s before and after committing the field
     * changes to the item data source. The whole commit is aborted and state is
     * restored to what it was before commit was called if any
     * {@link CommitHandler} throws a CommitException or there is a problem
     * committing the fields
     * 
     * @throws CommitException
     *             If the commit was aborted
     */
    public void commit() throws CommitException {
        if (!isBuffered()) {
            // Not using buffered mode, nothing to do
            return;
        }
        for (Field<?> f : fieldToPropertyId.keySet()) {
            Property.Transactional<?> property = (Property.Transactional<?>) f
                    .getPropertyDataSource();
            if (property == null) {
                throw new CommitException("Property \""
                        + fieldToPropertyId.get(f)
                        + "\" not bound to datasource.");
            }
            property.startTransaction();
        }
        try {
            firePreCommitEvent();
            // Commit the field values to the properties
            for (Field<?> f : fieldToPropertyId.keySet()) {
                f.commit();
            }
            firePostCommitEvent();

            // Commit the properties
            for (Field<?> f : fieldToPropertyId.keySet()) {
                ((Property.Transactional<?>) f.getPropertyDataSource())
                        .commit();
            }

        } catch (Exception e) {
            for (Field<?> f : fieldToPropertyId.keySet()) {
                try {
                    ((Property.Transactional<?>) f.getPropertyDataSource())
                            .rollback();
                } catch (Exception rollbackException) {
                    // FIXME: What to do ?
                }
            }

            throw new CommitException("Commit failed", e);
        }

    }

    /**
     * Sends a preCommit event to all registered commit handlers
     * 
     * @throws CommitException
     *             If the commit should be aborted
     */
    private void firePreCommitEvent() throws CommitException {
        CommitHandler[] handlers = commitHandlers
                .toArray(new CommitHandler[commitHandlers.size()]);

        for (CommitHandler handler : handlers) {
            handler.preCommit(new CommitEvent(this));
        }
    }

    /**
     * Sends a postCommit event to all registered commit handlers
     * 
     * @throws CommitException
     *             If the commit should be aborted
     */
    private void firePostCommitEvent() throws CommitException {
        CommitHandler[] handlers = commitHandlers
                .toArray(new CommitHandler[commitHandlers.size()]);

        for (CommitHandler handler : handlers) {
            handler.postCommit(new CommitEvent(this));
        }
    }

    /**
     * Discards all changes done to the bound fields.
     * <p>
     * Only has effect if buffered mode is used.
     * 
     */
    public void discard() {
        for (Field<?> f : fieldToPropertyId.keySet()) {
            try {
                f.discard();
            } catch (Exception e) {
                // TODO: handle exception
                // What can we do if discard fails other than try to discard all
                // other fields?
            }
        }
    }

    /**
     * Returns the field that is bound to the given property id
     * 
     * @param propertyId
     *            The property id to use to lookup the field
     * @return The field that is bound to the property id or null if no field is
     *         bound to that property id
     */
    public Field<?> getField(Object propertyId) {
        return propertyIdToField.get(propertyId);
    }

    /**
     * Returns the property id that is bound to the given field
     * 
     * @param field
     *            The field to use to lookup the property id
     * @return The property id that is bound to the field or null if the field
     *         is not bound to any property id by this FieldBinder
     */
    public Object getPropertyId(Field<?> field) {
        return fieldToPropertyId.get(field);
    }

    /**
     * Adds a commit handler.
     * <p>
     * The commit handler is called before the field values are committed to the
     * item ( {@link CommitHandler#preCommit(CommitEvent)}) and after the item
     * has been updated ({@link CommitHandler#postCommit(CommitEvent)}). If a
     * {@link CommitHandler} throws a CommitException the whole commit is
     * aborted and the fields retain their old values.
     * 
     * @param commitHandler
     *            The commit handler to add
     */
    public void addCommitHandler(CommitHandler commitHandler) {
        commitHandlers.add(commitHandler);
    }

    /**
     * Removes the given commit handler.
     * 
     * @see #addCommitHandler(CommitHandler)
     * 
     * @param commitHandler
     *            The commit handler to remove
     */
    public void removeCommitHandler(CommitHandler commitHandler) {
        commitHandlers.remove(commitHandler);
    }

    /**
     * Returns a list of all commit handlers for this {@link FieldGroup}.
     * <p>
     * Use {@link #addCommitHandler(CommitHandler)} and
     * {@link #removeCommitHandler(CommitHandler)} to register or unregister a
     * commit handler.
     * 
     * @return A collection of commit handlers
     */
    protected Collection<CommitHandler> getCommitHandlers() {
        return Collections.unmodifiableCollection(commitHandlers);
    }

    /**
     * CommitHandlers are used by {@link FieldGroup#commit()} as part of the
     * commit transactions. CommitHandlers can perform custom operations as part
     * of the commit and cause the commit to be aborted by throwing a
     * {@link CommitException}.
     */
    public interface CommitHandler extends Serializable {
        /**
         * Called before changes are committed to the field and the item is
         * updated.
         * <p>
         * Throw a {@link CommitException} to abort the commit.
         * 
         * @param commitEvent
         *            An event containing information regarding the commit
         * @throws CommitException
         *             if the commit should be aborted
         */
        public void preCommit(CommitEvent commitEvent) throws CommitException;

        /**
         * Called after changes are committed to the fields and the item is
         * updated..
         * <p>
         * Throw a {@link CommitException} to abort the commit.
         * 
         * @param commitEvent
         *            An event containing information regarding the commit
         * @throws CommitException
         *             if the commit should be aborted
         */
        public void postCommit(CommitEvent commitEvent) throws CommitException;
    }

    /**
     * FIXME javadoc
     * 
     */
    public static class CommitEvent implements Serializable {
        private FieldGroup fieldBinder;

        private CommitEvent(FieldGroup fieldBinder) {
            this.fieldBinder = fieldBinder;
        }

        /**
         * Returns the field binder that this commit relates to
         * 
         * @return The FieldBinder that is being committed.
         */
        public FieldGroup getFieldBinder() {
            return fieldBinder;
        }

    }

    /**
     * Checks the validity of the bound fields.
     * <p>
     * Call the {@link Field#validate()} for the fields to get the individual
     * error messages.
     * 
     * @return true if all bound fields are valid, false otherwise.
     */
    public boolean isValid() {
        try {
            for (Field<?> field : getFields()) {
                field.validate();
            }
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
    }

    /**
     * Checks if any bound field has been modified.
     * 
     * @return true if at least one field has been modified, false otherwise
     */
    public boolean isModified() {
        for (Field<?> field : getFields()) {
            if (field.isModified()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the field factory for the {@link FieldGroup}. The field factory is
     * only used when {@link FieldGroup} creates a new field.
     * 
     * @return The field factory in use
     * 
     */
    public FieldGroupFieldFactory getFieldFactory() {
        return fieldFactory;
    }

    /**
     * Sets the field factory for the {@link FieldGroup}. The field factory is
     * only used when {@link FieldGroup} creates a new field.
     * 
     * @param fieldFactory
     *            The field factory to use
     */
    public void setFieldFactory(FieldGroupFieldFactory fieldFactory) {
        this.fieldFactory = fieldFactory;
    }

    /**
     * Binds member fields found in the given object.
     * <p>
     * This method processes all (Java) member fields whose type extends
     * {@link Field} and that can be mapped to a property id. Property id
     * mapping is done based on the field name or on a @{@link PropertyId}
     * annotation on the field. All non-null fields for which a property id can
     * be determined are bound to the property id.
     * </p>
     * <p>
     * For example:
     * 
     * <pre>
     * public class MyForm extends VerticalLayout {
     * private TextField firstName = new TextField("First name");
     * @PropertyId("last")
     * private TextField lastName = new TextField("Last name"); 
     * private TextField age = new TextField("Age"); ... }
     * 
     * MyForm myForm = new MyForm(); 
     * ... 
     * fieldGroup.bindMemberFields(myForm);
     * </pre>
     * 
     * </p>
     * This binds the firstName TextField to a "firstName" property in the item,
     * lastName TextField to a "last" property and the age TextField to a "age"
     * property.
     * 
     * @param objectWithMemberFields
     *            The object that contains (Java) member fields to bind
     * @throws BindException
     *             If there is a problem binding a field
     */
    public void bindMemberFields(Object objectWithMemberFields)
            throws BindException {
        buildAndBindMemberFields(objectWithMemberFields, false);
    }

    /**
     * Binds member fields found in the given object and builds member fields
     * that have not been initialized.
     * <p>
     * This method processes all (Java) member fields whose type extends
     * {@link Field} and that can be mapped to a property id. Property ids are
     * searched in the following order: @{@link PropertyId} annotations, exact
     * field name matches and the case-insensitive matching that ignores
     * underscores. Fields that are not initialized (null) are built using the
     * field factory. All non-null fields for which a property id can be
     * determined are bound to the property id.
     * </p>
     * <p>
     * For example:
     * 
     * <pre>
     * public class MyForm extends VerticalLayout {
     * private TextField firstName = new TextField("First name");
     * @PropertyId("last")
     * private TextField lastName = new TextField("Last name"); 
     * private TextField age;
     * 
     * MyForm myForm = new MyForm(); 
     * ... 
     * fieldGroup.buildAndBindMemberFields(myForm);
     * </pre>
     * 
     * </p>
     * <p>
     * This binds the firstName TextField to a "firstName" property in the item,
     * lastName TextField to a "last" property and builds an age TextField using
     * the field factory and then binds it to the "age" property.
     * </p>
     * 
     * @param objectWithMemberFields
     *            The object that contains (Java) member fields to build and
     *            bind
     * @throws BindException
     *             If there is a problem binding or building a field
     */
    public void buildAndBindMemberFields(Object objectWithMemberFields)
            throws BindException {
        buildAndBindMemberFields(objectWithMemberFields, true);
    }

    /**
     * Binds member fields found in the given object and optionally builds
     * member fields that have not been initialized.
     * <p>
     * This method processes all (Java) member fields whose type extends
     * {@link Field} and that can be mapped to a property id. Property ids are
     * searched in the following order: @{@link PropertyId} annotations, exact
     * field name matches and the case-insensitive matching that ignores
     * underscores. Fields that are not initialized (null) are built using the
     * field factory is buildFields is true. All non-null fields for which a
     * property id can be determined are bound to the property id.
     * </p>
     * 
     * @param objectWithMemberFields
     *            The object that contains (Java) member fields to build and
     *            bind
     * @throws BindException
     *             If there is a problem binding or building a field
     */
    protected void buildAndBindMemberFields(Object objectWithMemberFields,
            boolean buildFields) throws BindException {
        Class<?> objectClass = objectWithMemberFields.getClass();

        for (java.lang.reflect.Field memberField : getFieldsInDeclareOrder(objectClass)) {

            if (!Field.class.isAssignableFrom(memberField.getType())) {
                // Process next field
                continue;
            }

            PropertyId propertyIdAnnotation = memberField
                    .getAnnotation(PropertyId.class);

            Class<? extends Field> fieldType = (Class<? extends Field>) memberField
                    .getType();

            Object propertyId = null;
            if (propertyIdAnnotation != null) {
                // @PropertyId(propertyId) always overrides property id
                propertyId = propertyIdAnnotation.value();
            } else {
                try {
                    propertyId = findPropertyId(memberField);
                } catch (SearchException e) {
                    // Property id was not found, skip this field
                    continue;
                }
                if (propertyId == null) {
                    // Property id was not found, skip this field
                    continue;
                }
            }

            // Ensure that the property id exists
            Class<?> propertyType;

            try {
                propertyType = getPropertyType(propertyId);
            } catch (BindException e) {
                // Property id was not found, skip this field
                continue;
            }

            Field<?> field;
            try {
                // Get the field from the object
                field = (Field<?>) ReflectTools.getJavaFieldValue(
                        objectWithMemberFields, memberField, Field.class);
            } catch (Exception e) {
                // If we cannot determine the value, just skip the field and try
                // the next one
                continue;
            }

            if (field == null && buildFields) {
                Caption captionAnnotation = memberField
                        .getAnnotation(Caption.class);
                String caption;
                if (captionAnnotation != null) {
                    caption = captionAnnotation.value();
                } else {
                    caption = DefaultFieldFactory
                            .createCaptionByPropertyId(propertyId);
                }

                // Create the component (Field)
                field = build(caption, propertyType, fieldType);

                // Store it in the field
                try {
                    ReflectTools.setJavaFieldValue(objectWithMemberFields,
                            memberField, field);
                } catch (IllegalArgumentException e) {
                    throw new BindException("Could not assign value to field '"
                            + memberField.getName() + "'", e);
                } catch (IllegalAccessException e) {
                    throw new BindException("Could not assign value to field '"
                            + memberField.getName() + "'", e);
                } catch (InvocationTargetException e) {
                    throw new BindException("Could not assign value to field '"
                            + memberField.getName() + "'", e);
                }
            }

            if (field != null) {
                // Bind it to the property id
                bind(field, propertyId);
            }
        }
    }

    /**
     * Searches for a property id from the current itemDataSource that matches
     * the given memberField.
     * <p>
     * If perfect match is not found, uses a case insensitive search that also
     * ignores underscores. Returns null if no match is found. Throws a
     * SearchException if no item data source has been set.
     * </p>
     * <p>
     * The propertyId search logic used by
     * {@link #buildAndBindMemberFields(Object, boolean)
     * buildAndBindMemberFields} can easily be customized by overriding this
     * method. No other changes are needed.
     * </p>
     * 
     * @param memberField
     *            The field an object id is searched for
     * @return
     */
    protected Object findPropertyId(java.lang.reflect.Field memberField) {
        String fieldName = memberField.getName();
        if (getItemDataSource() == null) {
            throw new SearchException(
                    "Property id type for field '"
                            + fieldName
                            + "' could not be determined. No item data source has been set.");
        }
        Item dataSource = getItemDataSource();
        if (dataSource.getItemProperty(fieldName) != null) {
            return fieldName;
        } else {
            String minifiedFieldName = minifyFieldName(fieldName);
            for (Object itemPropertyId : dataSource.getItemPropertyIds()) {
                if (itemPropertyId instanceof String) {
                    String itemPropertyName = (String) itemPropertyId;
                    if (minifiedFieldName
                            .equals(minifyFieldName(itemPropertyName))) {
                        return itemPropertyName;
                    }
                }
            }
        }
        return null;
    }

    protected static String minifyFieldName(String fieldName) {
        return fieldName.toLowerCase().replace("_", "");
    }

    public static class CommitException extends Exception {

        public CommitException() {
            super();
            // TODO Auto-generated constructor stub
        }

        public CommitException(String message, Throwable cause) {
            super(message, cause);
            // TODO Auto-generated constructor stub
        }

        public CommitException(String message) {
            super(message);
            // TODO Auto-generated constructor stub
        }

        public CommitException(Throwable cause) {
            super(cause);
            // TODO Auto-generated constructor stub
        }

    }

    public static class BindException extends RuntimeException {

        public BindException(String message) {
            super(message);
        }

        public BindException(String message, Throwable t) {
            super(message, t);
        }

    }

    public static class SearchException extends RuntimeException {

        public SearchException(String message) {
            super(message);
        }

        public SearchException(String message, Throwable t) {
            super(message, t);
        }

    }

    /**
     * Builds a field and binds it to the given property id using the field
     * binder.
     * 
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            finder.
     * @throws BindException
     *             If there is a problem while building or binding
     * @return The created and bound field
     */
    public Field<?> buildAndBind(Object propertyId) throws BindException {
        String caption = DefaultFieldFactory
                .createCaptionByPropertyId(propertyId);
        return buildAndBind(caption, propertyId);
    }

    /**
     * Builds a field using the given caption and binds it to the given property
     * id using the field binder.
     * 
     * @param caption
     *            The caption for the field
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            finder.
     * @throws BindException
     *             If there is a problem while building or binding
     * @return The created and bound field. Can be any type of {@link Field}.
     */
    public Field<?> buildAndBind(String caption, Object propertyId)
            throws BindException {
        return buildAndBind(caption, propertyId, Field.class);
    }

    /**
     * Builds a field using the given caption and binds it to the given property
     * id using the field binder. Ensures the new field is of the given type.
     * 
     * @param caption
     *            The caption for the field
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            finder.
     * @throws BindException
     *             If the field could not be created
     * @return The created and bound field. Can be any type of {@link Field}.
     */

    public <T extends Field> T buildAndBind(String caption, Object propertyId,
            Class<T> fieldType) throws BindException {
        Class<?> type = getPropertyType(propertyId);

        T field = build(caption, type, fieldType);
        bind(field, propertyId);

        return field;
    }

    /**
     * Creates a field based on the given data type.
     * <p>
     * The data type is the type that we want to edit using the field. The field
     * type is the type of field we want to create, can be {@link Field} if any
     * Field is good.
     * </p>
     * 
     * @param caption
     *            The caption for the new field
     * @param dataType
     *            The data model type that we want to edit using the field
     * @param fieldType
     *            The type of field that we want to create
     * @return A Field capable of editing the given type
     * @throws BindException
     *             If the field could not be created
     */
    protected <T extends Field> T build(String caption, Class<?> dataType,
            Class<T> fieldType) throws BindException {
        T field = getFieldFactory().createField(dataType, fieldType);
        if (field == null) {
            throw new BindException("Unable to build a field of type "
                    + fieldType.getName() + " for editing "
                    + dataType.getName());
        }

        field.setCaption(caption);
        return field;
    }

    /**
     * Returns an array containing Field objects reflecting all the fields of
     * the class or interface represented by this Class object. The elements in
     * the array returned are sorted in declare order from sub class to super
     * class.
     * 
     * @param searchClass
     * @return
     */
    protected static List<java.lang.reflect.Field> getFieldsInDeclareOrder(
            Class searchClass) {
        ArrayList<java.lang.reflect.Field> memberFieldInOrder = new ArrayList<java.lang.reflect.Field>();

        while (searchClass != null) {
            for (java.lang.reflect.Field memberField : searchClass
                    .getDeclaredFields()) {
                memberFieldInOrder.add(memberField);
            }
            searchClass = searchClass.getSuperclass();
        }
        return memberFieldInOrder;
    }
}
