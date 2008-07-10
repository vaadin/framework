package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1902 extends Application {

    public void init() {

        // Main layout and main window
        final OrderedLayout mainLayout = new OrderedLayout();
        setMainWindow(new Window("Testcase for #1902", mainLayout));
        setTheme("tests-tickets");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);
        mainLayout.addComponent(new Button("mainLayout.setSizeFull()",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        mainLayout.setSizeFull();
                        getMainWindow().showNotification(
                                "Set the main layout size full");
                    }
                }));
        mainLayout.addComponent(new Button("mainLayout.setWidth(\"100%\")",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        mainLayout.setWidth("100%");
                        getMainWindow().showNotification(
                                "Set the main layout width 100%");
                    }
                }));

        // 100% wide component
        TextField b2 = new TextField("100% wide field");
        mainLayout.addComponent(b2);
        b2.setWidth("100%");

        // 400px wide colored layout
        OrderedLayout lo = new OrderedLayout();
        lo.setStyleName("red-background");
        mainLayout.addComponent(lo);
        lo.setWidth(400);

        Button b = new Button("100% wide button");
        lo.addComponent(b);
        b.setWidth("100%");

        TextField tf = new TextField("100% wide textfield");
        lo.addComponent(tf);
        tf.setWidth("100%");

        // 400x100 colored layout
        OrderedLayout lo2 = new OrderedLayout();
        lo2.setStyleName("red-background");
        mainLayout.addComponent(lo2);
        lo2.setWidth("50%");
        lo2.setHeight(200);

        Button b3 = new Button("100% wide button");
        lo2.addComponent(b3);
        b3.setWidth("100%");

        TextField tf2 = new TextField("100% wide textfield");
        lo2.addComponent(tf2);
        tf2.setWidth("100%");
        // tf2 = new TextField("50% wide, 100% height textfield"); // does not
        // work with caption (10.7.2008 mac hosted mode) due layouts are broken
        // in trunk
        tf2 = new TextField();
        tf2.setRows(2); // trigger textArea impl.
        tf2.setHeight("100%");
        tf2.setWidth("50%");
        lo2.addComponent(tf2);
    }
}