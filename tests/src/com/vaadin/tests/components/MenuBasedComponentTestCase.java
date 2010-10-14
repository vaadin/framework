package com.vaadin.tests.components;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Field;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public abstract class MenuBasedComponentTestCase<T extends AbstractComponent>
        extends AbstractComponentTestCase<T> {

    private static final Resource SELECTED_ICON = new ThemeResource(
            "../runo/icons/16/ok.png");

    private MenuItem mainMenu;

    private MenuBar menu;

    private T component;

    // Used to determine if a menuItem should be selected and the other
    // unselected on click
    private Set<MenuItem> parentOfSelectableMenuItem = new HashSet<MenuItem>();

    protected static final String CATEGORY_STATE = "State";
    protected static final String CATEGORY_SIZE = "Size";
    protected static final String CATEGORY_SELECTION = "Selection";
    protected static final String CATEGORY_CONTENT = "Contents";
    protected static final String CATEGORY_LISTENERS = "Listeners";

    @Override
    protected final void setup() {
        // Create menu here so it appears before the components
        menu = new MenuBar();
        mainMenu = menu.addItem("Settings", null);
        addComponent(menu);

        getLayout().setSizeFull();
        enableLog();
        super.setup();

        // Create menu actions and trigger default actions
        populateMenu();

        // Clear initialization log messages
        clearLog();
    }

    /**
     * By default initializes just one instance of {@link #getTestClass()} using
     * {@link #constructComponent()}.
     */
    @Override
    protected void initializeComponents() {
        component = constructComponent();
        addTestComponent(component);
    }

    public T getComponent() {
        return component;
    }

    @Override
    protected void addTestComponent(T c) {
        super.addTestComponent(c);
        getLayout().setExpandRatio(c, 1);

    };

    /**
     * Construct the component that is to be tested. This method uses a no-arg
     * constructor by default. Override to customize.
     * 
     * @return Instance of the component that is to be tested.
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected T constructComponent() {
        try {
            return getTestClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate "
                    + getTestClass(), e);
        }
    }

    private void populateMenu() {
        createDefaultActions();
        createCustomActions();
    }

    private void createDefaultActions() {
        createBooleanAction("Immediate", CATEGORY_STATE, true, immediateCommand);
        createBooleanAction("Enabled", CATEGORY_STATE, true, enabledCommand);
        createBooleanAction("Readonly", CATEGORY_STATE, false, readonlyCommand);
        createBooleanAction("Error indicator", CATEGORY_STATE, false,
                errorIndicatorCommand);

        if (component instanceof Field) {
            createBooleanAction("Required", CATEGORY_STATE, false,
                    requiredCommand);
        }
        createWidthSelect(CATEGORY_SIZE);
        createHeightSelect(CATEGORY_SIZE);

        if (component instanceof AbstractSelect) {
            createNullSelectAllowedCheckbox(CATEGORY_SELECTION);
            createItemsInContainerSelect(CATEGORY_CONTENT);
            createColumnsInContainerSelect(CATEGORY_CONTENT);
        }
    }

    @SuppressWarnings("unchecked")
    protected void createItemsInContainerSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("20", 20);
        options.put("100", 100);
        options.put("1000", 1000);
        options.put("10000", 10000);
        options.put("100000", 100000);

        createSelectAction("Items in container", category, options, "20",
                (Command) itemsInContainerCommand);
    }

    @SuppressWarnings("unchecked")
    protected void createColumnsInContainerSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("5", 5);
        options.put("10", 10);
        options.put("50", 50);
        options.put("100", 100);
        options.put("1000", 1000);

        createSelectAction("Columns in container", category, options, "10",
                (Command) columnsInContainerCommand);
    }

    private Container createContainer(int properties, int items) {
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

    @SuppressWarnings("unchecked")
    protected void createNullSelectAllowedCheckbox(String category) {
        createBooleanAction("Null Selection Allowed", category, false,
                (Command) nullSelectionAllowedCommand);

    }

    @SuppressWarnings("unchecked")
    protected void createNullSelectItemId(String category) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<String, Object>();
        options.put("- None -", null);
        for (Object id : ((AbstractSelect) component).getContainerDataSource()
                .getContainerPropertyIds()) {
            options.put(id.toString(), id);
        }
        createSelectAction("Null Selection Item Id", category, options,
                "- None -", (Command) nullSelectItemIdCommand);
    }

    protected void createWidthSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("Undefined", null);
        options.put("50%", "50%");
        options.put("100%", "100%");
        for (int w = 200; w < 1000; w += 100) {
            options.put(w + "px", w + "px");
        }

        createSelectAction("Width", category, options, "Undefined",
                widthCommand, null);
    }

    protected void createHeightSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("Undefined", null);
        options.put("50%", "50%");
        options.put("100%", "100%");
        for (int w = 200; w < 1000; w += 100) {
            options.put(w + "px", w + "px");
        }

        createSelectAction("Height", category, options, "Undefined",
                heightCommand, null);
    }

    protected void createBooleanAction(String caption, String category,
            boolean initialState, final Command<T, Boolean> command) {
        createBooleanAction(caption, category, initialState, command, null);
    }

    protected <DATATYPE> void createBooleanAction(String caption,
            String category, boolean initialState,
            final Command<T, Boolean> command, Object data) {
        MenuItem categoryItem = getCategoryMenuItem(category);
        MenuItem item = categoryItem.addItem(caption,
                menuBooleanCommand(command, data));
        setSelected(item, initialState);
        doCommand(caption, command, initialState, data);
    }

    private MenuItem getCategoryMenuItem(String category) {
        if (category == null) {
            return getCategoryMenuItem("Misc");
        }

        if (mainMenu.getChildren() != null) {
            for (MenuItem i : mainMenu.getChildren()) {
                if (i.getText().equals(category)) {
                    return i;
                }
            }
        }
        return mainMenu.addItem(category, null);
    }

    /**
     * Provide custom actions for the test case by creating them in this method.
     */
    protected abstract void createCustomActions();

    private MenuBar.Command menuBooleanCommand(
            final com.vaadin.tests.components.ComponentTestCase.Command<T, Boolean> booleanCommand,
            final Object data) {

        return new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                boolean selected = !isSelected(selectedItem);
                doCommand(getText(selectedItem), booleanCommand, selected, data);
                setSelected(selectedItem, selected);
            }

        };
    }

    protected void setSelected(MenuItem item, boolean selected) {
        if (selected) {
            item.setIcon(SELECTED_ICON);
        } else {
            item.setIcon(null);
        }
    }

    protected boolean isSelected(MenuItem item) {
        return (item.getIcon() != null);
    }

    private <VALUETYPE> MenuBar.Command singleSelectMenuCommand(
            final com.vaadin.tests.components.ComponentTestCase.Command<T, VALUETYPE> cmd,
            final VALUETYPE object, final Object data) {
        return new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                doCommand(getText(selectedItem), cmd, object, data);

                if (parentOfSelectableMenuItem.contains(selectedItem
                        .getParent())) {
                    unselectChildren(selectedItem.getParent());
                    setSelected(selectedItem, true);
                }
            }

        };

    }

    /**
     * Unselect all child menu items
     * 
     * @param parent
     */
    protected void unselectChildren(MenuItem parent) {
        List<MenuItem> children = parent.getChildren();
        if (children == null) {
            return;
        }

        for (MenuItem child : children) {
            setSelected(child, false);
        }
    }

    protected String getText(MenuItem item) {
        if (!isCategory(item.getParent())) {
            return item.getParent().getText();
        } else {
            return item.getText();
        }
    }

    private boolean isCategory(MenuItem item) {
        return item.getParent() == mainMenu;
    }

    protected <TYPE> void createSelectAction(
            String caption,
            String category,
            LinkedHashMap<String, TYPE> options,
            String initialValue,
            com.vaadin.tests.components.ComponentTestCase.Command<T, TYPE> command) {
        createSelectAction(caption, category, options, initialValue, command,
                null);

    }

    protected <TYPE> void createSelectAction(
            String caption,
            String category,
            LinkedHashMap<String, TYPE> options,
            String initialValue,
            com.vaadin.tests.components.ComponentTestCase.Command<T, TYPE> command,
            Object data) {

        MenuItem parentItem = getCategoryMenuItem(category);
        MenuItem mainItem = parentItem.addItem(caption, null);

        parentOfSelectableMenuItem.add(mainItem);
        for (String option : options.keySet()) {
            MenuBar.Command cmd = singleSelectMenuCommand(command,
                    options.get(option), data);
            MenuItem item = mainItem.addItem(option, cmd);
            if (option.equals(initialValue)) {
                cmd.menuSelected(item);
            }
        }
    }

    /* COMMANDS */

    protected Command<AbstractSelect, Boolean> nullSelectionAllowedCommand = new Command<AbstractSelect, Boolean>() {

        public void execute(AbstractSelect c, Boolean value, Object data) {
            (c).setNullSelectionAllowed(value);
        }
    };

    protected Command<AbstractSelect, Object> nullSelectItemIdCommand = new Command<AbstractSelect, Object>() {

        public void execute(AbstractSelect c, Object value, Object data) {
            c.setNullSelectionItemId(value);
        }
    };

    protected Command<AbstractSelect, Integer> itemsInContainerCommand = new Command<AbstractSelect, Integer>() {

        public void execute(AbstractSelect t, Integer value, Object data) {
            t.setContainerDataSource(createContainer(t.getContainerDataSource()
                    .getContainerPropertyIds().size(), value));
        }
    };

    protected Command<AbstractSelect, Integer> columnsInContainerCommand = new Command<AbstractSelect, Integer>() {

        public void execute(AbstractSelect t, Integer value, Object data) {
            t.setContainerDataSource(createContainer(value, t
                    .getContainerDataSource().size()));
        }
    };

    /* COMMANDS END */
}
