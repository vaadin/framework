package com.vaadin.tests.components;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.vaadin.annotations.Theme;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.v7.ui.themes.BaseTheme;

@Theme("tests-components")
public abstract class AbstractComponentTest<T extends AbstractComponent> extends
        AbstractComponentTestCase<T> implements FocusListener, BlurListener {

    protected static final String TEXT_SHORT = "Short";
    protected static final String TEXT_MEDIUM = "This is a semi-long text that might wrap.";
    protected static final String TEXT_LONG = "This is a long text. "
            + LoremIpsum.get(500);
    protected static final String TEXT_VERY_LONG = "This is a very, very long text. "
            + LoremIpsum.get(5000);

    private static final Resource SELECTED_ICON = new ThemeResource(
            "../runo/icons/16/ok.png");

    private static final LinkedHashMap<String, String> sizeOptions = new LinkedHashMap<>();
    static {
        sizeOptions.put("auto", null);
        sizeOptions.put("50%", "50%");
        sizeOptions.put("100%", "100%");
        for (int w = 200; w < 1000; w += 100) {
            sizeOptions.put(w + "px", w + "px");
        }
    }

    // Menu related

    private MenuItem mainMenu;

    private MenuBar menu;

    private MenuItem settingsMenu;

    private T component;

    // Used to determine if a menuItem should be selected and the other
    // unselected on click
    private Set<MenuItem> parentOfSelectableMenuItem = new HashSet<>();

    /**
     * Maps the category name to a menu item
     */
    private Map<String, MenuItem> categoryToMenuItem = new HashMap<>();
    private Map<MenuItem, String> menuItemToCategory = new HashMap<>();

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
    protected final void setup(VaadinRequest request) {

        // Create menu here so it appears before the components
        addComponent(createMainMenu());

        getLayout().setSizeFull();
        createLog();
        super.setup(request);

        // Create menu actions and trigger default actions
        createActions();

        // Clear initialization log messages
        log.clear();
    }

    private MenuBar createMainMenu() {
        menu = new MenuBar();
        menu.setId("menu");
        mainMenu = menu.addItem("Component", null);
        settingsMenu = menu.addItem("Settings", null);
        populateSettingsMenu(settingsMenu);

        return menu;
    }

    /**
     * Override to add items to the "settings" menu.
     *
     * NOTE, Call super class first to preserve current order. If you override
     * this in a class and another class overrides it you might break tests
     * because the wrong items will be selected.
     *
     * @param settingsMenu
     */
    protected void populateSettingsMenu(MenuItem settingsMenu) {

        MenuItem showEventLog = settingsMenu.addItem("Show event log",
                new MenuBar.Command() {

                    @Override
                    public void menuSelected(MenuItem selectedItem) {
                        boolean selected = !isSelected(selectedItem);
                        setLogVisible(selected);
                        setSelected(selectedItem, selected);
                    }

                });
        setSelected(showEventLog, true);

        settingsMenu.addItem("Clear log", new MenuBar.Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                log.clear();
            }
        });
        MenuItem layoutSize = settingsMenu.addItem("Parent layout size", null);
        MenuItem layoutWidth = layoutSize.addItem("Width", null);
        MenuItem layoutHeight = layoutSize.addItem("Height", null);
        for (final String name : sizeOptions.keySet()) {
            layoutWidth.addItem(name, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuItem selectedItem) {
                    getTestComponents().get(0).getParent()
                            .setWidth(sizeOptions.get(name));
                    log("Parent layout width set to " + name);
                }
            });
            layoutHeight.addItem(name, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuItem selectedItem) {
                    getTestComponents().get(0).getParent()
                            .setHeight(sizeOptions.get(name));
                    log("Parent layout height set to " + name);
                }
            });
        }

    }

    protected void setLogVisible(boolean visible) {
        // This is only to be screenshot-compatible with Vaadin 6, where
        // invisible components cause spacing
        if (visible) {
            log.removeStyleName("displaynone");
            log.setCaption((String) log.getData());
        } else {
            log.addStyleName("displaynone");
            log.setCaption(null);
        }
    }

    private void createLog() {
        log = new Log(5).setNumberLogRows(true);
        log.setData(log.getCaption());
        log.setStyleName(BaseTheme.CLIP);
        getLayout().addComponent(log, 1);
    }

    /**
     * By default initializes just one instance of {@link #getTestClass()} using
     * {@link #constructComponent()}.
     */
    @Override
    protected void initializeComponents() {
        component = constructComponent();
        component.setId("testComponent");
        addTestComponent(component);
    }

    public T getComponent() {
        return component;
    }

    @Override
    protected void addTestComponent(T c) {
        super.addTestComponent(c);
        getLayout().setExpandRatio(c, 1);

    }

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
            throw new RuntimeException(
                    "Failed to instantiate " + getTestClass(), e);
        }
    }

    /**
     * Create actions for the component. Remember to call super.createActions()
     * when overriding.
     */
    protected void createActions() {
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

        createStyleNameSelect(CATEGORY_DECORATIONS);

        createFocusActions();
    }

    protected Command<T, Boolean> focusListenerCommand = new Command<T, Boolean>() {

        private Registration focusListenerRegistration;

        @Override
        public void execute(T c, Boolean value, Object data) {
            FocusNotifier focusNotifier = (FocusNotifier) c;
            if (value) {
                focusListenerRegistration = focusNotifier
                        .addFocusListener(AbstractComponentTest.this);
            } else if (focusListenerRegistration != null) {
                focusListenerRegistration.remove();
            }
        }
    };
    protected Command<T, Boolean> blurListenerCommand = new Command<T, Boolean>() {

        private Registration blurListenerRegistration;

        @Override
        public void execute(T c, Boolean value, Object data) {
            BlurNotifier bn = (BlurNotifier) c;
            if (value) {
                blurListenerRegistration = bn
                        .addBlurListener(AbstractComponentTest.this);
            } else if (blurListenerRegistration != null) {
                blurListenerRegistration.remove();
            }
        }
    };

    protected void createFocusListener(String category) {
        createBooleanAction("Focus listener", category, false,
                focusListenerCommand);

    }

    protected void createBlurListener(String category) {
        createBooleanAction("Blur listener", category, false,
                blurListenerCommand);

    }

    private void createFocusActions() {
        if (FocusNotifier.class.isAssignableFrom(getTestClass())) {
            createFocusListener(CATEGORY_LISTENERS);
        }
        if (BlurNotifier.class.isAssignableFrom(getTestClass())) {
            createBlurListener(CATEGORY_LISTENERS);
        }
        if (Focusable.class.isAssignableFrom(getTestClass())) {
            LinkedHashMap<String, Integer> tabIndexes = new LinkedHashMap<>();
            tabIndexes.put("0", 0);
            tabIndexes.put("-1", -1);
            tabIndexes.put("10", 10);
            createSelectAction("Tab index", "State", tabIndexes, "0",
                    new Command<T, Integer>() {
                        @Override
                        public void execute(T c, Integer tabIndex,
                                Object data) {
                            ((Focusable) c).setTabIndex(tabIndex);
                        }
                    });

            createClickAction("Set focus", "State", new Command<T, Void>() {
                @Override
                public void execute(T c, Void value, Object data) {
                    ((Focusable) c).focus();
                }
            }, null);
        }
    }

    private void createStyleNameSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put("Light blue background (background-lightblue)",
                "background-lightblue");
        options.put("1px red border (border-red-1px)", "border-red-1px");
        options.put("2px blue border (border-blue-2px)", "border-blue-2px");
        createComponentStyleNames(options);
        createSelectAction("Style name", category, options, "-",
                styleNameCommand);

    }

    protected void createComponentStyleNames(
            LinkedHashMap<String, String> options) {

    }

    private void createErrorMessageSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put(TEXT_SHORT, TEXT_SHORT);
        options.put("Medium", TEXT_MEDIUM);
        options.put("Long", TEXT_LONG);
        options.put("Very long", TEXT_VERY_LONG);
        createSelectAction("Error message", category, options, "-",
                errorMessageCommand);

    }

    private void createDescriptionSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put(TEXT_SHORT, TEXT_SHORT);
        options.put("Medium", TEXT_MEDIUM);
        options.put("Long", TEXT_LONG);
        options.put("Very long", TEXT_VERY_LONG);
        createSelectAction("Description / tooltip", category, options, "-",
                descriptionCommand);

    }

    private void createCaptionSelect(String category) {
        createSelectAction("Caption", category, createCaptionOptions(), "Short",
                captionCommand);

    }

    protected LinkedHashMap<String, String> createCaptionOptions() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put("Short", TEXT_SHORT);
        options.put("Medium", TEXT_MEDIUM);
        options.put("Long", TEXT_LONG);
        options.put("Very long", TEXT_VERY_LONG);
        return options;
    }

    private void createWidthAndHeightActions(String category) {
        String widthCategory = "Width";
        String heightCategory = "Height";

        createCategory(widthCategory, category);
        createCategory(heightCategory, category);

        for (String name : sizeOptions.keySet()) {
            createClickAction(name, widthCategory, widthCommand,
                    sizeOptions.get(name));
            createClickAction(name, heightCategory, heightCommand,
                    sizeOptions.get(name));
        }

        // Default to undefined size
        for (T c : getTestComponents()) {
            c.setWidth(null);
            c.setHeight(null);
        }
    }

    private void createIconSelect(String category) {
        LinkedHashMap<String, Resource> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put("16x16", ICON_16_USER_PNG_CACHEABLE);
        options.put("32x32", ICON_32_ATTENTION_PNG_CACHEABLE);
        options.put("64x64", ICON_64_EMAIL_REPLY_PNG_CACHEABLE);

        createSelectAction("Icon", category, options, "-", iconCommand, null);
    }

    private void createLocaleSelect(String category) {
        LinkedHashMap<String, Locale> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put("fi_FI", new Locale("fi", "FI"));
        options.put("en_US", Locale.US);
        options.put("zh_CN", Locale.SIMPLIFIED_CHINESE);
        options.put("fr_FR", Locale.FRANCE);

        createSelectAction("Locale", category, options, "en_US", localeCommand,
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

    protected <DATATYPE> void createClickAction(String caption, String category,
            final Command<T, DATATYPE> command, DATATYPE value) {
        createClickAction(caption, category, command, value, null);
    }

    protected <DATATYPE> void createClickAction(String caption, String category,
            final Command<T, DATATYPE> command, DATATYPE value, Object data) {
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
     * Creates category named "category" with id "categoryId" in parent category
     * "parentCategory". Each categoryId must be globally unique.
     *
     * @param category
     * @param categoryId
     * @param parentCategory
     * @return
     */
    protected MenuItem createCategory(String category, String parentCategory) {
        if (hasCategory(category)) {
            return categoryToMenuItem.get(category);
        }
        MenuItem item;
        if (parentCategory == null) {
            item = mainMenu.addItem(category, null);
        } else {
            item = getCategoryMenuItem(parentCategory).addItem(category, null);
        }
        categoryToMenuItem.put(category, item);
        menuItemToCategory.put(item, category);
        return item;
    }

    protected boolean hasCategory(String categoryId) {
        return categoryToMenuItem.containsKey(categoryId);
    }

    protected void removeCategory(String categoryId) {
        if (!hasCategory(categoryId)) {
            throw new IllegalArgumentException(
                    "Category '" + categoryId + "' does not exist");
        }

        MenuItem item = getCategoryMenuItem(categoryId);
        Object[] children = item.getChildren().toArray();
        for (Object child : children) {
            if (menuItemToCategory.containsKey(child)) {
                removeCategory(menuItemToCategory.get(child));
            }
        }
        // Detach from parent
        item.getParent().removeChild(item);
        // Clean mappings
        categoryToMenuItem.remove(categoryId);
        menuItemToCategory.remove(item);

    }

    private MenuBar.Command menuBooleanCommand(
            final com.vaadin.tests.components.ComponentTestCase.Command<T, Boolean> booleanCommand,
            final Object data) {

        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                boolean selected = !isSelected(selectedItem);
                doCommand(getText(selectedItem), booleanCommand, selected,
                        data);
                setSelected(selectedItem, selected);
            }

        };
    }

    private <DATATYPE> MenuBar.Command menuClickCommand(
            final com.vaadin.tests.components.ComponentTestCase.Command<T, DATATYPE> command,
            final DATATYPE value, final Object data) {

        return new MenuBar.Command() {
            @Override
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
        return item.getIcon() != null;
    }

    private <VALUETYPE> MenuBar.Command singleSelectMenuCommand(
            final com.vaadin.tests.components.ComponentTestCase.Command<T, VALUETYPE> cmd,
            final VALUETYPE object, final Object data) {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                doCommand(getText(selectedItem), cmd, object, data);

                if (parentOfSelectableMenuItem
                        .contains(selectedItem.getParent())) {
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

    protected <TYPE> void createSelectAction(String caption, String category,
            LinkedHashMap<String, TYPE> options, String initialValue,
            com.vaadin.tests.components.ComponentTestCase.Command<T, TYPE> command) {
        createSelectAction(caption, category, options, initialValue, command,
                null);

    }

    protected <TYPE extends Enum<TYPE>> void createSelectAction(String caption,
            String category, Class<TYPE> enumType, TYPE initialValue,
            com.vaadin.tests.components.ComponentTestCase.Command<T, TYPE> command) {
        LinkedHashMap<String, TYPE> options = new LinkedHashMap<>();
        for (TYPE value : EnumSet.allOf(enumType)) {
            options.put(value.toString(), value);
        }
        createSelectAction(caption, category, options, initialValue.toString(),
                command);
    }

    protected <TYPE> void createMultiClickAction(String caption,
            String category, LinkedHashMap<String, TYPE> options,
            com.vaadin.tests.components.ComponentTestCase.Command<T, TYPE> command,
            Object data) {

        MenuItem categoryItem = getCategoryMenuItem(category);
        MenuItem mainItem = categoryItem.addItem(caption, null);

        for (String option : options.keySet()) {
            MenuBar.Command cmd = menuClickCommand(command, options.get(option),
                    data);
            mainItem.addItem(option, cmd);
        }
    }

    protected <TYPE> void createMultiToggleAction(String caption,
            String category, LinkedHashMap<String, TYPE> options,
            com.vaadin.tests.components.ComponentTestCase.Command<T, Boolean> command,
            boolean defaultValue) {

        LinkedHashMap<String, Boolean> defaultValues = new LinkedHashMap<>();

        for (String option : options.keySet()) {
            defaultValues.put(option, defaultValue);
        }

        createMultiToggleAction(caption, category, options, command,
                defaultValues);
    }

    protected <TYPE> void createMultiToggleAction(String caption,
            String category, LinkedHashMap<String, TYPE> options,
            com.vaadin.tests.components.ComponentTestCase.Command<T, Boolean> command,
            LinkedHashMap<String, Boolean> defaultValues) {

        createCategory(caption, category);

        for (String option : options.keySet()) {
            createBooleanAction(option, caption, defaultValues.get(option),
                    command, options.get(option));

        }
    }

    protected <TYPE> void createSelectAction(String caption, String category,
            LinkedHashMap<String, TYPE> options, String initialValue,
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

    protected void createListenerAction(String caption, String category,
            Function<T, Registration> addListener) {

        createBooleanAction(caption, category, false,
                new Command<T, Boolean>() {
                    Registration registration;

                    @Override
                    public void execute(T c, Boolean enabled, Object data) {
                        if (enabled) {
                            registration = addListener.apply(c);
                        } else if (registration != null) {
                            registration.remove();
                            registration = null;
                        }
                    }
                });
    }

    protected LinkedHashMap<String, Integer> createIntegerOptions(int max) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<>();
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

    protected LinkedHashMap<String, Double> createDoubleOptions(double max) {
        LinkedHashMap<String, Double> options = new LinkedHashMap<>();
        for (double d = 0; d <= max && d < 10; d += 0.5) {
            options.put(String.valueOf(d), d);
        }
        for (double d = 10; d <= max; d *= 10) {
            options.put(String.valueOf(d), d);
            if (2.5 * d <= max) {
                options.put(String.valueOf(2 * d), 2 * d);
            }
            if (5 * d <= max) {
                options.put(String.valueOf(5 * d), 5 * d);
            }
        }

        return options;
    }

    protected LinkedHashMap<String, Resource> createIconOptions(
            boolean cacheable) {
        LinkedHashMap<String, Resource> options = new LinkedHashMap<>();
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
    public void focus(FocusEvent event) {
        log(event.getClass().getSimpleName());
    }

    @Override
    public void blur(BlurEvent event) {
        log(event.getClass().getSimpleName());
    }

}
