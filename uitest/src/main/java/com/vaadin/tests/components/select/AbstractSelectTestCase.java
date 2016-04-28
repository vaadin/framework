package com.vaadin.tests.components.select;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickNotifier;
import com.vaadin.server.Resource;
import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.AbstractSelect;

public abstract class AbstractSelectTestCase<T extends AbstractSelect> extends
        AbstractFieldTest<T> implements ItemClickListener {

    public static final String CATEGORY_DATA_SOURCE = "Data source";

    private int items = 0;
    private int properties = 0;

    protected static class ContextMenu {

        private List<Action> items = new ArrayList<Action>();

        public ContextMenu(String caption, Resource icon) {
            addItem(caption, icon);
        }

        public ContextMenu() {
        }

        public void addItem(String caption, Resource icon) {
            items.add(new Action(caption, icon));
        }

        public Action[] getActions(Object target, Object sender) {
            Action[] actions = new Action[items.size()];
            for (int i = 0; i < items.size(); i++) {
                actions[i] = items.get(i);
            }

            return actions;
        }

    }

    @Override
    protected void createActions() {
        super.createActions();
        createNullSelectAllowedCheckbox(CATEGORY_SELECTION);
        createMultiSelectCheckbox(CATEGORY_SELECTION);

        createPropertiesInContainerSelect(CATEGORY_DATA_SOURCE);
        createItemsInContainerSelect(CATEGORY_DATA_SOURCE);

    }

    protected void createNullSelectAllowedCheckbox(String category) {
        createBooleanAction("Null Selection Allowed", category, false,
                nullSelectionAllowedCommand);

    }

    protected void createMultiSelectCheckbox(String category) {
        createBooleanAction("Multi select", category, false, multiselectCommand);

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

        @Override
        public void execute(T c, Boolean value, Object data) {
            (c).setNullSelectionAllowed(value);
        }
    };

    protected Command<T, Boolean> multiselectCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setMultiSelect(value);
        }
    };

    protected Command<T, Object> nullSelectItemIdCommand = new Command<T, Object>() {

        @Override
        public void execute(T c, Object value, Object data) {
            c.setNullSelectionItemId(value);
        }
    };

    protected Command<T, Integer> itemsInContainerCommand = new Command<T, Integer>() {

        @Override
        public void execute(T t, Integer value, Object data) {
            items = value;
            updateContainer();
        }
    };

    protected Command<T, Integer> propertiesInContainerCommand = new Command<T, Integer>() {

        @Override
        public void execute(T t, Integer value, Object data) {
            properties = value;
            updateContainer();
        }
    };

    protected Command<T, Boolean> itemClickListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                ((ItemClickNotifier) c)
                        .addListener(AbstractSelectTestCase.this);
            } else {
                ((ItemClickNotifier) c)
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

    @Override
    public void itemClick(ItemClickEvent event) {
        String type = event.getButtonName();
        if (event.isDoubleClick()) {
            type += " double-click";
        } else {
            type += " click";
        }

        String target = "source: " + event.getSource();
        target += ", client: [" + event.getClientX() + "," + event.getClientY()
                + "];";
        target += ", relative: [" + event.getRelativeX() + ","
                + event.getRelativeY() + "]";
        target += ", itemId: " + event.getItemId();
        target += ", propertyId: " + event.getPropertyId();

        String modifierKeys = "";
        if (event.isCtrlKey()) {
            modifierKeys += "CTRL ";
        }
        if (event.isAltKey()) {
            modifierKeys += "ALT ";
        }
        if (event.isMetaKey()) {
            modifierKeys += "META ";
        }
        if (event.isShiftKey()) {
            modifierKeys += "SHIFT ";
        }
        log(modifierKeys + type + " on " + target);
    }
}
