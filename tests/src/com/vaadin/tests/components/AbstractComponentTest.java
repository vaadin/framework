package com.vaadin.tests.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public abstract class AbstractComponentTest<T extends AbstractComponent>
        extends AbstractComponentTestCase<T> {

    protected static final String TEXT_SHORT = "Short";
    protected static final String TEXT_MEDIUM = "This is a semi-long text that might wrap.";
    protected static final String TEXT_LONG = "This is a long text. "
            + LoremIpsum.get(500);
    protected static final String TEXT_VERY_LONG = "This is a very, very long text. "
            + LoremIpsum.get(5000);

    private static final Resource SELECTED_ICON = new ThemeResource(
            "../runo/icons/16/ok.png");

    // Menu related

    private MenuItem mainMenu;

    private MenuBar menu;

    private MenuItem settingsMenu;

    private T component;

    // Used to determine if a menuItem should be selected and the other
    // unselected on click
    private Set<MenuItem> parentOfSelectableMenuItem = new HashSet<MenuItem>();

    /**
     * Maps the category name to a menu item
     */
    private Map<String, MenuItem> categoryToMenuItem = new HashMap<String, MenuItem>();

    // Logging
    private Log log;

    protected static final String CATEGORY_STATE = "State";
    protected static final String CATEGORY_SIZE = "Size";
    protected static final String CATEGORY_SELECTION = "Selection";
    protected static final String CATEGORY_LISTENERS = "Listeners";
    protected static final String CATEGORY_FEATURES = "Features";
    protected static final String CATEGORY_ACTIONS = "Actions";
    protected static final String CATEGORY_DECORATIONS = "Decorations";

    @Override
    protected final void setup() {
        setTheme("tests-components");

        // Create menu here so it appears before the components
        addComponent(createMainMenu());

        getLayout().setSizeFull();
        createLog();
        super.setup();

        // Create menu actions and trigger default actions
        createActions();

        // Clear initialization log messages
        log.clear();
    }

    private MenuBar createMainMenu() {
        menu = new MenuBar();
        menu.setDebugId("menu");
        mainMenu = menu.addItem("Component", null);
        settingsMenu = menu.addItem("Settings", null);
        createSettingsMenu();

        return menu;
    }

    private void createSettingsMenu() {
        MenuItem showEventLog = settingsMenu.addItem("Show event log",
                new MenuBar.Command() {

                    public void menuSelected(MenuItem selectedItem) {
                        boolean selected = !isSelected(selectedItem);
                        setLogVisible(selected);
                        setSelected(selectedItem, selected);
                    }

                });
        setSelected(showEventLog, true);

        settingsMenu.addItem("Clear log", new MenuBar.Command() {

            public void menuSelected(MenuItem selectedItem) {
                log.clear();
            }
        });
    }

    protected void setLogVisible(boolean visible) {
        log.setVisible(visible);
    }

    private void createLog() {
        log = new Log(5).setNumberLogRows(true);
        getLayout().addComponent(log, 1);
    }

    /**
     * By default initializes just one instance of {@link #getTestClass()} using
     * {@link #constructComponent()}.
     */
    @Override
    protected void initializeComponents() {
        component = constructComponent();
        component.setDebugId("testComponent");
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

    /**
     * Create actions for the component. Remember to call super.createActions()
     * when overriding.
     */
    protected void createActions() {
        createBooleanAction("Immediate", CATEGORY_STATE, true, immediateCommand);
        createBooleanAction("Enabled", CATEGORY_STATE, true, enabledCommand);
        createBooleanAction("Readonly", CATEGORY_STATE, false, readonlyCommand);
        createBooleanAction("Visible", CATEGORY_STATE, true, visibleCommand);
        createBooleanAction("Error indicator", CATEGORY_STATE, false,
                errorIndicatorCommand);
        createLocaleSelect(CATEGORY_STATE);
        createErrorMessageSelect(CATEGORY_DECORATIONS);

        createDescriptionSelect(CATEGORY_DECORATIONS);
        createCaptionSelect(CATEGORY_DECORATIONS);
        createIconSelect(CATEGORY_DECORATIONS);

        createWidthAndHeightActions(CATEGORY_SIZE);

        // TODO Style name

    }

    private void createErrorMessageSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put(TEXT_SHORT, TEXT_SHORT);
        options.put("Medium", TEXT_MEDIUM);
        options.put("Long", TEXT_LONG);
        options.put("Very long", TEXT_VERY_LONG);
        createSelectAction("Error message", category, options, "-",
                errorMessageCommand);

    }

    private void createDescriptionSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put(TEXT_SHORT, TEXT_SHORT);
        options.put("Medium", TEXT_MEDIUM);
        options.put("Long", TEXT_LONG);
        options.put("Very long", TEXT_VERY_LONG);
        createSelectAction("Description / tooltip", category, options, "-",
                descriptionCommand);

    }

    private void createCaptionSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put("Short", TEXT_SHORT);
        options.put("Medium", TEXT_MEDIUM);
        options.put("Long", TEXT_LONG);
        options.put("Very long", TEXT_VERY_LONG);
        createSelectAction("Caption", category, options, "Short",
                captionCommand);

    }

    private void createWidthAndHeightActions(String category) {
        String widthCategory = "Width";
        String heightCategory = "Height";

        createCategory(widthCategory, category);
        createCategory(heightCategory, category);

        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("Undefined", null);
        options.put("50%", "50%");
        options.put("100%", "100%");
        for (int w = 200; w < 1000; w += 100) {
            options.put(w + "px", w + "px");
        }

        for (String name : options.keySet()) {
            createClickAction(name, widthCategory, widthCommand,
                    options.get(name));
            createClickAction(name, heightCategory, heightCommand,
                    options.get(name));
        }

        // Default to undefined size
        for (T c : getTestComponents()) {
            c.setWidth(null);
            c.setHeight(null);
        }
    }

    private void createIconSelect(String category) {
        LinkedHashMap<String, Resource> options = new LinkedHashMap<String, Resource>();
        options.put("-", null);
        options.put("16x16", ICON_16_USER_PNG_CACHEABLE);
        options.put("32x32", ICON_32_ATTENTION_PNG_CACHEABLE);
        options.put("64x64", ICON_64_EMAIL_REPLY_PNG_CACHEABLE);

        createSelectAction("Icon", category, options, "-", iconCommand, null);
    }

    private void createLocaleSelect(String category) {
        LinkedHashMap<String, Locale> options = new LinkedHashMap<String, Locale>();
        options.put("-", null);
        options.put("fi_FI", new Locale("fi", "FI"));
        options.put("en_US", Locale.US);
        options.put("zh_CN", Locale.SIMPLIFIED_CHINESE);
        options.put("fr_FR", Locale.FRANCE);

        createSelectAction("Locale", category, options, "-", localeCommand,
                null);
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

    protected <DATATYPE> void createClickAction(String caption,
            String category, final Command<T, DATATYPE> command, DATATYPE value) {
        createClickAction(caption, category, command, value, null);
    }

    protected <DATATYPE> void createClickAction(String caption,
            String category, final Command<T, DATATYPE> command,
            DATATYPE value, Object data) {
        MenuItem categoryItem = getCategoryMenuItem(category);
        categoryItem.addItem(caption, menuClickCommand(command, value, data));
    }

    private MenuItem getCategoryMenuItem(String category) {
        if (category == null) {
            return getCategoryMenuItem("Misc");
        }

        MenuItem item = categoryToMenuItem.get(category);
        if (item != null) {
            return item;
        }

        return createCategory(category, null);
    }

    /**
     * Creates category "category" in parent category "parentCategory". Each
     * category name must be globally unique.
     * 
     * @param category
     * @param parentCategory
     * @return
     */
    protected MenuItem createCategory(String category, String parentCategory) {
        if (categoryToMenuItem.containsKey(category)) {
            return categoryToMenuItem.get(category);
        }
        MenuItem item;
        if (parentCategory == null) {
            item = mainMenu.addItem(category, null);
        } else {
            item = getCategoryMenuItem(parentCategory).addItem(category, null);
        }
        categoryToMenuItem.put(category, item);
        return item;
    }

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

    private <DATATYPE> MenuBar.Command menuClickCommand(
            final com.vaadin.tests.components.ComponentTestCase.Command<T, DATATYPE> command,
            final DATATYPE value, final Object data) {

        return new MenuBar.Command() {
            public void menuSelected(MenuItem selectedItem) {
                doCommand(getText(selectedItem), command, value, data);
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
        String path = "";
        MenuItem parent = item.getParent();
        while (!isCategory(parent)) {
            path = parent.getText() + "/" + path;
            parent = parent.getParent();
        }

        return path + "/" + item.getText();
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

    protected <TYPE> void createMultiClickAction(
            String caption,
            String category,
            LinkedHashMap<String, TYPE> options,
            com.vaadin.tests.components.ComponentTestCase.Command<T, TYPE> command,
            Object data) {

        MenuItem categoryItem = getCategoryMenuItem(category);
        MenuItem mainItem = categoryItem.addItem(caption, null);

        for (String option : options.keySet()) {
            MenuBar.Command cmd = menuClickCommand(command,
                    options.get(option), data);
            mainItem.addItem(option, cmd);
        }
    }

    protected <TYPE> void createMultiToggleAction(
            String caption,
            String category,
            LinkedHashMap<String, TYPE> options,
            com.vaadin.tests.components.ComponentTestCase.Command<T, Boolean> command,
            boolean defaultValue) {

        LinkedHashMap<String, Boolean> defaultValues = new LinkedHashMap<String, Boolean>();

        for (String option : options.keySet()) {
            defaultValues.put(option, defaultValue);
        }

        createMultiToggleAction(caption, category, options, command,
                defaultValues);
    }

    protected <TYPE> void createMultiToggleAction(
            String caption,
            String category,
            LinkedHashMap<String, TYPE> options,
            com.vaadin.tests.components.ComponentTestCase.Command<T, Boolean> command,
            LinkedHashMap<String, Boolean> defaultValues) {

        createCategory(caption, category);

        for (String option : options.keySet()) {
            createBooleanAction(option, caption, defaultValues.get(option),
                    command, options.get(option));

        }
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

    protected LinkedHashMap<String, Integer> createIntegerOptions(int max) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        for (int i = 0; i <= 9 && i <= max; i++) {
            options.put(String.valueOf(i), i);
        }
        for (int i = 10; i <= max; i *= 10) {
            options.put(String.valueOf(i), i);
            if (2 * i <= max) {
                options.put(String.valueOf(2 * i), 2 * i);
            }
            if (5 * i <= max) {
                options.put(String.valueOf(5 * i), 5 * i);
            }
        }

        return options;
    }

    protected LinkedHashMap<String, Resource> createIconOptions(
            boolean cacheable) {
        LinkedHashMap<String, Resource> options = new LinkedHashMap<String, Resource>();
        options.put("-", null);
        if (cacheable) {
            options.put("16x16", ICON_16_USER_PNG_CACHEABLE);
            options.put("32x32", ICON_32_ATTENTION_PNG_CACHEABLE);
            options.put("64x64", ICON_64_EMAIL_REPLY_PNG_CACHEABLE);
        } else {
            options.put("16x16", ICON_16_USER_PNG_UNCACHEABLE);
            options.put("32x32", ICON_32_ATTENTION_PNG_UNCACHEABLE);
            options.put("64x64", ICON_64_EMAIL_REPLY_PNG_UNCACHEABLE);

        }
        return options;
    }

    protected void log(String msg) {
        log.log(msg);
    }

    protected boolean hasLog() {
        return log != null;
    }

    @Override
    protected <VALUET> void doCommand(String commandName,
            AbstractComponentTestCase.Command<T, VALUET> command, VALUET value,
            Object data) {
        if (hasLog()) {
            log("Command: " + commandName + "(" + value + ")");
        }
        super.doCommand(commandName, command, value, data);
    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        String logMsg = "Exception occured, "
                + event.getThrowable().getClass().getName();

        String exceptionMsg = event.getThrowable().getMessage();
        if (exceptionMsg != null && exceptionMsg.length() > 0) {
            logMsg += exceptionMsg;
        }
        log.log(logMsg);

    }
}
