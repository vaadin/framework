package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2040 extends com.itmill.toolkit.Application {

    TextField f = new TextField();

    @Override
    public void init() {
        Window main = new Window();
        setMainWindow(main);

        main.getLayout().setSizeFull();
        main.getLayout().setMargin(true);

        setTheme("tests-tickets");

        Accordion ts;

        ts = new Accordion();
        ts.setSizeFull();
        ts.setWidth("300px");

        TextField l = new TextField("DSFS");
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

        l = new TextField("DSFS");
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

        l = new TextField("DSFS");
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