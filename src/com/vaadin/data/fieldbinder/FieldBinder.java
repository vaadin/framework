/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldbinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;

/**
 * FIXME Javadoc
 */
public class FieldBinder implements Serializable {

    private Item item;
    private boolean fieldsBuffered = true;

    private boolean fieldsEnabled = true;
    private boolean fieldsReadOnly = false;

    private HashMap<Object, Field<?>> propertyIdToField = new HashMap<Object, Field<?>>();
    private LinkedHashMap<Field<?>, Object> fieldToPropertyId = new LinkedHashMap<Field<?>, Object>();
    private List<CommitHandler> commitHandlers = new ArrayList<CommitHandler>();

    /**
     * Updates the item that is used by this FieldBinder. Rebinds all fields to
     * the properties in the new item.
     * 
     * @param item
     *            The new item to use
     */
    public void setItemDataSource(Item item) {
        this.item = item;

        for (Field<?> f : fieldToPropertyId.keySet()) {
            bind(f, fieldToPropertyId.get(f));
        }
    }

    /**
     * Gets the item used by this FieldBinder. Note that you must call
     * {@link #commit()} for the item to be updated unless buffered mode has
     * been switched off.
     * 
     * @see #setFieldsBuffered(boolean)
     * @see #commit()
     * 
     * @return The item used by this FieldBinder
     */
    public Item getItemDataSource() {
        return item;
    }

    /**
     * Checks the buffered mode for the bound fields.
     * <p>
     * 
     * @see #setFieldsBuffered(boolean) for more details on buffered mode
     * 
     * @see Field#isFieldsBuffered()
     * @return true if buffered mode is on, false otherwise
     * 
     */
    public boolean isFieldsBuffered() {
        return fieldsBuffered;
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
     * @see Field#setFieldsBuffered(boolean)
     * @param fieldsBuffered
     *            true to turn on buffered mode, false otherwise
     */
    public void setFieldsBuffered(boolean fieldsBuffered) {
        if (fieldsBuffered == this.fieldsBuffered) {
            return;
        }

        this.fieldsBuffered = fieldsBuffered;
        for (Field<?> field : getFields()) {
            // FIXME: Could use setBuffered if that was in Field
            field.setReadThrough(!isFieldsBuffered());
            field.setWriteThrough(!isFieldsBuffered());

        }
    }

    /**
     * Returns the enabled status for the fields.
     * <p>
     * Note that this will not accurately represent the enabled status of all
     * fields if you change the enabled status of the fields through some other
     * method than {@link #setFieldsEnabled(boolean)}.
     * 
     * @return true if the fields are set to read only, false otherwise
     */
    public boolean isFieldsEnabled() {
        return fieldsEnabled;
    }

    /**
     * Updates the enabled state of all bound fields.
     * 
     * @param fieldsEnabled
     *            true to enable all bound fields, false to disable them
     */
    public void setFieldsEnabled(boolean fieldsEnabled) {
        this.fieldsEnabled = fieldsEnabled;
        for (Field<?> field : getFields()) {
            field.setEnabled(fieldsEnabled);
        }
    }

    /**
     * Returns the read only status for the fields.
     * <p>
     * Note that this will not accurately represent the read only status of all
     * fields if you change the read only status of the fields through some
     * other method than {@link #setFieldsReadOnly(boolean)}.
     * 
     * @return true if the fields are set to read only, false otherwise
     */
    public boolean isFieldsReadOnly() {
        return fieldsReadOnly;
    }

    /**
     * Updates the read only state of all bound fields.
     * 
     * @param fieldsReadOnly
     *            true to set all bound fields to read only, false to set them
     *            to read write
     */
    public void setFieldsReadOnly(boolean fieldsReadOnly) {
        this.fieldsReadOnly = fieldsReadOnly;
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
     * 
     * @param field
     *            The field to bind
     * @param propertyId
     *            The propertyId to bind to the field
     */
    public void bind(Field<?> field, Object propertyId) {
        if (propertyIdToField.containsKey(propertyId)
                && propertyIdToField.get(propertyId) != field) {
            throw new BindException("Property id " + propertyId
                    + " is already bound to another field");
        }
        fieldToPropertyId.put(field, propertyId);
        propertyIdToField.put(propertyId, field);
        if (item == null) {
            // Will be bound when data source is set
            return;
        }

        field.setPropertyDataSource(getItemProperty(propertyId));
        configureField(field);
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
    protected Property<?> getItemProperty(Object propertyId)
            throws BindException {
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
    public void remove(Field<?> field) throws BindException {
        Object propertyId = fieldToPropertyId.get(field);
        if (propertyId == null) {
            throw new BindException(
                    "The given field is not part of this FieldBinder");
        }

        if (field.getPropertyDataSource() == getItemProperty(propertyId)) {
            field.setPropertyDataSource(null);
        }
        fieldToPropertyId.remove(field);
        propertyIdToField.remove(propertyId);
    }

    /**
     * Configures a field with the settings set for this FieldBinder.
     * <p>
     * By default this updates the buffered, read only and enabled state of the
     * field.
     * 
     * @param field
     *            The field to update
     */
    protected void configureField(Field<?> field) {
        // FIXME: Could use setBuffered if that was in Field
        field.setReadThrough(!isFieldsBuffered());
        field.setWriteThrough(!isFieldsBuffered());

        field.setEnabled(isFieldsEnabled());
        field.setReadOnly(isFieldsReadOnly());
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
    protected Collection<Object> getUnboundPropertyIds() {
        if (getItemDataSource() == null) {
            return new ArrayList<Object>();
        }
        return Collections.unmodifiableCollection(propertyIdToField.keySet());
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
        // FIXME #8094 begintransaction()

        try {
            firePreCommitEvent();
            for (Field<?> f : fieldToPropertyId.keySet()) {
                f.commit();
            }
            firePostCommitEvent();
        } catch (CommitException e) {
            // rollback
        } catch (Exception e) {
            // rollback
            throw new CommitException("Commit failed", e);
        }
        // FIXME #8094 endtransaction()

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
    public Field<?> getFieldForPropertyId(Object propertyId) {
        return propertyIdToField.get(propertyId);
    }

    /**
     * Adds a commit handler.
     * <p>
     * The commit handler is called before the field values are committed to the
     * item ( {@link CommitHandler#preCommit(CommitEvent)}) and after the item
     * has been updated ({@link CommitHandler#postCommit(CommitEvent)}). If a
     * {@link CommitHandler} throws a CommitException the whole commit is
     * aborted and the fields retinas their old values.
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
     * Returns a list of all commit handlers for this {@link FieldBinder}.
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
     * FIXME Javadoc
     * 
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
        private FieldBinder fieldBinder;

        private CommitEvent(FieldBinder fieldBinder) {
            this.fieldBinder = fieldBinder;
        }

        /**
         * Returns the field binder that this commit relates to
         * 
         * @return The FieldBinder that is being committed.
         */
        protected FieldBinder getFieldBinder() {
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
    public boolean isAllFieldsValid() {
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
     * @return true if at least on field has been modified, false otherwise
     */
    public boolean isAnyFieldModified() {
        for (Field<?> field : getFields()) {
            if (field.isModified()) {
                return true;
            }
        }
        return false;
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

    public class BindException extends RuntimeException {

        public BindException(String message) {
            super(message);
        }

        public BindException(String message, Throwable t) {
            super(message, t);
        }

    }
}