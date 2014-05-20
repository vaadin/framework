package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class Ticket2289 extends LegacyApplication {

    TabSheet ts = null;
    Accordion acc = null;

    @Override
    public void init() {

        LegacyWindow w = new LegacyWindow();
        setMainWindow(w);
        VerticalLayout ol = new VerticalLayout();
        w.setContent(ol);
        Button b = new Button("close current tab");
        b.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                closeCurrentTab();

            }
        });
        ol.addComponent(b);

        b = new Button("close first tab");
        b.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                closeFirstTab();

            }
        });

        ol.addComponent(b);
        ts = new TabSheet();
        ts.setSizeUndefined();
        ts.setWidth("300px");
        ts.addTab(new MyTab("tab one"), "Caption1", null);
        ts.addTab(new MyTab("tab two"), "Caption2", null);
        ts.addTab(new MyTab("tab three"), "Caption3", null);
        ts.addTab(new MyTab("tab four"), "Caption4", null);
        ts.addTab(new MyTab("tab five"), "Caption5", null);

        acc = new Accordion();
        acc.setSizeUndefined();
        acc.addTab(new MyTab("tab one"), "Caption1", null);
        acc.addTab(new MyTab("tab two"), "Caption2", null);
        acc.addTab(new MyTab("tab three"), "Caption3", null);
        acc.addTab(new MyTab("tab four"), "Caption4", null);

        ol.addComponent(acc);
        ts = null;
        // ol.addComponent(ts);

    }

    private void closeCurrentTab() {
        if (ts != null) {
            MyTab m = (MyTab) ts.getSelectedTab();
            if (m != null) {
                ts.removeComponent(m);
            }
        }
        if (acc != null) {
            MyTab m = (MyTab) acc.getSelectedTab();
            if (m != null) {
                acc.removeComponent(m);
            }
        }
    }

    private void closeFirstTab() {
        if (ts != null) {
            ts.removeComponent(ts.getComponentIterator().next());
        }
        if (acc != null) {
            acc.removeComponent(acc.getComponentIterator().next());
        }
    }

}

class MyTab extends CustomComponent {
    public MyTab(String text) {
        setSizeUndefined();
        HorizontalLayout ol = new HorizontalLayout();
        setCompositionRoot(ol);
        ol.addComponent(new Label(text));
    }
}
