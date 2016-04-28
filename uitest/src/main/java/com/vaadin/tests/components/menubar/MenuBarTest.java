package com.vaadin.tests.components.menubar;

import java.util.Date;
import java.util.LinkedHashMap;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarTest extends AbstractComponentTest<MenuBar> {

    private static final String CATEGORY_MENU_ITEMS = "Menu items";
    private static final String CATEGORY_MENU_ITEM_STATES = "Menu item states";

    private int rootItems = -1;
    private int subItems = -1;
    private int subLevels = -1;
    private int subMenuDensity = -1;
    private Integer subMenuSeparatorDensity = null;
    private Boolean openRootMenuOnHover = false;
    private int iconInterval = -1;
    private Integer iconSize;
    private Integer disabledDensity;
    private Integer invisibleDensity;
    private Integer checkableDensity;

    @Override
    protected Class<MenuBar> getTestClass() {
        return MenuBar.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createRootMenuItemSelect(CATEGORY_MENU_ITEMS);
        createSubMenuItemSelect(CATEGORY_MENU_ITEMS);
        createSubMenuLevelsSelect(CATEGORY_MENU_ITEMS);
        createSubMenuDensitySelect(CATEGORY_MENU_ITEMS);
        createSubMenuSeparatorDensitySelect(CATEGORY_MENU_ITEMS);

        createBooleanAction("OpenRootMenuOnHover", CATEGORY_FEATURES,
                openRootMenuOnHover, setOpenRootOnHover);

        createMenuItemIconIntervalSelect(CATEGORY_MENU_ITEM_STATES);
        createMenuIconsSizeSelect(CATEGORY_MENU_ITEM_STATES);
        createMenuItemDisabledDensitySelect(CATEGORY_MENU_ITEM_STATES);
        createMenuItemInvisibleDensitySelect(CATEGORY_MENU_ITEM_STATES);
        createMenuItemCheckableDensitySelect(CATEGORY_MENU_ITEM_STATES);

    }

    private void createRootMenuItemSelect(String category) {
        createSelectAction("Root menu items", category,
                createIntegerOptions(100), "10", createRootMenuItems);
    }

    private void createSubMenuItemSelect(String category) {
        createSelectAction("Sub menu items", category,
                createIntegerOptions(100), "10", createSubMenuItems);
    }

    private void createSubMenuLevelsSelect(String category) {
        createSelectAction("Sub menu levels", category,
                createIntegerOptions(100), "2", setSubMenuLevels);
    }

    private void createMenuIconsSizeSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("16x16", 16);
        options.put("32x32", 32);
        options.put("64x64", 64);
        createSelectAction("Icon size", category, options, "16x16", selectIcon);
    }

    private void createMenuItemIconIntervalSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("None", 0);
        options.put("All", 1);
        options.put("Every second", 2);
        options.put("Every third", 3);

        createSelectAction("Icons", category, options, "None", setMenuIcons);
    }

    private void createSubMenuDensitySelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("All", 1);
        options.put("Every second", 2);
        options.put("Every third", 3);

        createSelectAction("Sub sub menus", category, options, "Every third",
                setSubMenuDensity);
    }

    private void createSubMenuSeparatorDensitySelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("No separators", null);
        options.put("Between all", 1);
        options.put("Between every second", 2);
        options.put("Between every third", 3);

        createSelectAction("Sub menu separators", category, options,
                "No separators", setSubMenuSeparatorDensity);
    }

    private void createMenuItemDisabledDensitySelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("All enabled", null);
        options.put("All disabled", 1);
        options.put("Every second", 2);
        options.put("Every third", 3);

        createSelectAction("Enabled", category, options, "All enabled",
                setMenuItemDisabledDensity);
    }

    private void createMenuItemInvisibleDensitySelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("All visible", null);
        options.put("All invisible", 1);
        options.put("Every second", 2);
        options.put("Every third", 3);

        createSelectAction("Visible", category, options, "All visible",
                setMenuItemInvisibleDensity);
    }

    private void createMenuItemCheckableDensitySelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("No items checkable", null);
        options.put("All checkable", 1);
        options.put("Every second", 2);
        options.put("Every third", 3);

        createSelectAction("Checkable", category, options,
                "No items checkable", setMenuItemCheckableDensity);
    }

    /* COMMANDS */
    Command<MenuBar, Integer> createRootMenuItems = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            rootItems = value;
            createRootItems(c);
        }
    };

    Command<MenuBar, Integer> createSubMenuItems = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            subItems = value;
            createSubItems(c);
        }
    };

    Command<MenuBar, Integer> setSubMenuLevels = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            subLevels = value;
            createSubItems(c);
        }
    };
    private Command<MenuBar, Integer> setMenuIcons = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            iconInterval = value;
            updateIcons(c);
        }
    };

    private Command<MenuBar, Integer> setSubMenuDensity = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            subMenuDensity = value;
            createSubItems(c);
        }
    };

    private Command<MenuBar, Integer> setMenuItemDisabledDensity = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            disabledDensity = value;
            createRootItems(c);
        }
    };

    private Command<MenuBar, Integer> setMenuItemInvisibleDensity = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            invisibleDensity = value;
            createRootItems(c);
        }
    };

    private Command<MenuBar, Integer> setMenuItemCheckableDensity = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            checkableDensity = value;
            createRootItems(c);
        }
    };

    private Command<MenuBar, Integer> setSubMenuSeparatorDensity = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            subMenuSeparatorDensity = value;
            createSubItems(c);
        }
    };

    private Command<MenuBar, Boolean> setOpenRootOnHover = new Command<MenuBar, Boolean>() {

        @Override
        public void execute(MenuBar c, Boolean value, Object data) {
            openRootMenuOnHover = value;
            c.setAutoOpen(value);
        }

    };

    private Command<MenuBar, Integer> selectIcon = new Command<MenuBar, Integer>() {

        @Override
        public void execute(MenuBar c, Integer value, Object data) {
            iconSize = value;
            updateIcons(c);
        }
    };

    /* End of commands */

    private MenuBar.Command menuCommand = new MenuBar.Command() {

        @Override
        public void menuSelected(MenuItem selectedItem) {
            log("Menu item '" + selectedItem.getText() + "' selected");

        }
    };

    protected void createSubItems(MenuBar c) {
        for (MenuItem rootItem : c.getItems()) {
            createSubItems(rootItem, 1);
        }
        updateIcons(c);

    }

    private void createSubItems(MenuItem parent, int level) {
        if (level > subLevels) {
            return;
        }

        parent.removeChildren();
        for (int i = 0; i < subItems; i++) {
            if (subMenuSeparatorDensity != null && i > 0
                    && i % subMenuSeparatorDensity == 0) {
                parent.addSeparator();
            }

            MenuItem subMenuItem = parent.addItem("Sub menu " + parent.getId()
                    + "/" + (i + 1), menuCommand);

            if (disabledDensity != null && i % disabledDensity == 0) {
                subMenuItem.setEnabled(false);
            }
            if (invisibleDensity != null && i % invisibleDensity == 0) {
                subMenuItem.setVisible(false);
            }

            if (i % subMenuDensity == 0 && level < subLevels) {
                subMenuItem.setCommand(null);
                createSubItems(subMenuItem, level + 1);
            }

            if (!subMenuItem.hasChildren() && level > 0
                    && checkableDensity != null && i % checkableDensity == 0) {
                subMenuItem.setCheckable(true);
            }
        }

    }

    protected void updateIcons(MenuBar c) {
        int idx = 0;
        for (MenuItem rootItem : c.getItems()) {
            updateIcons(rootItem, idx++);
        }
    }

    private void updateIcons(MenuItem item, int idx) {
        if (iconInterval > 0 && idx % iconInterval == 0) {
            item.setIcon(getIcon());
        } else {
            item.setIcon(null);
        }
        if (item.getChildren() != null) {
            int i = 0;
            for (MenuItem child : item.getChildren()) {
                updateIcons(child, i++);
            }
        }
    }

    private long iconCacheIndex = new Date().getTime();

    private Resource getIcon() {
        String resourceID = null;
        if (iconSize == 16) {
            resourceID = "../runo/icons/16/user.png";
        } else if (iconSize == 32) {
            resourceID = "../runo/icons/32/user.png";
        } else if (iconSize == 64) {
            resourceID = "../runo/icons/64/user.png";
        }

        if (resourceID != null) {
            return new ThemeResource(resourceID + "?" + iconCacheIndex++);
        }
        return null;
    }

    protected void createRootItems(MenuBar c) {
        // Remove all existing items
        c.removeItems();
        for (int i = 0; i < rootItems; i++) {
            MenuItem rootItem = c.addItem("Root menu " + (i + 1), null);
            if (disabledDensity != null && i % disabledDensity == 0) {
                rootItem.setEnabled(false);
            }
            if (invisibleDensity != null && i % invisibleDensity == 0) {
                rootItem.setVisible(false);
            }
        }
        createSubItems(c);

    }

}
