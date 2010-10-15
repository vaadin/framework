package com.vaadin.tests.components.select;

import java.util.LinkedHashMap;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.abstractfield.AbstractFieldTestCase;
import com.vaadin.ui.AbstractSelect;

public abstract class AbstractSelectTestCase<T extends AbstractSelect> extends
        AbstractFieldTestCase<T> {

    protected static final String CATEGORY_CONTENT = "Contents";

    @Override
    protected void createActions() {
        super.createActions();
        createNullSelectAllowedCheckbox(CATEGORY_SELECTION);
        createItemsInContainerSelect(CATEGORY_CONTENT);
        createColumnsInContainerSelect(CATEGORY_CONTENT);

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
        IndexedContainer c = new IndexedContainer();
        for (int i = 1; i <= properties; i++) {
            c.addContainerProperty("Column " + i, String.class, "");
        }
        for (int i = 1; i <= items; i++) {
            Item item = c.addItem("Item " + i);
            for (int j = 1; j <= properties; j++) {
                item.getItemProperty("Column " + j).setValue(
                        "Item " + i + "," + j);
            }
        }

        return c;
    }

    protected void createItemsInContainerSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("20", 20);
        options.put("100", 100);
        options.put("1000", 1000);
        options.put("10000", 10000);
        options.put("100000", 100000);

        createSelectAction("Items in container", category, options, "20",
                itemsInContainerCommand);
    }

    protected void createColumnsInContainerSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("5", 5);
        options.put("10", 10);
        options.put("50", 50);
        options.put("100", 100);
        options.put("1000", 1000);

        createSelectAction("Columns in container", category, options, "10",
                columnsInContainerCommand);
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
            t.setContainerDataSource(createContainer(t.getContainerDataSource()
                    .getContainerPropertyIds().size(), value));
        }
    };

    protected Command<T, Integer> columnsInContainerCommand = new Command<T, Integer>() {

        public void execute(T t, Integer value, Object data) {
            t.setContainerDataSource(createContainer(value, t
                    .getContainerDataSource().size()));
        }
    };

    /* COMMANDS END */

}
