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
package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

public class GridEditorTest {

    private static final Object PROPERTY_NAME = "name";
    private static final Object PROPERTY_AGE = "age";
    private static final String DEFAULT_NAME = "Some Valid Name";
    private static final Integer DEFAULT_AGE = 25;
    private static final Object ITEM_ID = new Object();

    private Grid grid;

    // Explicit field for the test session to save it from GC
    private VaadinSession session;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(PROPERTY_NAME, String.class, "[name]");
        container.addContainerProperty(PROPERTY_AGE, Integer.class,
                Integer.valueOf(-1));

        Item item = container.addItem(ITEM_ID);
        item.getItemProperty(PROPERTY_NAME).setValue(DEFAULT_NAME);
        item.getItemProperty(PROPERTY_AGE).setValue(DEFAULT_AGE);

        grid = new Grid(container);

        // VaadinSession needed for ConverterFactory
        VaadinService mockService = EasyMock
                .createNiceMock(VaadinService.class);
        session = new MockVaadinSession(mockService);
        VaadinSession.setCurrent(session);
        session.lock();
    }

    @After
    public void tearDown() {
        session.unlock();
        session = null;
        VaadinSession.setCurrent(null);
    }

    @Test
    public void initAssumptions() throws Exception {
        assertFalse(grid.isEditorEnabled());
        assertNull(grid.getEditedItemId());
        assertNotNull(grid.getEditorFieldGroup());
    }

    @Test
    public void setEnabled() throws Exception {
        assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        assertTrue(grid.isEditorEnabled());
    }

    @Test
    public void setDisabled() throws Exception {
        assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        grid.setEditorEnabled(false);
        assertFalse(grid.isEditorEnabled());
    }

    @Test
    public void setReEnabled() throws Exception {
        assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        grid.setEditorEnabled(false);
        grid.setEditorEnabled(true);
        assertTrue(grid.isEditorEnabled());
    }

    @Test
    public void detached() throws Exception {
        FieldGroup oldFieldGroup = grid.getEditorFieldGroup();
        grid.removeAllColumns();
        grid.setContainerDataSource(new IndexedContainer());
        assertFalse(oldFieldGroup == grid.getEditorFieldGroup());
    }

    @Test(expected = IllegalStateException.class)
    public void disabledEditItem() throws Exception {
        grid.editItem(ITEM_ID);
    }

    @Test
    public void editItem() throws Exception {
        startEdit();
        assertEquals(ITEM_ID, grid.getEditedItemId());
        assertEquals(getEditedItem(), grid.getEditorFieldGroup()
                .getItemDataSource());

        assertEquals(DEFAULT_NAME, grid.getEditorField(PROPERTY_NAME)
                .getValue());
        assertEquals(String.valueOf(DEFAULT_AGE),
                grid.getEditorField(PROPERTY_AGE).getValue());
    }

    @Test
    public void saveEditor() throws Exception {
        startEdit();
        TextField field = (TextField) grid.getEditorField(PROPERTY_NAME);

        field.setValue("New Name");
        assertEquals(DEFAULT_NAME, field.getPropertyDataSource().getValue());

        grid.saveEditor();
        assertTrue(grid.isEditorActive());
        assertFalse(field.isModified());
        assertEquals("New Name", field.getValue());
        assertEquals("New Name", getEditedProperty(PROPERTY_NAME).getValue());
    }

    @Test
    public void saveEditorCommitFail() throws Exception {
        startEdit();

        ((TextField) grid.getEditorField(PROPERTY_AGE)).setValue("Invalid");
        try {
            // Manual fail instead of @Test(expected=...) to check it is
            // saveEditor that fails and not setValue
            grid.saveEditor();
            Assert.fail("CommitException expected when saving an invalid field value");
        } catch (CommitException e) {
            // expected
        }
    }

    @Test
    public void cancelEditor() throws Exception {
        startEdit();
        TextField field = (TextField) grid.getEditorField(PROPERTY_NAME);
        field.setValue("New Name");

        grid.cancelEditor();
        assertFalse(grid.isEditorActive());
        assertNull(grid.getEditedItemId());
        assertFalse(field.isModified());
        assertEquals(DEFAULT_NAME, field.getValue());
        assertEquals(DEFAULT_NAME, field.getPropertyDataSource().getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonexistentEditItem() throws Exception {
        grid.setEditorEnabled(true);
        grid.editItem(new Object());
    }

    @Test
    public void getField() throws Exception {
        startEdit();

        assertNotNull(grid.getEditorField(PROPERTY_NAME));
    }

    @Test
    public void getFieldWithoutItem() throws Exception {
        grid.setEditorEnabled(true);
        assertNotNull(grid.getEditorField(PROPERTY_NAME));
    }

    @Test
    public void customBinding() {
        TextField textField = new TextField();
        grid.setEditorField(PROPERTY_NAME, textField);

        startEdit();

        assertSame(textField, grid.getEditorField(PROPERTY_NAME));
    }

    @Test(expected = IllegalStateException.class)
    public void disableWhileEditing() {
        startEdit();
        grid.setEditorEnabled(false);
    }

    @Test
    public void fieldIsNotReadonly() {
        startEdit();

        Field<?> field = grid.getEditorField(PROPERTY_NAME);
        assertFalse(field.isReadOnly());
    }

    @Test
    public void fieldIsReadonlyWhenFieldGroupIsReadonly() {
        startEdit();

        grid.getEditorFieldGroup().setReadOnly(true);
        Field<?> field = grid.getEditorField(PROPERTY_NAME);
        assertTrue(field.isReadOnly());
    }

    @Test
    public void columnRemoved() {
        Field<?> field = grid.getEditorField(PROPERTY_NAME);

        assertSame("field should be attached to grid.", grid, field.getParent());

        grid.removeColumn(PROPERTY_NAME);

        assertNull("field should be detached from grid.", field.getParent());
    }

    @Test
    public void setFieldAgain() {
        TextField field = new TextField();
        grid.setEditorField(PROPERTY_NAME, field);

        field = new TextField();
        grid.setEditorField(PROPERTY_NAME, field);

        assertSame("new field should be used.", field,
                grid.getEditorField(PROPERTY_NAME));
    }

    private void startEdit() {
        grid.setEditorEnabled(true);
        grid.editItem(ITEM_ID);
    }

    private Item getEditedItem() {
        assertNotNull(grid.getEditedItemId());
        return grid.getContainerDataSource().getItem(grid.getEditedItemId());
    }

    private Property<?> getEditedProperty(Object propertyId) {
        return getEditedItem().getItemProperty(PROPERTY_NAME);
    }
}
