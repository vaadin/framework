package com.vaadin.tests.server.component.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GridEditorMissingPropertyTest {

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_SIZE = "size";

    private static final String FOLDER_NAME_BEFORE = "Folder name";
    private static final String FOLDER_NAME_AFTER = "Modified folder name";
    private static final String FILE_NAME_BEFORE = "File name";
    private static final String FILE_NAME_AFTER = "Modified file name";
    private static final String FILE_SIZE_BEFORE = "10kB";
    private static final String FILE_SIZE_AFTER = "20MB";

    private final Grid grid = new Grid();

    // Test items
    private final Folder folder = new Folder(FOLDER_NAME_BEFORE);
    private final File file = new File(FILE_NAME_BEFORE, FILE_SIZE_BEFORE);

    @Before
    public void setup() throws SecurityException, NoSuchMethodException  {
        final BeanItem<Entry> folderItem = new BeanItem<Entry>(folder);
        final BeanItem<Entry> childItem = new BeanItem<Entry>(file);

        @SuppressWarnings("unchecked")
        TestContainer container = new TestContainer(
                Arrays.asList(folderItem, childItem),
                Arrays.asList(PROPERTY_NAME, PROPERTY_SIZE));

        grid.setContainerDataSource(container);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setEditorEnabled(true);
    }

    @Test
    public void testBindFields() {
        FieldGroup fieldGroup = grid.getEditorFieldGroup();

        // Item with incomplete property set
        fieldGroup.setItemDataSource(
                grid.getContainerDataSource().getItem(folder));
        grid.getColumn(PROPERTY_NAME)
                .getEditorField(); // called in grid.doEditItem
        assertTrue("Properties in item should be bound",
                fieldGroup.getBoundPropertyIds().contains(PROPERTY_NAME));
        assertFalse("Properties not present in item should not be bound",
                fieldGroup.getBoundPropertyIds().contains(PROPERTY_SIZE));
        assertTrue("All of item's properties should be bound",
                fieldGroup.getUnboundPropertyIds().isEmpty());

        // Unbind all fields
        fieldGroup.setItemDataSource(null);
        assertTrue("No properties should be bound",
                fieldGroup.getBoundPropertyIds().isEmpty());
        assertTrue("No unbound properties should exist",
                fieldGroup.getUnboundPropertyIds().isEmpty());

        // Item with complete property set
        fieldGroup
                .setItemDataSource(grid.getContainerDataSource().getItem(file));
        grid.getColumn(PROPERTY_NAME).getEditorField();
        grid.getColumn(PROPERTY_SIZE).getEditorField();
        assertTrue("Properties in item should be bound",
                fieldGroup.getBoundPropertyIds().contains(PROPERTY_NAME));
        assertTrue("Properties in item should be bound",
                fieldGroup.getBoundPropertyIds().contains(PROPERTY_SIZE));
        assertTrue("All of item's properties should be bound",
                fieldGroup.getUnboundPropertyIds().isEmpty());

        // Unbind all fields
        fieldGroup.setItemDataSource(null);
        assertTrue("No properties should be bound",
                fieldGroup.getBoundPropertyIds().isEmpty());
        assertTrue("No unbound properties should exist",
                fieldGroup.getUnboundPropertyIds().isEmpty());
    }

    @Test
    public void testSetEditorField() {
        FieldGroup fieldGroup = grid.getEditorFieldGroup();
        Field editorField = new PasswordField();

        // Explicitly set editor field
        fieldGroup.setItemDataSource(
                grid.getContainerDataSource().getItem(folder));
        grid.getColumn(PROPERTY_NAME).setEditorField(editorField);
        assertTrue("Editor field should be the one that was previously set",
                grid.getColumn(PROPERTY_NAME).getEditorField() == editorField);

        // Reset item
        fieldGroup.setItemDataSource(null);
        fieldGroup
                .setItemDataSource(grid.getContainerDataSource().getItem(file));
        assertTrue("Editor field should be the one that was previously set",
                grid.getColumn(PROPERTY_NAME).getEditorField() == editorField);
    }

    @Test
    public void testEditCell() {
        // Row with missing property
        startEdit(folder);
        assertEquals(folder, grid.getEditedItemId());
        assertEquals(getEditedItem(),
                grid.getEditorFieldGroup().getItemDataSource());

        assertEquals(FOLDER_NAME_BEFORE,
                grid.getColumn(PROPERTY_NAME).getEditorField().getValue());
        try {
            grid.getColumn(PROPERTY_SIZE).getEditorField();
            fail("Grid.editorFieldGroup should throw BindException by default");
        } catch (FieldGroup.BindException e) {
            // BindException is thrown using the default FieldGroup
        }
        grid.cancelEditor();

        // Row with all properties
        startEdit(file);
        assertEquals(file, grid.getEditedItemId());
        assertEquals(getEditedItem(),
                grid.getEditorFieldGroup().getItemDataSource());

        assertEquals(FILE_NAME_BEFORE,
                grid.getColumn(PROPERTY_NAME).getEditorField().getValue());
        assertEquals(FILE_SIZE_BEFORE,
                grid.getColumn(PROPERTY_SIZE).getEditorField().getValue());
        grid.cancelEditor();
    }

    @Test
    public void testCancelEditor() {
        // Row with all properties
        testCancel(file, PROPERTY_NAME, FILE_NAME_BEFORE, FILE_NAME_AFTER);
        testCancel(file, PROPERTY_SIZE, FILE_SIZE_BEFORE, FILE_SIZE_AFTER);

        // Row with missing property
        testCancel(folder, PROPERTY_NAME, FOLDER_NAME_BEFORE, FOLDER_NAME_AFTER);
    }

    private void testCancel(Object itemId, String propertyId,
            String valueBefore, String valueAfter) {
        startEdit(itemId);

        TextField field = (TextField) grid.getColumn(propertyId)
                .getEditorField();
        field.setValue(valueAfter);

        Property<?> datasource = field.getPropertyDataSource();

        grid.cancelEditor();
        assertFalse(grid.isEditorActive());
        assertNull(grid.getEditedItemId());
        assertFalse(field.isModified());
        assertEquals("", field.getValue());
        assertEquals(valueBefore, datasource.getValue());
        assertNull(field.getPropertyDataSource());
        assertNull(grid.getEditorFieldGroup().getItemDataSource());
    }

    @Test
    public void testSaveEditor() throws Exception {
        // Row with all properties
        testSave(file, PROPERTY_SIZE, FILE_SIZE_BEFORE, FILE_SIZE_AFTER);

        // Row with missing property
        testSave(folder, PROPERTY_NAME, FOLDER_NAME_BEFORE, FOLDER_NAME_AFTER);
    }

    private void testSave(Object itemId, String propertyId, String valueBefore,
            String valueAfter) throws Exception {
        startEdit(itemId);
        TextField field = (TextField) grid.getColumn(propertyId)
                .getEditorField();

        field.setValue(valueAfter);
        assertEquals(valueBefore, field.getPropertyDataSource().getValue());

        grid.saveEditor();
        assertTrue(grid.isEditorActive());
        assertFalse(field.isModified());
        assertEquals(valueAfter, field.getValue());
        assertEquals(valueAfter, getEditedProperty(propertyId).getValue());
        grid.cancelEditor();
    }

    private Item getEditedItem() {
        assertNotNull(grid.getEditedItemId());
        return grid.getContainerDataSource().getItem(grid.getEditedItemId());
    }

    private Property<?> getEditedProperty(Object propertyId) {
        return getEditedItem().getItemProperty(propertyId);
    }

    private void startEdit(Object itemId) {
        grid.setEditorEnabled(true);
        grid.editItem(itemId);
        // Simulate succesful client response to actually start the editing.
        grid.doEditItem();
    }

    private class TestContainer extends
            AbstractInMemoryContainer<Object, String, BeanItem> {

        private final List<BeanItem<Entry>> items;
        private final List<String> pids;

        public TestContainer(List<BeanItem<Entry>> items, List<String> pids) {
            this.items = items;
            this.pids = pids;
        }

        @Override
        protected List<Object> getAllItemIds() {
            List<Object> ids = new ArrayList<Object>();
            for (BeanItem<Entry> item : items) {
                ids.add(item.getBean());
            }
            return ids;
        }

        @Override
        protected BeanItem<Entry> getUnfilteredItem(Object itemId) {
            for (BeanItem<Entry> item : items) {
                if (item.getBean().equals(itemId)) {
                    return item;
                }
            }
            return null;
        }

        @Override
        public Collection<?> getContainerPropertyIds() {
            return pids;
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            return getItem(itemId).getItemProperty(propertyId);
        }

        @Override
        public Class<?> getType(Object propertyId) {
            return String.class;
        }
    }

    public class Entry {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Entry(String name) {
            this.name = name;
        }
    }

    public class Folder extends Entry {

        public Folder(String name) {
            super(name);
        }
    }

    public class File extends Entry {
        private String size;

        public File(String name, String size) {
            super(name);
            this.size = size;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }

    private class Grid extends com.vaadin.ui.Grid {
        @Override
        protected void doEditItem() {
            super.doEditItem();
        }
    }
}
