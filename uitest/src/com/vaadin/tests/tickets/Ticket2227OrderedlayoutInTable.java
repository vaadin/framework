package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class Ticket2227OrderedlayoutInTable extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow();
        Table t = new Table();
        t.setWidth("500px");
        t.setHeight("200px");
        t.addContainerProperty("pno", String.class, "");
        t.addContainerProperty("testi", String.class, "");
        t.addContainerProperty("testi2", Layout.class, null);
        t.addContainerProperty("komponentti", Component.class, null);
        t.addContainerProperty("nimi", String.class, "");
        t.setVisibleColumns(new Object[] { "pno", "testi", "testi2", "nimi" });

        t.setSelectable(true);

        Item i = t.addItem(1);
        i.getItemProperty("pno").setValue("1");
        i.getItemProperty("testi").setValue("12.12.08");
        VerticalLayout ol = new VerticalLayout();
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
