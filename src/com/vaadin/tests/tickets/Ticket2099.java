package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket2099 extends Application {

    private Label l1, l2, l3;
    private OrderedLayout ol1, ol2, ol3;
    private Window popup;

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        Button b = new Button("Show popup", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                getMainWindow().addWindow(popup);
                // popup.setVisible(true);
            }

        });
        popup = createPopup();
        addWindow(popup);

        layout.addComponent(b);
        layout.addComponent(new Button("Hide label '222'", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                l2.setVisible(!l2.isVisible());
            }

        }));

    }

    private Window createPopup() {
        Window w = new Window("Popup");
        TabSheet ts = new TabSheet();
        ol1 = new OrderedLayout();
        ol2 = new OrderedLayout();
        ol3 = new OrderedLayout();
        l1 = new Label("111");
        l2 = new Label("222");
        l3 = new Label("333");

        ol1.addComponent(l1);
        ol2.addComponent(l2);
        ol3.addComponent(l3);

        ts.addTab(ol1, "1", null);
        ts.addTab(ol2, "2", null);
        ts.addTab(ol3, "3", null);

        // l1.setVisible(false);
        // ts.setSelectedTab(l3);

        w.addComponent(ts);

        return w;
    }
}
