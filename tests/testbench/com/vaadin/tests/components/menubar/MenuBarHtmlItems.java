package com.vaadin.tests.components.menubar;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarHtmlItems extends ComponentTestCase<MenuBar> {

    @Override
    protected Class<MenuBar> getTestClass() {
        return MenuBar.class;
    }

    @Override
    protected void initializeComponents() {
        MenuBar m = new MenuBar();
        MenuItem submenu = m.addItem("Item <u>1</u>", getIcon(), null);
        MenuItem subsubmenu = submenu.addItem("<b>Bold</b> item", null);
        subsubmenu.addItem("<i><u>I</u>talic</i> item", getIcon(), null);
        submenu.addItem(
                "<span style='font-size: 30px'>Big</span> <span style='font-size: 8px'>disabled</span> item",
                null).setEnabled(false);

        m.addItem("<span style='font-size: 30px'>Big</span> item", null);

        addTestComponent(m);
    }

    private Resource getIcon() {
        return new ThemeResource("../runo/icons/16/user.png");
    }

    @Override
    protected List<Component> createActions() {
        return Arrays.asList(createSwitchHtmlAction());
    }

    private Component createSwitchHtmlAction() {
        return createBooleanAction("Html content allowed", false,
                new Command<MenuBar, Boolean>() {
                    @Override
                    public void execute(MenuBar c, Boolean value, Object data) {
                        c.setHtmlContentAllowed(value.booleanValue());
                    }
                });
    }

    @Override
    protected Integer getTicketNumber() {
        return 7187;
    }

    @Override
    protected String getDescription() {
        return "A menu containing items with embedded html. Items should chould either render the html or show it as plain text depending on the setting.";
    }

}
