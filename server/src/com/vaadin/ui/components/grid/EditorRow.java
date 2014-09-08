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
package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.BindException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.ui.Field;

/**
 * A class for configuring the editor row in a grid.
 * 
 * @since
 * @author Vaadin Ltd
 * @see Grid
 */
public class EditorRow implements Serializable {
    private Grid grid;

    private FieldGroup fieldGroup = new FieldGroup();
    private Object editedItemId = null;

    private HashSet<Object> uneditableProperties = new HashSet<Object>();

    /**
     * Constructs a new editor row for the given grid component.
     * 
     * @param grid
     *            the grid this editor row is attached to
     */
    EditorRow(Grid grid) {
        this.grid = grid;
    }

    /**
     * Checks whether the editor row feature is enabled for the grid or not.
     * 
     * @return <code>true</code> iff the editor row feature is enabled for the
     *         grid
     * @see #getEditedItemId()
     */
    public boolean isEnabled() {
        checkDetached();
        return grid.getState(false).editorRowEnabled;
    }

    /**
     * Sets whether or not the editor row feature is enabled for the grid.
     * 
     * @param isEnabled
     *            <code>true</code> to enable the feature, <code>false</code>
     *            otherwise
     * @throws IllegalStateException
     *             if an item is currently being edited
     * @see #getEditedItemId()
     */
    public void setEnabled(boolean isEnabled) throws IllegalStateException {
        checkDetached();
        if (getEditedItemId() != null) {
            throw new IllegalStateException("Cannot disable the editor row "
                    + "while an item (" + getEditedItemId()
                    + ") is being edited.");
        }
        if (isEnabled() != isEnabled) {
            grid.getState().editorRowEnabled = isEnabled;
        }
    }

    /**
     * Gets the field group that is backing this editor row.
     * 
     * @return the backing field group
     */
    public FieldGroup getFieldGroup() {
        checkDetached();
        return fieldGroup;
    }

    /**
     * Sets the field group that is backing this editor row.
     * 
     * @param fieldGroup
     *            the backing field group
     */
    public void setFieldGroup(FieldGroup fieldGroup) {
        checkDetached();
        this.fieldGroup = fieldGroup;
        if (editedItemId != null) {
            this.fieldGroup.setItemDataSource(getContainer().getItem(
                    editedItemId));
        }
    }

    /**
     * Builds a field using the given caption and binds it to the given property
     * id using the field binder. Ensures the new field is of the given type.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     * 
     * @param propertyId
     *            The property id to bind to. Must be present in the field
     *            finder
     * @param fieldType
     *            The type of field that we want to create
     * @throws BindException
     *             If the field could not be created
     * @return The created and bound field. Can be any type of {@link Field}.
     */
    public <T extends Field<?>> T buildAndBind(Object propertyId,
            Class<T> fieldComponent) throws BindException {
        checkDetached();
        return fieldGroup.buildAndBind(null, propertyId, fieldComponent);
    }

    /**
     * Binds the field with the given propertyId from the current item. If an
     * item has not been set then the binding is postponed until the item is set
     * using {@link #editItem(Object)}.
     * <p>
     * This method also adds validators when applicable.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     * 
     * @param field
     *            The field to bind
     * @param propertyId
     *            The propertyId to bind to the field
     * @throws BindException
     *             If the property id is already bound to another field by this
     *             field binder
     */
    public void bind(Object propertyId, Field<?> field) throws BindException {
        checkDetached();
        fieldGroup.bind(field, propertyId);
    }

    /**
     * Sets the field factory for the {@link FieldGroup}. The field factory is
     * only used when {@link FieldGroup} creates a new field.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     * 
     * @param fieldFactory
     *            The field factory to use
     */
    public void setFieldFactory(FieldGroupFieldFactory factory) {
        checkDetached();
        fieldGroup.setFieldFactory(factory);
    }

    /**
     * Gets the field component that represents a property.
     * <p>
     * If the property is not yet bound to a field, it will be bound during this
     * call. Otherwise the previously bound field will be used.
     * 
     * @param propertyId
     *            the property id of the property for which to find the field
     * @see #setPropertyUneditable(Object)
     */
    public Field<?> getField(Object propertyId) {
        checkDetached();

        final Field<?> field;
        if (fieldGroup.getUnboundPropertyIds().contains(propertyId)) {
            field = fieldGroup.buildAndBind(propertyId);
        } else {
            field = fieldGroup.getField(propertyId);
        }

        if (field != null) {
            boolean readonly = fieldGroup.isReadOnly()
                    || field.getPropertyDataSource().isReadOnly()
                    || !isPropertyEditable(propertyId);
            field.setReadOnly(readonly);
        }

        return field;
    }

    /**
     * Sets a property editable or not.
     * <p>
     * In order for a user to edit a particular value with a Field, it needs to
     * be both non-readonly and editable.
     * <p>
     * The difference between read-only and uneditable is that the read-only
     * state is propagated back into the property, while the editable property
     * is internal metadata for the editor row.
     * 
     * @param propertyId
     *            the id of the property to set as editable state
     * @param editable
     *            whether or not {@code propertyId} chould be editable
     */
    public void setPropertyEditable(Object propertyId, boolean editable) {
        checkDetached();
        checkPropertyExists(propertyId);
        if (editable) {
            uneditableProperties.remove(propertyId);
        } else {
            uneditableProperties.add(propertyId);
        }
    }

    /**
     * Checks whether a property is uneditable or not.
     * <p>
     * This only checks whether the property is configured as uneditable in this
     * editor row. The property's or field's readonly status will ultimately
     * decide whether the value can be edited or not.
     * 
     * @param propertyId
     *            the id of the property to check for editable status
     * @return <code>true</code> iff the property is editable according to this
     *         editor row
     */
    public boolean isPropertyEditable(Object propertyId) {
        checkDetached();
        checkPropertyExists(propertyId);
        return !uneditableProperties.contains(propertyId);
    }

    /**
     * Commits all changes done to the bound fields.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     * 
     * @throws CommitException
     *             If the commit was aborted
     */
    public void commit() throws CommitException {
        checkDetached();
        fieldGroup.commit();
    }

    /**
     * Discards all changes done to the bound fields.
     * <p>
     * <em>Note:</em> This is a pass-through call to the backing field group.
     */
    public void discard() {
        checkDetached();
        fieldGroup.discard();
    }

    /**
     * Internal method to inform the editor row that it is no longer attached to
     * a Grid.
     */
    void detach() {
        checkDetached();
        grid = null;
    }

    /**
     * Sets an item as editable.
     * 
     * @param itemId
     *            the id of the item to edit
     * @throws IllegalStateException
     *             if the editor row is not enabled
     * @throws IllegalArgumentException
     *             if the {@code itemId} is not in the backing container
     * @see #setEnabled(boolean)
     */
    public void editItem(Object itemId) throws IllegalStateException,
            IllegalArgumentException {
        checkDetached();

        if (!isEnabled()) {
            throw new IllegalStateException("This "
                    + getClass().getSimpleName() + " is not enabled");
        }

        Item item = getContainer().getItem(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item with id " + itemId
                    + " not found in current container");
        }

        fieldGroup.setItemDataSource(item);
        editedItemId = itemId;
    }

    /**
     * Gets the id of the item that is currently being edited.
     * 
     * @return the id of the item that is currently being edited, or
     *         <code>null</code> if no item is being edited at the moment
     */
    public Object getEditedItemId() {
        checkDetached();
        return editedItemId;
    }

    /**
     * Gets a collection of all fields represented by this editor row.
     * <p>
     * All non-editable fields (either readonly or uneditable) are in read-only
     * mode.
     * 
     * @return a collection of all the fields represented by this editor row
     */
    Collection<Field<?>> getFields() {
        checkDetached();
        /*
         * Maybe this isn't the best idea, however. Maybe the components should
         * always be transferred over the wire, to increase up-front load-time
         * and decrease on-demand load-time.
         */
        if (!isEnabled()) {
            return Collections.emptySet();
        }

        for (Object propertyId : fieldGroup.getUnboundPropertyIds()) {
            fieldGroup.buildAndBind(propertyId);
        }

        /*
         * We'll collect this ourselves instead of asking fieldGroup.getFields()
         * because we might have marked something as uneditable even though it
         * might not read-only.
         */
        ArrayList<Field<?>> fields = new ArrayList<Field<?>>();
        for (Object propertyId : getContainer().getContainerPropertyIds()) {
            Field<?> field = getField(propertyId);
            if (field != null) {
                fields.add(field);
            }
        }

        return fields;
    }

    private Container getContainer() {
        return grid.getContainerDatasource();
    }

    private void checkDetached() throws IllegalStateException {
        if (grid == null) {
            throw new IllegalStateException("The method cannot be "
                    + "processed as this " + getClass().getSimpleName()
                    + " has become detached.");
        }
    }

    private void checkPropertyExists(Object propertyId) {
        if (!getContainer().getContainerPropertyIds().contains(propertyId)) {
            throw new IllegalArgumentException("Property with id " + propertyId
                    + " is not in the current Container");
        }
    }
}
