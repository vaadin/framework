package com.vaadin.tests.tickets;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class Ticket2040 extends com.vaadin.server.LegacyApplication {

    TextField f = new TextField();

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow();
        setMainWindow(main);

        main.getContent().setSizeFull();
        ((MarginHandler) main.getContent()).setMargin(true);

        setTheme("tests-tickets");

        Accordion ts;

        ts = new Accordion();
        ts.setSizeFull();
        ts.setWidth("300px");

        TextArea l = new TextArea("DSFS");
        l.setRows(2);
        l.setStyleName("red");
        l.setSizeFull();
        ts.addTab(l, "100% h component", null);

        Label testContent = new Label(
                "TabSheet by default uses caption, icon, errors etc. from Components. ");

        testContent.setCaption("Introduction to test");

        ts.addTab(testContent);

        // main.addComponent(ts);

        ts = new Accordion();
        ts.setSizeFull();
        ts.setHeight("200px");
        ts.setWidth("300px");

        l = new TextArea("DSFS");
        l.setRows(2);
        l.setStyleName("red");
        l.setSizeFull();
        ts.addTab(l, "200px h component", null);

        testContent = new Label(
                "TabSheet by default uses caption, icon, errors etc. from Components. ");

        testContent.setCaption("Introduction to test");

        ts.addTab(testContent);

        main.addComponent(ts);

        ts = new Accordion();
        ts.setSizeFull();
        ts.setHeight("50%");
        ts.setWidth("300px");

        l = new TextArea("DSFS");
        l.setRows(2);
        l.setStyleName("red");
        l.setSizeFull();
        ts.addTab(l, "50% h component", null);

        testContent = new Label(
                "TabSheet by default uses caption, icon, errors etc. from Components. ");

        testContent.setCaption("Introduction to test");

        ts.addTab(testContent);

        // main.addComponent(ts);

    }

}
