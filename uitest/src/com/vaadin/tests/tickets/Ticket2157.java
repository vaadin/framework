package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket2157 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        VerticalLayout ol;
        Panel p;
        ComboBox cb;

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox without width");
        // p.setWidth("100px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        // cb.setWidth("100%");
        ol.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox without width with caption");
        // p.setWidth("100px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        // cb.setWidth("100px");
        ol.addComponent(cb);
        layout.addComponent(p);

        //
        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100px wide");
        // p.setWidth("100px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("100px");
        ol.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100px wide with caption");
        // p.setWidth("100px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("100px");
        ol.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 500px wide");
        // p.setWidth("500px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("500px");
        ol.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 500px wide with caption");
        // p.setWidth("500px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("500px");
        ol.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100% wide");
        p.setWidth("200px");
        ol.setWidth("100%");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("100%");
        ol.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100% wide with caption");
        p.setWidth("200px");
        ol.setWidth("100%");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("100%");
        ol.addComponent(cb);
        layout.addComponent(p);

    }
}
