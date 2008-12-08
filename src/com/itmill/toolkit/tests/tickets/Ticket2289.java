package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2289 extends Application {

    TabSheet ts = null;
    Accordion acc = null;

    public void init() {

        Window w = new Window();
        setMainWindow(w);
        VerticalLayout ol = new VerticalLayout();
        w.setLayout(ol);
        Button b = new Button("close current tab");
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                closeCurrentTab();

            }
        });
        ol.addComponent(b);

        b = new Button("close first tab");
        b.addListener(new Button.ClickListener() {
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
            ts.removeComponent((Component) ts.getComponentIterator().next());
        }
        if (acc != null) {
            acc.removeComponent((Component) acc.getComponentIterator().next());
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