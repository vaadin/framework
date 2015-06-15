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

import java.lang.reflect.Method;

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

    // Explicit field for the test session to save it from GC
    private VaadinSession session;

    private final Grid grid = new Grid();
    private Method doEditMethod;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws SecurityException, NoSuchMethodException {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(PROPERTY_NAME, String.class, "[name]");
        container.addContainerProperty(PROPERTY_AGE, Integer.class,
                Integer.valueOf(-1));

        Item item = container.addItem(ITEM_ID);
        item.getItemProperty(PROPERTY_NAME).setValue(DEFAULT_NAME);
        item.getItemProperty(PROPERTY_AGE).setValue(DEFAULT_AGE);
        grid.setContainerDataSource(container);

        // VaadinSession needed for ConverterFactory
        VaadinService mockService = EasyMock
                .createNiceMock(VaadinService.class);
        session = new MockVaadinSession(mockService);
        VaadinSession.setCurrent(session);
        session.lock();

        // Access to method for actual editing.
        doEditMethod = Grid.class.getDeclaredMethod("doEditItem");
        doEditMethod.setAccessible(true);
    }

    @After
    public void tearDown() {
        session.unlock();
        session = null;
        VaadinSession.setCurrent(null);
    }

    @Test
    public void testInitAssumptions() throws Exception {
        assertFalse(grid.isEditorEnabled());
        assertNull(grid.getEditedItemId());
        assertNotNull(grid.getEditorFieldGroup());
    }

    @Test
    public void testSetEnabled() throws Exception {
        assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        assertTrue(grid.isEditorEnabled());
    }

    @Test
    public void testSetDisabled() throws Exception {
        assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        grid.setEditorEnabled(false);
        assertFalse(grid.isEditorEnabled());
    }

    @Test
    public void testSetReEnabled() throws Exception {
        assertFalse(grid.isEditorEnabled());
        grid.setEditorEnabled(true);
        grid.setEditorEnabled(false);
        grid.setEditorEnabled(true);
        assertTrue(grid.isEditorEnabled());
    }

    @Test
    public void testDetached() throws Exception {
        FieldGroup oldFieldGroup = grid.getEditorFieldGroup();
        grid.removeAllColumns();
        grid.setContainerDataSource(new IndexedContainer());
        assertFalse(oldFieldGroup == grid.getEditorFieldGroup());
    }

    @Test(expected = IllegalStateException.class)
    public void testDisabledEditItem() throws Exception {
        grid.editItem(ITEM_ID);
    }

    @Test
    public void testEditItem() throws Exception {
        startEdit();
        assertEquals(ITEM_ID, grid.getEditedItemId());
        assertEquals(getEditedItem(), grid.getEditorFieldGroup()
                .getItemDataSource());

        assertEquals(DEFAULT_NAME, grid.getColumn(PROPERTY_NAME)
                .getEditorField().getValue());
        assertEquals(String.valueOf(DEFAULT_AGE), grid.getColumn(PROPERTY_AGE)
                .getEditorField().getValue());
    }

    @Test
    public void testSaveEditor() throws Exception {
        startEdit();
        TextField field = (TextField) grid.getColumn(PROPERTY_NAME)
                .getEditorField();

        field.setValue("New Name");
        assertEquals(DEFAULT_NAME, field.getPropertyDataSource().getValue());

        grid.saveEditor();
        assertTrue(grid.isEditorActive());
        assertFalse(field.isModified());
        assertEquals("New Name", field.getValue());
        assertEquals("New Name", getEditedProperty(PROPERTY_NAME).getValue());
    }

    @Test
    public void testSaveEditorCommitFail() throws Exception {
        startEdit();

        ((TextField) grid.getColumn(PROPERTY_AGE).getEditorField())
                .setValue("Invalid");
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
    public void testCancelEditor() throws Exception {
        startEdit();
        TextField field = (TextField) grid.getColumn(PROPERTY_NAME)
                .getEditorField();
        field.setValue("New Name");

        Property<?> datasource = field.getPropertyDataSource();

        grid.cancelEditor();
        assertFalse(grid.isEditorActive());
        assertNull(grid.getEditedItemId());
        assertFalse(field.isModified());
        assertEquals("", field.getValue());
        assertEquals(DEFAULT_NAME, datasource.getValue());
        assertNull(field.getPropertyDataSource());
        assertNull(grid.getEditorFieldGroup().getItemDataSource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonexistentEditItem() throws Exception {
        grid.setEditorEnabled(true);
        grid.editItem(new Object());
    }

    @Test
    public void testGetField() throws Exception {
        startEdit();

        assertNotNull(grid.getColumn(PROPERTY_NAME).getEditorField());
    }

    @Test
    public void testGetFieldWithoutItem() throws Exception {
        grid.setEditorEnabled(true);
        assertNotNull(grid.getColumn(PROPERTY_NAME).getEditorField());
    }

    @Test
    public void testCustomBinding() {
        TextField textField = new TextField();
        grid.getColumn(PROPERTY_NAME).setEditorField(textField);

        startEdit();

        assertSame(textField, grid.getColumn(PROPERTY_NAME).getEditorField());
    }

    @Test(expected = IllegalStateException.class)
    public void testDisableWhileEditing() {
        startEdit();
        grid.setEditorEnabled(false);
    }

    @Test
    public void testFieldIsNotReadonly() {
        startEdit();

        Field<?> field = grid.getColumn(PROPERTY_NAME).getEditorField();
        assertFalse(field.isReadOnly());
    }

    @Test
    public void testFieldIsReadonlyWhenFieldGroupIsReadonly() {
        startEdit();

        grid.getEditorFieldGroup().setReadOnly(true);
        Field<?> field = grid.getColumn(PROPERTY_NAME).getEditorField();
        assertTrue(field.isReadOnly());
    }

    @Test
    public void testColumnRemoved() {
        Field<?> field = grid.getColumn(PROPERTY_NAME).getEditorField();

        assertSame("field should be attached to ", grid, field.getParent());

        grid.removeColumn(PROPERTY_NAME);

        assertNull("field should be detached from ", field.getParent());
    }

    @Test
    public void testSetFieldAgain() {
        TextField field = new TextField();
        grid.getColumn(PROPERTY_NAME).setEditorField(field);

        field = new TextField();
        grid.getColumn(PROPERTY_NAME).setEditorField(field);

        assertSame("new field should be used.", field,
                grid.getColumn(PROPERTY_NAME).getEditorField());
    }

    private void startEdit() {
        grid.setEditorEnabled(true);
        grid.editItem(ITEM_ID);
        // Simulate succesful client response to actually start the editing.
        try {
            doEditMethod.invoke(grid);
        } catch (Exception e) {
            Assert.fail("Editing item " + ITEM_ID + " failed. Cause: "
                    + e.getCause().toString());
        }
    }

    private Item getEditedItem() {
        assertNotNull(grid.getEditedItemId());
        return grid.getContainerDataSource().getItem(grid.getEditedItemId());
    }

    private Property<?> getEditedProperty(Object propertyId) {
        return getEditedItem().getItemProperty(PROPERTY_NAME);
    }
}
