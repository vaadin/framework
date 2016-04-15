package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2099 extends LegacyApplication {

    private Label l1, l2, l3;
    private VerticalLayout ol1, ol2, ol3;
    private Window popup;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        Button b = new Button("Show popup", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().addWindow(popup);
                // popup.setVisible(true);
            }

        });
        popup = createPopup();
        getMainWindow().addWindow(popup);

        layout.addComponent(b);
        layout.addComponent(new Button("Hide label '222'", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l2.setVisible(!l2.isVisible());
            }

        }));

    }

    private Window createPopup() {
        Window w = new Window("Popup");
        TabSheet ts = new TabSheet();
        ol1 = new VerticalLayout();
        ol2 = new VerticalLayout();
        ol3 = new VerticalLayout();
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

        w.setContent(ts);

        return w;
    }
}
