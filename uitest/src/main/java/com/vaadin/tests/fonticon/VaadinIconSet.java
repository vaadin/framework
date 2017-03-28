package com.vaadin.tests.fonticon;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

public class VaadinIconSet extends AbstractTestUI {

    private List<Component> componentz;

    @Override
    protected void setup(VaadinRequest request) {
        buildUI(com.vaadin.icons.VaadinIcons.VAADIN_V);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void buildUI(final Resource icon) {
        VerticalLayout layout = new VerticalLayout();
        addComponent(layout);
        layout.setSpacing(false);

        layout.setIcon(icon);

        GridLayout gl = new GridLayout(5, 5);

        layout.addComponent(new Button("Switch icon type",
                event -> gl.iterator().forEachRemaining(
                        c -> c.setIcon(VaadinIcons.AIRPLANE))));

        // Notification
        Notification n = new Notification("Hey there!");
        n.setIcon(icon);
        n.setPosition(Position.BOTTOM_CENTER);
        n.setDelayMsec(300000);
        n.show(Page.getCurrent());

        // grid of compoents
        layout.addComponent(gl);

        // Basic components, caption icon only
        Class<?>[] components = { Button.class, CheckBox.class, DateField.class,
                NativeButton.class, Link.class, Label.class, Panel.class,
                Slider.class, TextArea.class, TextField.class, Upload.class };
        for (Class<?> clazz : components) {
            Component c;
            try {
                c = (Component) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
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

        Grid<String> grid = new Grid<>();
        grid.setCaption("Grid");
        grid.setIcon(icon);
        grid.setItems("item 1", "item 2", "item 3");
        grid.addColumn(string -> string).setCaption("column 1");
        grid.addColumn(string -> "Another " + string).setCaption("column 2");
        // vaadin/framework#8207
        // grid.setHeightByRows(3);
        grid.setHeight("150px");
        gl.addComponent(grid);

        // Selects, caption + item icons
        Class<?>[] selects = { ComboBox.class, NativeSelect.class,
                ListSelect.class, TwinColSelect.class, RadioButtonGroup.class,
                CheckBoxGroup.class };
        for (Class<?> clazz : selects) {
            AbstractListing<String> sel;
            try {
                sel = (AbstractListing<String>) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            sel.setCaption(clazz.getSimpleName());
            sel.setIcon(icon);
            sel.setItems("One", "Two");
            if (sel instanceof RadioButtonGroup) {
                ((RadioButtonGroup) sel).setItemIconGenerator(item -> icon);
            } else if (sel instanceof CheckBoxGroup) {
                ((CheckBoxGroup) sel).setItemIconGenerator(item -> icon);
            } else if (sel instanceof ComboBox) {
                ((ComboBox) sel).setItemIconGenerator(item -> icon);
            }
            gl.addComponent(sel);
            sel.setWidth("100%");
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

        // All of Vaadin Icons
        String allIcons = "";
        for (FontIcon ic : com.vaadin.icons.VaadinIcons.values()) {
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
        return "Vaadin Icons should show up in all the right places";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7979;
    }

}
