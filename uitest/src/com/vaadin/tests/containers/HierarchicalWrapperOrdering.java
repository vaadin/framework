package com.vaadin.tests.containers;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;

public class HierarchicalWrapperOrdering extends TestBase {

    private static final long serialVersionUID = 1L;

    IndexedContainer indexedContainer = new IndexedContainer();

    Container.Hierarchical hier = new ContainerHierarchicalWrapper(
            indexedContainer);

    Tree tree1;

    private static void sort(IndexedContainer container) {
        Object[] properties = new Object[1];
        properties[0] = "name";

        boolean[] ascending = new boolean[1];
        ascending[0] = true;

        container.sort(properties, ascending);
    }

    private static void populateContainer(Container.Hierarchical container) {
        container.addContainerProperty("name", String.class, null);

        addItem(container, "Games", null);
        addItem(container, "Call of Duty", "Games");
        addItem(container, "Might and Magic", "Games");
        addItem(container, "Fallout", "Games");
        addItem(container, "Red Alert", "Games");

        addItem(container, "Cars", null);
        addItem(container, "Toyota", "Cars");
        addItem(container, "Volvo", "Cars");
        addItem(container, "Audi", "Cars");
        addItem(container, "Ford", "Cars");

        addItem(container, "Natural languages", null);
        addItem(container, "Swedish", "Natural languages");
        addItem(container, "English", "Natural languages");
        addItem(container, "Finnish", "Natural languages");

        addItem(container, "Programming languages", null);
        addItem(container, "C++", "Programming languages");
        addItem(container, "PHP", "Programming languages");
        addItem(container, "Java", "Programming languages");
        addItem(container, "Python", "Programming languages");

    }

    public static void addItem(Container.Hierarchical container, String string,
            String parent) {
        Item item = container.addItem(string);
        item.getItemProperty("name").setValue(string);

        if (parent != null) {
            container.setParent(string, parent);
        }

    }

    @Override
    protected void setup() {
        Layout l = getLayout();

        populateContainer(hier);

        // sort(indexedContainer);

        tree1 = new Tree("Tree with wrapped IndexedContainer");
        tree1.setContainerDataSource(hier);
        tree1.setItemCaptionPropertyId("name");
        for (Object id : hier.rootItemIds()) {
            tree1.expandItemsRecursively(id);
        }
        l.addComponent(tree1);

        // This contains a bug, changes not reflected back to client
        Button modify = new Button("Modify and sort (has a bug)",
                new ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        // Get first item
                        Object itemId = indexedContainer.getIdByIndex(0);
                        Item item = indexedContainer.getItem(itemId);
                        Property<String> property = item
                                .getItemProperty("name");
                        // Prepend with Z so item should get sorted later
                        property.setValue("Z " + property.getValue());
                        // this does not work alone, requires extraneous
                        // setContainerDataSource for server-side changes to be
                        // reflected back to client-side
                        sort(indexedContainer);
                    }
                });
        l.addComponent(modify);

        Table t = new Table("Table with indexed container", indexedContainer);

        l.addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "Items should be in same order as in wrapped container after sorting.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3688;
    }

}
