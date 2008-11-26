package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket2227OrderedlayoutInTable extends Application {

    @Override
    public void init() {
        Window w = new Window();
        Table t = new Table();
        t.setWidth("500px");
        t.setHeight("200px");
        t.addContainerProperty("pno", String.class, "");
        t.addContainerProperty("testi", String.class, "");
        t.addContainerProperty("testi2", OrderedLayout.class, null);
        t.addContainerProperty("komponentti", Component.class, null);
        t.addContainerProperty("nimi", String.class, "");
        t.setVisibleColumns(new Object[] { "pno", "testi", "testi2", "nimi" });

        t.setSelectable(true);

        Item i = t.addItem(1);
        i.getItemProperty("pno").setValue("1");
        i.getItemProperty("testi").setValue("12.12.08");
        OrderedLayout ol = new OrderedLayout();
        ol.setWidth("100%");
        ol.setHeight(null);
        ol.addComponent(new Label("monirivi testi"));
        ol.addComponent(new Label("monirivi testi2"));

        i.getItemProperty("testi2").setValue(ol);
        i.getItemProperty("nimi").setValue("test");

        w.addComponent(t);
        setMainWindow(w);
    }

}
