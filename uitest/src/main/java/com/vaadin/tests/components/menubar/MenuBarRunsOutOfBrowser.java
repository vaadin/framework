package com.vaadin.tests.components.menubar;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;

public class MenuBarRunsOutOfBrowser extends AbstractTestCase {

    @Override
    public void init() {
        setTheme("runo");
        LegacyWindow main = new LegacyWindow("Test");
        main.setSizeFull();
        setMainWindow(main);
        main.getContent().setSizeFull();

        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Test", new ThemeResource("icons/16/calendar.png"),
                null);
        menuBar.addItem("ABC", new ThemeResource("icons/16/document.png"), null);
        menuBar.addItem("123", new ThemeResource("icons/16/help.png"), null);

        main.addComponent(menuBar);
        ((VerticalLayout) main.getContent()).setComponentAlignment(menuBar,
                Alignment.TOP_RIGHT);

    }

    @Override
    protected String getDescription() {
        return "The menubar should be right aligned but not run out of the browser";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5894;
    }

}
