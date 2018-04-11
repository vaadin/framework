package com.vaadin.tests.themes;

import com.vaadin.annotations.Theme;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Table;

@Theme("reindeer")
public class LegacyComponentThemeChange extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        vl.setCaption("Change theme by clicking a button");
        HorizontalLayout hl = new HorizontalLayout();
        for (final String theme : new String[] { "reindeer", "runo" }) {
            Button b = new Button(theme);
            b.setId(theme + "");
            b.addClickListener(event -> getUI().setTheme(theme));
            hl.addComponent(b);
        }
        vl.addComponent(hl);

        // Always wants to use icon from Runo, even if we change theme
        ThemeResource alwaysTheSameIconImage = new ThemeResource(
                "../runo/icons/16/ok.png");
        ThemeResource varyingIcon = new ThemeResource("menubar-theme-icon.png");
        MenuBar bar = new MenuBar();
        bar.addItem("runo", alwaysTheSameIconImage, null);
        bar.addItem("selectedtheme", varyingIcon, null);
        MenuItem sub = bar.addItem("sub menu", null);
        sub.addItem("runo", alwaysTheSameIconImage, null);
        sub.addItem("selectedtheme", varyingIcon, null);

        vl.addComponent(bar);

        ComboBox cb = new ComboBox("ComboBox");
        cb.addItem("No icon");
        cb.addItem("Icon");
        cb.setItemIcon("Icon", new ThemeResource("comboboxicon.png"));
        cb.setValue("Icon");

        vl.addComponent(cb);

        Embedded e = new Embedded("embedded");
        e.setMimeType("application/x-shockwave-flash");
        e.setType(Embedded.TYPE_OBJECT);
        e.setSource(new ThemeResource("embedded.src"));
        vl.addComponent(e);

        Table t = new Table();
        t.addActionHandler(new Handler() {
            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new Action("Theme icon",
                        new ThemeResource("action-icon.png")) };
            }
        });
        PersonContainer pc = PersonContainer.createWithTestData();
        pc.addNestedContainerBean("address");
        t.setContainerDataSource(pc);
        vl.addComponent(t);

        addComponent(vl);
    }

}
