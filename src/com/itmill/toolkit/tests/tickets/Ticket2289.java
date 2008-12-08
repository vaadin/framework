package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2289 extends Application {

    TabSheet ts;

    public void init() {

        Window w = new Window();
        setMainWindow(w);
        OrderedLayout ol = new OrderedLayout();
        w.setLayout(ol);

        w
                .addComponent(new Label(
                        "When one tab is removed by clicking 'close a tab', client side get's mixed up."));

        Button b = new Button("close a tab");
        b.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                closeCurrentTab();

            }

        });
        ol.addComponent(b);
        ts = new TabSheet();

        ts.addTab(new MyTab("tab one"), "Caption1", null);
        ts.addTab(new MyTab("tab two"), "Caption2", null);
        ts.addTab(new MyTab("tab three"), "Caption3", null);
        ts.addTab(new MyTab("tab four"), "Caption4", null);
        ol.addComponent(ts);

    }

    private void closeCurrentTab() {
        MyTab m = (MyTab) ts.getSelectedTab();
        if (m != null) {
            ts.removeComponent(m);
        }
    }

}

class MyTab extends CustomComponent {
    public MyTab(String text) {
        OrderedLayout ol = new OrderedLayout();
        setCompositionRoot(ol);
        ol.addComponent(new Label(text));
    }
}