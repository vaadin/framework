package com.vaadin.tests.fonticon;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.PopupDateField;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.Align;
import com.vaadin.v7.ui.Table.RowHeaderMode;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.TwinColSelect;

/**
 * This is a legacy test ui for using font icons (font awesome).
 * <p>
 * {@link VaadinIconSet} is the newer one for testing valo+vaadin icons.
 */
public class FontIcons extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildUI(FontAwesome.ANDROID);
    }

    private void buildUI(final Resource icon) {
        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.setSpacing(false);

        layout.setIcon(icon);

        layout.addComponent(new Button("Switch icon type",
                event -> buildUI(icon instanceof FontIcon
                        ? new ThemeResource("../runo/icons/16/user.png")
                        : FontAwesome.ANDROID)));

        Handler actionHandler = new Handler() {
            Action[] actions = { new Action("Do it!", icon) };

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {

            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return actions;
            }
        };

        // Notification
        Notification n = new Notification("Hey there!");
        n.setIcon(icon);
        n.setPosition(Position.BOTTOM_CENTER);
        n.setDelayMsec(300000);
        n.show(Page.getCurrent());

        // grid of compoents
        GridLayout gl = new GridLayout(4, 5);
        gl.setSpacing(true);
        layout.addComponent(gl);

        // Basic components, caption icon only
        Class<?>[] components = { Button.class, CheckBox.class,
                PopupDateField.class, NativeButton.class, Link.class,
                Label.class, Panel.class, Slider.class, TextArea.class,
                TextField.class, Upload.class };
        for (Class<?> clazz : components) {
            Component c;
            try {
                c = (Component) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (c instanceof Upload) {
                ((Upload) c).setImmediateMode(false);
            }
            c.setCaption(clazz.getSimpleName());
            c.setIcon(icon);
            gl.addComponent(c);
        }

        // TabSheet, caption + tab icons
        TabSheet tabs = new TabSheet();
        tabs.setCaption("TabSheet");
        tabs.setIcon(icon);
        tabs.addStyleName("myTabs");
        tabs.addTab(createLabel("Content 1"), "Tab 1", icon);
        tabs.addTab(createLabel("Content 2"), "Tab 2", icon);
        tabs.setWidth("150px");
        gl.addComponent(tabs);

        // Accordion, caption + tab icons
        Accordion acc = new Accordion();
        acc.setCaption("Accordion");
        acc.setIcon(icon);
        acc.addTab(createLabel(""), "Section 1", icon);
        acc.addTab(createLabel(""), "Section 2", icon);
        gl.addComponent(acc);

        // Table, caption + column + row + action icons
        Table tbl = new Table("Table");
        tbl.setRowHeaderMode(RowHeaderMode.ICON_ONLY);
        tbl.setIcon(icon);
        tbl.addContainerProperty("Column 1", String.class, "Row", "Column 1",
                icon, Align.LEFT);
        tbl.addContainerProperty("Column 2", String.class, "Row", "Column 2",
                icon, Align.LEFT);
        tbl.setItemIcon(tbl.addItem(), icon);
        tbl.setItemIcon(tbl.addItem(), icon);
        tbl.setItemIcon(tbl.addItem(), icon);
        tbl.setPageLength(3);
        gl.addComponent(tbl);
        tbl.addActionHandler(actionHandler);

        // Selects, caption + item icons
        Class<?>[] selects = { ComboBox.class, NativeSelect.class,
                ListSelect.class, TwinColSelect.class, OptionGroup.class };
        for (Class<?> clazz : selects) {
            AbstractSelect sel;
            try {
                sel = (AbstractSelect) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            sel.setCaption(clazz.getSimpleName());
            sel.setIcon(icon);
            sel.addItem("One");
            sel.setItemIcon("One", icon);
            sel.addItem("Two");
            sel.setItemIcon("Two", icon);
            gl.addComponent(sel);
        }

        // MenuBar, caption + item + sub-item icons
        MenuBar menu = new MenuBar();
        menu.setIcon(icon);
        menu.setCaption("MenuBar");
        MenuItem mi = menu.addItem("File", icon, null);
        MenuItem smi = mi.addItem("Item", icon, null);
        smi = mi.addItem("Item", icon, null);
        smi = smi.addItem("Item", icon, null);
        gl.addComponent(menu);

        // Tree, caption + item + subitem + action icons
        Tree tree = new Tree("Tree");
        tree.addItem("Root");
        tree.setItemIcon("Root", icon);
        tree.addItem("Leaf");
        tree.setItemIcon("Leaf", icon);
        tree.setParent("Leaf", "Root");
        tree.expandItemsRecursively("Root");
        tree.addActionHandler(actionHandler);
        gl.addComponent(tree);

        // All of FontAwesome
        String allIcons = "";
        for (FontIcon ic : FontAwesome.values()) {
            allIcons += ic.getHtml() + " ";
        }
        Label label = new Label(allIcons, ContentMode.HTML);
        label.setWidth("100%");
        layout.addComponent(label);
    }

    private Label createLabel(String caption) {
        Label label = new Label(caption);
        label.setWidth("100%");
        return label;
    }

    @Override
    protected String getTestDescription() {
        return "Font icons should show up in all the right places";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13152;
    }

}
