package com.vaadin.tests.components.menubar;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * A UI for testing VMenuBar.getSubPartElement(String). The UI contains two
 * MenuBars, one without icons and one containing items with and without icons.
 * Some of the icons are textual (using VaadinIcons) and should behave like
 * items with image icons: the icon should not be considered to be a part of the
 * item's caption.
 *
 * @author Vaadin
 */
@SuppressWarnings("serial")
public class MenuBarsWithNesting extends AbstractReindeerTestUI {

    // The label displays the last selection.
    private final Label label = new Label("Initial content");

    // The captions and icons used in the second MenuBar.
    public static final String[] itemNames = { "Icon item", "Arrow down",
            "Arrow up", "Warning" };
    private static final Resource[] itemIcons = {
            new ThemeResource("window/img/restore.png"), VaadinIcons.ARROW_DOWN,
            VaadinIcons.ARROW_UP, VaadinIcons.WARNING };

    // The last menu item is nested with the following submenu items.
    public static final String[] nestedItemnames = { "No icon", "Font icon",
            "Image icon" };
    private static final Resource[] nestedItemIcons = { null, VaadinIcons.LINK,
            new ThemeResource("window/img/restore.png") };

    private MenuBar.Command selectionCommand;

    @Override
    protected void setup(VaadinRequest request) {
        selectionCommand = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                label.setValue(selectedItem.getText());
            }
        };
        addComponent(createFirstMenuBar());
        addComponent(createSecondMenuBar());
        addComponent(label);
    }

    /*
     * Returns a menu bar with three levels of nesting but no icons.
     */
    private MenuBar createFirstMenuBar() {
        MenuBar menuBar = new MenuBar();
        MenuItem file = menuBar.addItem("File", null);
        file.addItem("Open", selectionCommand);
        file.addItem("Save", selectionCommand);
        file.addItem("Save As..", selectionCommand);
        file.addSeparator();
        MenuItem export = file.addItem("Export..", null);
        export.addItem("As PDF...", selectionCommand);
        export.addItem("As Doc...", selectionCommand);
        file.addSeparator();
        file.addItem("Exit", selectionCommand);

        MenuItem edit = menuBar.addItem("Edit", null);
        edit.addItem("Copy", selectionCommand);
        edit.addItem("Cut", selectionCommand);
        edit.addItem("Paste", selectionCommand);

        menuBar.addItem("Help", selectionCommand);

        MenuItem disabled = menuBar.addItem("Disabled", null);
        disabled.setEnabled(false);
        disabled.addItem("Can't reach", selectionCommand);

        return menuBar;
    }

    /*
     * Returns a menu bar containing items with icons. The last menu item is
     * nested and its submenu contains items with and without icons.
     */
    private MenuBar createSecondMenuBar() {
        MenuBar menuBar = new MenuBar();
        int n = itemNames.length;
        for (int i = 0; i < n - 1; i++) {
            menuBar.addItem(itemNames[i], itemIcons[i], selectionCommand);
        }
        MenuItem last = menuBar.addItem(itemNames[n - 1], itemIcons[n - 1],
                null);
        for (int i = 0; i < nestedItemnames.length; i++) {
            last.addItem(nestedItemnames[i], nestedItemIcons[i],
                    selectionCommand);
        }
        return menuBar;
    }

    @Override
    protected String getTestDescription() {
        return "This UI is used for testing subpart functionality of MenuBar. The "
                + "functionality is used in TestBench tests.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14879;
    }
}
