package com.vaadin.tests.components.select;

import java.util.LinkedHashMap;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickSource;
import com.vaadin.tests.components.abstractfield.AbstractFieldTestCase;
import com.vaadin.ui.AbstractSelect;

public abstract class AbstractSelectTestCase<T extends AbstractSelect> extends
        AbstractFieldTestCase<T> implements ItemClickListener {

    protected static final String CATEGORY_CONTENT = "Contents";

    private int items = 0;
    private int properties = 0;

    @Override
    protected void createActions() {
        super.createActions();
        createNullSelectAllowedCheckbox(CATEGORY_SELECTION);
        createPropertiesInContainerSelect(CATEGORY_CONTENT);
        createItemsInContainerSelect(CATEGORY_CONTENT);

    }

    protected void createNullSelectAllowedCheckbox(String category) {
        createBooleanAction("Null Selection Allowed", category, false,
                nullSelectionAllowedCommand);

    }

    protected void createNullSelectItemId(String category) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<String, Object>();
        options.put("- None -", null);
        for (Object id : (getComponent()).getContainerDataSource()
                .getContainerPropertyIds()) {
            options.put(id.toString(), id);
        }
        createSelectAction("Null Selection Item Id", category, options,
                "- None -", nullSelectItemIdCommand);
    }

    protected Container createContainer(int properties, int items) {
        return createIndexedContainer(properties, items);
    }

    private Container createIndexedContainer(int properties, int items) {
        IndexedContainer c = new IndexedContainer();
        populateContainer(c, properties, items);

        return c;
    }

    protected void populateContainer(Container c, int properties, int items) {
        c.removeAllItems();
        for (int i = 1; i <= properties; i++) {
            c.addContainerProperty("Property " + i, String.class, "");
        }
        for (int i = 1; i <= items; i++) {
            Item item = c.addItem("Item " + i);
            for (int j = 1; j <= properties; j++) {
                item.getItemProperty("Property " + j).setValue(
                        "Item " + i + "," + j);
            }
        }

    }

    protected void createItemsInContainerSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        for (int i = 0; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("20", 20);
        options.put("100", 100);
        options.put("1000", 1000);
        options.put("10000", 10000);
        options.put("100000", 100000);

        createSelectAction("Items in container", category, options, "20",
                itemsInContainerCommand);
    }

    protected void createPropertiesInContainerSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        for (int i = 0; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("50", 50);
        options.put("100", 100);
        options.put("1000", 1000);

        createSelectAction("Properties in container", category, options, "10",
                propertiesInContainerCommand);
    }

    protected void createItemClickListener(String category) {
        createBooleanAction("Item click listener", category, false,
                itemClickListenerCommand);
    }

    /* COMMANDS */

    protected Command<T, Boolean> nullSelectionAllowedCommand = new Command<T, Boolean>() {

        public void execute(T c, Boolean value, Object data) {
            (c).setNullSelectionAllowed(value);
        }
    };

    protected Command<T, Object> nullSelectItemIdCommand = new Command<T, Object>() {

        public void execute(T c, Object value, Object data) {
            c.setNullSelectionItemId(value);
        }
    };

    protected Command<T, Integer> itemsInContainerCommand = new Command<T, Integer>() {

        public void execute(T t, Integer value, Object data) {
            items = value;
            updateContainer();
        }
    };

    protected Command<T, Integer> propertiesInContainerCommand = new Command<T, Integer>() {

        public void execute(T t, Integer value, Object data) {
            properties = value;
            updateContainer();
        }
    };

    protected Command<T, Boolean> itemClickListenerCommand = new Command<T, Boolean>() {

        public void execute(T c, Boolean value, Object data) {
            if (value) {
                ((ItemClickSource) c).addListener(AbstractSelectTestCase.this);
            } else {
                ((ItemClickSource) c)
                        .removeListener(AbstractSelectTestCase.this);
            }

        }
    };

    protected void setContainer(Container newContainer) {
        getComponent().setContainerDataSource(newContainer);

    }

    protected void updateContainer() {
        setContainer(createContainer(properties, items));
    }

    /* COMMANDS END */

    public void itemClick(ItemClickEvent event) {
        log("ItemClick on itemId: " + event.getItemId() + ", propertyId: "
                + event.getPropertyId() + " using " + event.getButtonName());
    }
}
