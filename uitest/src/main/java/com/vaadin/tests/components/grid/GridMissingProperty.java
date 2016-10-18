package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridMissingProperty extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();

        final Folder folder = new Folder("Folder name");
        final BeanItem<Entry> folderItem = new BeanItem<Entry>(folder);

        final File file = new File("File name", "10kB");
        final BeanItem<Entry> fileItem = new BeanItem<Entry>(file);

        @SuppressWarnings("unchecked")
        TestContainer container = new TestContainer(
                Arrays.asList(folderItem, fileItem),
                Arrays.asList("name", "size"));

        grid.setContainerDataSource(container);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setEditorEnabled(true);

        addComponent(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Grid Editor should not throw exception even when items are missing properties.";
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
}
