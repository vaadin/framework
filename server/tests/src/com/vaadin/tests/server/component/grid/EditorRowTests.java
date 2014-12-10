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
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

public class EditorRowTests {

    private static final Object PROPERTY_NAME = "name";
    private static final Object PROPERTY_AGE = "age";
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
        item.getItemProperty(PROPERTY_NAME).setValue("Some Valid Name");
        item.getItemProperty(PROPERTY_AGE).setValue(Integer.valueOf(25));

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
        assertFalse(grid.isEditorRowEnabled());
        assertNull(grid.getEditedItemId());
        assertNotNull(grid.getEditorRowFieldGroup());
    }

    @Test
    public void setEnabled() throws Exception {
        assertFalse(grid.isEditorRowEnabled());
        grid.setEditorRowEnabled(true);
        assertTrue(grid.isEditorRowEnabled());
    }

    @Test
    public void setDisabled() throws Exception {
        assertFalse(grid.isEditorRowEnabled());
        grid.setEditorRowEnabled(true);
        grid.setEditorRowEnabled(false);
        assertFalse(grid.isEditorRowEnabled());
    }

    @Test
    public void setReEnabled() throws Exception {
        assertFalse(grid.isEditorRowEnabled());
        grid.setEditorRowEnabled(true);
        grid.setEditorRowEnabled(false);
        grid.setEditorRowEnabled(true);
        assertTrue(grid.isEditorRowEnabled());
    }

    @Test
    public void detached() throws Exception {
        FieldGroup oldFieldGroup = grid.getEditorRowFieldGroup();
        grid.removeAllColumns();
        grid.setContainerDataSource(new IndexedContainer());
        assertFalse(oldFieldGroup == grid.getEditorRowFieldGroup());
    }

    @Test
    public void propertyUneditable() throws Exception {
        assertTrue(grid.isPropertyEditable(PROPERTY_NAME));
        grid.setPropertyEditable(PROPERTY_NAME, false);
        assertFalse(grid.isPropertyEditable(PROPERTY_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonexistentPropertyUneditable() throws Exception {
        grid.setPropertyEditable(new Object(), false);
    }

    @Test(expected = IllegalStateException.class)
    public void disabledEditItem() throws Exception {
        grid.editItem(ITEM_ID);
    }

    @Test
    public void editItem() throws Exception {
        startEdit();
        assertEquals(ITEM_ID, grid.getEditedItemId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonexistentEditItem() throws Exception {
        grid.setEditorRowEnabled(true);
        grid.editItem(new Object());
    }

    @Test
    public void getField() throws Exception {
        startEdit();

        assertNotNull(grid.getEditorRowField(PROPERTY_NAME));
    }

    @Test
    public void getFieldWithoutItem() throws Exception {
        grid.setEditorRowEnabled(true);
        assertNull(grid.getEditorRowField(PROPERTY_NAME));
    }

    @Test
    public void getFieldAfterReSettingFieldAsEditable() throws Exception {
        startEdit();

        grid.setPropertyEditable(PROPERTY_NAME, false);
        grid.setPropertyEditable(PROPERTY_NAME, true);
        assertNotNull(grid.getEditorRowField(PROPERTY_NAME));
    }

    @Test
    public void isEditable() {
        assertTrue(grid.isPropertyEditable(PROPERTY_NAME));
    }

    @Test
    public void isUneditable() {
        grid.setPropertyEditable(PROPERTY_NAME, false);
        assertFalse(grid.isPropertyEditable(PROPERTY_NAME));
    }

    @Test
    public void isEditableAgain() {
        grid.setPropertyEditable(PROPERTY_NAME, false);
        grid.setPropertyEditable(PROPERTY_NAME, true);
        assertTrue(grid.isPropertyEditable(PROPERTY_NAME));
    }

    @Test
    public void isUneditableAgain() {
        grid.setPropertyEditable(PROPERTY_NAME, false);
        grid.setPropertyEditable(PROPERTY_NAME, true);
        grid.setPropertyEditable(PROPERTY_NAME, false);
        assertFalse(grid.isPropertyEditable(PROPERTY_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNonexistentEditable() {
        grid.isPropertyEditable(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNonexistentUneditable() {
        grid.setPropertyEditable(new Object(), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNonexistentEditable() {
        grid.setPropertyEditable(new Object(), true);
    }

    @Test
    public void customBinding() {
        TextField textField = new TextField();
        grid.bindEditorRowField(PROPERTY_NAME, textField);

        startEdit();

        assertSame(textField, grid.getEditorRowField(PROPERTY_NAME));
    }

    @Test(expected = IllegalStateException.class)
    public void disableWhileEditing() {
        startEdit();
        grid.setEditorRowEnabled(false);
    }

    @Test
    public void fieldIsNotReadonly() {
        startEdit();

        Field<?> field = grid.getEditorRowField(PROPERTY_NAME);
        assertFalse(field.isReadOnly());
    }

    @Test
    public void fieldIsReadonlyWhenFieldGroupIsReadonly() {
        startEdit();

        grid.getEditorRowFieldGroup().setReadOnly(true);
        Field<?> field = grid.getEditorRowField(PROPERTY_NAME);
        assertTrue(field.isReadOnly());
    }

    @Test
    public void fieldIsReadonlyWhenPropertyIsNotEditable() {
        startEdit();

        grid.setPropertyEditable(PROPERTY_NAME, false);
        Field<?> field = grid.getEditorRowField(PROPERTY_NAME);
        assertTrue(field.isReadOnly());
    }

    private void startEdit() {
        grid.setEditorRowEnabled(true);
        grid.editItem(ITEM_ID);
    }
}
