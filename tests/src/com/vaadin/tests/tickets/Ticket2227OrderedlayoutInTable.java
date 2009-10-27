package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

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
