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
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Field;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.EditorRow;
import com.vaadin.ui.TextField;

public class EditorRowTests {

    private static final Object PROPERTY_NAME = "name";
    private static final Object PROPERTY_AGE = "age";
    private static final Object ITEM_ID = new Object();

    private Grid grid;
    private EditorRow row;

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
        row = grid.getEditorRow();

        // VaadinSession needed for ConverterFactory
        VaadinService mockService = EasyMock
                .createNiceMock(VaadinService.class);
        VaadinSession session = new MockVaadinSession(mockService);
        VaadinSession.setCurrent(session);
        session.lock();
    }

    @After
    public void tearDown() {
        VaadinSession.getCurrent().unlock();
        VaadinSession.setCurrent(null);
    }

    @Test
    public void initAssumptions() throws Exception {
        assertNotNull(row);
        assertFalse(row.isEnabled());
        assertNull(row.getEditedItemId());
        assertNotNull(row.getFieldGroup());
    }

    @Test
    public void setEnabled() throws Exception {
        assertFalse(row.isEnabled());
        row.setEnabled(true);
        assertTrue(row.isEnabled());
    }

    @Test
    public void setDisabled() throws Exception {
        assertFalse(row.isEnabled());
        row.setEnabled(true);
        row.setEnabled(false);
        assertFalse(row.isEnabled());
    }

    @Test
    public void setReEnabled() throws Exception {
        assertFalse(row.isEnabled());
        row.setEnabled(true);
        row.setEnabled(false);
        row.setEnabled(true);
        assertTrue(row.isEnabled());
    }

    @Test(expected = IllegalStateException.class)
    public void detached() throws Exception {
        EditorRow oldEditorRow = row;
        grid.setContainerDataSource(new IndexedContainer());
        oldEditorRow.isEnabled();
    }

    @Test
    public void propertyUneditable() throws Exception {
        row.setPropertyEditable(PROPERTY_NAME, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonexistentPropertyUneditable() throws Exception {
        row.setPropertyEditable(new Object(), false);
    }

    @Test(expected = IllegalStateException.class)
    public void disabledEditItem() throws Exception {
        row.editItem(ITEM_ID);
    }

    @Test
    public void editItem() throws Exception {
        startEdit();
        assertEquals(ITEM_ID, row.getEditedItemId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonexistentEditItem() throws Exception {
        row.setEnabled(true);
        row.editItem(new Object());
    }

    @Test
    public void getField() throws Exception {
        startEdit();

        assertNotNull(row.getField(PROPERTY_NAME));
    }

    @Test
    public void getFieldWithoutItem() throws Exception {
        row.setEnabled(true);
        assertNull(row.getField(PROPERTY_NAME));
    }

    @Test
    public void getFieldAfterReSettingFieldAsEditable() throws Exception {
        startEdit();

        row.setPropertyEditable(PROPERTY_NAME, false);
        row.setPropertyEditable(PROPERTY_NAME, true);
        assertNotNull(row.getField(PROPERTY_NAME));
    }

    @Test
    public void isEditable() {
        assertTrue(row.isPropertyEditable(PROPERTY_NAME));
    }

    @Test
    public void isUneditable() {
        row.setPropertyEditable(PROPERTY_NAME, false);
        assertFalse(row.isPropertyEditable(PROPERTY_NAME));
    }

    @Test
    public void isEditableAgain() {
        row.setPropertyEditable(PROPERTY_NAME, false);
        row.setPropertyEditable(PROPERTY_NAME, true);
        assertTrue(row.isPropertyEditable(PROPERTY_NAME));
    }

    @Test
    public void isUneditableAgain() {
        row.setPropertyEditable(PROPERTY_NAME, false);
        row.setPropertyEditable(PROPERTY_NAME, true);
        row.setPropertyEditable(PROPERTY_NAME, false);
        assertFalse(row.isPropertyEditable(PROPERTY_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNonexistentEditable() {
        row.isPropertyEditable(new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNonexistentUneditable() {
        row.setPropertyEditable(new Object(), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNonexistentEditable() {
        row.setPropertyEditable(new Object(), true);
    }

    @Test
    public void customBinding() {
        TextField textField = new TextField();
        row.bind(PROPERTY_NAME, textField);

        startEdit();

        assertSame(textField, row.getField(PROPERTY_NAME));
    }

    @Test(expected = IllegalStateException.class)
    public void disableWhileEditing() {
        startEdit();
        row.setEnabled(false);
    }

    @Test
    public void fieldIsNotReadonly() {
        startEdit();

        Field<?> field = row.getField(PROPERTY_NAME);
        assertFalse(field.isReadOnly());
    }

    @Test
    public void fieldIsReadonlyWhenFieldGroupIsReadonly() {
        startEdit();

        row.getFieldGroup().setReadOnly(true);
        Field<?> field = row.getField(PROPERTY_NAME);
        assertTrue(field.isReadOnly());
    }

    @Test
    public void fieldIsReadonlyWhenPropertyIsNotEditable() {
        startEdit();

        row.setPropertyEditable(PROPERTY_NAME, false);
        Field<?> field = row.getField(PROPERTY_NAME);
        assertTrue(field.isReadOnly());
    }

    private void startEdit() {
        row.setEnabled(true);
        row.editItem(ITEM_ID);
    }
}
