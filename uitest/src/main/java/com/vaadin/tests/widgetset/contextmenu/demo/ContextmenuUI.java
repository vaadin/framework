package com.vaadin.tests.widgetset.contextmenu.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.contextmenu.ContextMenu;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial", "unchecked" })
@Theme("contextmenu")
@Widgetset(TestingWidgetSet.NAME)

public class ContextmenuUI extends AbstractTestUI {
    Button but3 = new Button("remove Tooltip");

    @Override
    protected void setup(VaadinRequest request) {

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Button button = new Button("Button 1");
        layout.addComponent(button);

        Button button2 = new Button("Button 2");
        layout.addComponent(button2);

        ContextMenu contextMenu = new ContextMenu(this, false);
        fillMenu(contextMenu);

        contextMenu.setAsContextMenuOf(button);
        contextMenu.setAsContextMenuOf(button2);

        contextMenu.addContextMenuOpenListener(
                new ContextMenu.ContextMenuOpenListener() {
                    @Override
                    public void onContextMenuOpen(ContextMenuOpenEvent event) {
                        Notification.show("Context menu on"
                                + event.getSourceComponent().getCaption());
                    }
                });

        layout.addComponent(new GridWithGenericListener());
        layout.addComponent(new GridWithGridListener());
        layout.addComponent(but3);
        layout.addComponent(new Button("Remove items from context menu", e -> {
            contextMenu.removeItems();
        }));
    }

    private void fillMenu(ContextMenu menu) {
        final MenuBar.MenuItem item = menu.addItem("Checkable", e -> {
            Notification.show("checked: " + e.isChecked());
        });
        item.setCheckable(true);
        item.setChecked(true);

        MenuBar.MenuItem item2 = menu.addItem("Disabled", e -> {
            Notification.show("disabled");
        });
        item2.setDescription("Disabled item");
        item2.setEnabled(false);

        MenuBar.MenuItem item3 = menu.addItem("Invisible", e -> {
            Notification.show("invisible");
        });
        item3.setVisible(false);

        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).addSeparator();
        }

        MenuBar.MenuItem item4 = menu
                .addItem("Icon + Description + <b>HTML</b>", e -> {
                    Notification.show("icon");
                });
        item4.setIcon(VaadinIcons.ADJUST);
        item4.setDescription("Test tooltip");
        but3.addClickListener(e -> item4.setDescription(""));
        MenuBar.MenuItem item5 = menu.addItem("Custom stylename", e -> {
            Notification.show("stylename");
        });
        item5.setStyleName("teststyle");

        MenuBar.MenuItem item6 = menu.addItem("Submenu", e -> {
        });
        item6.addItem("Subitem", e -> Notification.show("SubItem"));
        item6.addSeparator();
        item6.addItem("Subitem", e -> Notification.show("SubItem"))
                .setDescription("Test");
    }
}
