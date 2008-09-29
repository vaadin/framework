package com.itmill.toolkit.tests.tickets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.data.validator.StringLengthValidator;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CoordinateLayout;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.MenuBar;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Slider;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.CoordinateLayout.Coordinates;
import com.itmill.toolkit.ui.MenuBar.Command;
import com.itmill.toolkit.ui.MenuBar.MenuItem;
import com.itmill.toolkit.ui.Window.CloseEvent;
import com.itmill.toolkit.ui.Window.CloseListener;

public class Ticket1267 extends Application {

    ArrayList<Component> componentList;

    CoordinateLayout coordinateLayout = new CoordinateLayout();
    Window main = new Window("Coordinatelayout demo");
    HashMap<Component, Panel> control = new HashMap<Component, Panel>();
    ExpandLayout mainLayout = new ExpandLayout();
    // Components
    MenuBar mainMenu;
    Window controlPanel = new Window("Control panel for components");

    OrderedLayout ol = new OrderedLayout();

    boolean defaultmargins = false;

    Application hostApp = this;
    Class<Ticket1267> hostClass = Ticket1267.class;

    private int numberOfTables;

    private IndexedContainer emptyContainer = new IndexedContainer();
    private HierarchicalContainer emptyHierarchy = new HierarchicalContainer();

    private int numberOfPanels;

    private int numberOfTextFields;

    private int numberOfTrees;

    public void init() {

        // Setup container
        emptyContainer.addContainerProperty("First Name", String.class, null);
        emptyContainer.addContainerProperty("Last Name", String.class, null);
        emptyContainer.addContainerProperty("Year", Integer.class, null);

        // Setup menubar
        mainMenu = getMainMenuBar();

        // Setup control panel window
        controlPanel.addListener(new CloseListener() {

            public void windowClose(CloseEvent e) {
                main.removeWindow(e.getWindow());

            }

        });

        // TestField
        TextField testField = new TextField("Pixel values");
        testField.setImmediate(true);
        final Coordinates xy = new Coordinates("0,0");

        testField.addListener(new ValueChangeListener() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * com.itmill.toolkit.data.Property.ValueChangeListener#valueChange
             * (com.itmill.toolkit.data.Property.ValueChangeEvent)
             */
            public void valueChange(ValueChangeEvent event) {
                String str = (String) event.getProperty().getValue();
                try {
                    xy.setCoordinates(str);
                } catch (Exception e) {
                    main.showNotification("Wrong string format", e.toString(),
                            Window.Notification.TYPE_WARNING_MESSAGE);
                    return;
                }
                main.showNotification("Component added to " + xy.toString(),
                        Window.Notification.TYPE_TRAY_NOTIFICATION);
            }
        });

        coordinateLayout.addComponent(testField, xy);

        // Setup coordinatelayout
        coordinateLayout.setMargin(defaultmargins);
        mainLayout.addComponent(mainMenu);
        mainLayout.addComponent(coordinateLayout);
        mainLayout.expand(coordinateLayout);

        main.setLayout(mainLayout);

        setMainWindow(main);

    }

    private MenuBar getMainMenuBar() {
        MenuBar menu = new MenuBar();
        MenuItem add = menu.addItem("Add component", null);
        MenuItem remove = menu.addItem("Remove component", null);
        MenuItem modify = menu.addItem("Modify component", null);
        MenuItem cPanel = menu.addItem("Show / hide control panel", null);
        cPanel.setCommand(new Command() {

            public void menuSelected(MenuItem selectedItem) {
                if (main.getChildWindows().contains(controlPanel)) {
                    main.removeWindow(controlPanel);
                } else {
                    main.addWindow(controlPanel);

                }
            }
        });

        final MenuItem margins = menu.addItem("Toggle margins", null);
        margins.setCommand(new Command() {

            boolean marginsEnabled = defaultmargins;

            public void menuSelected(MenuItem selectedItem) {
                if (marginsEnabled) {
                    margins.setIcon(null);
                    marginsEnabled = false;
                    coordinateLayout.setMargin(false);
                } else {
                    margins.setIcon(new ThemeResource("icons/16/ok.png"));
                    marginsEnabled = true;
                    coordinateLayout.setMargin(true);
                }
            }

        });
        try {
            add.addItem("Table", new AddCommand(hostClass
                    .getMethod("getNewTable"), remove, modify));

            add.addItem("Panel", new AddCommand(hostClass
                    .getMethod("getNewPanel"), remove, modify));

            add.addItem("TextField", new AddCommand(hostClass
                    .getMethod("getNewTextField"), remove, modify));
            add.addItem("Tree", new AddCommand(hostClass
                    .getMethod("getNewTree"), remove, modify));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menu;
    }

    public Table getNewTable() {
        Table table = new Table("Table " + numberOfTables++);
        table.setContainerDataSource(emptyContainer);
        table.setImmediate(true);
        table.setIcon(new ThemeResource("icons/16/globe.png"));

        return table;
    }

    public Panel getNewPanel() {
        final Panel panel = new Panel("Panel " + numberOfPanels++);
        Button addButton = new Button("Add component");
        addButton.addListener(new Button.ClickListener() {
            int count = 0;

            public void buttonClick(Button.ClickEvent event) {
                panel.addComponent(new Label("Component nro. " + count++));

            }
        });
        panel.addComponent(addButton);

        return panel;
    }

    public TextField getNewTextField() {
        TextField textField = new TextField(
                "TextField " + numberOfTextFields++,
                "This textfield has a validator");
        textField.setImmediate(true);
        textField.setIcon(new ThemeResource("icons/16/document-image.png"));
        textField.setRequired(true);
        textField.addValidator(new StringLengthValidator(
                "5 < String.length() < 10", 5, 10, false));

        return textField;
    }

    public Tree getNewTree() {

        Tree tree = new Tree("Tree " + numberOfTrees++);

        return tree;
    }

    private class AddCommand implements Command {

        private Method addMethod;
        private MenuItem removeMenu;
        private MenuItem modifyMenu;

        public void menuSelected(MenuItem selectedItem) {
            Component toAdd = null;
            try {
                toAdd = (Component) addMethod.invoke(hostApp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String caption = toAdd.getCaption();
            Coordinates xy = coordinateLayout.addComponent(toAdd, "50%,50%");
            getMainWindow().showNotification(
                    "Component added to " + xy.toString(),
                    Window.Notification.TYPE_TRAY_NOTIFICATION);

            MenuItem modItem = getModMenuItem(modifyMenu, caption, toAdd);
            removeMenu.addItem(caption, new RemoveCommand(toAdd, modItem,
                    modifyMenu));

            controlPanel.addComponent(getControlPanel(toAdd));

        }

        AddCommand(Method addMethod, MenuItem removeMenu, MenuItem modifyMenu) {

            this.addMethod = addMethod;
            this.removeMenu = removeMenu;
            this.modifyMenu = modifyMenu;
        }

    }

    private class RemoveCommand implements Command {

        private Component toRemove;
        private MenuItem modifyItem;
        private MenuItem modifyMenu;

        public RemoveCommand(Component toRemove, MenuBar.MenuItem modifyItem,
                MenuItem modifyMenu) {
            this.toRemove = toRemove;
            this.modifyItem = modifyItem;
            this.modifyMenu = modifyMenu;
        }

        public void menuSelected(MenuItem selectedItem) {
            if (coordinateLayout.contains(toRemove)) {
                coordinateLayout.removeComponent(toRemove);
                MenuItem parent = selectedItem.getParent();
                parent.removeChild(selectedItem);

                if (modifyItem != null) {
                    modifyMenu.removeChild(modifyItem);
                }

                controlPanel.removeComponent(getControlPanel(toRemove));
                control.remove(toRemove);
            }
        }
    }

    public Panel getControlPanel(final Component c) {

        if (control.get(c) == null) {
            Panel newPanel = new Panel("Controls for " + c.getCaption());
            newPanel.setLayout(new GridLayout(3, 6));

            final int[] values = new int[6];
            Arrays.fill(values, -1);

            final CheckBox[] checkBoxArray = new CheckBox[6];
            final Slider[] sliderArray = new Slider[6];

            String[] directions = { "Left", "Top", "Width", "Height", "Right",
                    "Bottom" };

            final Label[] labelArray = new Label[6];

            for (int i = 0; i < checkBoxArray.length; i++) {
                final int j = i;

                checkBoxArray[i] = new CheckBox(directions[i]);
                sliderArray[i] = new Slider(-1, 100, 1);

                labelArray[j] = new Label("-1");
                labelArray[j].setEnabled(false);
                checkBoxArray[j].setImmediate(true);
                sliderArray[j].setImmediate(true);
                sliderArray[j].setEnabled(false);
                sliderArray[j].setWidth("300px");

                checkBoxArray[j].addListener(new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        Boolean isChecked = (Boolean) checkBoxArray[j]
                                .getValue();
                        if (isChecked.booleanValue()) {
                            sliderArray[j].setEnabled(true);
                            labelArray[j].setEnabled(true);
                        } else {
                            values[j] = -1;
                            labelArray[j].setValue(new Integer(values[j]));
                            sliderArray[j].setEnabled(false);
                            labelArray[j].setEnabled(false);
                            CoordinateLayout.Coordinates coords = coordinateLayout
                                    .getCoordinates(c);
                            coords.setCoordinates(values[0], values[1],
                                    values[2], values[3], values[4], values[5]);
                            coords.setUnitsPercent(true, true, true, true,
                                    true, true);

                        }
                    }
                });

                sliderArray[j].addListener(new ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        Double newValue = (Double) event.getProperty()
                                .getValue();
                        values[j] = (int) newValue.doubleValue();
                        CoordinateLayout.Coordinates coords = coordinateLayout
                                .getCoordinates(c);
                        coords.setCoordinates(values[0], values[1], values[2],
                                values[3], values[4], values[5]);
                        coords.setUnitsPercent(true, true, true, true, true,
                                true);

                        labelArray[j].setValue(new Integer(values[j]));

                    }
                });
            }

            for (int i = 0; i < sliderArray.length; i++) {
                newPanel.addComponent(checkBoxArray[i]);
                newPanel.addComponent(sliderArray[i]);
                newPanel.addComponent(labelArray[i]);
            }
            control.put(c, newPanel);
            return newPanel;
        } else {
            return control.get(c);
        }
    }

    public MenuBar.MenuItem getModMenuItem(MenuItem parent, String id,
            Component c) {
        MenuBar.MenuItem item = null;
        if (c instanceof Table) {
            final Table table = (Table) c;
            item = parent.addItem(id, null);
            MenuBar.Command addContent = new MenuBar.Command() {

                public void menuSelected(MenuItem selectedItem) {
                    table
                            .addContainerProperty("First Name", String.class,
                                    null);
                    table.addContainerProperty("Last Name", String.class, null);
                    table.addContainerProperty("Year", Integer.class, null);

                    table.addItem(new Object[] { "Nicolaus", "Copernicus",
                            new Integer(1473) }, Integer.valueOf(1));
                    table.addItem(new Object[] { "Tycho", "Brahe",
                            new Integer(1546) }, Integer.valueOf(2));
                    table.addItem(new Object[] { "Giordano", "Bruno",
                            new Integer(1548) }, Integer.valueOf(3));
                    table.addItem(new Object[] { "Galileo", "Galilei",
                            new Integer(1564) }, Integer.valueOf(4));
                    table.addItem(new Object[] { "Johannes", "Kepler",
                            new Integer(1571) }, Integer.valueOf(5));
                    table.addItem(new Object[] { "Isaac", "Newton",
                            new Integer(1643) }, Integer.valueOf(6));
                }

            };

            MenuBar.Command removeContent = new MenuBar.Command() {

                public void menuSelected(MenuItem selectedItem) {
                    table.setContainerDataSource(emptyContainer);

                }

            };

            item.addItem("Add content", addContent);
            item.addItem("Remove content", removeContent);

        } else if (c instanceof Tree) {
            final Tree tree = (Tree) c;
            item = parent.addItem(id, null);

            MenuBar.Command addContent = new MenuBar.Command() {
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    final Object[][] planets = new Object[][] {
                            new Object[] { "Mercury" },
                            new Object[] { "Venus" },
                            new Object[] { "Earth", "The Moon" },
                            new Object[] { "Mars", "Phobos", "Deimos" },
                            new Object[] { "Jupiter", "Io", "Europa",
                                    "Ganymedes", "Callisto" },

                            new Object[] { "Saturn", "Titan", "Tethys",
                                    "Dione", "Rhea", "Iapetus" },
                            new Object[] { "Uranus", "Miranda", "Ariel",
                                    "Umbriel", "Titania", "Oberon" },
                            new Object[] { "Neptune", "Triton", "Proteus",
                                    "Nereid", "Larissa" } };

                    /* Add planets as root items in the tree. */
                    for (int i = 0; i < planets.length; i++) {
                        String planet = (String) (planets[i][0]);
                        tree.addItem(planet);
                        if (planets[i].length == 1) {
                            /* The planet has no moons so make it a leaf. */
                            tree.setChildrenAllowed(planet, false);
                        } else {
                            /* Add children (moons) under the planets. */
                            for (int j = 1; j < planets[i].length; j++) {
                                String moon = (String) planets[i][j];
                                /* Add the item as a regular item. */
                                tree.addItem(moon);
                                /* Set it to be a child. */
                                tree.setParent(moon, planet);
                                /* Make the moons look like leaves. */
                                tree.setChildrenAllowed(moon, false);
                            }
                            /* Expand the subtree. */
                            tree.expandItemsRecursively(planet);
                        }
                    }
                }
            };

            MenuBar.Command removeContent = new MenuBar.Command() {

                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    tree.setContainerDataSource(emptyHierarchy);
                }

            };

            item.addItem("Add content", addContent);
            item.addItem("Remove content", removeContent);
        }

        return item;
    }
}
